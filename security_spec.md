# NEXCORE Security Specification (Phase 0: Payload-First Security TDD)

## 1. Data Invariants
1. **Authentication Invariant**: Every read and write operation across `/users`, `/listings`, `/crusher_bookings`, `/quotations`, and `/reports` requires a valid authenticated session (`request.auth != null`).
2. **PII Isolation Invariant**: `/users/{userId}` and `/crusher_bookings/{bookingId}` contain contact details (`email`, `phone`, `contactNumber`, `siteLocation`) and are strictly restricted to the document owner (`request.auth.uid == userId` / `resource.data.userId == request.auth.uid`) or verified administrators (`isAdmin()`).
3. **Identity & Ownership Invariant**: On creation, `userId`, `sellerId`, `buyerId`, and `reporterId` must strictly equal `request.auth.uid`. On update, ownership and `createdAt` fields are immutable.
4. **Role & Verification Anti-Escalation**: Users cannot self-assign `isVerified = true` or `accountType = 'Administrator'` upon creation or update unless `isAdmin()` is true.
5. **Query Alignment Invariant**: `allow list` rules never allow blanket `isSignedIn()` reads; they evaluate `resource.data` (`visibility == 'public'`, `sellerId == request.auth.uid`, `userId == request.auth.uid`, `buyerId == request.auth.uid`, or `reporterId == request.auth.uid`).
6. **Terminal State Locking**: Once a crusher booking or quotation reaches `'Completed'` or `'Cancelled'`, non-admin users cannot mutate it further.

## 2. The "Dirty Dozen" Payloads
1. **Unauthenticated Listing Read**: `unauthDb.collection("listings").where("visibility", "==", "public").get()` -> Reject (`PERMISSION_DENIED`).
2. **PII Blanket Read on User Profile**: `bobDb.collection("users").doc("alice_123").get()` -> Reject (`PERMISSION_DENIED`).
3. **Privilege Escalation (Self-Verified User)**: Alice creates `/users/alice_123` with `isVerified: true` -> Reject (`PERMISSION_DENIED`).
4. **Identity Spoofing on Listing Creation**: Alice creates `/listings/item_1` with `sellerId: "bob_456"` -> Reject (`PERMISSION_DENIED`).
5. **Shadow Update / Ghost Field Injection**: Alice updates `/listings/item_1` adding `unapprovedField: "hack"` -> Reject (`PERMISSION_DENIED` via `hasOnly`).
6. **Value Poisoning (Oversized String)**: Alice creates a listing with a 5,000-character `title` -> Reject (`PERMISSION_DENIED` via `.size()` bounds).
7. **Timestamp Clock-Skew / Forge**: Alice updates a listing and overwrites `createdAt` -> Reject (`PERMISSION_DENIED` via `incoming().createdAt == existing().createdAt`).
8. **Cross-User Booking Snooping**: Bob attempts `get` or `list` on Alice's `/crusher_bookings` -> Reject (`PERMISSION_DENIED`).
9. **Terminal State Re-opening**: Alice attempts to update a `'Cancelled'` crusher booking back to `'Accepted'` -> Reject (`PERMISSION_DENIED`).
10. **Unauthorized Quotation Price Tampering**: Buyer Alice attempts to modify `unitPrice` or `totalAmount` on an existing quotation after submission -> Reject (`PERMISSION_DENIED` via action-based `affectedKeys().hasOnly(...)`).
11. **Unfiltered Collection Scraping**: Alice runs `aliceDb.collection("quotations").get()` without `.where("buyerId", "==", "alice_123")` -> Reject (`PERMISSION_DENIED`).
12. **ID Poisoning**: Creating a document with invalid characters or >128 chars in ID -> Reject (`PERMISSION_DENIED` via `isValidId`).
