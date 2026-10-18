package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.CrusherBooking
import com.example.data.MarketplaceListing
import com.example.data.QuotationOrder
import com.example.data.SampleMarketplaceData
import com.example.data.TrustReport
import com.example.data.UserProfile
import com.example.ui.NexcoreDestination
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidGlassChip
import com.example.ui.components.LiquidPillTextField
import com.example.ui.components.LiquidPrimaryButton
import com.example.ui.components.LiquidSecondaryButton
import com.example.ui.components.LiquidSegmentedTabs
import com.example.ui.components.MainGlassPanel
import com.example.ui.components.StatusPillBadge
import com.example.ui.theme.AccentBluePurple
import com.example.ui.theme.PrimaryPurpleStart
import com.example.ui.theme.SecondaryTealStart
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TrackOrdersScreen(
  buyerQuotations: List<QuotationOrder>,
  sellerQuotations: List<QuotationOrder>,
  crusherBookings: List<CrusherBooking>,
  onUpdateQuotationStatus: (String, String, String?) -> Unit,
  onUpdateBookingStatus: (String, String, Double?, String?) -> Unit,
  onNavigate: (NexcoreDestination) -> Unit,
) {
  var selectedSection by rememberSaveable { mutableIntStateOf(0) } // 0 = Material Orders & Quotes, 1 = Mobile Crusher Bookings

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    MainGlassPanel(
      title = "Track Orders, Quotations & Crusher Bookings",
      subtitle = "Only saved transaction records are displayed — no simulated GPS vehicle positions."
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        LiquidSegmentedTabs(
          tabs = listOf(
            "Material Orders (${buyerQuotations.size + sellerQuotations.size})",
            "Crusher Bookings (${crusherBookings.size})"
          ),
          selectedIndex = selectedSection,
          onTabSelected = { selectedSection = it }
        )

        if (selectedSection == 0) {
          val allQuotes = (buyerQuotations + sellerQuotations).distinctBy { it.id }
          if (allQuotes.isEmpty()) {
            LiquidGlassCard(cornerRadius = 18.dp) {
              Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                  text = "No material orders or quotation requests yet.",
                  style = MaterialTheme.typography.titleMedium,
                  color = TextDark
                )
                Text(
                  text = "Browse the Buy Materials marketplace and tap 'Request Quote' on any listing to initiate a quotation record.",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondary
                )
                LiquidPrimaryButton(
                  text = "Browse Recycled Materials",
                  onClick = { onNavigate(NexcoreDestination.BUY_MATERIALS) }
                )
              }
            }
          } else {
            allQuotes.forEach { order ->
              QuotationOrderTimelineCard(
                order = order,
                onAcceptQuote = {
                  onUpdateQuotationStatus(
                    order.id,
                    "Accepted",
                    "Invoice Requested (Payment Pending)"
                  )
                },
                onAdvanceFulfillment = { nextStatus ->
                  onUpdateQuotationStatus(order.id, nextStatus, null)
                },
                onCancelOrder = {
                  onUpdateQuotationStatus(order.id, "Cancelled", "Cancelled")
                }
              )
            }
          }
        } else {
          if (crusherBookings.isEmpty()) {
            LiquidGlassCard(cornerRadius = 18.dp) {
              Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                  text = "No mobile crusher bookings found.",
                  style = MaterialTheme.typography.titleMedium,
                  color = TextDark
                )
                Text(
                  text = "Submit an on-site mobile crushing or screening request to generate a booking reference and track its assessment.",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondary
                )
                LiquidPrimaryButton(
                  text = "Book a Mobile Crusher",
                  onClick = { onNavigate(NexcoreDestination.BOOK_CRUSHER) }
                )
              }
            }
          } else {
            crusherBookings.forEach { booking ->
              CrusherBookingTimelineCard(
                booking = booking,
                onUpdateStatus = { newStatus, quotedAmt, provider ->
                  onUpdateBookingStatus(booking.id, newStatus, quotedAmt, provider)
                }
              )
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuotationOrderTimelineCard(
  order: QuotationOrder,
  onAcceptQuote: () -> Unit,
  onAdvanceFulfillment: (String) -> Unit,
  onCancelOrder: () -> Unit,
) {
  LiquidGlassCard(cornerRadius = 22.dp) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "${order.orderRef} • ${order.listingTitle}",
            style = MaterialTheme.typography.titleLarge,
            color = TextDark
          )
          Text(
            text = "Supplier: ${order.sellerName} • Buyer: ${order.buyerName}",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
        }
        StatusPillBadge(status = order.orderStatus)
      }

      // Itemized Cost Breakdown (Section 12 requirement)
      LiquidGlassCard(cornerRadius = 16.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "Separated Quotation & Order Breakdown (${order.quantityTonnes} tonnes)",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = TextDark
          )
          Text(
            text = String.format(
              Locale.US,
              "Material Price ($%.2f/t × %.1f t): $%.2f",
              order.unitPrice,
              order.quantityTonnes,
              order.materialSubtotal
            ),
            style = MaterialTheme.typography.bodySmall
          )
          Text(
            text = String.format(Locale.US, "Transportation Cost (%s): $%.2f", order.deliveryPreference, order.transportCost),
            style = MaterialTheme.typography.bodySmall
          )
          Text(
            text = String.format(Locale.US, "Processing / Service Fee: $%.2f", order.processingFee),
            style = MaterialTheme.typography.bodySmall
          )
          Text(
            text = String.format(Locale.US, "Applicable Taxes (8%%): $%.2f", order.taxAmount),
            style = MaterialTheme.typography.bodySmall
          )
          Text(
            text = String.format(Locale.US, "Final Quoted Amount: $%.2f", order.totalAmount),
            style = MaterialTheme.typography.titleMedium,
            color = AccentBluePurple,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Payment Status: ${order.paymentStatus} • Validity: ${order.validityDays} days",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
        }
      }

      // Chronological Status Timeline
      Text(
        text = "Order Status Timeline",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = TextDark
      )
      ChronologicalTimelineRow(
        steps = listOf(
          "Quote Requested",
          "Quote Received",
          "Accepted",
          "Processing",
          "Ready for Collection",
          "Delivered",
          "Completed"
        ),
        currentStatus = order.orderStatus
      )

      if (order.orderStatus !in listOf("Completed", "Cancelled")) {
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          if (order.orderStatus in listOf("Quote Requested", "Quote Received")) {
            LiquidPrimaryButton(
              text = "Accept Quotation",
              icon = Icons.Default.Check,
              onClick = onAcceptQuote,
              testTag = "accept_quote_btn_${order.id}"
            )
          }
          val nextStep = when (order.orderStatus) {
            "Accepted" -> "Processing"
            "Processing" -> "Ready for Collection"
            "Ready for Collection" -> "Dispatched"
            "Dispatched" -> "Delivered"
            "Delivered" -> "Completed"
            else -> null
          }
          if (nextStep != null) {
            LiquidSecondaryButton(
              text = "Mark as $nextStep",
              onClick = { onAdvanceFulfillment(nextStep) }
            )
          }
          LiquidGlassChip(
            text = "Decline / Cancel",
            leadingIcon = Icons.Default.Close,
            onClick = onCancelOrder
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CrusherBookingTimelineCard(
  booking: CrusherBooking,
  onUpdateStatus: (String, Double?, String?) -> Unit,
) {
  LiquidGlassCard(cornerRadius = 22.dp) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "${booking.bookingRef} • ${booking.serviceType} Service",
            style = MaterialTheme.typography.titleLarge,
            color = TextDark
          )
          Text(
            text = "Site: ${booking.siteLocation} • Preferred Date: ${booking.preferredDate}",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
        }
        StatusPillBadge(status = booking.status)
      }

      Text(
        text = "Debris Type: ${booking.wasteType} (${booking.estimatedTonnes.toInt()} tonnes) • Access: ${booking.siteAccessInfo}",
        style = MaterialTheme.typography.bodyMedium,
        color = TextDark
      )
      Text(
        text = if (booking.quotedAmount > 0.0) {
          String.format(Locale.US, "Assessed Service Quotation: $%.2f (%s)", booking.quotedAmount, booking.providerName)
        } else {
          "Quotation Status: Pending engineering & site access assessment (${booking.providerName})"
        },
        style = MaterialTheme.typography.bodySmall,
        color = AccentBluePurple,
        fontWeight = FontWeight.SemiBold
      )

      Text(
        text = "Booking Lifecycle Timeline",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = TextDark
      )
      ChronologicalTimelineRow(
        steps = SampleMarketplaceData.bookingStatuses.filter { it != "Cancelled" },
        currentStatus = booking.status
      )

      if (booking.status !in listOf("Completed", "Cancelled")) {
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          if (booking.status == "Quotation Sent") {
            LiquidPrimaryButton(
              text = "Accept Service Quotation",
              icon = Icons.Default.Check,
              onClick = { onUpdateStatus("Accepted", null, null) }
            )
          }
          LiquidGlassChip(
            text = "Cancel Booking Request",
            leadingIcon = Icons.Default.Close,
            onClick = { onUpdateStatus("Cancelled", null, null) }
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChronologicalTimelineRow(
  steps: List<String>,
  currentStatus: String,
) {
  val currentIdx = steps.indexOf(currentStatus).coerceAtLeast(0)
  FlowRow(
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    steps.forEachIndexed { idx, step ->
      val reached = idx <= currentIdx && currentStatus != "Cancelled"
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(50))
          .background(
            if (reached) SuccessGreen.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.45f)
          )
          .border(
            1.dp,
            if (step == currentStatus) AccentBluePurple else Color.White.copy(alpha = 0.7f),
            RoundedCornerShape(50)
          )
          .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(if (reached) SuccessGreen else TextMuted)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = step,
          style = MaterialTheme.typography.labelMedium.copy(
            color = if (step == currentStatus) AccentBluePurple else TextDark,
            fontWeight = if (step == currentStatus) FontWeight.Bold else FontWeight.Normal
          )
        )
      }
    }
  }
}

