package com.example.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

sealed interface UiState<out T> {
  data object Loading : UiState<Nothing>
  data class Success<T>(val data: T) : UiState<T>
  data class Error(val message: String) : UiState<Nothing>
}

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
  val sellerRole: String = "",
  val sellerVerified: Boolean = false,
  val listingType: String = "MATERIAL", // "MATERIAL" or "WASTE"
  val title: String = "",
  val category: String = "",
  val description: String = "",
  val conditionSpecs: String = "",
  val quantityTonnes: Double = 0.0,
  val pricePerTonne: Double = 0.0,
  val unit: String = "per tonne",
  val priceType: String = "Fixed Price",
  val location: String = "",
  val preferredCollectionDate: String = "Immediate / Flexible",
  val deliveryOptions: String = "Pickup or Delivery",
  val transportIncluded: Boolean = false,
  val contactPreference: String = "Platform Quote",
  val status: String = "Active",
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
  val wasteType: String = "",
  val estimatedTonnes: Double = 0.0,
  val siteAccessInfo: String = "",
  val preferredDate: String = "",
  val serviceType: String = "Both",
  val additionalNotes: String = "",
  val status: String = "Request Submitted",
  val quotedAmount: Double = 0.0,
  val providerName: String = "NEXCORE Mobile Crushing Partner Network",
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
  val deliveryPreference: String = "",
  val buyerMessage: String = "",
  val sellerNotes: String = "",
  val validityDays: Long = 14L,
  val orderStatus: String = "Quote Requested",
  val paymentStatus: String = "Payment Pending",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

