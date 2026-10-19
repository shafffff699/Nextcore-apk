package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.data.CrusherBooking
import com.example.data.SampleMarketplaceData
import com.example.data.UserProfile
import com.example.ui.components.LiquidFeatureGradientCard
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidGlassChip
import com.example.ui.components.LiquidGlassToggle
import com.example.ui.components.LiquidPillTextField
import com.example.ui.components.LiquidPrimaryButton
import com.example.ui.components.LiquidSegmentedTabs
import com.example.ui.components.MainGlassPanel
import com.example.ui.components.MaterialVisualBanner
import com.example.ui.components.StatusPillBadge
import com.example.ui.theme.AccentBluePurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SellWasteScreen(
  isSubmitting: Boolean,
  onPublishListing: (
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
  ) -> Unit,
  onNavigateToCrusher: () -> Unit,
) {
  var listingModeIndex by rememberSaveable { mutableStateOf(0) } // 0 = Sell C&D Waste, 1 = Sell Recycled Material
  val listingType = if (listingModeIndex == 0) "WASTE" else "MATERIAL"

  val availableCategories = if (listingModeIndex == 0) {
    SampleMarketplaceData.wasteCategories
  } else {
    SampleMarketplaceData.materialCategories.filter { it != "Mobile Crushing Services" }
  }

  var title by rememberSaveable { mutableStateOf("") }
  var selectedCategory by rememberSaveable { mutableStateOf(availableCategories.first()) }
  var description by rememberSaveable { mutableStateOf("") }
  var conditionSpecs by rememberSaveable {
    mutableStateOf("Segregated C&D material; zero asbestos or hazardous contamination.")
  }
  var quantityText by rememberSaveable { mutableStateOf("120") }
  var priceText by rememberSaveable { mutableStateOf("12.50") }
  var priceType by rememberSaveable { mutableStateOf("Negotiable") }
  var location by rememberSaveable { mutableStateOf("") }
  var preferredDate by rememberSaveable { mutableStateOf("Immediate / Within 7 Days") }
  var deliveryOptions by rememberSaveable { mutableStateOf("Pickup or Delivery") }
  var transportIncluded by rememberSaveable { mutableStateOf(false) }
  var contactPreference by rememberSaveable { mutableStateOf("Platform Quote") }
  var selectedImageKey by rememberSaveable { mutableStateOf("recycled_concrete") }
  var validationError by rememberSaveable { mutableStateOf<String?>(null) }

  val imageOptions = listOf(
    "recycled_concrete" to "Recycled Aggregates",
    "crushed_concrete" to "Crushed Concrete",
    "crushed_brick" to "Recycled Bricks",
    "road_base" to "Road-Base Materials",
    "scrap_metal" to "Recovered Metal & Scrap"
  )

  fun imageKeyForCategory(cat: String): String {
    val c = cat.lowercase()
    return when {
      c.contains("recycled aggregate") -> "recycled_concrete"
      c.contains("concrete") -> "crushed_concrete"
      c.contains("brick") || c.contains("masonry") -> "crushed_brick"
      c.contains("road") || c.contains("asphalt") || c.contains("sub-base") -> "road_base"
      c.contains("metal") || c.contains("scrap") -> "scrap_metal"
      else -> "recycled_concrete"
    }
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("sell_waste_screen"),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    LiquidFeatureGradientCard(
      title = "Turn Demolition Debris Into Revenue",
      subtitle = "Post segregated concrete, brick rubble, or processed recycled aggregates directly to verified regional buyers.",
      badgeText = "Post a Listing",
      buttonText = "Need On-Site Crushing First?",
      onButtonClick = onNavigateToCrusher,
      testTag = "sell_waste_hero_card"
    )

    MainGlassPanel(
      title = "Create Marketplace Listing",
      subtitle = "Choose whether you are listing raw site debris or processed recycled materials"
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        LiquidSegmentedTabs(
          tabs = listOf("Sell C&D Waste / Debris", "Sell Recycled Material"),
          selectedIndex = listingModeIndex,
          onTabSelected = { idx ->
            listingModeIndex = idx
            selectedCategory = if (idx == 0) {
              SampleMarketplaceData.wasteCategories.first()
            } else {
              SampleMarketplaceData.materialCategories.first()
            }
            selectedImageKey = imageKeyForCategory(selectedCategory)
          }
        )

        MaterialVisualBanner(
          imageKey = selectedImageKey,
          category = selectedCategory,
          isSample = false
        )

        Text(
          text = "Visual Spec Banner Theme",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
          color = TextDark
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          imageOptions.forEach { (key, label) ->
            LiquidGlassChip(
              text = label,
              selected = selectedImageKey == key,
              onClick = { selectedImageKey = key }
            )
          }
        }

        LiquidPillTextField(
          value = title,
          onValueChange = {
            title = it
            validationError = null
          },
          label = "Listing Title *",
          placeholder = "e.g., 300T Segregated Structural Concrete Slab Rubble (<400mm)",
          testTag = "sell_title_input"
        )

        Text(
          text = "Category *",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
          color = TextDark
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          availableCategories.forEach { cat ->
            LiquidGlassChip(
              text = cat,
              selected = selectedCategory == cat,
              onClick = {
                selectedCategory = cat
                selectedImageKey = imageKeyForCategory(cat)
              }
            )
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          LiquidPillTextField(
            value = quantityText,
            onValueChange = { quantityText = it },
            label = "Quantity (Tonnes) *",
            placeholder = "e.g., 250",
            trailingIcon = Icons.Default.Scale,
            modifier = Modifier.weight(1f),
            testTag = "sell_quantity_input"
          )
          LiquidPillTextField(
            value = priceText,
            onValueChange = { priceText = it },
            label = "Price / Tonne (USD) *",
            placeholder = "e.g., 14.00",
            trailingIcon = Icons.Default.AttachMoney,
            modifier = Modifier.weight(1f),
            testTag = "sell_price_input"
          )
        }

        Text(
          text = "Pricing Model",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
          color = TextDark
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("Fixed Price", "Negotiable", "Free Pickup (Zero Tipping Fee)").forEach { pt ->
            LiquidGlassChip(
              text = pt,
              selected = priceType == pt,
              onClick = {
                priceType = pt
                if (pt.startsWith("Free")) priceText = "0.0"
              }
            )
          }
        }

        LiquidPillTextField(
          value = location,
          onValueChange = {
            location = it
            validationError = null
          },
          label = "Site / Yard Location *",
          placeholder = "e.g., South Industrial Park, Gate 3",
          trailingIcon = Icons.Default.LocationOn,
          testTag = "sell_location_input"
        )

        LiquidPillTextField(
          value = preferredDate,
          onValueChange = { preferredDate = it },
          label = "Availability / Collection Window",
          placeholder = "e.g., Available Mon–Sat, 07:00–17:00",
          trailingIcon = Icons.Default.CalendarMonth,
          testTag = "sell_collection_date_input"
        )

        LiquidPillTextField(
          value = conditionSpecs,
          onValueChange = { conditionSpecs = it },
          label = "Grading, Particle Size & Contamination Specs",
          placeholder = "e.g., 0-40mm crushed concrete, rebar removed, <1% fines",
          singleLine = false,
          testTag = "sell_specs_input"
        )

        LiquidPillTextField(
          value = description,
          onValueChange = { description = it },
          label = "Detailed Description & Loading Equipment On-Site",
          placeholder = "Describe origin of debris, excavator/loader availability on site, and truck access...",
          singleLine = false,
          testTag = "sell_description_input"
        )

        Text(
          text = "Logistics & Handover Options",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
          color = TextDark
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("Pickup or Delivery", "Pickup Only", "Delivery Available").forEach { opt ->
            LiquidGlassChip(
              text = opt,
              selected = deliveryOptions == opt,
              onClick = { deliveryOptions = opt }
            )
          }
        }

        LiquidGlassToggle(
          checked = transportIncluded,
          onCheckedChange = { transportIncluded = it },
          label = "Haulage / transport within 25 km included in per-tonne price",
          testTag = "sell_transport_toggle"
        )

        if (validationError != null) {
          LiquidGlassCard(cornerRadius = 18.dp) {
            Text(
              text = validationError ?: "",
              style = MaterialTheme.typography.bodySmall,
              color = com.example.ui.theme.DangerCoral
            )
          }
        }

        LiquidPrimaryButton(
          text = if (isSubmitting) "Publishing Listing..." else "Publish Listing to NEXCORE",
          icon = Icons.Default.Publish,
          enabled = !isSubmitting,
          onClick = {
            val qty = quantityText.toDoubleOrNull() ?: 0.0
            val price = priceText.toDoubleOrNull() ?: -1.0
            when {
              title.trim().length < 4 ->
                validationError = "Please enter a clear listing title (at least 4 characters)."
              location.trim().isEmpty() ->
                validationError = "Please enter the site or yard location."
              qty <= 0.0 ->
                validationError = "Please enter a valid quantity in tonnes (> 0)."
              price < 0.0 ->
                validationError = "Please enter a valid price per tonne (0 or greater)."
              else -> {
                validationError = null
                onPublishListing(
                  listingType,
                  title.trim(),
                  selectedCategory,
                  description.trim().ifBlank {
                    "Verified $selectedCategory available at ${location.trim()}."
                  },
                  conditionSpecs.trim(),
                  qty,
                  price,
                  priceType,
                  location.trim(),
                  preferredDate.trim(),
                  deliveryOptions,
                  transportIncluded,
                  contactPreference,
                  selectedImageKey
                )
              }
            }
          },
          modifier = Modifier.fillMaxWidth(),
          testTag = "publish_listing_submit_button"
        )
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookCrusherScreen(
  userProfile: UserProfile?,
  bookings: List<CrusherBooking>,
  isSubmitting: Boolean,
  onSubmitBooking: (
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
  ) -> Unit,
) {
  var customerName by rememberSaveable {
    mutableStateOf(userProfile?.fullName?.ifBlank { "Site Operations Manager" } ?: "")
  }
  var contactNumber by rememberSaveable {
    mutableStateOf(userProfile?.phone ?: "")
  }
  var siteLocation by rememberSaveable {
    mutableStateOf(userProfile?.city ?: "")
  }
  var selectedWasteType by rememberSaveable {
    mutableStateOf(SampleMarketplaceData.wasteCategories.first())
  }
  var estimatedTonnesText by rememberSaveable { mutableStateOf("350") }
  var siteAccessInfo by rememberSaveable {
    mutableStateOf("Wide gate (>4.5m), compacted hardstand pad available for tracked unit")
  }
  var preferredDate by rememberSaveable { mutableStateOf("Next Monday / Flexible") }
  var serviceType by rememberSaveable { mutableStateOf("Crushing & Multi-Deck Screening") }
  var additionalNotes by rememberSaveable {
    mutableStateOf("Target output: 20mm RCA and 40mm Type 1 sub-base for on-site reuse + NEXCORE resale.")
  }
  var lastSubmittedBooking by rememberSaveable { mutableStateOf<String?>(null) }
  var formError by rememberSaveable { mutableStateOf<String?>(null) }

  val estTonnes = estimatedTonnesText.toDoubleOrNull()?.coerceAtLeast(50.0) ?: 350.0
  val mobilizationFee = 850.0
  val ratePerTonne = when (serviceType) {
    "Primary Jaw Crushing Only" -> 6.50
    "Multi-Deck Screening Only" -> 4.25
    else -> 8.75
  }
  val estimatedServiceQuote = mobilizationFee + (estTonnes * ratePerTonne)

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("book_crusher_screen"),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    MainGlassPanel(
      title = "Book On-Site Mobile Crusher & Screener",
      subtitle = "Deploy tracked jaw/impact crushers and multi-deck screeners directly to your demolition site"
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        MaterialVisualBanner(
          imageKey = "mobile_crusher",
          category = "Mobile Crushing Services",
          isSample = false
        )

        LiquidGlassCard(cornerRadius = 22.dp, iridescentBorder = true) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Estimated Mobilization & Processing Cost",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
              )
              Text(
                text = "$${String.format("%.2f", estimatedServiceQuote)}",
                style = MaterialTheme.typography.headlineMedium.copy(
                  color = AccentBluePurple,
                  fontWeight = FontWeight.ExtraBold
                )
              )
              Text(
                text = "Includes $850 mobilization + $${String.format("%.2f", ratePerTonne)}/t (${estTonnes.toInt()} t)",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
            }
            StatusPillBadge(status = "On-Site Service")
          }
        }

        if (lastSubmittedBooking != null) {
          LiquidGlassCard(cornerRadius = 20.dp) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(22.dp)
              )
              Column {
                Text(
                  text = "Booking Reference: $lastSubmittedBooking",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = TextDark
                )
                Text(
                  text = "Status: Request Submitted • Track real-time dispatch in Track Orders.",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondary
                )
              }
            }
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          LiquidPillTextField(
            value = customerName,
            onValueChange = {
              customerName = it
              formError = null
            },
            label = "Contact / Site Manager *",
            placeholder = "e.g., Eng. R. Sharma",
            trailingIcon = Icons.Default.Person,
            modifier = Modifier.weight(1f),
            testTag = "crusher_customer_name_input"
          )
          LiquidPillTextField(
            value = contactNumber,
            onValueChange = {
              contactNumber = it
              formError = null
            },
            label = "Site Phone *",
            placeholder = "+1 555-0148",
            trailingIcon = Icons.Default.Phone,
            modifier = Modifier.weight(1f),
            testTag = "crusher_contact_input"
          )
        }

        LiquidPillTextField(
          value = siteLocation,
          onValueChange = {
            siteLocation = it
            formError = null
          },
          label = "Project / Demolition Site Location *",
          placeholder = "e.g., Plot 14 Industrial Corridor, Sector 9",
          trailingIcon = Icons.Default.LocationOn,
          testTag = "crusher_location_input"
        )

        Text(
          text = "Debris / Feedstock Material Type *",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
          color = TextDark
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          SampleMarketplaceData.wasteCategories.forEach { wt ->
            LiquidGlassChip(
              text = wt,
              selected = selectedWasteType == wt,
              onClick = { selectedWasteType = wt }
            )
          }
        }

        Text(
          text = "Equipment Configuration",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
          color = TextDark
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(
            "Crushing & Multi-Deck Screening",
            "Primary Jaw Crushing Only",
            "Multi-Deck Screening Only"
          ).forEach { st ->
            LiquidGlassChip(
              text = st,
              selected = serviceType == st,
              onClick = { serviceType = st }
            )
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          LiquidPillTextField(
            value = estimatedTonnesText,
            onValueChange = { estimatedTonnesText = it },
            label = "Est. Debris Volume (Tonnes) *",
            placeholder = "e.g., 350",
            trailingIcon = Icons.Default.Scale,
            modifier = Modifier.weight(1f),
            testTag = "crusher_tonnes_input"
          )
          LiquidPillTextField(
            value = preferredDate,
            onValueChange = { preferredDate = it },
            label = "Preferred Mobilization Date *",
            placeholder = "e.g., 25 Oct 2026",
            trailingIcon = Icons.Default.CalendarMonth,
            modifier = Modifier.weight(1f),
            testTag = "crusher_date_input"
          )
        }

        LiquidPillTextField(
          value = siteAccessInfo,
          onValueChange = { siteAccessInfo = it },
          label = "Site Access, Low-Loader Turning & Hardstand Details",
          placeholder = "Describe gate width, overhead clearance, and excavator availability...",
          singleLine = false,
          testTag = "crusher_access_input"
        )

        LiquidPillTextField(
          value = additionalNotes,
          onValueChange = { additionalNotes = it },
          label = "Desired Aggregate Output Sizes & Marketplace Resale Plan",
          placeholder = "e.g., Screen into 10mm, 20mm, and 40mm; list 50% surplus on NEXCORE...",
          singleLine = false,
          testTag = "crusher_notes_input"
        )

        if (formError != null) {
          LiquidGlassCard(cornerRadius = 18.dp) {
            Text(
              text = formError ?: "",
              style = MaterialTheme.typography.bodySmall,
              color = com.example.ui.theme.DangerCoral
            )
          }
        }

        LiquidPrimaryButton(
          text = if (isSubmitting) "Submitting Booking..." else "Submit Mobile Crusher Booking",
          icon = Icons.Default.Factory,
          enabled = !isSubmitting,
          onClick = {
            val tonnes = estimatedTonnesText.toDoubleOrNull() ?: 0.0
            when {
              customerName.trim().isEmpty() ->
                formError = "Please enter a contact person or site manager name."
              contactNumber.trim().isEmpty() ->
                formError = "Please enter a valid contact phone number."
              siteLocation.trim().isEmpty() ->
                formError = "Please enter the construction or demolition site address."
              tonnes <= 0.0 ->
                formError = "Please enter an estimated tonnage greater than 0."
              else -> {
                formError = null
                onSubmitBooking(
                  customerName.trim(),
                  contactNumber.trim(),
                  siteLocation.trim(),
                  selectedWasteType,
                  tonnes,
                  siteAccessInfo.trim(),
                  preferredDate.trim().ifBlank { "Flexible" },
                  serviceType,
                  additionalNotes.trim()
                ) { created ->
                  lastSubmittedBooking = created.bookingRef
                }
              }
            }
          },
          modifier = Modifier.fillMaxWidth(),
          testTag = "submit_crusher_booking_button"
        )
      }
    }

    if (bookings.isNotEmpty()) {
      MainGlassPanel(
        title = "Your Recent Mobile Crusher Bookings (${bookings.size})",
        subtitle = "Live mobilization and crushing status"
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          bookings.take(3).forEach { booking ->
            LiquidGlassCard(cornerRadius = 22.dp) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "${booking.bookingRef} • ${booking.wasteType}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextDark
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "${booking.estimatedTonnes.toInt()} tonnes at ${booking.siteLocation} • ${booking.preferredDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                StatusPillBadge(status = booking.status)
              }
            }
          }
        }
      }
    }
  }
}
