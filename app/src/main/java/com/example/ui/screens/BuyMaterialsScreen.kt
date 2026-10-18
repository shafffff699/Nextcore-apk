package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MarketplaceListing
import com.example.data.SampleMarketplaceData
import com.example.data.UserProfile
import com.example.ui.MarketplaceFilterState
import com.example.ui.components.ClearGlassCircleButton
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidGlassChip
import com.example.ui.components.LiquidGlassSearchBar
import com.example.ui.components.LiquidPillTextField
import com.example.ui.components.LiquidPrimaryButton
import com.example.ui.components.LiquidSecondaryButton
import com.example.ui.components.MainGlassPanel
import com.example.ui.components.MaterialVisualBanner
import com.example.ui.components.StatusPillBadge
import com.example.ui.theme.AccentBluePurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BuyMaterialsScreen(
  listings: List<MarketplaceListing>,
  filterState: MarketplaceFilterState,
  userProfile: UserProfile?,
  isSubmitting: Boolean,
  selectedListing: MarketplaceListing?,
  onSearchQueryChange: (String) -> Unit,
  onCategoryChange: (String) -> Unit,
  onListingTypeChange: (String) -> Unit,
  onDeliveryFilterChange: (String) -> Unit,
  onLocationFilterChange: (String) -> Unit,
  onMaxPriceChange: (Float) -> Unit,
  onMinQuantityChange: (Float) -> Unit,
  onSortByChange: (String) -> Unit,
  onClearFilters: () -> Unit,
  onSelectListing: (MarketplaceListing) -> Unit,
  onCloseListingDetail: () -> Unit,
  onToggleSaveListing: (String) -> Unit,
  onRequestQuotation: (MarketplaceListing, Double, String, String) -> Unit,
  onReportListing: (MarketplaceListing, String, String) -> Unit,
) {
  if (selectedListing != null) {
    BackHandler { onCloseListingDetail() }
    ListingDetailView(
      listing = selectedListing,
      isSaved = userProfile?.savedListingIds?.contains(selectedListing.id) == true,
      isSubmitting = isSubmitting,
      onBack = onCloseListingDetail,
      onToggleSave = { onToggleSaveListing(selectedListing.id) },
      onRequestQuotation = { qty, deliveryPref, message ->
        onRequestQuotation(selectedListing, qty, deliveryPref, message)
      },
      onReportListing = { reason, details ->
        onReportListing(selectedListing, reason, details)
      }
    )
    return
  }

  val categories = listOf("All") + SampleMarketplaceData.materialCategories.filter {
    it != "Mobile Crushing Services"
  }
  val sortOptions = listOf("Recently Listed", "Lowest Price", "Highest Price", "Highest Quantity")
  val typeOptions = listOf("ALL" to "All Items", "MATERIAL" to "Recycled Materials", "WASTE" to "Raw C&D Debris")

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("buy_materials_screen"),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    MainGlassPanel(
      title = "Buy Recycled Materials & Browse Debris",
      subtitle = "Filter verified C&D aggregates, crushed concrete, brick sub-base, road-base, and scrap metal"
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        LiquidGlassSearchBar(
          query = filterState.searchQuery,
          onQueryChange = onSearchQueryChange,
          placeholder = "Search by grade (20mm, Type 1), material, or supplier...",
          testTag = "buy_materials_search_input"
        )

        Text(
          text = "Listing Type",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = TextDark
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          typeOptions.forEach { (code, label) ->
            LiquidGlassChip(
              text = label,
              selected = filterState.listingTypeFilter == code,
              onClick = { onListingTypeChange(code) },
              testTag = "type_filter_$code"
            )
          }
        }

        Text(
          text = "Material Category",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = TextDark
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          categories.forEach { cat ->
            LiquidGlassChip(
              text = cat,
              selected = filterState.selectedCategory.equals(cat, ignoreCase = true),
              onClick = { onCategoryChange(cat) },
              testTag = "cat_filter_${cat.take(8).lowercase().replace(" ", "_")}"
            )
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Max Price: $${filterState.maxPricePerTonne.toInt()}/t",
              style = MaterialTheme.typography.labelMedium,
              color = TextDark
            )
            Slider(
              value = filterState.maxPricePerTonne,
              onValueChange = onMaxPriceChange,
              valueRange = 5f..500f,
              colors = SliderDefaults.colors(
                thumbColor = AccentBluePurple,
                activeTrackColor = AccentBluePurple
              )
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Min Qty: ${filterState.minQuantityTonnes.toInt()} t",
              style = MaterialTheme.typography.labelMedium,
              color = TextDark
            )
            Slider(
              value = filterState.minQuantityTonnes,
              onValueChange = onMinQuantityChange,
              valueRange = 0f..500f,
              colors = SliderDefaults.colors(
                thumbColor = AccentBluePurple,
                activeTrackColor = AccentBluePurple
              )
            )
          }
        }

        LiquidPillTextField(
          value = filterState.locationQuery,
          onValueChange = onLocationFilterChange,
          label = "Filter by Location / Yard / Sector",
          placeholder = "e.g., North Industrial, Docklands, Harbor...",
          trailingIcon = Icons.Default.LocationOn,
          testTag = "location_filter_input"
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
          ) {
            sortOptions.forEach { sort ->
              LiquidGlassChip(
                text = sort,
                selected = filterState.sortBy == sort,
                onClick = { onSortByChange(sort) }
              )
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
          LiquidGlassChip(
            text = "Reset",
            leadingIcon = Icons.Default.FilterAltOff,
            onClick = onClearFilters,
            testTag = "reset_filters_button"
          )
        }
      }
    }

    MainGlassPanel(
      title = "Available Marketplace Listings (${listings.size})",
      subtitle = "Tap any material card to inspect sieve specifications and request a formal quotation"
    ) {
      if (listings.isEmpty()) {
        LiquidGlassCard(cornerRadius = 22.dp) {
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = "No listings match your current filters.",
              style = MaterialTheme.typography.titleMedium,
              color = TextDark
            )
            Text(
              text = "Try expanding your maximum price per tonne or clearing location/category filters.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
            LiquidPrimaryButton(
              text = "Reset All Filters",
              onClick = onClearFilters,
              testTag = "empty_reset_filters_button"
            )
          }
        }
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
          listings.forEach { item ->
            val isSaved = userProfile?.savedListingIds?.contains(item.id) == true
            MarketplaceListingCard(
              listing = item,
              isSaved = isSaved,
              onCardClick = { onSelectListing(item) },
              onToggleSave = { onToggleSaveListing(item.id) }
            )
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ListingDetailView(
  listing: MarketplaceListing,
  isSaved: Boolean,
  isSubmitting: Boolean,
  onBack: () -> Unit,
  onToggleSave: () -> Unit,
  onRequestQuotation: (Double, String, String) -> Unit,
  onReportListing: (String, String) -> Unit,
) {
  var requestedTonnesText by rememberSaveable(listing.id) {
    mutableStateOf(listing.quantityTonnes.coerceAtMost(50.0).toInt().toString())
  }
  var deliveryPreference by rememberSaveable(listing.id) {
    mutableStateOf("Site Delivery Required")
  }
  var buyerMessage by rememberSaveable(listing.id) {
    mutableStateOf("Please include haulage estimate and earliest dispatch window.")
  }
  var showReportSection by rememberSaveable(listing.id) { mutableStateOf(false) }
  var reportReason by rememberSaveable(listing.id) { mutableStateOf("Inaccurate Material Specification") }
  var reportDetails by rememberSaveable(listing.id) { mutableStateOf("") }

  val qtyTonnes = requestedTonnesText.toDoubleOrNull()?.coerceAtLeast(1.0) ?: 25.0
  val estSubtotal = qtyTonnes * listing.pricePerTonne
  val estTransport = if (listing.transportIncluded || deliveryPreference == "Buyer Self-Pickup") {
    0.0
  } else {
    (qtyTonnes * 3.25).coerceAtLeast(95.0)
  }
  val estProcessing = (estSubtotal * 0.025).coerceAtLeast(25.0)
  val estTax = (estSubtotal + estTransport + estProcessing) * 0.08
  val estTotal = estSubtotal + estTransport + estProcessing + estTax

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("listing_detail_view"),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        ClearGlassCircleButton(
          icon = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back to listings",
          onClick = onBack,
          testTag = "detail_back_button"
        )
        Text(
          text = "Material Specification & Quote",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = TextDark
        )
      }

      ClearGlassCircleButton(
        icon = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
        contentDescription = if (isSaved) "Saved" else "Save Listing",
        onClick = onToggleSave,
        iridescentRing = isSaved,
        testTag = "detail_save_button"
      )
    }

    MainGlassPanel(
      title = listing.title,
      subtitle = "${listing.category} • Listed by ${listing.sellerName}"
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        MaterialVisualBanner(
          imageKey = listing.imageKey,
          category = listing.category,
          isSample = listing.isSample
        )

        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          StatusPillBadge(status = listing.status)
          StatusPillBadge(status = listing.priceType)
          StatusPillBadge(
            status = if (listing.listingType == "WASTE") "Construction Debris" else "Recycled Aggregate"
          )
        }

        LiquidGlassCard(cornerRadius = 22.dp) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Indicative Unit Price",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
              )
              Text(
                text = "$${String.format("%.2f", listing.pricePerTonne)} ${listing.unit}",
                style = MaterialTheme.typography.headlineMedium.copy(
                  color = AccentBluePurple,
                  fontWeight = FontWeight.ExtraBold
                )
              )
            }
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "Available Stock",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
              )
              Text(
                text = "${listing.quantityTonnes.toInt()} Tonnes",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextDark
              )
            }
          }
        }

        Text(
          text = "Material Overview",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = TextDark
        )
        Text(
          text = listing.description,
          style = MaterialTheme.typography.bodyMedium,
          color = TextSecondary
        )

        Text(
          text = "Grading & Contamination Specifications",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = TextDark
        )
        LiquidGlassCard(cornerRadius = 20.dp) {
          Text(
            text = listing.conditionSpecs.ifBlank { "Standard uncontaminated C&D specification." },
            style = MaterialTheme.typography.bodySmall,
            color = TextDark
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          LiquidGlassCard(modifier = Modifier.weight(1f), cornerRadius = 20.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.LocationOn,
                  contentDescription = null,
                  tint = AccentBluePurple,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Site / Yard Location",
                  style = MaterialTheme.typography.labelMedium,
                  color = TextSecondary
                )
              }
              Text(
                text = listing.location,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = TextDark
              )
            }
          }

          LiquidGlassCard(modifier = Modifier.weight(1f), cornerRadius = 20.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.LocalShipping,
                  contentDescription = null,
                  tint = AccentBluePurple,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Logistics & Dispatch",
                  style = MaterialTheme.typography.labelMedium,
                  color = TextSecondary
                )
              }
              Text(
                text = "${listing.deliveryOptions} (${listing.preferredCollectionDate})",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = TextDark
              )
            }
          }
        }

        LiquidGlassCard(cornerRadius = 20.dp) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = listing.sellerName,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = TextDark
                )
                if (listing.sellerVerified) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Verified Supplier",
                    tint = SuccessGreen,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
              Text(
                text = "${listing.sellerRole} • Contact via ${listing.contactPreference}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
            }
            LiquidGlassChip(
              text = "Report",
              leadingIcon = Icons.Default.Flag,
              onClick = { showReportSection = !showReportSection },
              testTag = "toggle_report_listing_button"
            )
          }
        }
      }
    }

    // Request Formal Quotation Panel
    MainGlassPanel(
      title = "Request Official Quotation (RFQ)",
      subtitle = "Instant itemized estimate with direct seller quote confirmation"
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        LiquidPillTextField(
          value = requestedTonnesText,
          onValueChange = { requestedTonnesText = it },
          label = "Required Quantity (Tonnes)",
          placeholder = "e.g., 50",
          trailingIcon = Icons.Default.Scale,
          testTag = "rfq_quantity_input"
        )

        Text(
          text = "Delivery / Collection Preference",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
          color = TextDark
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("Site Delivery Required", "Buyer Self-Pickup", "NEXCORE Logistics Hauler").forEach { pref ->
            LiquidGlassChip(
              text = pref,
              selected = deliveryPreference == pref,
              onClick = { deliveryPreference = pref }
            )
          }
        }

        LiquidPillTextField(
          value = buyerMessage,
          onValueChange = { buyerMessage = it },
          label = "Delivery Address, Unloading Access & Notes",
          placeholder = "Specify gate access, tipper truck constraints, or required test certificates...",
          singleLine = false,
          testTag = "rfq_message_input"
        )

        // Itemized Quotation Breakdown Preview
        LiquidGlassCard(cornerRadius = 22.dp, iridescentBorder = true) {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = "Estimated Quotation Summary (${qtyTonnes.toInt()} t)",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = TextDark
            )
            QuoteLineItem("Material Subtotal", estSubtotal)
            QuoteLineItem("Estimated Haulage / Transport", estTransport)
            QuoteLineItem("Platform & Quality Processing Fee", estProcessing)
            QuoteLineItem("Estimated Tax (8%)", estTax)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Total Estimated Order Value",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = TextDark
              )
              Text(
                text = "$${String.format("%.2f", estTotal)}",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 18.sp
                ),
                color = AccentBluePurple
              )
            }
          }
        }

        LiquidPrimaryButton(
          text = if (isSubmitting) "Submitting RFQ..." else "Submit Quotation Request",
          icon = Icons.Default.RequestQuote,
          enabled = !isSubmitting,
          onClick = {
            onRequestQuotation(qtyTonnes, deliveryPreference, buyerMessage)
          },
          modifier = Modifier.fillMaxWidth(),
          testTag = "submit_rfq_button"
        )
      }
    }

    if (showReportSection) {
      MainGlassPanel(
        title = "Report Listing to NEXCORE Trust & Safety",
        subtitle = "Flag suspected contamination, hazardous waste (asbestos), or inaccurate grading"
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(
              "Inaccurate Material Specification",
              "Suspected Contamination / Hazardous Debris",
              "Misleading Price or Tonnage",
              "Unresponsive Supplier"
            ).forEach { r ->
              LiquidGlassChip(
                text = r,
                selected = reportReason == r,
                onClick = { reportReason = r }
              )
            }
          }
          LiquidPillTextField(
            value = reportDetails,
            onValueChange = { reportDetails = it },
            label = "Additional Details for Compliance Review",
            placeholder = "Describe the issue...",
            singleLine = false,
            testTag = "report_details_input"
          )
          LiquidSecondaryButton(
            text = "Submit Compliance Report",
            icon = Icons.Default.CheckCircle,
            onClick = {
              onReportListing(reportReason, reportDetails)
              showReportSection = false
            },
            modifier = Modifier.fillMaxWidth(),
            testTag = "submit_report_button"
          )
        }
      }
    }
  }
}

@Composable
private fun QuoteLineItem(label: String, amount: Double) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = TextSecondary
    )
    Text(
      text = "$${String.format("%.2f", amount)}",
      style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
      color = TextDark
    )
  }
}
