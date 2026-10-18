const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

function validListingPayload(sellerId) {
  const now = new Date();
  return {
    sellerId,
    sellerName: "Apex Demolition Ltd",
    sellerRole: "Demolition Contractor / Waste Seller",
    sellerVerified: false,
    listingType: "MATERIAL",
    title: "20mm Recycled Concrete Aggregate",
    category: "Recycled Aggregates",
    description: "Clean crushed 20mm RCA tested for sub-base compaction.",
    conditionSpecs: "Gradation 0-20mm, <1% deleterious material",
    quantityTonnes: 250,
    pricePerTonne: 18.5,
    unit: "per tonne",
    priceType: "Fixed Price",
    location: "Austin Industrial Zone",
    preferredCollectionDate: "Immediate",
    deliveryOptions: "Pickup or Delivery",
    transportIncluded: false,
    contactPreference: "Platform Quote",
    status: "Active",
    visibility: "public",
    isSample: false,
    imageKey: "recycled_concrete",
    createdAt: now,
    updatedAt: now,
  };
}

function validBookingPayload(userId) {
  const now = new Date();
  return {
    userId,
    bookingRef: "NXC-CR-1001",
    customerName: "Alice Builder",
    contactNumber: "+1-555-0192",
    siteLocation: "North Austin Yard, Gate 4",
    wasteType: "Concrete Debris",
    estimatedTonnes: 400,
    siteAccessInfo: "Wide 6m articulated truck access, level hardstand.",
    preferredDate: "2026-11-05",
    serviceType: "Both",
    additionalNotes: "Reinforced concrete slabs pre-broken to <600mm.",
    status: "Request Submitted",
    quotedAmount: 0,
    providerName: "Pending Assessment",
    createdAt: now,
    updatedAt: now,
  };
}

test("1. Unauthenticated user cannot list public marketplace listings", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(
    unauthDb.collection("listings").where("visibility", "==", "public").get()
  );
});

test("2. PII Isolation: Authenticated user cannot read another user's profile", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    await ctx.firestore().collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      fullName: "Alice Contractor",
      companyName: "Alice Builds",
      email: "alice@example.com",
      phone: "555-1234",
      city: "Austin",
      accountType: "Buyer / Customer",
      isVerified: false,
      savedListingIds: [],
      createdAt: now,
      updatedAt: now,
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
});

test("3. Privilege Escalation Guard: User cannot self-assign isVerified = true on signup", async () => {
  const now = new Date();
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      fullName: "Alice Contractor",
      companyName: "Alice Builds",
      email: "alice@example.com",
      phone: "555-1234",
      city: "Austin",
      accountType: "Buyer / Customer",
      isVerified: true,
      savedListingIds: [],
      createdAt: now,
      updatedAt: now,
    })
  );
});

test("4. Authenticated user can create valid listing and query public listings", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("listings").doc("listing_1").set(validListingPayload(ALICE_UID))
  );

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertSucceeds(
    bobDb.collection("listings").where("visibility", "==", "public").get()
  );
});

test("5. Identity Spoofing Guard: Alice cannot create a listing with Bob's sellerId", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("listings").doc("listing_spoof").set(validListingPayload(BOB_UID))
  );
});

test("6. Shadow Update Guard: Adding a ghost field to listing update is rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("listings").doc("listing_1").set(validListingPayload(ALICE_UID))
  );

  await assertFails(
    aliceDb.collection("listings").doc("listing_1").update({
      title: "Updated Title",
      ghostField: "malicious",
      updatedAt: new Date(),
    })
  );
});

test("7. Crusher Booking Isolation & Terminal State Lock", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("crusher_bookings").doc("booking_1").set(validBookingPayload(ALICE_UID))
  );

  // Bob cannot read Alice's booking
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("crusher_bookings").doc("booking_1").get());

  // Alice cancels her booking
  await assertSucceeds(
    aliceDb.collection("crusher_bookings").doc("booking_1").update({
      status: "Cancelled",
      updatedAt: new Date(),
    })
  );

  // Terminal state lock: Alice cannot reopen a Cancelled booking
  await assertFails(
    aliceDb.collection("crusher_bookings").doc("booking_1").update({
      status: "Accepted",
      updatedAt: new Date(),
    })
  );
});
