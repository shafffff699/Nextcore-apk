package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

private fun Throwable.unwrapFirestoreException(): FirebaseFirestoreException? {
  var current: Throwable? = this
  while (current != null) {
    if (current is FirebaseFirestoreException) return current
    current = current.cause
  }
  return null
}

class NexcoreRepository(
  private val db: FirebaseFirestore,
  private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) {
  // CRITICAL: Always initialize with R.string.firestore_database_id
  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    ),
    FirebaseAuth.getInstance()
  )

  private fun requireUserId(): String {
    return auth.currentUser?.uid
      ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
  }

  // ===========================================================================
  // 1. User Profile Operations (/users/{userId})
  // ===========================================================================
  fun observeUserProfile(userId: String): Flow<UserProfile?> =
    db.collection("users")
      .document(userId)
      .snapshots()
      .map { snapshot ->
        if (snapshot.exists()) {
          snapshot.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
        } else {
          null
        }
      }
      .catch { error ->
        val fsEx = error.unwrapFirestoreException()
        if (fsEx != null) {
          handleFirestoreError(fsEx, OperationType.GET, "users/$userId")
          throw fsEx
        }
        if (error is Exception) handleFirestoreError(error, OperationType.GET, "users/$userId")
        throw error
      }

  suspend fun getUserProfile(userId: String): Result<UserProfile?> = runCatching {
    val path = "users/$userId"
    try {
      val doc = db.collection("users").document(userId).get().await()
      if (doc.exists()) {
        doc.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
      } else {
        null
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.GET, path)
      throw e
    }
  }

  suspend fun saveUserProfile(
    fullName: String,
    companyName: String,
    email: String,
    phone: String,
    city: String,
    accountType: String,
    existingProfile: UserProfile? = null,
  ): Result<Unit> = runCatching {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid)
    try {
      val roleSafe = if (accountType == "Administrator") "Buyer / Customer" else accountType
      if (existingProfile == null || existingProfile.createdAt == null) {
        val payload = mapOf(
          "userId" to uid,
          "fullName" to fullName.trim().take(100).ifEmpty { "NEXCORE Member" },
          "companyName" to companyName.trim().take(120),
          "email" to email.trim().take(150).ifEmpty { "user@nexcore.app" },
          "phone" to phone.trim().take(40),
          "city" to city.trim().take(100).ifEmpty { "Metro Area" },
          "accountType" to roleSafe,
          "isVerified" to false,
          "savedListingIds" to emptyList<String>(),
          "createdAt" to FieldValue.serverTimestamp(),
          "updatedAt" to FieldValue.serverTimestamp(),
        )
        docRef.set(payload).await()
      } else {
        val updates = mapOf(
          "fullName" to fullName.trim().take(100).ifEmpty { existingProfile.fullName },
          "companyName" to companyName.trim().take(120),
          "phone" to phone.trim().take(40),
          "city" to city.trim().take(100).ifEmpty { existingProfile.city },
          "accountType" to roleSafe,
          "updatedAt" to FieldValue.serverTimestamp(),
        )
        docRef.update(updates).await()
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, docRef.path)
      throw e
    }
  }

  suspend fun toggleSavedListing(
    listingId: String,
    currentSavedIds: List<String>,
  ): Result<List<String>> = runCatching {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid)
    val updatedList =
      if (currentSavedIds.contains(listingId)) {
        currentSavedIds - listingId
      } else {
        (currentSavedIds + listingId).distinct().take(50)
      }
    try {
      docRef.update(
        mapOf(
          "savedListingIds" to updatedList,
          "updatedAt" to FieldValue.serverTimestamp(),
        )
      ).await()
      updatedList
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      throw e
    }
  }

  // ===========================================================================
  // 2. Marketplace Listings (/listings/{listingId})
  // ===========================================================================
  fun observePublicListings(): Flow<List<MarketplaceListing>> =
    db.collection("listings")
      .whereEqualTo("visibility", "public")
      .snapshots()
      .map { snapshot ->
        snapshot.toObjects(
          MarketplaceListing::class.java,
          DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
        )
      }
      .catch { error ->
        val fsEx = error.unwrapFirestoreException()
        if (fsEx != null) {
          handleFirestoreError(fsEx, OperationType.LIST, "listings")
          throw fsEx
        }
        if (error is Exception) handleFirestoreError(error, OperationType.LIST, "listings")
        throw error
      }

  fun observeSellerListings(sellerId: String): Flow<List<MarketplaceListing>> =
    db.collection("listings")
      .whereEqualTo("sellerId", sellerId)
      .snapshots()
      .map { snapshot ->
        snapshot.toObjects(
          MarketplaceListing::class.java,
          DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
        )
      }
      .catch { error ->
        val fsEx = error.unwrapFirestoreException()
        if (fsEx != null) {
          handleFirestoreError(fsEx, OperationType.LIST, "listings")
          throw fsEx
        }
        if (error is Exception) handleFirestoreError(error, OperationType.LIST, "listings")
        throw error
      }

  suspend fun getListingById(listingId: String): Result<MarketplaceListing> = runCatching {
    val docRef = db.collection("listings").document(listingId)
    try {
      val snapshot = docRef.get().await()
      snapshot.toObject(
        MarketplaceListing::class.java,
        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
      ) ?: throw IllegalStateException("Listing not found")
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.GET, docRef.path)
      throw e
    }
  }

  suspend fun createListing(
    sellerName: String,
    sellerRole: String,
    listingType: String,
    title: String,
    category: String,
    description: String,
    conditionSpecs: String,
    quantityTonnes: Double,
    pricePerTonne: Double,
    unit: String = "per tonne",
    priceType: String,
    location: String,
    preferredCollectionDate: String,
    deliveryOptions: String,
    transportIncluded: Boolean,
    contactPreference: String,
    imageKey: String,
  ): Result<String> = runCatching {
    val uid = requireUserId()
    val docId = "lst_${UUID.randomUUID().toString().replace("-", "").take(16)}"
    val docRef = db.collection("listings").document(docId)
    val payload = mapOf(
      "sellerId" to uid,
      "sellerName" to sellerName.trim().take(120).ifEmpty { "NEXCORE Seller" },
      "sellerRole" to sellerRole.trim().take(80),
      "sellerVerified" to false,
      "listingType" to if (listingType in listOf("MATERIAL", "WASTE")) listingType else "WASTE",
      "title" to title.trim().take(140),
      "category" to category.trim().take(80),
      "description" to description.trim().take(1500),
      "conditionSpecs" to conditionSpecs.trim().take(600),
      "quantityTonnes" to quantityTonnes.coerceIn(0.1, 1000000.0),
      "pricePerTonne" to pricePerTonne.coerceIn(0.0, 1000000.0),
      "unit" to unit.trim().take(40).ifEmpty { "per tonne" },
      "priceType" to if (priceType in listOf("Fixed Price", "Negotiable")) priceType else "Negotiable",
      "location" to location.trim().take(150),
      "preferredCollectionDate" to preferredCollectionDate.trim().take(60).ifEmpty { "Flexible" },
      "deliveryOptions" to if (deliveryOptions in listOf("Pickup Only", "Delivery Available", "Pickup or Delivery")) deliveryOptions else "Pickup or Delivery",
      "transportIncluded" to transportIncluded,
      "contactPreference" to contactPreference.trim().take(80).ifEmpty { "Platform Quote" },
      "status" to "Active",
      "visibility" to "public",
      "isSample" to false,
      "imageKey" to imageKey.trim().take(60).ifEmpty { "recycled_concrete" },
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp(),
    )
    try {
      docRef.set(payload).await()
      docId
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path)
      throw e
    }
  }

  suspend fun updateListingStatus(
    listingId: String,
    newStatus: String,
  ): Result<Unit> = runCatching {
    requireUserId()
    val docRef = db.collection("listings").document(listingId)
    try {
      docRef.update(
        mapOf(
          "status" to newStatus,
          "updatedAt" to FieldValue.serverTimestamp(),
        )
      ).await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      throw e
    }
  }

  suspend fun deleteListing(listingId: String): Result<Unit> = runCatching {
    requireUserId()
    val docRef = db.collection("listings").document(listingId)
    try {
      docRef.delete().await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, docRef.path)
      throw e
    }
  }

  // ===========================================================================
  // 3. Mobile Crusher Bookings (/crusher_bookings/{bookingId})
  // ===========================================================================
  fun observeUserCrusherBookings(userId: String): Flow<List<CrusherBooking>> =
    db.collection("crusher_bookings")
      .whereEqualTo("userId", userId)
      .snapshots()
      .map { snapshot ->
        snapshot.toObjects(
          CrusherBooking::class.java,
          DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
        )
      }
      .catch { error ->
        val fsEx = error.unwrapFirestoreException()
        if (fsEx != null) {
          handleFirestoreError(fsEx, OperationType.LIST, "crusher_bookings")
          throw fsEx
        }
        if (error is Exception) handleFirestoreError(error, OperationType.LIST, "crusher_bookings")
        throw error
      }

  suspend fun getCrusherBookingById(bookingId: String): Result<CrusherBooking> = runCatching {
    val docRef = db.collection("crusher_bookings").document(bookingId)
    try {
      val snapshot = docRef.get().await()
      snapshot.toObject(
        CrusherBooking::class.java,
        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
      ) ?: throw IllegalStateException("Booking not found")
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.GET, docRef.path)
      throw e
    }
  }

  suspend fun createCrusherBooking(
    customerName: String,
    contactNumber: String,
    siteLocation: String,
    wasteType: String,
    estimatedTonnes: Double,
    siteAccessInfo: String,
    preferredDate: String,
    serviceType: String,
    additionalNotes: String,
  ): Result<CrusherBooking> = runCatching {
    val uid = requireUserId()
    val docId = "crb_${UUID.randomUUID().toString().replace("-", "").take(16)}"
    val bookingRef = "NXC-CR-${(1000..9999).random()}"
    val docRef = db.collection("crusher_bookings").document(docId)
    val payload = mapOf(
      "userId" to uid,
      "bookingRef" to bookingRef,
      "customerName" to customerName.trim().take(100),
      "contactNumber" to contactNumber.trim().take(40),
      "siteLocation" to siteLocation.trim().take(200),
      "wasteType" to wasteType.trim().take(80),
      "estimatedTonnes" to estimatedTonnes.coerceIn(1.0, 1000000.0),
      "siteAccessInfo" to siteAccessInfo.trim().take(500),
      "preferredDate" to preferredDate.trim().take(60),
      "serviceType" to if (serviceType in listOf("Crushing", "Screening", "Both")) serviceType else "Both",
      "additionalNotes" to additionalNotes.trim().take(800),
      "status" to "Request Submitted",
      "quotedAmount" to 0.0,
      "providerName" to "Pending Service Assessment",
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp(),
    )
    try {
      docRef.set(payload).await()
      CrusherBooking(
        id = docId,
        userId = uid,
        bookingRef = bookingRef,
        customerName = customerName.trim(),
        contactNumber = contactNumber.trim(),
        siteLocation = siteLocation.trim(),
        wasteType = wasteType.trim(),
        estimatedTonnes = estimatedTonnes,
        siteAccessInfo = siteAccessInfo.trim(),
        preferredDate = preferredDate.trim(),
        serviceType = serviceType,
        additionalNotes = additionalNotes.trim(),
        status = "Request Submitted",
        quotedAmount = 0.0,
        providerName = "Pending Service Assessment",
      )
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path)
      throw e
    }
  }

  suspend fun updateCrusherBookingStatus(
    bookingId: String,
    newStatus: String,
    quotedAmount: Double? = null,
    providerName: String? = null,
  ): Result<Unit> = runCatching {
    requireUserId()
    val docRef = db.collection("crusher_bookings").document(bookingId)
    val updates = mutableMapOf<String, Any>(
      "status" to newStatus,
      "updatedAt" to FieldValue.serverTimestamp(),
    )
    if (quotedAmount != null) updates["quotedAmount"] = quotedAmount
    if (providerName != null) updates["providerName"] = providerName.take(120)
    try {
      docRef.update(updates).await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      throw e
    }
  }

  // ===========================================================================
  // 4. Quotations & Orders (/quotations/{quotationId})
  // ===========================================================================
  fun observeBuyerQuotations(buyerId: String): Flow<List<QuotationOrder>> =
    db.collection("quotations")
      .whereEqualTo("buyerId", buyerId)
      .snapshots()
      .map { snapshot ->
        snapshot.toObjects(
          QuotationOrder::class.java,
          DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
        )
      }
      .catch { error ->
        val fsEx = error.unwrapFirestoreException()
        if (fsEx != null) {
          handleFirestoreError(fsEx, OperationType.LIST, "quotations")
          throw fsEx
        }
        if (error is Exception) handleFirestoreError(error, OperationType.LIST, "quotations")
        throw error
      }

  fun observeSellerQuotations(sellerId: String): Flow<List<QuotationOrder>> =
    db.collection("quotations")
      .whereEqualTo("sellerId", sellerId)
      .snapshots()
      .map { snapshot ->
        snapshot.toObjects(
          QuotationOrder::class.java,
          DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
        )
      }
      .catch { error ->
        val fsEx = error.unwrapFirestoreException()
        if (fsEx != null) {
          handleFirestoreError(fsEx, OperationType.LIST, "quotations")
          throw fsEx
        }
        if (error is Exception) handleFirestoreError(error, OperationType.LIST, "quotations")
        throw error
      }

  suspend fun createQuotationRequest(
    buyerName: String,
    listing: MarketplaceListing,
    quantityTonnes: Double,
    deliveryPreference: String,
    buyerMessage: String,
  ): Result<String> = runCatching {
    val uid = requireUserId()
    val docId = "quo_${UUID.randomUUID().toString().replace("-", "").take(16)}"
    val orderRef = "NXC-QT-${(1000..9999).random()}"
    val docRef = db.collection("quotations").document(docId)
    val unitPrice = listing.pricePerTonne
    val materialSubtotal = (quantityTonnes * unitPrice)
    val transportCost = if (listing.transportIncluded || deliveryPreference == "Pickup / Collection") 0.0 else (quantityTonnes * 3.5).coerceAtLeast(65.0)
    val processingFee = 25.0
    val taxAmount = ((materialSubtotal + transportCost + processingFee) * 0.08)
    val totalAmount = materialSubtotal + transportCost + processingFee + taxAmount

    val payload = mapOf(
      "orderRef" to orderRef,
      "buyerId" to uid,
      "buyerName" to buyerName.trim().take(100).ifEmpty { "NEXCORE Buyer" },
      "sellerId" to listing.sellerId,
      "sellerName" to listing.sellerName.take(120),
      "listingId" to listing.id,
      "listingTitle" to listing.title.take(140),
      "category" to listing.category.take(80),
      "quantityTonnes" to quantityTonnes.coerceIn(0.5, 1000000.0),
      "unitPrice" to unitPrice,
      "materialSubtotal" to materialSubtotal,
      "transportCost" to transportCost,
      "processingFee" to processingFee,
      "taxAmount" to taxAmount,
      "totalAmount" to totalAmount,
      "deliveryPreference" to deliveryPreference.take(80),
      "buyerMessage" to buyerMessage.trim().take(600),
      "sellerNotes" to "Indicative quotation calculation based on listing rate.",
      "validityDays" to 14L,
      "orderStatus" to "Quote Requested",
      "paymentStatus" to "Payment Pending (Prototype)",
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp(),
    )
    try {
      docRef.set(payload).await()
      docId
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path)
      throw e
    }
  }

  suspend fun submitSellerQuotationResponse(
    quotationId: String,
    quantityTonnes: Double,
    offeredUnitPrice: Double,
    transportCost: Double,
    processingFee: Double,
    validityDays: Long,
    sellerNotes: String,
  ): Result<Unit> = runCatching {
    requireUserId()
    val docRef = db.collection("quotations").document(quotationId)
    val materialSubtotal = quantityTonnes * offeredUnitPrice
    val taxAmount = (materialSubtotal + transportCost + processingFee) * 0.08
    val totalAmount = materialSubtotal + transportCost + processingFee + taxAmount
    val updates = mapOf(
      "unitPrice" to offeredUnitPrice.coerceAtLeast(0.0),
      "materialSubtotal" to materialSubtotal.coerceAtLeast(0.0),
      "transportCost" to transportCost.coerceAtLeast(0.0),
      "processingFee" to processingFee.coerceAtLeast(0.0),
      "taxAmount" to taxAmount.coerceAtLeast(0.0),
      "totalAmount" to totalAmount.coerceAtLeast(0.0),
      "validityDays" to validityDays.coerceIn(1L, 365L),
      "sellerNotes" to sellerNotes.trim().take(600),
      "orderStatus" to "Quote Received",
      "updatedAt" to FieldValue.serverTimestamp(),
    )
    try {
      docRef.update(updates).await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      throw e
    }
  }

  suspend fun updateQuotationOrderStatus(
    quotationId: String,
    newStatus: String,
    paymentStatus: String? = null,
  ): Result<Unit> = runCatching {
    requireUserId()
    val docRef = db.collection("quotations").document(quotationId)
    val updates = mutableMapOf<String, Any>(
      "orderStatus" to newStatus,
      "updatedAt" to FieldValue.serverTimestamp(),
    )
    if (paymentStatus != null) {
      updates["paymentStatus"] = paymentStatus.take(80)
    }
    try {
      docRef.update(updates).await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      throw e
    }
  }

  // ===========================================================================
  // 5. Trust & Safety Reports (/reports/{reportId})
  // ===========================================================================
  fun observeUserReports(reporterId: String): Flow<List<TrustReport>> =
    db.collection("reports")
      .whereEqualTo("reporterId", reporterId)
      .snapshots()
      .map { snapshot ->
        snapshot.toObjects(
          TrustReport::class.java,
          DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
        )
      }
      .catch { error ->
        val fsEx = error.unwrapFirestoreException()
        if (fsEx != null) {
          handleFirestoreError(fsEx, OperationType.LIST, "reports")
          throw fsEx
        }
        if (error is Exception) handleFirestoreError(error, OperationType.LIST, "reports")
        throw error
      }

  suspend fun submitTrustReport(
    targetType: String,
    targetId: String,
    targetTitle: String,
    reason: String,
    details: String,
  ): Result<String> = runCatching {
    val uid = requireUserId()
    val docId = "rep_${UUID.randomUUID().toString().replace("-", "").take(16)}"
    val docRef = db.collection("reports").document(docId)
    val payload = mapOf(
      "reporterId" to uid,
      "targetType" to if (targetType in listOf("Listing", "User")) targetType else "Listing",
      "targetId" to targetId.trim().take(128).ifEmpty { "unknown" },
      "targetTitle" to targetTitle.trim().take(140).ifEmpty { "Reported Item" },
      "reason" to reason.trim().take(100).ifEmpty { "Policy Concern" },
      "details" to details.trim().take(600),
      "status" to "Submitted",
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp(),
    )
    try {
      docRef.set(payload).await()
      docId
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path)
      throw e
    }
  }
}
