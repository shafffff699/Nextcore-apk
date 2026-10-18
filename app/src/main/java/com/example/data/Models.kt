package com.example.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
  @DocumentId val id: String = "",
  val userId: String = "",
  val fullName: String = "",
  val companyName: String = "",
  val email: String = "",
  val phone: String = "",
  val city: String = "",
  val accountType: String = "Buyer / Customer",
  val isVerified: Boolean = false,
  val savedListingIds: List<String> = emptyList(),
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

@IgnoreExtraProperties
data class MarketplaceListing(
  @DocumentId val id: String = "",
  val sellerId: String = "",
  val sellerName: String = "",
  val sellerRole: String = "Demolition Contractor / Waste Seller",
  val sellerVerified: Boolean = false,
  val listingType: String = "MATERIAL", // "MATERIAL" or "WASTE"
  val title: String = "",
  val category: String = "Recycled Aggregates",
  val description: String = "",
  val conditionSpecs: String = "",
  val quantityTonnes: Double = 0.0,
  val pricePerTonne: Double = 0.0,
  val unit: String = "per tonne",
  val priceType: String = "Fixed Price", // "Fixed Price" or "Negotiable"
  val location: String = "",
  val preferredCollectionDate: String = "Immediate",
  val deliveryOptions: String = "Pickup or Delivery", // "Pickup Only", "Delivery Available", "Pickup or Delivery"
  val transportIncluded: Boolean = false,
  val contactPreference: String = "Platform Quote",
  val status: String = "Active", // "Active", "Paused", "Pending Review", "Removed"
  val visibility: String = "public",
  val isSample: Boolean = false,
  val imageKey: String = "recycled_concrete",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

@IgnoreExtraProperties
data class CrusherBooking(
  @DocumentId val id: String = "",
  val userId: String = "",
  val bookingRef: String = "",
  val customerName: String = "",
  val contactNumber: String = "",
  val siteLocation: String = "",
  val wasteType: String = "Concrete Debris",
  val estimatedTonnes: Double = 0.0,
  val siteAccessInfo: String = "",
  val preferredDate: String = "",
  val serviceType: String = "Both", // "Crushing", "Screening", "Both"
  val additionalNotes: String = "",
  val status: String = "Request Submitted",
  val quotedAmount: Double = 0.0,
  val providerName: String = "Pending Service Assessment",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

@IgnoreExtraProperties
data class QuotationOrder(
  @DocumentId val id: String = "",
  val orderRef: String = "",
  val buyerId: String = "",
  val buyerName: String = "",
  val sellerId: String = "",
  val sellerName: String = "",
  val listingId: String = "",
  val listingTitle: String = "",
  val category: String = "",
  val quantityTonnes: Double = 0.0,
  val unitPrice: Double = 0.0,
  val materialSubtotal: Double = 0.0,
  val transportCost: Double = 0.0,
  val processingFee: Double = 0.0,
  val taxAmount: Double = 0.0,
  val totalAmount: Double = 0.0,
  val deliveryPreference: String = "Pickup / Collection",
  val buyerMessage: String = "",
  val sellerNotes: String = "",
  val validityDays: Long = 14L,
  val orderStatus: String = "Quote Requested",
  val paymentStatus: String = "Payment Pending (Prototype)",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

@IgnoreExtraProperties
data class TrustReport(
  @DocumentId val id: String = "",
  val reporterId: String = "",
  val targetType: String = "Listing", // "Listing" or "User"
  val targetId: String = "",
  val targetTitle: String = "",
  val reason: String = "",
  val details: String = "",
  val status: String = "Submitted", // "Submitted", "Reviewed", "Resolved"
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

sealed interface UiState<out T> {
  data object Loading : UiState<Nothing>
  data class Success<T>(val data: T) : UiState<T>
  data class Error(val message: String) : UiState<Nothing>
}

object SampleMarketplaceData {
  val materialCategories = listOf(
    "Recycled Aggregates",
    "Crushed Concrete",
    "Recycled Bricks",
    "Road-Base Materials",
    "Recovered Metal and Scrap",
    "Mobile Crushing Services"
  )

  val wasteCategories = listOf(
    "Concrete Debris",
    "Brick and Masonry",
    "Mixed Mineral Debris",
    "Excavated Material",
    "Recoverable Metal",
    "Other C&D Waste"
  )

  val accountRoles = listOf(
    "Buyer / Customer",
    "Demolition Contractor / Waste Seller",
    "Recycling Service Provider"
  )

  val bookingStatuses = listOf(
    "Request Submitted",
    "Under Review",
    "Quotation Sent",
    "Accepted",
    "Scheduled",
    "In Progress",
    "Completed",
    "Cancelled"
  )

  val orderStatuses = listOf(
    "Pending",
    "Quote Requested",
    "Quote Received",
    "Accepted",
    "Processing",
    "Ready for Collection",
    "Dispatched",
    "Delivered",
    "Completed",
    "Cancelled"
  )

  // Clearly labelled fictional demonstration sample listings (Section 19)
  val sampleListings: List<MarketplaceListing> = listOf(
    MarketplaceListing(
      id = "sample_rca_20mm",
      sellerId = "sample_seller_1",
      sellerName = "Metro Recovery Demo Yard (Sample)",
      sellerRole = "Recycling Service Provider",
      sellerVerified = false,
      listingType = "MATERIAL",
      title = "Recycled Concrete Aggregate (20mm Graded)",
      category = "Recycled Aggregates",
      description = "Demonstration Sample Listing: Mechanically crushed and screened 20mm recycled concrete aggregate suitable for sub-base, temporary haul roads, and general backfill.",
      conditionSpecs = "Gradation: 0–20mm | Sieve analysis available upon request | Non-structural sub-base grade (Sample specification).",
      quantityTonnes = 420.0,
      pricePerTonne = 16.50,
      unit = "per tonne",
      priceType = "Fixed Price",
      location = "North Industrial Sector (Sample Location)",
      preferredCollectionDate = "Available Daily (Mon-Sat)",
      deliveryOptions = "Pickup or Delivery",
      transportIncluded = false,
      contactPreference = "Platform Quote",
      status = "Active",
      visibility = "public",
      isSample = true,
      imageKey = "recycled_concrete"
    ),
    MarketplaceListing(
      id = "sample_crushed_brick",
      sellerId = "sample_seller_2",
      sellerName = "Urban Deconstruction Demo Co. (Sample)",
      sellerRole = "Demolition Contractor / Waste Seller",
      sellerVerified = false,
      listingType = "MATERIAL",
      title = "Crushed Brick Material (40mm Drainage & Fill)",
      category = "Recycled Bricks",
      description = "Demonstration Sample Listing: Clean crushed red clay brick and masonry material processed from commercial redevelopment debris. Suitable for landscape drainage layers and pipe bedding.",
      conditionSpecs = "40mm down | Low fines (<5%) | Free of timber, plastics, and hazardous materials (Sample specification).",
      quantityTonnes = 185.0,
      pricePerTonne = 14.00,
      unit = "per tonne",
      priceType = "Negotiable",
      location = "East Docklands Yard (Sample Location)",
      preferredCollectionDate = "Within 7 Days",
      deliveryOptions = "Pickup Only",
      transportIncluded = false,
      contactPreference = "Platform Quote",
      status = "Active",
      visibility = "public",
      isSample = true,
      imageKey = "crushed_brick"
    ),
    MarketplaceListing(
      id = "sample_road_base",
      sellerId = "sample_seller_3",
      sellerName = "Civic Aggregates Demo Hub (Sample)",
      sellerRole = "Recycling Service Provider",
      sellerVerified = false,
      listingType = "MATERIAL",
      title = "Recycled Road-Base Aggregate (Type 1 Sub-Base)",
      category = "Road-Base Materials",
      description = "Demonstration Sample Listing: Blended crushed concrete and mineral aggregate engineered for high compaction on access roads, parking pads, and industrial hardstands.",
      conditionSpecs = "0–40mm well-graded sub-base | Compaction CBR >80% indicative demo value.",
      quantityTonnes = 650.0,
      pricePerTonne = 19.00,
      unit = "per tonne",
      priceType = "Fixed Price",
      location = "West Corridor Recycling Hub (Sample Location)",
      preferredCollectionDate = "Immediate",
      deliveryOptions = "Delivery Available",
      transportIncluded = true,
      contactPreference = "Platform Quote",
      status = "Active",
      visibility = "public",
      isSample = true,
      imageKey = "road_base"
    ),
    MarketplaceListing(
      id = "sample_steel_scrap",
      sellerId = "sample_seller_4",
      sellerName = "Apex Site Clearance Demo (Sample)",
      sellerRole = "Demolition Contractor / Waste Seller",
      sellerVerified = false,
      listingType = "MATERIAL",
      title = "Recoverable Steel Scrap & Segregated Rebar",
      category = "Recovered Metal and Scrap",
      description = "Demonstration Sample Listing: Magnetic-separated reinforcement bar and structural steel offcuts recovered during concrete slab crushing.",
      conditionSpecs = "Cut lengths 0.8m–2.5m | <3% concrete adherence | Weighbridge ticket provided on collection.",
      quantityTonnes = 48.0,
      pricePerTonne = 215.00,
      unit = "per tonne",
      priceType = "Negotiable",
      location = "South Metro Demolition Site (Sample Location)",
      preferredCollectionDate = "Flexible",
      deliveryOptions = "Pickup or Delivery",
      transportIncluded = false,
      contactPreference = "Platform Quote",
      status = "Active",
      visibility = "public",
      isSample = true,
      imageKey = "scrap_metal"
    ),
    MarketplaceListing(
      id = "sample_concrete_debris_waste",
      sellerId = "sample_seller_5",
      sellerName = "Horizon Civil Demo Contractors (Sample)",
      sellerRole = "Demolition Contractor / Waste Seller",
      sellerVerified = false,
      listingType = "WASTE",
      title = "Segregated Concrete Slab Debris (Ready for Crushing)",
      category = "Concrete Debris",
      description = "Demonstration Sample Listing: Clean reinforced concrete foundation slabs broken to <500mm pieces. Seeking recycling collector or on-site mobile crusher partner.",
      conditionSpecs = "Uncontaminated C&D concrete debris | No asbestos or gypsum.",
      quantityTonnes = 310.0,
      pricePerTonne = 6.00,
      unit = "per tonne",
      priceType = "Negotiable",
      location = "Central Business Park Site B (Sample Location)",
      preferredCollectionDate = "Next Week",
      deliveryOptions = "Pickup Only",
      transportIncluded = false,
      contactPreference = "Platform Quote",
      status = "Active",
      visibility = "public",
      isSample = true,
      imageKey = "crushed_concrete"
    )
  )
}
