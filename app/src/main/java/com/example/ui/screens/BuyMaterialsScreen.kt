package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.MarketplaceListing
import com.example.data.SampleMarketplaceData
import com.example.ui.MarketplaceFilterState
import com.example.ui.components.ClearGlassCircleButton
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidGlassChip
import com.example.ui.components.LiquidGlassSearchBar
import com.example.ui.components.LiquidPillTextField
import com.example.ui.components.LiquidPrimaryButton
import com.example.ui.components.LiquidSecondaryButton
import com.example.ui.components.LiquidSegmentedTabs
import com.example.ui.components.MainGlassPanel
import com.example.ui.components.MaterialVisualBanner
import com.example.ui.components.StatusPillBadge
import com.example.ui.theme.AccentBluePurple
import com.example.ui.theme.PrimaryPurpleStart
import com.example.ui.theme.SecondaryTealStart
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BuyMaterialsScreen(
  listings: List<MarketplaceListing>,
  filterState: MarketplaceFilterState,
  savedListingIds: List<String>,
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
  onToggleSaved: (String) -> Unit,
  onRequestQuote: (MarketplaceListing, Double, String, String) -> Unit,
  onReportItem: (String, String, String, String, String) -> Unit,
) {
  var quoteModalListing by remember { mutableStateOf<MarketplaceListing?>(null) }

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    // 1. Search & Filters Panel
    MainGlassPanel(
      title = "Buy Recycled Materials & Browse C&D Listings",
      subtitle = "Filter by category, price per tonne, quantity, city, or delivery options"
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        LiquidGlassSearchBar(
          query = filterState.searchQuery,
          onQueryChange = onSearchQueryChange,
          placeholder = "Search material name, waste category, or city..."
        )

        // Listing Type Segmented Control
        val typeTabs = listOf("All Listings", "Recycled Materials", "C&D Waste")
        val selectedTabIdx = when (filterState.listingTypeFilter) {
          "MATERIAL" -> 1
          "WASTE" -> 2
          else -> 0
        }
        LiquidSegmentedTabs(
          tabs = typeTabs,
          selectedIndex = selectedTabIdx,
          onTabSelected = { idx ->
            val code = when (idx) {
              1 -> "MATERIAL"
              2 -> "WASTE"
              else -> "ALL"
            }
            onListingTypeChange(code)
          }
        )

        // Category Filter Chips
        Text(
          text = "Material & Waste Categories",
          style = MaterialTheme.typography.labelMedium,
          color = TextDark,
          fontWeight = FontWeight.SemiBold
        )
        val categories = listOf(
          "All",
          "Recycled Aggregates",
          "Crushed Concrete",
          "Recycled Bricks",
          "Road-Base Materials",
          "Recovered Metal and Scrap",
          "Concrete Debris",
          "Brick and Masonry"
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          categories.forEach { cat ->
            LiquidGlassChip(
              text = cat,
              selected = filterState.selectedCategory == cat,
              onClick = { onCategoryChange(cat) },
              testTag = "filter_cat_${cat.take(10).lowercase().replace(" ", "_")}"
            )
          }
        }

        // Location & Delivery Filter Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          LiquidPillTextField(
            value = filterState.locationQuery,
            onValueChange = onLocationFilterChange,
            label = "City / Location Filter",
            placeholder = "Enter city manually...",
            trailingIcon = Icons.Default.LocationOn,
            modifier = Modifier.weight(1f),
            testTag = "location_filter_input"
          )
        }

        // Delivery Filter Chips
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("All", "Pickup Only", "Delivery Available", "Pickup or Delivery").forEach { del ->
            LiquidGlassChip(
              text = del,
              selected = filterState.deliveryFilter == del,
              onClick = { onDeliveryFilterChange(del) }
            )
          }
        }

        // Price Range Slider
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Max Price: $${filterState.maxPricePerTonne.toInt()} / tonne",
              style = MaterialTheme.typography.bodySmall,
              color = TextDark,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "Min Qty: ${filterState.minQuantityTonnes.toInt()} tonnes",
              style = MaterialTheme.typography.bodySmall,
              color = TextDark,
              fontWeight = FontWeight.SemiBold
            )
          }
          Slider(
            value = filterState.maxPricePerTonne,
            onValueChange = onMaxPriceChange,
            valueRange = 5f..500f,
            colors = SliderDefaults.colors(
              thumbColor = AccentBluePurple,
              activeTrackColor = PrimaryPurpleStart
            )
          )
        }

        // Sort Options + Clear Filters
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
          ) {
            listOf("Recently Listed", "Lowest Price", "Highest Price", "Highest Quantity").forEach { sortOpt ->
              LiquidGlassChip(
                text = sortOpt,
                selected = filterState.sortBy == sortOpt,
                onClick = { onSortByChange(sortOpt) }
              )
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
          LiquidSecondaryButton(
            text = "Clear Filters",
            icon = Icons.Default.FilterAltOff,
            onClick = onClearFilters,
            testTag = "clear_filters_button"
          )
        }
      }
    }

    // 2. Results Count & Empty State
    if (listings.isEmpty()) {
      MainGlassPanel(
        title = "No matching listings found.",
        subtitle = "Try broadening your price range, location, or category filters."
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Inventory2,
            contentDescription = null,
            tint = AccentBluePurple,
            modifier = Modifier.size(48.dp)
          )
          Text(
            text = "No recycled materials or C&D waste listings match your current filter criteria.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
          )
          LiquidPrimaryButton(
            text = "Clear Filters",
            icon = Icons.Default.FilterAltOff,
            onClick = onClearFilters,
            testTag = "empty_state_clear_filters_btn"
          )
        }
      }
    } else {
      listings.forEach { listing ->
        MarketplaceProductCard(
          listing = listing,
          isSaved = savedListingIds.contains(listing.id),
          onToggleSaved = { onToggleSaved(listing.id) },
          onViewDetails = { onSelectListing(listing) },
          onRequestQuote = { quoteModalListing = listing }
        )
      }
    }
  }

  if (quoteModalListing != null) {
    RequestQuotationDialog(
      listing = quoteModalListing!!,
      onDismiss = { quoteModalListing = null },
      onSubmit = { qty, deliveryPref, msg ->
        onRequestQuote(quoteModalListing!!, qty, deliveryPref, msg)
        quoteModalListing = null
      }
    )
  }
}

