package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class NexcoreRepository(
  private val db: FirebaseFirestore,
  private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) {
  constructor(context: Context) : this(
    db = FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    ),
    auth = FirebaseAuth.getInstance()
  )

  private fun DocumentSnapshot.toUserProfileModel(): UserProfile? {
    if (!exists()) return null
    val savedRaw = get("savedListingIds") as? List<*>
    val savedList = savedRaw?.mapNotNull { it as? String } ?: emptyList()
    return UserProfile(
      id = id,
      userId = getString("userId") ?: id,
      fullName = getString("fullName") ?: "",
      companyName = getString("companyName") ?: "",
      email = getString("email") ?: "",
      phone = getString("phone") ?: "",
      city = getString("city") ?: "",
      accountType = getString("accountType") ?: "Buyer / Customer",
      isVerified = getBoolean("isVerified") ?: false,
      savedListingIds = savedList,
      createdAt = getTimestamp("createdAt"),
      updatedAt = getTimestamp("updatedAt")
    )
  }

  private fun DocumentSnapshot.toListingModel(): MarketplaceListing? {
    if (!exists()) return null
    return MarketplaceListing(
      id = id,
      sellerId = getString("sellerId") ?: "",
      sellerName = getString("sellerName") ?: "",
      sellerRole = getString("sellerRole") ?: "",
      sellerVerified = getBoolean("sellerVerified") ?: false,
      listingType = getString("listingType") ?: "MATERIAL",
      title = getString("title") ?: "",
      category = getString("category") ?: "",
      description = getString("description") ?: "",
      conditionSpecs = getString("conditionSpecs") ?: "",
      quantityTonnes = getDouble("quantityTonnes") ?: 0.0,
      pricePerTonne = getDouble("pricePerTonne") ?: 0.0,
      unit = getString("unit") ?: "per tonne",
      priceType = getString("priceType") ?: "Fixed Price",
      location = getString("location") ?: "",
      preferredCollectionDate = getString("preferredCollectionDate") ?: "Immediate / Flexible",
      deliveryOptions = getString("deliveryOptions") ?: "Pickup or Delivery",
      transportIncluded = getBoolean("transportIncluded") ?: false,
      contactPreference = getString("contactPreference") ?: "Platform Quote",
      status = getString("status") ?: "Active",
      visibility = getString("visibility") ?: "public",
      isSample = getBoolean("isSample") ?: false,
      imageKey = getString("imageKey") ?: "recycled_concrete",
      createdAt = getTimestamp("createdAt"),
      updatedAt = getTimestamp("updatedAt")
    )
  }

  private fun DocumentSnapshot.toCrusherBookingModel(): CrusherBooking? {
    if (!exists()) return null
    return CrusherBooking(
      id = id,
      userId = getString("userId") ?: "",
      bookingRef = getString("bookingRef") ?: "",
      customerName = getString("customerName") ?: "",
      contactNumber = getString("contactNumber") ?: "",
      siteLocation = getString("siteLocation") ?: "",
      wasteType = getString("wasteType") ?: "",
      estimatedTonnes = getDouble("estimatedTonnes") ?: 0.0,
      siteAccessInfo = getString("siteAccessInfo") ?: "",
      preferredDate = getString("preferredDate") ?: "",
      serviceType = getString("serviceType") ?: "Both",
      additionalNotes = getString("additionalNotes") ?: "",
      status = getString("status") ?: "Request Submitted",
      quotedAmount = getDouble("quotedAmount") ?: 0.0,
      providerName = getString("providerName") ?: "NEXCORE Mobile Crushing Partner Network",
      createdAt = getTimestamp("createdAt"),
      updatedAt = getTimestamp("updatedAt")
    )
  }

  private fun DocumentSnapshot.toQuotationOrderModel(): QuotationOrder? {
    if (!exists()) return null
    return QuotationOrder(
      id = id,
      orderRef = getString("orderRef") ?: "",
      buyerId = getString("buyerId") ?: "",
      buyerName = getString("buyerName") ?: "",
      sellerId = getString("sellerId") ?: "",
      sellerName = getString("sellerName") ?: "",
      listingId = getString("listingId") ?: "",
      listingTitle = getString("listingTitle") ?: "",
      category = getString("category") ?: "",
      quantityTonnes = getDouble("quantityTonnes") ?: 0.0,
      unitPrice = getDouble("unitPrice") ?: 0.0,
      materialSubtotal = getDouble("materialSubtotal") ?: 0.0,
      transportCost = getDouble("transportCost") ?: 0.0,
      processingFee = getDouble("processingFee") ?: 0.0,
      taxAmount = getDouble("taxAmount") ?: 0.0,
      totalAmount = getDouble("totalAmount") ?: 0.0,
      deliveryPreference = getString("deliveryPreference") ?: "",
      buyerMessage = getString("buyerMessage") ?: "",
      sellerNotes = getString("sellerNotes") ?: "",
      validityDays = getLong("validityDays") ?: 14L,
      orderStatus = getString("orderStatus") ?: "Quote Requested",
      paymentStatus = getString("paymentStatus") ?: "Payment Pending",
      createdAt = getTimestamp("createdAt"),
      updatedAt = getTimestamp("updatedAt")
    )
  }

  private fun DocumentSnapshot.toTrustReportModel(): TrustReport? {
    if (!exists()) return null
    return TrustReport(
      id = id,
      reporterId = getString("reporterId") ?: "",
      targetType = getString("targetType") ?: "Listing",
      targetId = getString("targetId") ?: "",
      targetTitle = getString("targetTitle") ?: "",
      reason = getString("reason") ?: "",
      details = getString("details") ?: "",
      status = getString("status") ?: "Submitted",
      createdAt = getTimestamp("createdAt"),
      updatedAt = getTimestamp("updatedAt")
    )
  }

  // 1. User Profile Operations
  fun observeUserProfile(userId: String): Flow<UserProfile?> = callbackFlow {
    val registration = db.collection("users").document(userId)
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          val wrapped = runCatching {
            handleFirestoreError(error, OperationType.GET, "users/$userId", auth)
          }.exceptionOrNull() ?: error
          close(wrapped)
          return@addSnapshotListener
        }
        trySend(snapshot?.toUserProfileModel())
      }
    awaitClose { registration.remove() }
  }

  suspend fun getUserProfile(userId: String): Result<UserProfile?> = runCatching {
    try {
      val doc = db.collection("users").document(userId).get().await()
      doc.toUserProfileModel()
    } catch (e: FirebaseFirestoreException) {
      if (e.code == FirebaseFirestoreException.Code.UNAVAILABLE) {
        null
      } else {
        handleFirestoreError(e, OperationType.GET, "users/$userId", auth)
      }
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.GET, "users/$userId", auth)
    }
  }

  suspend fun saveUserProfile(
    fullName: String,
    companyName: String,
    email: String,
    phone: String,
    city: String,
    accountType: String,
    existingProfile: UserProfile?,
  ): Result<UserProfile> = runCatching {
    val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not authenticated")
    val safeName = fullName.trim().ifBlank { "NEXCORE Member" }.take(100)
    val safeCity = city.trim().ifBlank { "Metro Hub" }.take(100)
    val safeEmail = email.trim().ifBlank { "member@nexcore.app" }.take(150)
    val safeRole = if (accountType in SampleMarketplaceData.accountRoles) {
      accountType
    } else {
      "Buyer / Customer"
    }

    val docRef = db.collection("users").document(uid)
    try {
      if (existingProfile == null) {
        val createPayload = mapOf(
          "userId" to uid,
          "fullName" to safeName,
          "companyName" to companyName.trim().take(120),
          "email" to safeEmail,
          "phone" to phone.trim().take(40),
          "city" to safeCity,
          "accountType" to safeRole,
          "isVerified" to false,
          "savedListingIds" to emptyList<String>(),
          "createdAt" to FieldValue.serverTimestamp(),
          "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(createPayload).await()
      } else {
        val updatePayload = mapOf(
          "fullName" to safeName,
          "companyName" to companyName.trim().take(120),
          "phone" to phone.trim().take(40),
          "city" to safeCity,
          "accountType" to safeRole,
          "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(updatePayload, SetOptions.merge()).await()
      }
      docRef.get().await().toUserProfileModel()
        ?: UserProfile(
          id = uid,
          userId = uid,
          fullName = safeName,
          companyName = companyName.trim(),
          email = safeEmail,
          phone = phone.trim(),
          city = safeCity,
          accountType = safeRole
        )
    } catch (e: Exception) {
      val op = if (existingProfile == null) OperationType.CREATE else OperationType.UPDATE
      handleFirestoreError(e, op, "users/$uid", auth)
    }
  }

  suspend fun toggleSavedListing(
    listingId: String,
    currentSavedIds: List<String>,
  ): Result<List<String>> = runCatching {
    val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not authenticated")
    val updated = if (currentSavedIds.contains(listingId)) {
      currentSavedIds - listingId
    } else {
      (currentSavedIds + listingId).distinct().take(50)
    }
    try {
      db.collection("users").document(uid).set(
        mapOf(
          "savedListingIds" to updated,
          "updatedAt" to FieldValue.serverTimestamp()
        ),
        SetOptions.merge()
      ).await()
      updated
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, "users/$uid", auth)
    }
  }

  // 2. Marketplace Listings Operations
  fun observePublicListings(): Flow<List<MarketplaceListing>> = callbackFlow {
    val registration = db.collection("listings")
      .whereEqualTo("visibility", "public")
      .limit(50)
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          val wrapped = runCatching {
            handleFirestoreError(error, OperationType.LIST, "listings", auth)
          }.exceptionOrNull() ?: error
          close(wrapped)
          return@addSnapshotListener
        }
        val items = snapshot?.documents?.mapNotNull { it.toListingModel() } ?: emptyList()
        trySend(items)
      }
    awaitClose { registration.remove() }
  }

  fun observeSellerListings(sellerId: String): Flow<List<MarketplaceListing>> = callbackFlow {
    val registration = db.collection("listings")
      .whereEqualTo("sellerId", sellerId)
      .limit(50)
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          val wrapped = runCatching {
            handleFirestoreError(error, OperationType.LIST, "listings", auth)
          }.exceptionOrNull() ?: error
          close(wrapped)
          return@addSnapshotListener
        }
        val items = snapshot?.documents?.mapNotNull { it.toListingModel() } ?: emptyList()
        trySend(items)
      }
    awaitClose { registration.remove() }
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
    priceType: String,
    location: String,
    preferredCollectionDate: String,
    deliveryOptions: String,
    transportIncluded: Boolean,
    contactPreference: String,
    imageKey: String,
  ): Result<MarketplaceListing> = runCatching {
    val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not authenticated")
    val listingId = "lst_${UUID.randomUUID().toString().replace("-", "").take(16)}"
    val docRef = db.collection("listings").document(listingId)

    val payload = mapOf(
      "sellerId" to uid,
      "sellerName" to sellerName.trim().ifBlank { "NEXCORE Seller" }.take(120),
      "sellerRole" to sellerRole.trim().take(80),
      "sellerVerified" to false,
      "listingType" to if (listingType == "WASTE") "WASTE" else "MATERIAL",
      "title" to title.trim().take(140),
      "category" to category.trim().take(80),
      "description" to description.trim().take(1500),
      "conditionSpecs" to conditionSpecs.trim().take(600),
      "quantityTonnes" to quantityTonnes.coerceIn(0.1, 1000000.0),
      "pricePerTonne" to pricePerTonne.coerceIn(0.0, 1000000.0),
      "unit" to "per tonne",
      "priceType" to if (priceType == "Negotiable") "Negotiable" else "Fixed Price",
      "location" to location.trim().take(150),
      "preferredCollectionDate" to preferredCollectionDate.trim().ifBlank { "Flexible" }.take(60),
      "deliveryOptions" to if (deliveryOptions in listOf("Pickup Only", "Delivery Available", "Pickup or Delivery")) {
        deliveryOptions
      } else {
        "Pickup or Delivery"
      },
      "transportIncluded" to transportIncluded,
      "contactPreference" to contactPreference.trim().ifBlank { "Platform Quote" }.take(80),
      "status" to "Active",
      "visibility" to "public",
      "isSample" to false,
      "imageKey" to imageKey.trim().ifBlank { "recycled_concrete" }.take(60),
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )

    try {
      docRef.set(payload).await()
      docRef.get().await().toListingModel() ?: throw IllegalStateException("Listing not found after creation")
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, "listings/$listingId", auth)
    }
  }

  suspend fun updateListingStatus(listingId: String, newStatus: String): Result<Unit> = runCatching {
    val allowed = listOf("Active", "Paused", "Pending Review", "Removed")
    val safeStatus = if (newStatus in allowed) newStatus else "Active"
    try {
      db.collection("listings").document(listingId).set(
        mapOf(
          "status" to safeStatus,
          "updatedAt" to FieldValue.serverTimestamp()
        ),
        SetOptions.merge()
      ).await()
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, "listings/$listingId", auth)
    }
  }

  suspend fun deleteListing(listingId: String): Result<Unit> = runCatching {
    try {
      db.collection("listings").document(listingId).delete().await()
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, "listings/$listingId", auth)
    }
  }

  // 3. Mobile Crusher Bookings Operations
  fun observeUserCrusherBookings(userId: String): Flow<List<CrusherBooking>> = callbackFlow {
    val registration = db.collection("crusher_bookings")
      .whereEqualTo("userId", userId)
      .limit(50)
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          val wrapped = runCatching {
            handleFirestoreError(error, OperationType.LIST, "crusher_bookings", auth)
          }.exceptionOrNull() ?: error
          close(wrapped)
          return@addSnapshotListener
        }
        val items = snapshot?.documents?.mapNotNull { it.toCrusherBookingModel() } ?: emptyList()
        trySend(items)
      }
    awaitClose { registration.remove() }
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
    val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not authenticated")
    val shortCode = UUID.randomUUID().toString().uppercase().replace("-", "").take(6)
    val bookingId = "crb_${shortCode.lowercase()}"
    val bookingRef = "CRB-$shortCode"
    val docRef = db.collection("crusher_bookings").document(bookingId)

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
      "providerName" to "NEXCORE Mobile Crushing Partner Network",
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )

    try {
      docRef.set(payload).await()
      docRef.get().await().toCrusherBookingModel()
        ?: throw IllegalStateException("Booking not found after creation")
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, "crusher_bookings/$bookingId", auth)
    }
  }

  suspend fun updateCrusherBookingStatus(
    bookingId: String,
    newStatus: String,
    quotedAmount: Double? = null,
    providerName: String? = null,
  ): Result<Unit> = runCatching {
    val updates = mutableMapOf<String, Any>(
      "status" to newStatus,
      "updatedAt" to FieldValue.serverTimestamp()
    )
    if (quotedAmount != null) {
      updates["quotedAmount"] = quotedAmount.coerceAtLeast(0.0)
    }
    if (!providerName.isNullOrBlank()) {
      updates["providerName"] = providerName.trim().take(120)
    }
    try {
      db.collection("crusher_bookings").document(bookingId)
        .set(updates, SetOptions.merge())
        .await()
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, "crusher_bookings/$bookingId", auth)
    }
  }

  // 4. Quotations & Orders Operations
  fun observeBuyerQuotations(buyerId: String): Flow<List<QuotationOrder>> = callbackFlow {
    val registration = db.collection("quotations")
      .whereEqualTo("buyerId", buyerId)
      .limit(50)
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          val wrapped = runCatching {
            handleFirestoreError(error, OperationType.LIST, "quotations", auth)
          }.exceptionOrNull() ?: error
          close(wrapped)
          return@addSnapshotListener
        }
        val items = snapshot?.documents?.mapNotNull { it.toQuotationOrderModel() } ?: emptyList()
        trySend(items)
      }
    awaitClose { registration.remove() }
  }

  fun observeSellerQuotations(sellerId: String): Flow<List<QuotationOrder>> = callbackFlow {
    val registration = db.collection("quotations")
      .whereEqualTo("sellerId", sellerId)
      .limit(50)
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          val wrapped = runCatching {
            handleFirestoreError(error, OperationType.LIST, "quotations", auth)
          }.exceptionOrNull() ?: error
          close(wrapped)
          return@addSnapshotListener
        }
        val items = snapshot?.documents?.mapNotNull { it.toQuotationOrderModel() } ?: emptyList()
        trySend(items)
      }
    awaitClose { registration.remove() }
  }

  suspend fun createQuotationRequest(
    buyerName: String,
    listing: MarketplaceListing,
    quantityTonnes: Double,
    deliveryPreference: String,
    buyerMessage: String,
  ): Result<QuotationOrder> = runCatching {
    val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not authenticated")
    val shortCode = UUID.randomUUID().toString().uppercase().replace("-", "").take(6)
    val quotationId = "qto_${shortCode.lowercase()}"
    val orderRef = "NXQ-$shortCode"

    val qty = quantityTonnes.coerceIn(1.0, 1000000.0)
    val unitPrice = listing.pricePerTonne.coerceAtLeast(0.0)
    val materialSubtotal = qty * unitPrice
    val transportCost = if (listing.transportIncluded || deliveryPreference.contains("Pickup", ignoreCase = true)) {
      0.0
    } else {
      (qty * 3.5).coerceAtLeast(65.0)
    }
    val processingFee = 25.0
    val taxAmount = (materialSubtotal + transportCost + processingFee) * 0.08
    val totalAmount = materialSubtotal + transportCost + processingFee + taxAmount

    val docRef = db.collection("quotations").document(quotationId)
    val payload = mapOf(
      "orderRef" to orderRef,
      "buyerId" to uid,
      "buyerName" to buyerName.trim().ifBlank { "NEXCORE Buyer" }.take(100),
      "sellerId" to listing.sellerId.ifBlank { uid },
      "sellerName" to listing.sellerName.take(120),
      "listingId" to listing.id,
      "listingTitle" to listing.title.take(140),
      "category" to listing.category.take(80),
      "quantityTonnes" to qty,
      "unitPrice" to unitPrice,
      "materialSubtotal" to materialSubtotal,
      "transportCost" to transportCost,
      "processingFee" to processingFee,
      "taxAmount" to taxAmount,
      "totalAmount" to totalAmount,
      "deliveryPreference" to deliveryPreference.trim().take(80),
      "buyerMessage" to buyerMessage.trim().take(600),
      "sellerNotes" to "Awaiting formal supplier review & confirmation.",
      "validityDays" to 14L,
      "orderStatus" to "Quote Requested",
      "paymentStatus" to "Payment Pending",
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )

    try {
      docRef.set(payload).await()
      docRef.get().await().toQuotationOrderModel()
        ?: throw IllegalStateException("Quotation not found after creation")
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, "quotations/$quotationId", auth)
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
    val qty = quantityTonnes.coerceAtLeast(1.0)
    val unitPrice = offeredUnitPrice.coerceAtLeast(0.0)
    val materialSubtotal = qty * unitPrice
    val safeTransport = transportCost.coerceAtLeast(0.0)
    val safeProcessing = processingFee.coerceAtLeast(0.0)
    val taxAmount = (materialSubtotal + safeTransport + safeProcessing) * 0.08
    val totalAmount = materialSubtotal + safeTransport + safeProcessing + taxAmount

    val updates = mapOf(
      "unitPrice" to unitPrice,
      "materialSubtotal" to materialSubtotal,
      "transportCost" to safeTransport,
      "processingFee" to safeProcessing,
      "taxAmount" to taxAmount,
      "totalAmount" to totalAmount,
      "validityDays" to validityDays.coerceIn(1L, 365L),
      "sellerNotes" to sellerNotes.trim().take(600),
      "orderStatus" to "Quote Received",
      "updatedAt" to FieldValue.serverTimestamp()
    )
    try {
      db.collection("quotations").document(quotationId)
        .set(updates, SetOptions.merge())
        .await()
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, "quotations/$quotationId", auth)
    }
  }

  suspend fun updateQuotationOrderStatus(
    quotationId: String,
    newStatus: String,
    paymentStatus: String? = null,
  ): Result<Unit> = runCatching {
    val updates = mutableMapOf<String, Any>(
      "orderStatus" to newStatus,
      "updatedAt" to FieldValue.serverTimestamp()
    )
    if (!paymentStatus.isNullOrBlank()) {
      updates["paymentStatus"] = paymentStatus.trim().take(80)
    }
    try {
      db.collection("quotations").document(quotationId)
        .set(updates, SetOptions.merge())
        .await()
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, "quotations/$quotationId", auth)
    }
  }

  // 5. Trust & Safety Reports Operations
  fun observeUserReports(reporterId: String): Flow<List<TrustReport>> = callbackFlow {
    val registration = db.collection("reports")
      .whereEqualTo("reporterId", reporterId)
      .limit(50)
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          val wrapped = runCatching {
            handleFirestoreError(error, OperationType.LIST, "reports", auth)
          }.exceptionOrNull() ?: error
          close(wrapped)
          return@addSnapshotListener
        }
        val items = snapshot?.documents?.mapNotNull { it.toTrustReportModel() } ?: emptyList()
        trySend(items)
      }
    awaitClose { registration.remove() }
  }

  suspend fun submitTrustReport(
    targetType: String,
    targetId: String,
    targetTitle: String,
    reason: String,
    details: String,
  ): Result<TrustReport> = runCatching {
    val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not authenticated")
    val reportId = "rep_${UUID.randomUUID().toString().replace("-", "").take(14)}"
    val docRef = db.collection("reports").document(reportId)

    val payload = mapOf(
      "reporterId" to uid,
      "targetType" to if (targetType == "User") "User" else "Listing",
      "targetId" to targetId.trim().take(128),
      "targetTitle" to targetTitle.trim().ifBlank { "Marketplace Item" }.take(140),
      "reason" to reason.trim().ifBlank { "Policy Review" }.take(100),
      "details" to details.trim().take(600),
      "status" to "Submitted",
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )
    try {
      docRef.set(payload).await()
      docRef.get().await().toTrustReportModel()
        ?: throw IllegalStateException("Report not found after creation")
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, "reports/$reportId", auth)
    }
  }
}