/**
 * SECTION 13: ROLE-SPECIFIC USER DASHBOARDS
 * A. Buyer Dashboard | B. Seller Dashboard | C. Service Provider Dashboard | D. Admin Dashboard
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserDashboardsScreen(
  userProfile: UserProfile?,
  allListings: List<MarketplaceListing>,
  sellerListings: List<MarketplaceListing>,
  buyerQuotations: List<QuotationOrder>,
  sellerQuotations: List<QuotationOrder>,
  crusherBookings: List<CrusherBooking>,
  userReports: List<TrustReport>,
  onSaveProfile: (String, String, String, String, String) -> Unit,
  onUpdateListingStatus: (String, String) -> Unit,
  onDeleteListing: (String) -> Unit,
  onSubmitSellerQuote: (String, Double, Double, Double, Double, Long, String) -> Unit,
  onUpdateBookingStatus: (String, String, Double?, String?) -> Unit,
  onNavigate: (NexcoreDestination) -> Unit,
  onSignOut: () -> Unit,
) {
  val initialTab = when (userProfile?.accountType) {
    "Demolition Contractor / Waste Seller" -> 1
    "Recycling Service Provider" -> 2
    else -> 0
  }
  var activeRoleTab by rememberSaveable { mutableIntStateOf(initialTab) }
  var editingQuoteOrder by remember { mutableStateOf<QuotationOrder?>(null) }

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    MainGlassPanel(
      title = "NEXCORE Role Dashboards",
      subtitle = "Active Account: ${userProfile?.fullName ?: "Member"} (${userProfile?.accountType ?: "Buyer / Customer"})"
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        LiquidSegmentedTabs(
          tabs = listOf("Buyer", "Seller", "Crusher Provider", "Admin & Trust"),
          selectedIndex = activeRoleTab,
          onTabSelected = { activeRoleTab = it }
        )

        when (activeRoleTab) {
          0 -> BuyerDashboardPanel(
            userProfile = userProfile,
            allListings = allListings,
            buyerQuotations = buyerQuotations,
            onSaveProfile = onSaveProfile,
            onNavigate = onNavigate,
            onSignOut = onSignOut
          )
          1 -> SellerDashboardPanel(
            sellerListings = sellerListings,
            sellerQuotations = sellerQuotations,
            onUpdateListingStatus = onUpdateListingStatus,
            onDeleteListing = onDeleteListing,
            onOpenQuoteResponseModal = { editingQuoteOrder = it },
            onNavigate = onNavigate
          )
          2 -> ServiceProviderDashboardPanel(
            crusherBookings = crusherBookings,
            onUpdateBookingStatus = onUpdateBookingStatus
          )
          3 -> AdminModerationDashboardPanel(
            userProfile = userProfile,
            allListings = allListings,
            sellerListings = sellerListings,
            crusherBookings = crusherBookings,
            buyerQuotations = buyerQuotations,
            userReports = userReports,
            onUpdateListingStatus = onUpdateListingStatus
          )
        }
      }
    }
  }

  if (editingQuoteOrder != null) {
    val q = editingQuoteOrder!!
    SellerQuoteResponseDialog(
      order = q,
      onDismiss = { editingQuoteOrder = null },
      onSubmit = { unitPrice, transport, processing, validity, notes ->
        onSubmitSellerQuote(q.id, q.quantityTonnes, unitPrice, transport, processing, validity, notes)
        editingQuoteOrder = null
      }
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BuyerDashboardPanel(
  userProfile: UserProfile?,
  allListings: List<MarketplaceListing>,
  buyerQuotations: List<QuotationOrder>,
  onSaveProfile: (String, String, String, String, String) -> Unit,
  onNavigate: (NexcoreDestination) -> Unit,
  onSignOut: () -> Unit,
) {
  var fullName by rememberSaveable(userProfile?.fullName) { mutableStateOf(userProfile?.fullName ?: "") }
  var companyName by rememberSaveable(userProfile?.companyName) { mutableStateOf(userProfile?.companyName ?: "") }
  var phone by rememberSaveable(userProfile?.phone) { mutableStateOf(userProfile?.phone ?: "") }
  var city by rememberSaveable(userProfile?.city) { mutableStateOf(userProfile?.city ?: "") }
  var accountType by rememberSaveable(userProfile?.accountType) {
    mutableStateOf(userProfile?.accountType ?: "Buyer / Customer")
  }

  val savedListings = allListings.filter { userProfile?.savedListingIds?.contains(it.id) == true }

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    // Summary Metrics
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      LiquidGlassCard(modifier = Modifier.weight(1f), cornerRadius = 18.dp) {
        Column {
          Text("Requested Quotes", style = MaterialTheme.typography.bodySmall)
          Text("${buyerQuotations.size}", style = MaterialTheme.typography.headlineMedium, color = AccentBluePurple)
        }
      }
      LiquidGlassCard(modifier = Modifier.weight(1f), cornerRadius = 18.dp) {
        Column {
          Text("Saved Materials", style = MaterialTheme.typography.bodySmall)
          Text("${savedListings.size}", style = MaterialTheme.typography.headlineMedium, color = SecondaryTealStart)
        }
      }
    }

    // Saved Materials Shortlist
    LiquidGlassCard(cornerRadius = 20.dp) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Saved Materials Shortlist", style = MaterialTheme.typography.titleMedium)
        if (savedListings.isEmpty()) {
          Text(
            text = "No materials bookmarked yet. Tap the bookmark icon on any listing in Buy Materials.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
        } else {
          savedListings.forEach { item ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("${item.title} (${item.location})", style = MaterialTheme.typography.bodyMedium)
              Text("$${item.pricePerTonne}/t", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // Account Details & Role Switcher
    LiquidGlassCard(cornerRadius = 20.dp) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Account Details & Role", style = MaterialTheme.typography.titleMedium)
          LiquidGlassChip(
            text = if (userProfile?.isVerified == true) "Verified Account" else "Standard Account (Unverified)",
            isSuccessChip = userProfile?.isVerified == true
          )
        }

        LiquidPillTextField(
          value = fullName,
          onValueChange = { fullName = it },
          label = "Full Name",
          placeholder = "Your Name"
        )
        LiquidPillTextField(
          value = companyName,
          onValueChange = { companyName = it },
          label = "Company / Organization",
          placeholder = "Company Name"
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          LiquidPillTextField(
            value = city,
            onValueChange = { city = it },
            label = "City / Service Location",
            placeholder = "City",
            modifier = Modifier.weight(1f)
          )
          LiquidPillTextField(
            value = phone,
            onValueChange = { phone = it },
            label = "Phone Number",
            placeholder = "Phone",
            modifier = Modifier.weight(1f)
          )
        }

        Text("Primary Marketplace Role", style = MaterialTheme.typography.labelMedium)
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          SampleMarketplaceData.accountRoles.forEach { role ->
            LiquidGlassChip(
              text = role,
              selected = accountType == role,
              onClick = { accountType = role }
            )
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          LiquidPrimaryButton(
            text = "Save Profile Changes",
            onClick = { onSaveProfile(fullName, companyName, phone, city, accountType) },
            modifier = Modifier.weight(1f),
            testTag = "save_profile_changes_btn"
          )
          LiquidSecondaryButton(
            text = "Sign Out",
            onClick = onSignOut,
            testTag = "dashboard_sign_out_btn"
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SellerDashboardPanel(
  sellerListings: List<MarketplaceListing>,
  sellerQuotations: List<QuotationOrder>,
  onUpdateListingStatus: (String, String) -> Unit,
  onDeleteListing: (String) -> Unit,
  onOpenQuoteResponseModal: (QuotationOrder) -> Unit,
  onNavigate: (NexcoreDestination) -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "My Published Listings (${sellerListings.size})",
        style = MaterialTheme.typography.titleLarge
      )
      LiquidPrimaryButton(
        text = "Add New Listing",
        icon = Icons.Default.Add,
        onClick = { onNavigate(NexcoreDestination.SELL_WASTE) },
        testTag = "seller_add_listing_btn"
      )
    }

    if (sellerListings.isEmpty()) {
      LiquidGlassCard(cornerRadius = 18.dp) {
        Text(
          text = "You haven't published any C&D waste or recycled material listings yet. Tap 'Add New Listing' to post your first inventory.",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )
      }
    } else {
      sellerListings.forEach { listing ->
        LiquidGlassCard(cornerRadius = 20.dp) {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(listing.title, style = MaterialTheme.typography.titleMedium)
                Text(
                  text = "${listing.category} • ${listing.quantityTonnes} tonnes • $${listing.pricePerTonne}/t",
                  style = MaterialTheme.typography.bodySmall
                )
              }
              StatusPillBadge(status = listing.status)
            }
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              if (listing.status == "Active") {
                LiquidGlassChip(
                  text = "Pause Listing",
                  leadingIcon = Icons.Default.Pause,
                  onClick = { onUpdateListingStatus(listing.id, "Paused") }
                )
              } else {
                LiquidGlassChip(
                  text = "Activate Listing",
                  leadingIcon = Icons.Default.PlayArrow,
                  onClick = { onUpdateListingStatus(listing.id, "Active") }
                )
              }
              LiquidGlassChip(
                text = "Remove Listing",
                leadingIcon = Icons.Default.Delete,
                onClick = { onDeleteListing(listing.id) }
              )
            }
          }
        }
      }
    }

    HorizontalDivider(color = Color.White.copy(alpha = 0.6f))

    Text(
      text = "Incoming Buyer Quotation Requests (${sellerQuotations.size})",
      style = MaterialTheme.typography.titleLarge
    )
    if (sellerQuotations.isEmpty()) {
      LiquidGlassCard(cornerRadius = 18.dp) {
        Text(
          text = "No buyer quotation enquiries received on your listings yet.",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )
      }
    } else {
      sellerQuotations.forEach { q ->
        LiquidGlassCard(cornerRadius = 20.dp) {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("${q.orderRef} • ${q.listingTitle}", style = MaterialTheme.typography.titleMedium)
              StatusPillBadge(status = q.orderStatus)
            }
            Text("Buyer: ${q.buyerName} • Qty: ${q.quantityTonnes} tonnes (${q.deliveryPreference})", style = MaterialTheme.typography.bodySmall)
            if (q.buyerMessage.isNotBlank()) {
              Text("Enquiry Note: \"${q.buyerMessage}\"", style = MaterialTheme.typography.bodySmall)
            }
            LiquidPrimaryButton(
              text = "Review & Send Formal Quotation",
              icon = Icons.Default.RequestQuote,
              onClick = { onOpenQuoteResponseModal(q) }
            )
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ServiceProviderDashboardPanel(
  crusherBookings: List<CrusherBooking>,
  onUpdateBookingStatus: (String, String, Double?, String?) -> Unit,
) {
  var quoteAmountText by rememberSaveable { mutableStateOf("1850") }
  var providerNameText by rememberSaveable { mutableStateOf("NEXCORE Mobile Crushing Unit #1") }

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Text(
      text = "Mobile Crusher & Screening Service Dispatch (${crusherBookings.size})",
      style = MaterialTheme.typography.titleLarge
    )
    Text(
      text = "Review site access, debris tonnage, and issue quotations or update job progress.",
      style = MaterialTheme.typography.bodySmall,
      color = TextSecondary
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      LiquidPillTextField(
        value = providerNameText,
        onValueChange = { providerNameText = it },
        label = "Assigned Provider / Plant Unit",
        placeholder = "NEXCORE Mobile Crushing Unit #1",
        modifier = Modifier.weight(1f)
      )
      LiquidPillTextField(
        value = quoteAmountText,
        onValueChange = { quoteAmountText = it },
        label = "Assessed Quote ($)",
        placeholder = "1850",
        modifier = Modifier.weight(1f)
      )
    }

    if (crusherBookings.isEmpty()) {
      LiquidGlassCard(cornerRadius = 18.dp) {
        Text(
          text = "No mobile crusher service requests saved yet.",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )
      }
    } else {
      crusherBookings.forEach { booking ->
        LiquidGlassCard(cornerRadius = 20.dp) {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("${booking.bookingRef} • ${booking.customerName}", style = MaterialTheme.typography.titleMedium)
              StatusPillBadge(status = booking.status)
            }
            Text(
              text = "Site: ${booking.siteLocation} • ${booking.estimatedTonnes.toInt()}t ${booking.wasteType} (${booking.serviceType})",
              style = MaterialTheme.typography.bodySmall
            )
            Text(
              text = "Access Specs: ${booking.siteAccessInfo}",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )

            if (booking.status !in listOf("Completed", "Cancelled")) {
              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                listOf(
                  "Under Review",
                  "Quotation Sent",
                  "Accepted",
                  "Scheduled",
                  "In Progress",
                  "Completed"
                ).forEach { st ->
                  LiquidGlassChip(
                    text = st,
                    selected = booking.status == st,
                    onClick = {
                      val amt = quoteAmountText.toDoubleOrNull() ?: 1850.0
                      onUpdateBookingStatus(booking.id, st, amt, providerNameText)
                    }
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun AdminModerationDashboardPanel(
  userProfile: UserProfile?,
  allListings: List<MarketplaceListing>,
  sellerListings: List<MarketplaceListing>,
  crusherBookings: List<CrusherBooking>,
  buyerQuotations: List<QuotationOrder>,
  userReports: List<TrustReport>,
  onUpdateListingStatus: (String, String) -> Unit,
) {
  val liveListingsCount = allListings.count { !it.isSample }
  val sampleListingsCount = allListings.count { it.isSample }

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    LiquidGlassCard(cornerRadius = 18.dp) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "Administration & Trust Moderation Overview",
          style = MaterialTheme.typography.titleMedium
        )
        Text(
          text = "Transparency Notice: Counts below reflect actual saved Firestore records plus clearly labeled Sample Listings (never fabricated business metrics).",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )
        Text(
          text = "• User Saved Listings: $liveListingsCount | Demonstration Sample Listings: $sampleListingsCount",
          style = MaterialTheme.typography.bodySmall
        )
        Text(
          text = "• Active Crusher Requests: ${crusherBookings.size} | Quotation Records: ${buyerQuotations.size}",
          style = MaterialTheme.typography.bodySmall
        )
        Text(
          text = "• Submitted Trust & Safety Reports: ${userReports.size}",
          style = MaterialTheme.typography.bodySmall
        )
      }
    }

    Text("Submitted Trust & Safety Reports", style = MaterialTheme.typography.titleMedium)
    if (userReports.isEmpty()) {
      LiquidGlassCard(cornerRadius = 18.dp) {
        Text(
          text = "Zero open complaints or listing reports.",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )
      }
    } else {
      userReports.forEach { rep ->
        LiquidGlassCard(cornerRadius = 18.dp) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("${rep.targetType}: ${rep.targetTitle}", style = MaterialTheme.typography.titleMedium)
              StatusPillBadge(status = rep.status)
            }
            Text("Reason: ${rep.reason}", style = MaterialTheme.typography.bodySmall)
            if (rep.details.isNotBlank()) {
              Text("Details: ${rep.details}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SellerQuoteResponseDialog(
  order: QuotationOrder,
  onDismiss: () -> Unit,
  onSubmit: (Double, Double, Double, Long, String) -> Unit,
) {
  var unitPriceText by rememberSaveable { mutableStateOf(order.unitPrice.toString()) }
  var transportText by rememberSaveable { mutableStateOf(order.transportCost.toString()) }
  var processingText by rememberSaveable { mutableStateOf(order.processingFee.toString()) }
  var validityText by rememberSaveable { mutableStateOf(order.validityDays.toString()) }
  var sellerNotes by rememberSaveable {
    mutableStateOf("Material tested and ready for loading. Price valid for ${order.validityDays} days.")
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Submit Seller Quotation (${order.orderRef})", style = MaterialTheme.typography.titleLarge) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Buyer: ${order.buyerName} • Qty: ${order.quantityTonnes} tonnes", style = MaterialTheme.typography.bodySmall)
        LiquidPillTextField(
          value = unitPriceText,
          onValueChange = { unitPriceText = it },
          label = "Offered Unit Price ($/tonne)",
          placeholder = "16.50"
        )
        LiquidPillTextField(
          value = transportText,
          onValueChange = { transportText = it },
          label = "Transportation Charges ($)",
          placeholder = "120.00"
        )
        LiquidPillTextField(
          value = processingText,
          onValueChange = { processingText = it },
          label = "Processing / Screening Fee ($)",
          placeholder = "25.00"
        )
        LiquidPillTextField(
          value = validityText,
          onValueChange = { validityText = it },
          label = "Quotation Validity (Days)",
          placeholder = "14"
        )
        LiquidPillTextField(
          value = sellerNotes,
          onValueChange = { sellerNotes = it },
          label = "Quotation Terms & Loading Notes",
          placeholder = "Terms...",
          singleLine = false
        )
      }
    },
    confirmButton = {
      LiquidPrimaryButton(
        text = "Send Quotation",
        onClick = {
          onSubmit(
            unitPriceText.toDoubleOrNull() ?: order.unitPrice,
            transportText.toDoubleOrNull() ?: order.transportCost,
            processingText.toDoubleOrNull() ?: order.processingFee,
            validityText.toLongOrNull() ?: 14L,
            sellerNotes
          )
        }
      )
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}