@Composable
private fun MarketplaceProductCard(
  listing: MarketplaceListing,
  isSaved: Boolean,
  onToggleSaved: () -> Unit,
  onViewDetails: () -> Unit,
  onRequestQuote: () -> Unit,
) {
  LiquidGlassCard(cornerRadius = 24.dp) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
      MaterialVisualBanner(
        imageKey = listing.imageKey,
        category = listing.category,
        isSample = listing.isSample
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = listing.title,
            style = MaterialTheme.typography.titleLarge,
            color = TextDark
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Supplier: ${listing.sellerName} (${listing.sellerRole})",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
          Text(
            text = "Location: ${listing.location} • Collection: ${listing.preferredCollectionDate}",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
        }

        ClearGlassCircleButton(
          icon = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
          contentDescription = if (isSaved) "Remove from Saved" else "Save Material",
          onClick = onToggleSaved,
          testTag = "save_listing_btn_${listing.id}"
        )
      }

      // Price, Quantity & Logistics Badges
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = String.format(Locale.US, "$%.2f %s", listing.pricePerTonne, listing.unit),
            style = MaterialTheme.typography.headlineMedium.copy(
              color = AccentBluePurple,
              fontWeight = FontWeight.Bold
            )
          )
          Text(
            text = "${listing.quantityTonnes.toInt()} tonnes available • ${listing.priceType}",
            style = MaterialTheme.typography.bodySmall,
            color = TextDark
          )
        }
        StatusPillBadge(status = listing.status)
      }

      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        LiquidGlassChip(
          text = listing.deliveryOptions,
          leadingIcon = Icons.Default.LocalShipping
        )
        LiquidGlassChip(
          text = if (listing.transportIncluded) "Transport Included" else "Transport Quoted Separately",
          isSuccessChip = listing.transportIncluded
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        LiquidSecondaryButton(
          text = "View Details",
          onClick = onViewDetails,
          modifier = Modifier.weight(1f),
          testTag = "view_details_btn_${listing.id}"
        )
        LiquidPrimaryButton(
          text = "Request Quote",
          icon = Icons.Default.RequestQuote,
          onClick = onRequestQuote,
          modifier = Modifier.weight(1f),
          testTag = "request_quote_btn_${listing.id}"
        )
      }
    }
  }
}

