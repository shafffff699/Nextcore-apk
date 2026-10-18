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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.CrusherBooking
import com.example.data.SampleMarketplaceData
import com.example.ui.components.LiquidFeatureGradientCard
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidGlassChip
import com.example.ui.components.LiquidGlassToggle
import com.example.ui.components.LiquidPillTextField
import com.example.ui.components.LiquidPrimaryButton
import com.example.ui.components.LiquidSecondaryButton
import com.example.ui.components.LiquidSegmentedTabs
import com.example.ui.components.MainGlassPanel
import com.example.ui.components.MaterialVisualBanner
import com.example.ui.components.StatusPillBadge
import com.example.ui.theme.AccentBluePurple
import com.example.ui.theme.DangerCoral
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

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
    onSuccess: () -> Unit,
  ) -> Unit,
  onNavigateToDashboard: () -> Unit,
) {
  var modeTabIndex by rememberSaveable { mutableIntStateOf(0) } // 0 = Form, 1 = Preview
  var listingType by rememberSaveable { mutableStateOf("WASTE") } // "WASTE" or "MATERIAL"
  var title by rememberSaveable { mutableStateOf("") }
  var category by rememberSaveable { mutableStateOf(SampleMarketplaceData.wasteCategories.first()) }
  var description by rememberSaveable { mutableStateOf("") }
  var conditionSpecs by rememberSaveable { mutableStateOf("Uncontaminated segregated C&D mineral debris, <500mm pieces") }
  var quantityText by rememberSaveable { mutableStateOf("120") }
  var priceText by rememberSaveable { mutableStateOf("10.00") }
  var priceType by rememberSaveable { mutableStateOf("Negotiable") }
  var location by rememberSaveable { mutableStateOf("") }
  var preferredDate by rememberSaveable { mutableStateOf("Within 7 Days") }
  var deliveryOptions by rememberSaveable { mutableStateOf("Pickup or Delivery") }
  var transportIncluded by rememberSaveable { mutableStateOf(false) }
  var contactPreference by rememberSaveable { mutableStateOf("Platform Quote") }
  var imageKey by rememberSaveable { mutableStateOf("crushed_concrete") }
  var nonHazardousConfirmed by rememberSaveable { mutableStateOf(true) }
  var validationError by remember { mutableStateOf<String?>(null) }
  var publishedSuccess by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    MainGlassPanel(
      title = "Sell C&D Waste or List Recycled Material",
      subtitle = "Publish segregated construction debris or processed recycled materials to the NEXCORE marketplace"
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Form vs Preview Segmented Control
        LiquidSegmentedTabs(
          tabs = listOf("1. Listing Details Form", "2. Preview Before Publishing"),
          selectedIndex = modeTabIndex,
          onTabSelected = { modeTabIndex = it }
        )

        if (publishedSuccess) {
          LiquidGlassCard(cornerRadius = 18.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = SuccessGreen,
                  modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Listing Saved & Published!",
                  style = MaterialTheme.typography.titleMedium,
                  color = TextDark
                )
              }
              Text(
                text = "Your listing is now live in the marketplace and manageable under your Seller Dashboard.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
              LiquidPrimaryButton(
                text = "View in Seller Dashboard",
                onClick = onNavigateToDashboard,
                testTag = "go_to_seller_dashboard_btn"
              )
            }
          }
        }

        if (modeTabIndex == 0) {
          // Listing Type Selector (Sell C&D Waste vs Sell Recycled Material)
          Text(
            text = "Listing Classification",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextDark
          )
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LiquidGlassChip(
              text = "C&D Waste / Debris",
              selected = listingType == "WASTE",
              onClick = {
                listingType = "WASTE"
                category = SampleMarketplaceData.wasteCategories.first()
              }
            )
            LiquidGlassChip(
              text = "Processed Recycled Material",
              selected = listingType == "MATERIAL",
              onClick = {
                listingType = "MATERIAL"
                category = SampleMarketplaceData.materialCategories.first()
              }
            )
          }

          LiquidPillTextField(
            value = title,
            onValueChange = { title = it },
            label = "Listing Title *",
            placeholder = "e.g., 150t Segregated Concrete Foundation Debris",
            testTag = "sell_title_input"
          )

          Text(
            text = "Waste / Material Category *",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextDark
          )
          val categoryOptions = if (listingType == "WASTE") {
            SampleMarketplaceData.wasteCategories
          } else {
            SampleMarketplaceData.materialCategories.filter { it != "Mobile Crushing Services" }
          }
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            categoryOptions.forEach { cat ->
              LiquidGlassChip(
                text = cat,
                selected = category == cat,
                onClick = {
                  category = cat
                  imageKey = when {
                    cat.contains("Brick", ignoreCase = true) -> "crushed_brick"
                    cat.contains("Metal", ignoreCase = true) -> "scrap_metal"
                    cat.contains("Road", ignoreCase = true) -> "road_base"
                    else -> "recycled_concrete"
                  }
                }
              )
            }
          }

          LiquidPillTextField(
            value = description,
            onValueChange = { description = it },
            label = "Material / Debris Description *",
            placeholder = "Describe source structure, segregation level, and loading equipment on site...",
            singleLine = false,
            testTag = "sell_description_input"
          )

          LiquidPillTextField(
            value = conditionSpecs,
            onValueChange = { conditionSpecs = it },
            label = "Condition, Sizing & Testing Notes",
            placeholder = "e.g., Broken <450mm, rebar trimmed, zero asbestos/gypsum",
            testTag = "sell_specs_input"
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            LiquidPillTextField(
              value = quantityText,
              onValueChange = { quantityText = it },
              label = "Est. Quantity (tonnes) *",
              placeholder = "120",
              trailingIcon = Icons.Default.Scale,
              modifier = Modifier.weight(1f),
              testTag = "sell_quantity_input"
            )
            LiquidPillTextField(
              value = priceText,
              onValueChange = { priceText = it },
              label = "Expected Price ($/t)",
              placeholder = "10.00",
              modifier = Modifier.weight(1f),
              testTag = "sell_price_input"
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            LiquidPillTextField(
              value = location,
              onValueChange = { location = it },
              label = "Site Address / City *",
              placeholder = "e.g., South Industrial Park, Gate 2",
              trailingIcon = Icons.Default.LocationOn,
              modifier = Modifier.weight(1f),
              testTag = "sell_location_input"
            )
            LiquidPillTextField(
              value = preferredDate,
              onValueChange = { preferredDate = it },
              label = "Preferred Collection Date",
              placeholder = "e.g., Next Week",
              trailingIcon = Icons.Default.CalendarMonth,
              modifier = Modifier.weight(1f),
              testTag = "sell_date_input"
            )
          }

          Text(
            text = "Price Type & Logistics",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextDark
          )
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf("Fixed Price", "Negotiable").forEach { pt ->
              LiquidGlassChip(
                text = pt,
                selected = priceType == pt,
                onClick = { priceType = pt }
              )
            }
            listOf("Pickup Only", "Delivery Available", "Pickup or Delivery").forEach { del ->
              LiquidGlassChip(
                text = del,
                selected = deliveryOptions == del,
                onClick = { deliveryOptions = del }
              )
            }
          }

          Text(
            text = "Visual Photograph / Material Preset",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextDark
          )
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(
              "crushed_concrete" to "Concrete Debris Photo",
              "crushed_brick" to "Brick & Masonry Photo",
              "road_base" to "Road-Base Aggregate Photo",
              "scrap_metal" to "Recoverable Steel Photo"
            ).forEach { (key, label) ->
              LiquidGlassChip(
                text = label,
                selected = imageKey == key,
                onClick = { imageKey = key }
              )
            }
          }

          LiquidGlassToggle(
            checked = transportIncluded,
            onCheckedChange = { transportIncluded = it },
            label = "Transportation included in expected price",
            testTag = "sell_transport_toggle"
          )

          LiquidGlassToggle(
            checked = nonHazardousConfirmed,
            onCheckedChange = { nonHazardousConfirmed = it },
            label = "I confirm this debris contains NO asbestos, hazardous chemicals, or illegal waste",
            testTag = "sell_non_hazardous_toggle"
          )

          // Hazardous Material Warning (Section 8 requirement)
          LiquidGlassCard(cornerRadius = 16.dp) {
            Row(
              verticalAlignment = Alignment.Top,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = null,
                tint = WarningAmber,
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = "Compliance Notice: Illegal, hazardous, or asbestos-containing materials cannot be listed as ordinary recyclable construction debris on NEXCORE.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
            }
          }

          if (validationError != null) {
            Text(
              text = validationError ?: "",
              style = MaterialTheme.typography.bodySmall,
              color = DangerCoral,
              fontWeight = FontWeight.Bold
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            LiquidSecondaryButton(
              text = "Preview Listing",
              onClick = { modeTabIndex = 1 },
              modifier = Modifier.weight(1f),
              testTag = "sell_preview_button"
            )
            LiquidPrimaryButton(
              text = if (isSubmitting) "Publishing..." else "Publish Listing",
              icon = Icons.Default.Publish,
              enabled = !isSubmitting,
              onClick = {
                val qty = quantityText.toDoubleOrNull() ?: 0.0
                val prc = priceText.toDoubleOrNull() ?: 0.0
                when {
                  title.trim().length < 3 ->
                    validationError = "Please enter a listing title (at least 3 characters)."
                  description.trim().length < 5 ->
                    validationError = "Please provide a brief description of the debris or material."
                  location.trim().length < 2 ->
                    validationError = "Please enter the site address or city."
                  qty <= 0.0 ->
                    validationError = "Please enter an estimated quantity greater than 0 tonnes."
                  !nonHazardousConfirmed ->
                    validationError = "You must confirm that the material is non-hazardous C&D debris."
                  else -> {
                    validationError = null
                    onPublishListing(
                      listingType,
                      title,
                      category,
                      description,
                      conditionSpecs,
                      qty,
                      prc,
                      priceType,
                      location,
                      preferredDate,
                      deliveryOptions,
                      transportIncluded,
                      contactPreference,
                      imageKey
                    ) {
                      publishedSuccess = true
                      title = ""
                      description = ""
                    }
                  }
                }
              },
              modifier = Modifier.weight(1f),
              testTag = "sell_publish_button"
            )
          }
        } else {
          // LIVE PREVIEW BEFORE PUBLISHING (Section 8 requirement)
          LiquidGlassCard(cornerRadius = 22.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              MaterialVisualBanner(
                imageKey = imageKey,
                category = category,
                isSample = false
              )
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = title.ifBlank { "Untitled C&D Waste Listing" },
                  style = MaterialTheme.typography.titleLarge,
                  color = TextDark
                )
                StatusPillBadge(status = "Preview Mode")
              }
              Text(
                text = "Estimated Quantity: ${quantityText.ifBlank { "0" }} tonnes • $${priceText.ifBlank { "0.00" }} per tonne ($priceType)",
                style = MaterialTheme.typography.bodyMedium,
                color = AccentBluePurple,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Site Location: ${location.ifBlank { "Not specified" }} • Collection: $preferredDate",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
              Text(
                text = description.ifBlank { "No description entered yet." },
                style = MaterialTheme.typography.bodyMedium,
                color = TextDark
              )
              Text(
                text = "Specs: $conditionSpecs",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                LiquidSecondaryButton(
                  text = "Back to Edit",
                  onClick = { modeTabIndex = 0 },
                  modifier = Modifier.weight(1f)
                )
                LiquidPrimaryButton(
                  text = "Confirm & Publish",
                  icon = Icons.Default.Publish,
                  onClick = {
                    val qty = quantityText.toDoubleOrNull() ?: 100.0
                    val prc = priceText.toDoubleOrNull() ?: 10.0
                    if (title.trim().length >= 3 && description.trim().length >= 5 && location.trim().length >= 2 && nonHazardousConfirmed) {
                      onPublishListing(
                        listingType,
                        title,
                        category,
                        description,
                        conditionSpecs,
                        qty,
                        prc,
                        priceType,
                        location,
                        preferredDate,
                        deliveryOptions,
                        transportIncluded,
                        contactPreference,
                        imageKey
                      ) {
                        publishedSuccess = true
                        modeTabIndex = 0
                      }
                    } else {
                      validationError = "Please complete all required fields (*) before publishing."
                      modeTabIndex = 0
                    }
                  },
                  modifier = Modifier.weight(1f)
                )
              }
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookCrusherScreen(
  isSubmitting: Boolean,
  defaultCustomerName: String,
  defaultPhone: String,
  defaultCity: String,
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
  onViewTrackOrders: () -> Unit,
) {
  var customerName by rememberSaveable { mutableStateOf(defaultCustomerName) }
  var contactNumber by rememberSaveable { mutableStateOf(defaultPhone) }
  var siteLocation by rememberSaveable { mutableStateOf(defaultCity) }
  var wasteType by rememberSaveable { mutableStateOf("Concrete Debris") }
  var estimatedTonnesText by rememberSaveable { mutableStateOf("350") }
  var siteAccessInfo by rememberSaveable {
    mutableStateOf("6m wide level hardstand access suitable for low-loader transporter")
  }
  var preferredDate by rememberSaveable { mutableStateOf("2026-11-15") }
  var serviceTypeIndex by rememberSaveable { mutableIntStateOf(2) } // 0=Crushing, 1=Screening, 2=Both
  var additionalNotes by rememberSaveable {
    mutableStateOf("Segregated concrete slab stockpile, pre-broken to <500mm, ready for jaw crusher.")
  }
  var errorMsg by remember { mutableStateOf<String?>(null) }
  var createdBooking by remember { mutableStateOf<CrusherBooking?>(null) }

  val serviceTypes = listOf("Crushing", "Screening", "Both")

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    // Proposed Mobile Crusher Service Disclosure Banner (Section 9 requirement)
    LiquidFeatureGradientCard(
      title = "Book On-Site Mobile Crusher & Screening",
      subtitle = "Proposed Service Feature: Request mobile processing of segregated C&D debris at or near your site. Subject to site access, equipment availability, and service feasibility.",
      badgeText = "Proposed Service",
      buttonText = "View My Bookings",
      onButtonClick = onViewTrackOrders
    )

    if (createdBooking != null) {
      val booking = createdBooking!!
      MainGlassPanel(
        title = "Booking Request Submitted",
        subtitle = "Reference: ${booking.bookingRef}"
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Current Status (Awaiting Provider Review):",
              style = MaterialTheme.typography.bodyMedium,
              color = TextDark
            )
            StatusPillBadge(status = booking.status)
          }
          Text(
            text = "Important: A booking is NOT confirmed until an authorized service provider assesses quantity, site access, and equipment availability and you accept their formal quotation.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
          Text(
            text = "Site: ${booking.siteLocation} • ${booking.estimatedTonnes.toInt()} tonnes (${booking.wasteType}) • Service: ${booking.serviceType}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextDark
          )
          LiquidPrimaryButton(
            text = "Track Booking Status Timeline",
            onClick = onViewTrackOrders,
            testTag = "track_submitted_booking_btn"
          )
        }
      }
    }

    MainGlassPanel(
      title = "Mobile Crusher Service Request Form",
      subtitle = "No live GPS tracking or guaranteed instant pricing is fabricated — an itemized quotation is issued after site review."
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          LiquidPillTextField(
            value = customerName,
            onValueChange = { customerName = it },
            label = "Customer / Contractor Name *",
            placeholder = "e.g., Apex Civil Contractors",
            trailingIcon = Icons.Default.Person,
            modifier = Modifier.weight(1f),
            testTag = "crusher_customer_name_input"
          )
          LiquidPillTextField(
            value = contactNumber,
            onValueChange = { contactNumber = it },
            label = "Contact Number *",
            placeholder = "+1 555-0192",
            trailingIcon = Icons.Default.Phone,
            modifier = Modifier.weight(1f),
            testTag = "crusher_phone_input"
          )
        }

        LiquidPillTextField(
          value = siteLocation,
          onValueChange = { siteLocation = it },
          label = "Construction-Site Location / Address *",
          placeholder = "e.g., Plot 14, North Industrial Corridor",
          trailingIcon = Icons.Default.LocationOn,
          testTag = "crusher_location_input"
        )

        Text(
          text = "Segregated Waste Type *",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold,
          color = TextDark
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(
            "Concrete Debris",
            "Brick and Masonry",
            "Mixed Mineral Debris",
            "Asphalt / Road Planings"
          ).forEach { wt ->
            LiquidGlassChip(
              text = wt,
              selected = wasteType == wt,
              onClick = { wasteType = wt }
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
            label = "Estimated Quantity (tonnes) *",
            placeholder = "350",
            trailingIcon = Icons.Default.Scale,
            modifier = Modifier.weight(1f),
            testTag = "crusher_tonnes_input"
          )
          LiquidPillTextField(
            value = preferredDate,
            onValueChange = { preferredDate = it },
            label = "Preferred Service Date *",
            placeholder = "2026-11-15",
            trailingIcon = Icons.Default.CalendarMonth,
            modifier = Modifier.weight(1f),
            testTag = "crusher_date_input"
          )
        }

        Text(
          text = "Required Service *",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold,
          color = TextDark
        )
        LiquidSegmentedTabs(
          tabs = serviceTypes,
          selectedIndex = serviceTypeIndex,
          onTabSelected = { serviceTypeIndex = it }
        )

        LiquidPillTextField(
          value = siteAccessInfo,
          onValueChange = { siteAccessInfo = it },
          label = "Site Access & Clearance Information *",
          placeholder = "Describe gate width, ground bearing capacity, overhead cables, working hours...",
          singleLine = false,
          testTag = "crusher_access_input"
        )

        LiquidPillTextField(
          value = additionalNotes,
          onValueChange = { additionalNotes = it },
          label = "Debris Condition, Photographs Reference & Additional Requirements",
          placeholder = "Specify feed size (<600mm), desired output aggregate size (e.g., 20mm / 40mm), dust suppression water supply...",
          singleLine = false,
          testTag = "crusher_notes_input"
        )

        if (errorMsg != null) {
          Text(
            text = errorMsg ?: "",
            style = MaterialTheme.typography.bodySmall,
            color = DangerCoral,
            fontWeight = FontWeight.Bold
          )
        }

        LiquidPrimaryButton(
          text = if (isSubmitting) "Submitting Request..." else "Submit Mobile Crusher Request",
          icon = Icons.Default.Factory,
          enabled = !isSubmitting,
          onClick = {
            val tonnes = estimatedTonnesText.toDoubleOrNull() ?: 0.0
            when {
              customerName.trim().length < 2 ->
                errorMsg = "Please enter customer or contractor name."
              contactNumber.trim().length < 4 ->
                errorMsg = "Please enter a valid contact phone number."
              siteLocation.trim().length < 3 ->
                errorMsg = "Please enter the construction-site location."
              tonnes <= 0.0 ->
                errorMsg = "Please enter an estimated waste quantity in tonnes."
              preferredDate.trim().length < 2 ->
                errorMsg = "Please enter a preferred service date."
              else -> {
                errorMsg = null
                onSubmitBooking(
                  customerName,
                  contactNumber,
                  siteLocation,
                  wasteType,
                  tonnes,
                  siteAccessInfo,
                  preferredDate,
                  serviceTypes[serviceTypeIndex],
                  additionalNotes
                ) { booking ->
                  createdBooking = booking
                }
              }
            }
          },
          modifier = Modifier.fillMaxWidth(),
          testTag = "submit_crusher_booking_btn"
        )
      }
    }
  }
}
