package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CrusherBooking
import com.example.data.MarketplaceListing
import com.example.data.NexcoreRepository
import com.example.data.QuotationOrder
import com.example.data.SampleMarketplaceData
import com.example.data.TrustReport
import com.example.data.UiState
import com.example.data.UserProfile
import com.example.ui.auth.RegistrationDraft
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NexcoreDestination(val title: String) {
  HOME("Home"),
  BUY_MATERIALS("Buy Materials"),
  SELL_WASTE("Sell Waste"),
  BOOK_CRUSHER("Book Mobile Crusher"),
  TRACK_ORDERS("Track Orders"),
  DASHBOARD("Dashboard"),
}

data class MarketplaceFilterState(
  val searchQuery: String = "",
  val selectedCategory: String = "All",
  val listingTypeFilter: String = "ALL", // "ALL", "MATERIAL", "WASTE"
  val deliveryFilter: String = "All",
  val priceTypeFilter: String = "All",
  val maxPricePerTonne: Float = 500f,
  val minQuantityTonnes: Float = 0f,
  val locationQuery: String = "",
  val sortBy: String = "Recently Listed", // "Recently Listed", "Lowest Price", "Highest Price", "Highest Quantity"
)

class NexcoreViewModel(
  private val repository: NexcoreRepository,
  val currentUserId: String,
) : ViewModel() {

  private val _currentDestination = MutableStateFlow(NexcoreDestination.HOME)
  val currentDestination: StateFlow<NexcoreDestination> = _currentDestination.asStateFlow()

  private val _selectedListingForDetail = MutableStateFlow<MarketplaceListing?>(null)
  val selectedListingForDetail: StateFlow<MarketplaceListing?> = _selectedListingForDetail.asStateFlow()

  private val _filterState = MutableStateFlow(MarketplaceFilterState())
  val filterState: StateFlow<MarketplaceFilterState> = _filterState.asStateFlow()

  private val _bannerMessage = MutableStateFlow<String?>(null)
  val bannerMessage: StateFlow<String?> = _bannerMessage.asStateFlow()

  private val _isSubmitting = MutableStateFlow(false)
  val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

  // Two-Tier Kotlin Flow Error Handling Architecture
  val userProfileState: StateFlow<UiState<UserProfile?>> =
    repository.observeUserProfile(currentUserId)
      .map<UserProfile?, UiState<UserProfile?>> { UiState.Success(it) }
      .catch { e ->
        Log.w(TAG, "Error observing user profile", e)
        emit(UiState.Error(e.message ?: "Failed to load user profile"))
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UiState.Loading
      )

  val firestoreListingsState: StateFlow<UiState<List<MarketplaceListing>>> =
    repository.observePublicListings()
      .map<List<MarketplaceListing>, UiState<List<MarketplaceListing>>> { list ->
        UiState.Success(list)
      }
      .catch { e ->
        Log.w(TAG, "Error observing public listings", e)
        emit(UiState.Error(e.message ?: "Failed to load marketplace listings"))
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UiState.Loading
      )

  // Combined live Firestore listings + clearly labeled demonstration sample listings
  val combinedListings: StateFlow<List<MarketplaceListing>> =
    combine(firestoreListingsState, _filterState) { state, filters ->
      val liveItems = (state as? UiState.Success)?.data ?: emptyList()
      val allActive = (liveItems.filter { it.status == "Active" } + SampleMarketplaceData.sampleListings)
        .distinctBy { it.id }

      allActive.filter { item ->
        val matchesType =
          filters.listingTypeFilter == "ALL" || item.listingType == filters.listingTypeFilter
        val matchesCategory =
          filters.selectedCategory == "All" ||
            item.category.equals(filters.selectedCategory, ignoreCase = true)
        val matchesDelivery =
          filters.deliveryFilter == "All" ||
            item.deliveryOptions.equals(filters.deliveryFilter, ignoreCase = true)
        val matchesPriceType =
          filters.priceTypeFilter == "All" ||
            item.priceType.equals(filters.priceTypeFilter, ignoreCase = true)
        val matchesPrice = item.pricePerTonne <= filters.maxPricePerTonne.toDouble()
        val matchesQty = item.quantityTonnes >= filters.minQuantityTonnes.toDouble()
        val matchesLocation =
          filters.locationQuery.isBlank() ||
            item.location.contains(filters.locationQuery.trim(), ignoreCase = true)
        val matchesQuery =
          filters.searchQuery.isBlank() ||
            item.title.contains(filters.searchQuery.trim(), ignoreCase = true) ||
            item.category.contains(filters.searchQuery.trim(), ignoreCase = true) ||
            item.description.contains(filters.searchQuery.trim(), ignoreCase = true) ||
            item.location.contains(filters.searchQuery.trim(), ignoreCase = true) ||
            item.sellerName.contains(filters.searchQuery.trim(), ignoreCase = true)

        matchesType && matchesCategory && matchesDelivery && matchesPriceType &&
          matchesPrice && matchesQty && matchesLocation && matchesQuery
      }.let { filtered ->
        when (filters.sortBy) {
          "Lowest Price" -> filtered.sortedBy { it.pricePerTonne }
          "Highest Price" -> filtered.sortedByDescending { it.pricePerTonne }
          "Highest Quantity" -> filtered.sortedByDescending { it.quantityTonnes }
          else -> filtered.sortedByDescending { it.createdAt?.seconds ?: 0L }
        }
      }
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000L),
      initialValue = SampleMarketplaceData.sampleListings
    )

  val sellerListingsState: StateFlow<UiState<List<MarketplaceListing>>> =
    repository.observeSellerListings(currentUserId)
      .map<List<MarketplaceListing>, UiState<List<MarketplaceListing>>> { UiState.Success(it) }
      .catch { e ->
        Log.w(TAG, "Error observing seller listings", e)
        emit(UiState.Error(e.message ?: "Failed to load your listings"))
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UiState.Loading
      )

  val crusherBookingsState: StateFlow<UiState<List<CrusherBooking>>> =
    repository.observeUserCrusherBookings(currentUserId)
      .map<List<CrusherBooking>, UiState<List<CrusherBooking>>> { UiState.Success(it) }
      .catch { e ->
        Log.w(TAG, "Error observing crusher bookings", e)
        emit(UiState.Error(e.message ?: "Failed to load mobile crusher bookings"))
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UiState.Loading
      )

  val buyerQuotationsState: StateFlow<UiState<List<QuotationOrder>>> =
    repository.observeBuyerQuotations(currentUserId)
      .map<List<QuotationOrder>, UiState<List<QuotationOrder>>> { UiState.Success(it) }
      .catch { e ->
        Log.w(TAG, "Error observing buyer quotations", e)
        emit(UiState.Error(e.message ?: "Failed to load requested quotations"))
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UiState.Loading
      )

  val sellerQuotationsState: StateFlow<UiState<List<QuotationOrder>>> =
    repository.observeSellerQuotations(currentUserId)
      .map<List<QuotationOrder>, UiState<List<QuotationOrder>>> { UiState.Success(it) }
      .catch { e ->
        Log.w(TAG, "Error observing seller quotations", e)
        emit(UiState.Error(e.message ?: "Failed to load incoming quotation enquiries"))
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UiState.Loading
      )

  val userReportsState: StateFlow<UiState<List<TrustReport>>> =
    repository.observeUserReports(currentUserId)
      .map<List<TrustReport>, UiState<List<TrustReport>>> { UiState.Success(it) }
      .catch { e ->
        Log.w(TAG, "Error observing reports", e)
        emit(UiState.Error(e.message ?: "Failed to load reports"))
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UiState.Loading
      )

  fun navigateTo(
    destination: NexcoreDestination,
    categoryFilter: String? = null,
    listingType: String? = null,
  ) {
    if (categoryFilter != null || listingType != null) {
      _filterState.value = _filterState.value.copy(
        selectedCategory = categoryFilter ?: _filterState.value.selectedCategory,
        listingTypeFilter = listingType ?: _filterState.value.listingTypeFilter
      )
    }
    _selectedListingForDetail.value = null
    _currentDestination.value = destination
  }

  fun openListingDetail(listing: MarketplaceListing) {
    _selectedListingForDetail.value = listing
  }

  fun closeListingDetail() {
    _selectedListingForDetail.value = null
  }

  fun clearBannerMessage() {
    _bannerMessage.value = null
  }

  fun updateSearchQuery(query: String) {
    _filterState.value = _filterState.value.copy(searchQuery = query)
  }

  fun updateCategoryFilter(category: String) {
    _filterState.value = _filterState.value.copy(selectedCategory = category)
  }

  fun updateListingTypeFilter(type: String) {
    _filterState.value = _filterState.value.copy(listingTypeFilter = type)
  }

  fun updateDeliveryFilter(delivery: String) {
    _filterState.value = _filterState.value.copy(deliveryFilter = delivery)
  }

  fun updateLocationFilter(location: String) {
    _filterState.value = _filterState.value.copy(locationQuery = location)
  }

  fun updateMaxPriceFilter(maxPrice: Float) {
    _filterState.value = _filterState.value.copy(maxPricePerTonne = maxPrice)
  }

  fun updateMinQuantityFilter(minQty: Float) {
    _filterState.value = _filterState.value.copy(minQuantityTonnes = minQty)
  }

  fun updateSortBy(sortBy: String) {
    _filterState.value = _filterState.value.copy(sortBy = sortBy)
  }

  fun clearAllFilters() {
    _filterState.value = MarketplaceFilterState()
  }

  fun ensureUserProfileInitialized(draft: RegistrationDraft?) {
    viewModelScope.launch {
      val existing = repository.getUserProfile(currentUserId).getOrNull()
      if (existing == null) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val fullName = draft?.fullName?.takeIf { it.isNotBlank() }
          ?: firebaseUser?.displayName?.takeIf { it.isNotBlank() }
          ?: "NEXCORE User"
        val email = firebaseUser?.email?.takeIf { it.isNotBlank() } ?: "member@nexcore.app"
        val city = draft?.city?.takeIf { it.isNotBlank() } ?: "Metro Industrial Hub"
        val company = draft?.companyName ?: ""
        val phone = draft?.phone ?: ""
        val role = draft?.accountType ?: "Buyer / Customer"

        repository.saveUserProfile(
          fullName = fullName,
          companyName = company,
          email = email,
          phone = phone,
          city = city,
          accountType = role,
          existingProfile = null
        )
      }
    }
  }

  fun saveProfileUpdates(
    fullName: String,
    companyName: String,
    phone: String,
    city: String,
    accountType: String,
  ) {
    viewModelScope.launch {
      _isSubmitting.value = true
      val currentProfile = (userProfileState.value as? UiState.Success)?.data
      val email = currentProfile?.email
        ?: FirebaseAuth.getInstance().currentUser?.email
        ?: "member@nexcore.app"
      val res = repository.saveUserProfile(
        fullName = fullName,
        companyName = companyName,
        email = email,
        phone = phone,
        city = city,
        accountType = accountType,
        existingProfile = currentProfile
      )
      _isSubmitting.value = false
      if (res.isSuccess) {
        _bannerMessage.value = "Account profile & role updated."
      } else {
        _bannerMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Failed to update profile."
      }
    }
  }

  fun toggleSavedListing(listingId: String) {
    viewModelScope.launch {
      val currentProfile = (userProfileState.value as? UiState.Success)?.data ?: return@launch
      val res = repository.toggleSavedListing(listingId, currentProfile.savedListingIds)
      if (res.isSuccess) {
        val isNowSaved = res.getOrNull()?.contains(listingId) == true
        _bannerMessage.value = if (isNowSaved) "Saved to your shortlist." else "Removed from saved materials."
      }
    }
  }

  fun publishNewListing(
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
    onSuccess: () -> Unit,
  ) {
    viewModelScope.launch {
      _isSubmitting.value = true
      val profile = (userProfileState.value as? UiState.Success)?.data
      val sellerName = profile?.companyName?.takeIf { it.isNotBlank() }
        ?: profile?.fullName?.takeIf { it.isNotBlank() }
        ?: FirebaseAuth.getInstance().currentUser?.displayName
        ?: "NEXCORE Contractor"
      val sellerRole = profile?.accountType ?: "Demolition Contractor / Waste Seller"

      val res = repository.createListing(
        sellerName = sellerName,
        sellerRole = sellerRole,
        listingType = listingType,
        title = title,
        category = category,
        description = description,
        conditionSpecs = conditionSpecs,
        quantityTonnes = quantityTonnes,
        pricePerTonne = pricePerTonne,
        priceType = priceType,
        location = location,
        preferredCollectionDate = preferredCollectionDate,
        deliveryOptions = deliveryOptions,
        transportIncluded = transportIncluded,
        contactPreference = contactPreference,
        imageKey = imageKey
      )
      _isSubmitting.value = false
      if (res.isSuccess) {
        _bannerMessage.value = "Listing published to NEXCORE Marketplace!"
        onSuccess()
      } else {
        _bannerMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Failed to publish listing."
      }
    }
  }

  fun updateListingStatus(listingId: String, newStatus: String) {
    viewModelScope.launch {
      val res = repository.updateListingStatus(listingId, newStatus)
      if (res.isSuccess) {
        _bannerMessage.value = "Listing status updated to $newStatus."
      } else {
        _bannerMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Could not update listing status."
      }
    }
  }

  fun deleteListing(listingId: String) {
    viewModelScope.launch {
      val res = repository.deleteListing(listingId)
      if (res.isSuccess) {
        _bannerMessage.value = "Listing removed."
      } else {
        _bannerMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Could not delete listing."
      }
    }
  }

  fun submitCrusherBooking(
    customerName: String,
    contactNumber: String,
    siteLocation: String,
    wasteType: String,
    estimatedTonnes: Double,
    siteAccessInfo: String,
    preferredDate: String,
    serviceType: String,
    additionalNotes: String,
    onSuccess: (CrusherBooking) -> Unit,
  ) {
    viewModelScope.launch {
      _isSubmitting.value = true
      val res = repository.createCrusherBooking(
        customerName = customerName,
        contactNumber = contactNumber,
        siteLocation = siteLocation,
        wasteType = wasteType,
        estimatedTonnes = estimatedTonnes,
        siteAccessInfo = siteAccessInfo,
        preferredDate = preferredDate,
        serviceType = serviceType,
        additionalNotes = additionalNotes
      )
      _isSubmitting.value = false
      if (res.isSuccess) {
        val booking = res.getOrThrow()
        _bannerMessage.value = "Booking ${booking.bookingRef} submitted (Status: Request Submitted)."
        onSuccess(booking)
      } else {
        _bannerMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Failed to submit crusher booking."
      }
    }
  }

  fun updateCrusherBookingStatus(
    bookingId: String,
    newStatus: String,
    quotedAmount: Double? = null,
    providerName: String? = null,
  ) {
    viewModelScope.launch {
      val res = repository.updateCrusherBookingStatus(
        bookingId = bookingId,
        newStatus = newStatus,
        quotedAmount = quotedAmount,
        providerName = providerName
      )
      if (res.isSuccess) {
        _bannerMessage.value = "Booking status updated to $newStatus."
      } else {
        _bannerMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Failed to update booking."
      }
    }
  }

  fun requestQuotation(
    listing: MarketplaceListing,
    quantityTonnes: Double,
    deliveryPreference: String,
    buyerMessage: String,
    onSuccess: () -> Unit,
  ) {
    viewModelScope.launch {
      _isSubmitting.value = true
      val profile = (userProfileState.value as? UiState.Success)?.data
      val buyerName = profile?.companyName?.takeIf { it.isNotBlank() }
        ?: profile?.fullName?.takeIf { it.isNotBlank() }
        ?: FirebaseAuth.getInstance().currentUser?.displayName
        ?: "NEXCORE Buyer"

      val res = repository.createQuotationRequest(
        buyerName = buyerName,
        listing = listing,
        quantityTonnes = quantityTonnes,
        deliveryPreference = deliveryPreference,
        buyerMessage = buyerMessage
      )
      _isSubmitting.value = false
      if (res.isSuccess) {
        _bannerMessage.value = "Quotation request submitted! Track status in Track Orders."
        onSuccess()
      } else {
        _bannerMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Failed to request quotation."
      }
    }
  }

  fun submitSellerQuoteResponse(
    quotationId: String,
    quantityTonnes: Double,
    offeredUnitPrice: Double,
    transportCost: Double,
    processingFee: Double,
    validityDays: Long,
    sellerNotes: String,
  ) {
    viewModelScope.launch {
      val res = repository.submitSellerQuotationResponse(
        quotationId = quotationId,
        quantityTonnes = quantityTonnes,
        offeredUnitPrice = offeredUnitPrice,
        transportCost = transportCost,
        processingFee = processingFee,
        validityDays = validityDays,
        sellerNotes = sellerNotes
      )
      if (res.isSuccess) {
        _bannerMessage.value = "Formal quotation sent to buyer!"
      } else {
        _bannerMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Failed to submit quotation."
      }
    }
  }

  fun updateQuotationStatus(
    quotationId: String,
    newStatus: String,
    paymentStatus: String? = null,
  ) {
    viewModelScope.launch {
      val res = repository.updateQuotationOrderStatus(quotationId, newStatus, paymentStatus)
      if (res.isSuccess) {
        _bannerMessage.value = "Order status updated to $newStatus."
      } else {
        _bannerMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Failed to update order status."
      }
    }
  }

  fun submitReport(
    targetType: String,
    targetId: String,
    targetTitle: String,
    reason: String,
    details: String,
  ) {
    viewModelScope.launch {
      val res = repository.submitTrustReport(
        targetType = targetType,
        targetId = targetId,
        targetTitle = targetTitle,
        reason = reason,
        details = details
      )
      if (res.isSuccess) {
        _bannerMessage.value = "Report submitted for Trust & Safety review."
      } else {
        _bannerMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Could not submit report."
      }
    }
  }

  private companion object {
    const val TAG = "NexcoreVM"
  }
}