@IgnoreExtraProperties
data class TrustReport(
  @DocumentId val id: String = "",
  val reporterId: String = "",
  val targetType: String = "Listing",
  val targetId: String = "",
  val targetTitle: String = "",
  val reason: String = "",
  val details: String = "",
  val status: String = "Submitted",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

object SampleMarketplaceData {
  val accountRoles = listOf(
    "Buyer / Customer",
    "Demolition Contractor / Waste Seller",
    "Recycling Service Provider"
  )

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
    "Asphalt / Road Planings",
    "Recovered Metal and Scrap",
    "Excavated Rock & Sub-Base"
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

  val sampleListings: List<MarketplaceListing> = listOf(
    MarketplaceListing(
      id = "sample_rca_20mm",
      sellerId = "sample_supplier_1",
      sellerName = "Metro Aggregate Recovery Ltd (Sample)",
      sellerRole = "Recycling Service Provider",
      sellerVerified = true,
      listingType = "MATERIAL",
      title = "20mm Graded Recycled Concrete Aggregate (RCA)",
      category = "Recycled Aggregates",
      description = "Clean, screened 20mm recycled concrete aggregate suitable for sub-base, drainage backfill, and temporary haul roads. Processed from segregated commercial structural concrete.",
      conditionSpecs = "Sieve graded 0-20mm; <1% deleterious material; LA abrasion tested; moisture 4.2%.",
      quantityTonnes = 450.0,
      pricePerTonne = 850.0,
      unit = "per tonne",
      priceType = "Fixed Price",
      location = "North Industrial Yard, Sector 4",
      preferredCollectionDate = "Immediate / Mon-Sat",
      deliveryOptions = "Pickup or Delivery",
      transportIncluded = false,
      contactPreference = "Platform Quote",
      status = "Active",
      visibility = "public",
      isSample = true,
      imageKey = "recycled_concrete"
    ),
    MarketplaceListing(
      id = "sample_crushed_concrete_40mm",
      sellerId = "sample_supplier_2",
      sellerName = "Apex Civil Demolition (Sample)",
      sellerRole = "Demolition Contractor / Waste Seller",
      sellerVerified = false,
      listingType = "WASTE",
      title = "Segregated Structural Concrete Slab Debris (<450mm)",
      category = "Crushed Concrete",
      description = "Segregated reinforced concrete foundation debris from commercial warehouse demolition. Rebar sheared and primary broken, ready for mobile jaw crushing or off-site recycling.",
      conditionSpecs = "Uncontaminated C&D concrete; zero asbestos/gypsum; pieces <450mm.",
      quantityTonnes = 680.0,
      pricePerTonne = 450.0,
      unit = "per tonne",
      priceType = "Negotiable",
      location = "East Docklands Site, Gate B",
      preferredCollectionDate = "Within 10 Days",
      deliveryOptions = "Pickup Only",
      transportIncluded = false,
      contactPreference = "Platform Quote",
      status = "Active",
      visibility = "public",
      isSample = true,
      imageKey = "crushed_concrete"
    ),
    MarketplaceListing(
      id = "sample_crushed_brick_subbase",
      sellerId = "sample_supplier_3",
      sellerName = "GreenCycle Masonry Materials (Sample)",
      sellerRole = "Recycling Service Provider",
      sellerVerified = true,
      listingType = "MATERIAL",
      title = "Crushed Red Brick & Terracotta Sub-Base (40mm Down)",
      category = "Recycled Bricks",
      description = "Crushed clay brick and masonry aggregate ideal for lightly trafficked sub-bases, hardstands, compound areas, and landscape drainage layers.",
      conditionSpecs = "40mm down well-graded crushed masonry; magnetically separated.",
      quantityTonnes = 220.0,
      pricePerTonne = 720.0,
      unit = "per tonne",
      priceType = "Fixed Price",
      location = "West Ring Road Recycling Hub",
      preferredCollectionDate = "Immediate",
      deliveryOptions = "Pickup or Delivery",
      transportIncluded = true,
      contactPreference = "Platform Quote",
      status = "Active",
      visibility = "public",
      isSample = true,
      imageKey = "crushed_brick"
    ),
    MarketplaceListing(
      id = "sample_road_base_type1",
      sellerId = "sample_supplier_4",
      sellerName = "TerraCore Circular Infrastructure (Sample)",
      sellerRole = "Recycling Service Provider",
      sellerVerified = true,
      listingType = "MATERIAL",
      title = "Compaction-Grade Recycled Road-Base (Type 1 Unbound)",
      category = "Road-Base Materials",
      description = "Blended crushed concrete and mineral aggregate engineered for road sub-bases, parking pads, and heavy equipment access tracks.",
      conditionSpecs = "0-40mm unbound mixture; CBR >80% laboratory test report available on request.",
      quantityTonnes = 850.0,
      pricePerTonne = 950.0,
      unit = "per tonne",
      priceType = "Negotiable",
      location = "Central Logistics Corridor, Mile 12",
      preferredCollectionDate = "Scheduled Daily Dispatch",
      deliveryOptions = "Delivery Available",
      transportIncluded = false,
      contactPreference = "Platform Quote",
      status = "Active",
      visibility = "public",
      isSample = true,
      imageKey = "road_base"
    ),
    MarketplaceListing(
      id = "sample_recovered_rebar_steel",
      sellerId = "sample_supplier_5",
      sellerName = "Vanguard Demolition & Salvage (Sample)",
      sellerRole = "Demolition Contractor / Waste Seller",
      sellerVerified = true,
      listingType = "MATERIAL",
      title = "Recovered Structural Steel Beams & Sheared Rebar Scrap",
      category = "Recovered Metal and Scrap",
      description = "Magnetically separated reinforcing steel and structural I-beam offcuts recovered during bridge and commercial frame dismantling.",
      conditionSpecs = "Clean ferrous steel scrap; Concrete spalling removed; bundled for crane/grab loading.",
      quantityTonnes = 65.0,
      pricePerTonne = 18500.0,
      unit = "per tonne",
      priceType = "Negotiable",
      location = "Harbor Industrial Salvage Yard",
      preferredCollectionDate = "By Appointment",
      deliveryOptions = "Pickup or Delivery",
      transportIncluded = false,
      contactPreference = "Platform Quote",
      status = "Active",
      visibility = "public",
      isSample = true,
      imageKey = "scrap_metal"
    )
  )
}
