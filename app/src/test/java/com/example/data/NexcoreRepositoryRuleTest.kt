package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NexcoreRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun createListing_andObservePublicListings_succeedsForAuthenticatedUser() = runBlocking {
    signInTestUser(ALICE_EMAIL)
    val repo = NexcoreRepository(firestore, auth)

    val createRes = withTimeout(DEFAULT_TIMEOUT_MS) {
      repo.createListing(
        sellerName = "Alice Demolition",
        sellerRole = "Demolition Contractor / Waste Seller",
        listingType = "WASTE",
        title = "Crushed Concrete Slab Debris",
        category = "Concrete Debris",
        description = "Clean concrete slabs ready for mobile crushing.",
        conditionSpecs = "Pieces < 500mm, no rebar",
        quantityTonnes = 120.0,
        pricePerTonne = 12.5,
        priceType = "Negotiable",
        location = "Austin Site 3",
        preferredCollectionDate = "Next Monday",
        deliveryOptions = "Pickup Only",
        transportIncluded = false,
        contactPreference = "Platform Quote",
        imageKey = "crushed_concrete"
      )
    }
    assertTrue("Expected listing creation to succeed: ${createRes.exceptionOrNull()}", createRes.isSuccess)
    val listingId = createRes.getOrThrow()

    val publicListings = withTimeout(FLOW_TIMEOUT_MS) {
      repo.observePublicListings().first { list -> list.any { it.id == listingId } }
    }
    assertTrue(publicListings.any { it.id == listingId && it.title == "Crushed Concrete Slab Debris" })
  }

  @Test
  fun createCrusherBooking_crossUserAccess_failsWithPermissionDenied() = runBlocking {
    signInTestUser(ALICE_EMAIL)
    val aliceRepo = NexcoreRepository(firestore, auth)

    val bookingRes = withTimeout(DEFAULT_TIMEOUT_MS) {
      aliceRepo.createCrusherBooking(
        customerName = "Alice Builder",
        contactNumber = "555-0199",
        siteLocation = "West Industrial Park",
        wasteType = "Concrete Debris",
        estimatedTonnes = 300.0,
        siteAccessInfo = "Level access road for mobile jaw crusher",
        preferredDate = "2026-11-12",
        serviceType = "Both",
        additionalNotes = "Segregated stockpile"
      )
    }
    assertTrue("Expected booking creation to succeed: ${bookingRes.exceptionOrNull()}", bookingRes.isSuccess)
    val booking = bookingRes.getOrThrow()

    // Switch to Bob and verify cross-user get fails with PERMISSION_DENIED
    signInTestUser(BOB_EMAIL)
    val bobRepo = NexcoreRepository(firestore, auth)
    val bobReadRes = withTimeout(DEFAULT_TIMEOUT_MS) {
      bobRepo.getCrusherBookingById(booking.id)
    }
    assertTrue("Expected cross-user read to fail", bobReadRes.isFailure)
    val ex = bobReadRes.exceptionOrNull() as? FirebaseFirestoreException
    assertNotNull("Expected FirebaseFirestoreException", ex)
    assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, ex?.code)
  }

  @Test
  fun observeCrusherBookings_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
    auth.signOut()
    val repo = NexcoreRepository(firestore, auth)

    var caughtException: FirebaseFirestoreException? = null
    try {
      withTimeout(FLOW_TIMEOUT_MS) {
        repo.observeUserCrusherBookings("unauthenticated_test_user").first()
      }
    } catch (e: FirebaseFirestoreException) {
      caughtException = e
    }
    assertNotNull("Expected unauthenticated query to throw FirebaseFirestoreException", caughtException)
    assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, caughtException?.code)
  }

  private companion object {
    const val ALICE_EMAIL = "alice@nexcore.test"
    const val BOB_EMAIL = "bob@nexcore.test"
    const val DEFAULT_TIMEOUT_MS = 5000L
    const val FLOW_TIMEOUT_MS = 4000L
  }
}