@Composable
fun ProductDetailScreen(
  listing: MarketplaceListing,
  isSaved: Boolean,
  onBack: () -> Unit,
  onToggleSaved: () -> Unit,
  onRequestQuote: (Double, String, String) -> Unit,
  onReportListing: (String, String) -> Unit,
) {
  var showQuoteModal by remember { mutableStateOf(false) }
  var showReportModal by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    MainGlassPanel(
      title = listing.title,
      subtitle = "${listing.category} • ${if (listing.isSample) "Sample Listing (Demonstration Data)" else "User Marketplace Listing"}",
      headerTrailing = {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          ClearGlassCircleButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back to Marketplace",
            onClick = onBack,
            testTag = "detail_back_button"
          )
          ClearGlassCircleButton(
            icon = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
            contentDescription = "Save Listing",
            onClick = onToggleSaved
          )
        }
      }
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        MaterialVisualBanner(
          imageKey = listing.imageKey,
          category = listing.category,
          isSample = listing.isSample
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = String.format(Locale.US, "$%.2f %s", listing.pricePerTonne, listing.unit),
              style = MaterialTheme.typography.headlineLarge.copy(color = AccentBluePurple)
            )
            Text(
              text = "Available Quantity: ${listing.quantityTonnes} tonnes (${listing.priceType})",
              style = MaterialTheme.typography.bodyMedium,
              color = TextDark
            )
          }
          StatusPillBadge(status = listing.status)
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.6f))

        Text(
          text = "Material Description",
          style = MaterialTheme.typography.titleMedium,
          color = TextDark
        )
        Text(
          text = listing.description,
          style = MaterialTheme.typography.bodyMedium,
          color = TextSecondary
        )

        // Engineering & Suitability Disclaimer (Section 7 requirement)
        LiquidGlassCard(cornerRadius = 18.dp) {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Science,
                contentDescription = null,
                tint = AccentBluePurple,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Material Condition, Grading & Suitability Specifications",
                style = MaterialTheme.typography.titleMedium,
                color = TextDark
              )
            }
            Text(
              text = listing.conditionSpecs.ifBlank {
                "Seller has not uploaded laboratory gradation certificates yet. Request sieve/compaction test records before structural use."
              },
              style = MaterialTheme.typography.bodySmall,
              color = TextDark
            )
            Text(
              text = "Note: Recovered C&D materials are not automatically suitable for every structural application. Verify engineering specifications with the supplier.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }
        }

        // Logistics & Seller Details
        LiquidGlassCard(cornerRadius = 18.dp) {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = "Collection, Logistics & Seller Information",
              style = MaterialTheme.typography.titleMedium,
              color = TextDark
            )
            Text("Seller / Supplier: ${listing.sellerName}", style = MaterialTheme.typography.bodyMedium)
            Text("Marketplace Role: ${listing.sellerRole}", style = MaterialTheme.typography.bodySmall)
            Text(
              text = "Verification Status: ${if (listing.sellerVerified) "Verified Account" else "Unverified / Standard Account"}",
              style = MaterialTheme.typography.bodySmall
            )
            Text("Collection Location: ${listing.location}", style = MaterialTheme.typography.bodyMedium)
            Text("Pickup / Delivery Options: ${listing.deliveryOptions}", style = MaterialTheme.typography.bodySmall)
            Text(
              text = "Transportation Included: ${if (listing.transportIncluded) "Yes (within local service zone)" else "No (quoted separately based on distance)"}",
              style = MaterialTheme.typography.bodySmall
            )
            Text("Preferred Collection Window: ${listing.preferredCollectionDate}", style = MaterialTheme.typography.bodySmall)
            Text("Contact Preference: ${listing.contactPreference}", style = MaterialTheme.typography.bodySmall)
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          LiquidPrimaryButton(
            text = "Request Quotation",
            icon = Icons.Default.RequestQuote,
            onClick = { showQuoteModal = true },
            modifier = Modifier.weight(1f),
            testTag = "detail_request_quote_btn"
          )
          LiquidSecondaryButton(
            text = "Report Listing",
            icon = Icons.Default.Flag,
            onClick = { showReportModal = true },
            testTag = "detail_report_listing_btn"
          )
        }
      }
    }
  }

  if (showQuoteModal) {
    RequestQuotationDialog(
      listing = listing,
      onDismiss = { showQuoteModal = false },
      onSubmit = { qty, pref, msg ->
        onRequestQuote(qty, pref, msg)
        showQuoteModal = false
      }
    )
  }

  if (showReportModal) {
    ReportModerationDialog(
      targetTitle = listing.title,
      onDismiss = { showReportModal = false },
      onSubmit = { reason, details ->
        onReportListing(reason, details)
        showReportModal = false
      }
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RequestQuotationDialog(
  listing: MarketplaceListing,
  onDismiss: () -> Unit,
  onSubmit: (Double, String, String) -> Unit,
) {
  var quantityText by rememberSaveable { mutableStateOf("50") }
  var deliveryPref by rememberSaveable { mutableStateOf("Pickup / Collection") }
  var message by rememberSaveable { mutableStateOf("Please confirm availability and loading schedule.") }

  val qty = quantityText.toDoubleOrNull()?.coerceAtLeast(1.0) ?: 50.0
  val materialSub = qty * listing.pricePerTonne
  val transportEst = if (listing.transportIncluded || deliveryPref == "Pickup / Collection") 0.0 else (qty * 3.5).coerceAtLeast(65.0)
  val processingFee = 25.0
  val taxEst = (materialSub + transportEst + processingFee) * 0.08
  val finalTotal = materialSub + transportEst + processingFee + taxEst

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Request Quotation",
        style = MaterialTheme.typography.titleLarge
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = listing.title,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.SemiBold
        )
        LiquidPillTextField(
          value = quantityText,
          onValueChange = { quantityText = it },
          label = "Required Quantity (tonnes) *",
          placeholder = "e.g., 50",
          testTag = "quote_qty_input"
        )
        Text(
          text = "Delivery / Collection Preference",
          style = MaterialTheme.typography.labelMedium,
          color = TextDark
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("Pickup / Collection", "Site Delivery", "Request Transport Quote").forEach { opt ->
            LiquidGlassChip(
              text = opt,
              selected = deliveryPref == opt,
              onClick = { deliveryPref = opt }
            )
          }
        }
        LiquidPillTextField(
          value = message,
          onValueChange = { message = it },
          label = "Message to Seller",
          placeholder = "Specify site access, delivery date, or grading questions...",
          singleLine = false,
          testTag = "quote_message_input"
        )

        // Itemized Cost Breakdown (Section 12 requirement)
        LiquidGlassCard(cornerRadius = 16.dp) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "Indicative Quotation Breakdown",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = TextDark
            )
            Text(
              text = String.format(Locale.US, "Material Price (%.1f t): $%.2f", qty, materialSub),
              style = MaterialTheme.typography.bodySmall
            )
            Text(
              text = String.format(Locale.US, "Estimated Transport: $%.2f", transportEst),
              style = MaterialTheme.typography.bodySmall
            )
            Text(
              text = String.format(Locale.US, "Platform / Processing Fee: $%.2f", processingFee),
              style = MaterialTheme.typography.bodySmall
            )
            Text(
              text = String.format(Locale.US, "Estimated Taxes (8%%): $%.2f", taxEst),
              style = MaterialTheme.typography.bodySmall
            )
            Text(
              text = String.format(Locale.US, "Estimated Total: $%.2f", finalTotal),
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold,
              color = AccentBluePurple
            )
          }
        }
      }
    },
    confirmButton = {
      LiquidPrimaryButton(
        text = "Submit Quote Request",
        onClick = { onSubmit(qty, deliveryPref, message) },
        testTag = "confirm_quote_request_btn"
      )
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReportModerationDialog(
  targetTitle: String,
  onDismiss: () -> Unit,
  onSubmit: (String, String) -> Unit,
) {
  var selectedReason by rememberSaveable { mutableStateOf("Hazardous / Unsuitable Material") }
  var details by rememberSaveable { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text("Report Listing / Trust & Safety", style = MaterialTheme.typography.titleLarge)
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Reporting: $targetTitle", style = MaterialTheme.typography.bodySmall)
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf(
            "Hazardous / Unsuitable Material",
            "Inaccurate Material Specs",
            "Suspected Unverified Seller",
            "Duplicate / Spam"
          ).forEach { r ->
            LiquidGlassChip(
              text = r,
              selected = selectedReason == r,
              onClick = { selectedReason = r }
            )
          }
        }
        LiquidPillTextField(
          value = details,
          onValueChange = { details = it },
          label = "Additional Moderation Notes",
          placeholder = "Describe the issue for administrative review...",
          singleLine = false
        )
      }
    },
    confirmButton = {
      LiquidPrimaryButton(
        text = "Submit Report",
        onClick = { onSubmit(selectedReason, details) }
      )
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
