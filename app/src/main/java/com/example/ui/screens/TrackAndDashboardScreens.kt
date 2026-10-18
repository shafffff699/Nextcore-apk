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
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
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
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

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
  var selectedTab by rememberSaveable { mutableStateOf(0) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("track_orders_screen"),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    MainGlassPanel(
      title = "Track Orders, Quotations & Crusher Dispatch",
      subtitle = "End-to-end order lifecycle from RFQ to aggregate dispatch and on-site crushing"
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        LiquidSegmentedTabs(
          tabs = listOf(
            "Material Orders (${buyerQuotations.size})",
            "Mobile Crusher (${crusherBookings.size})",
            "Seller Enquiries (${sellerQuotations.size})"
          ),
          selectedIndex = selectedTab,
          onTabSelected = { selectedTab = it }
        )

        when (selectedTab) {
          0 -> {
            if (buyerQuotations.isEmpty()) {
              LiquidGlassCard(cornerRadius = 22.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Text(
                    text = "No material quotations or orders yet.",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextDark
                  )
                  Text(
                    text = "Browse recycled aggregates or demolition waste listings and tap 'Inspect & Quote' to request an itemized quotation.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                  )
                  LiquidPrimaryButton(
                    text = "Browse Materials",
                    onClick = { onNavigate(NexcoreDestination.BUY_MATERIALS) },
                    testTag = "track_empty_browse_materials_btn"
                  )
                }
              }
            } else {
              Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                buyerQuotations.forEach { order ->
                  QuotationOrderCard(
                    order = order,
                    isBuyerView = true,
                    onUpdateStatus = onUpdateQuotationStatus
                  )
                }
              }
            }
          }

          1 -> {
            if (crusherBookings.isEmpty()) {
              LiquidGlassCard(cornerRadius = 22.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Text(
                    text = "No mobile crusher bookings found.",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextDark
                  )
                  Text(
                    text = "Schedule a tracked jaw or impact crusher to process concrete and masonry rubble directly on your site.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                  )
                  LiquidPrimaryButton(
                    text = "Book Mobile Crusher",
                    icon = Icons.Default.Factory,
                    onClick = { onNavigate(NexcoreDestination.BOOK_CRUSHER) },
                    testTag = "track_empty_book_crusher_btn"
                  )
                }
              }
            } else {
              Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                crusherBookings.forEach { booking ->
                  CrusherBookingTrackingCard(
                    booking = booking,
                    onUpdateStatus = onUpdateBookingStatus
                  )
                }
              }
            }
          }

          2 -> {
            if (sellerQuotations.isEmpty()) {
              LiquidGlassCard(cornerRadius = 22.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Text(
                    text = "No incoming buyer enquiries on your listings yet.",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextDark
                  )
                  Text(
                    text = "Post construction debris or recycled materials to receive direct quotation requests from regional contractors.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                  )
                  LiquidSecondaryButton(
                    text = "Post a Listing",
                    onClick = { onNavigate(NexcoreDestination.SELL_WASTE) }
                  )
                }
              }
            } else {
              Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                sellerQuotations.forEach { order ->
                  QuotationOrderCard(
                    order = order,
                    isBuyerView = false,
                    onUpdateStatus = onUpdateQuotationStatus
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuotationOrderCard(
  order: QuotationOrder,
  isBuyerView: Boolean,
  onUpdateStatus: (String, String, String?) -> Unit,
) {
  LiquidGlassCard(cornerRadius = 24.dp) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "${order.orderRef} • ${order.listingTitle}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextDark
          )
          Text(
            text = if (isBuyerView) {
              "Supplier: ${order.sellerName} • ${order.quantityTonnes.toInt()} tonnes"
            } else {
              "Buyer: ${order.buyerName} • ${order.quantityTonnes.toInt()} tonnes"
            },
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
        }
        StatusPillBadge(status = order.orderStatus)
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Total Order Value (incl. Haulage & Tax)",
            style = MaterialTheme.typography.labelMedium,
            color = TextMuted
          )
          Text(
            text = "$${String.format("%.2f", order.totalAmount)}",
            style = MaterialTheme.typography.titleMedium.copy(
              color = AccentBluePurple,
              fontWeight = FontWeight.ExtraBold
            )
          )
        }
        StatusPillBadge(status = order.paymentStatus)
      }

      Text(
        text = "Material: $${String.format("%.2f", order.materialSubtotal)} • Haulage: $${String.format("%.2f", order.transportCost)} • Processing: $${String.format("%.2f", order.processingFee)} • Tax: $${String.format("%.2f", order.taxAmount)}",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary
      )

      if (order.buyerMessage.isNotBlank()) {
        Text(
          text = "Logistics Note: ${order.buyerMessage}",
          style = MaterialTheme.typography.bodySmall,
          color = TextDark
        )
      }

      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (isBuyerView) {
          if (order.orderStatus == "Quote Requested" || order.orderStatus == "Quote Received") {
            LiquidGlassChip(
              text = "Accept Quote",
              leadingIcon = Icons.Default.CheckCircle,
              isSuccessChip = true,
              onClick = { onUpdateStatus(order.id, "Accepted", "Invoice Issued") }
            )
          }
          if (order.paymentStatus != "Paid / Escrow Confirmed" && order.orderStatus != "Cancelled") {
            LiquidGlassChip(
              text = "Confirm Escrow Payment",
              leadingIcon = Icons.Default.Payment,
              onClick = { onUpdateStatus(order.id, order.orderStatus, "Paid / Escrow Confirmed") }
            )
          }
          if (order.orderStatus == "Dispatched" || order.orderStatus == "Accepted") {
            LiquidGlassChip(
              text = "Mark Delivered & Completed",
              leadingIcon = Icons.Default.LocalShipping,
              onClick = { onUpdateStatus(order.id, "Completed", "Paid / Settled") }
            )
          }
        } else {
          listOf("Quote Received", "Processing", "Ready for Collection", "Dispatched", "Completed").forEach { st ->
            LiquidGlassChip(
              text = st,
              selected = order.orderStatus == st,
              onClick = { onUpdateStatus(order.id, st, null) }
            )
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CrusherBookingTrackingCard(
  booking: CrusherBooking,
  onUpdateStatus: (String, String, Double?, String?) -> Unit,
) {
  LiquidGlassCard(cornerRadius = 24.dp) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "${booking.bookingRef} • ${booking.serviceType}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextDark
          )
          Text(
            text = "${booking.wasteType} (${booking.estimatedTonnes.toInt()} t) • ${booking.siteLocation}",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
        }
        StatusPillBadge(status = booking.status)
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Preferred Mobilization: ${booking.preferredDate}",
          style = MaterialTheme.typography.bodySmall,
          color = TextDark
        )
        Text(
          text = "Est. Quote: $${String.format("%.2f", booking.quotedAmount)}",
          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
          color = AccentBluePurple
        )
      }

      Text(
        text = "Advance Mobilization Workflow:",
        style = MaterialTheme.typography.labelMedium,
        color = TextMuted
      )
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        SampleMarketplaceData.bookingStatuses.forEach { st ->
          LiquidGlassChip(
            text = st,
            selected = booking.status == st,
            onClick = { onUpdateStatus(booking.id, st, null, null) }
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
  userProfile: UserProfile?,
  allListings: List<MarketplaceListing>,
  sellerListings: List<MarketplaceListing>,
  buyerQuotations: List<QuotationOrder>,
  sellerQuotations: List<QuotationOrder>,
  crusherBookings: List<CrusherBooking>,
  userReports: List<TrustReport>,
  isSubmitting: Boolean,
  onSaveProfile: (String, String, String, String, String) -> Unit,
  onUpdateListingStatus: (String, String) -> Unit,
  onDeleteListing: (String) -> Unit,
  onSubmitSellerQuote: (String, Double, Double, Double, Double, Long, String) -> Unit,
  onUpdateBookingStatus: (String, String, Double?, String?) -> Unit,
  onSelectListing: (MarketplaceListing) -> Unit,
  onSignOut: () -> Unit,
) {
  var selectedTab by rememberSaveable { mutableStateOf(0) }

  var fullName by rememberSaveable(userProfile?.fullName) {
    mutableStateOf(userProfile?.fullName ?: "")
  }
  var companyName by rememberSaveable(userProfile?.companyName) {
    mutableStateOf(userProfile?.companyName ?: "")
  }
  var phone by rememberSaveable(userProfile?.phone) {
    mutableStateOf(userProfile?.phone ?: "")
  }
  var city by rememberSaveable(userProfile?.city) {
    mutableStateOf(userProfile?.city ?: "")
  }
  var accountType by rememberSaveable(userProfile?.accountType) {
    mutableStateOf(userProfile?.accountType ?: SampleMarketplaceData.accountRoles.first())
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("dashboard_screen"),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    MainGlassPanel(
      title = "NEXCORE Role Dashboard & Account",
      subtitle = "${userProfile?.fullName ?: "Member"} • ${userProfile?.accountType ?: "Buyer / Customer"}",
      headerTrailing = {
        LiquidGlassChip(
          text = "Sign Out",
          leadingIcon = Icons.Default.Logout,
          onClick = onSignOut,
          testTag = "dashboard_sign_out_button"
        )
      }
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        LiquidSegmentedTabs(
          tabs = listOf("Buyer & Saved", "Seller & Listings", "Crusher & Safety", "Profile"),
          selectedIndex = selectedTab,
          onTabSelected = { selectedTab = it }
        )

        when (selectedTab) {
          0 -> {
            val savedIds = userProfile?.savedListingIds ?: emptyList()
            val savedListings = allListings.filter { savedIds.contains(it.id) }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Text(
                text = "Saved Material Shortlist (${savedListings.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextDark
              )
              if (savedListings.isEmpty()) {
                LiquidGlassCard(cornerRadius = 20.dp) {
                  Text(
                    text = "You have not bookmarked any listings yet. Tap the bookmark icon on any material card to save it here.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                  )
                }
              } else {
                savedListings.forEach { item ->
                  LiquidGlassCard(
                    cornerRadius = 22.dp,
                    onClick = { onSelectListing(item) }
                  ) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = item.title,
                          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                          color = TextDark
                        )
                        Text(
                          text = "${item.category} • $${String.format("%.2f", item.pricePerTonne)}/t • ${item.location}",
                          style = MaterialTheme.typography.bodySmall,
                          color = TextSecondary
                        )
                      }
                      StatusPillBadge(status = item.status)
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Active Buyer Quotations (${buyerQuotations.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextDark
              )
              buyerQuotations.take(4).forEach { q ->
                LiquidGlassCard(cornerRadius = 20.dp) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = "${q.orderRef} • ${q.listingTitle}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextDark
                      )
                      Text(
                        text = "${q.quantityTonnes.toInt()} t • Total: $${String.format("%.2f", q.totalAmount)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                      )
                    }
                    StatusPillBadge(status = q.orderStatus)
                  }
                }
              }
            }
          }

          1 -> {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Text(
                text = "Your Published Listings (${sellerListings.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextDark
              )
              if (sellerListings.isEmpty()) {
                LiquidGlassCard(cornerRadius = 20.dp) {
                  Text(
                    text = "You haven't published any construction waste or recycled material listings yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                  )
                }
              } else {
                sellerListings.forEach { listing ->
                  LiquidGlassCard(cornerRadius = 22.dp) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Column(modifier = Modifier.weight(1f)) {
                          Text(
                            text = listing.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextDark
                          )
                          Text(
                            text = "${listing.quantityTonnes.toInt()} t • $${String.format("%.2f", listing.pricePerTonne)}/t • ${listing.location}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                          )
                        }
                        StatusPillBadge(status = listing.status)
                      }
                      FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                      ) {
                        listOf("Active", "Reserved", "Sold").forEach { st ->
                          LiquidGlassChip(
                            text = st,
                            selected = listing.status == st,
                            onClick = { onUpdateListingStatus(listing.id, st) }
                          )
                        }
                        LiquidGlassChip(
                          text = "Delete",
                          leadingIcon = Icons.Default.DeleteOutline,
                          onClick = { onDeleteListing(listing.id) }
                        )
                      }
                    }
                  }
                }
              }

              if (sellerQuotations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "Respond to Incoming Buyer RFQs (${sellerQuotations.size})",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = TextDark
                )
                sellerQuotations.forEach { rfq ->
                  SellerRfqResponseCard(
                    order = rfq,
                    onSubmitSellerQuote = onSubmitSellerQuote
                  )
                }
              }
            }
          }

          2 -> {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Text(
                text = "Mobile Crusher Service Jobs (${crusherBookings.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextDark
              )
              if (crusherBookings.isEmpty()) {
                LiquidGlassCard(cornerRadius = 20.dp) {
                  Text(
                    text = "No mobile crusher service jobs scheduled yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                  )
                }
              } else {
                crusherBookings.forEach { job ->
                  CrusherBookingTrackingCard(
                    booking = job,
                    onUpdateStatus = onUpdateBookingStatus
                  )
                }
              }

              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Trust, Safety & Compliance Reports (${userReports.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextDark
              )
              if (userReports.isEmpty()) {
                LiquidGlassCard(cornerRadius = 20.dp) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Security,
                      contentDescription = null,
                      tint = AccentBluePurple,
                      modifier = Modifier.size(20.dp)
                    )
                    Text(
                      text = "Zero compliance issues reported. All listings adhere to uncontaminated C&D recovery standards.",
                      style = MaterialTheme.typography.bodySmall,
                      color = TextSecondary
                    )
                  }
                }
              } else {
                userReports.forEach { rep ->
                  LiquidGlassCard(cornerRadius = 20.dp) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = "${rep.targetTitle} (${rep.reason})",
                          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                          color = TextDark
                        )
                        if (rep.details.isNotBlank()) {
                          Text(
                            text = rep.details,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                          )
                        }
                      }
                      StatusPillBadge(status = rep.status)
                    }
                  }
                }
              }
            }
          }

          3 -> {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Text(
                text = "Organization & Marketplace Role Settings",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextDark
              )
              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                SampleMarketplaceData.accountRoles.forEach { role ->
                  LiquidGlassChip(
                    text = role,
                    selected = accountType == role,
                    isSuccessChip = accountType == role,
                    onClick = { accountType = role }
                  )
                }
              }

              LiquidPillTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = "Full Name",
                placeholder = "Your Name",
                trailingIcon = Icons.Default.Person,
                testTag = "profile_name_input"
              )

              LiquidPillTextField(
                value = companyName,
                onValueChange = { companyName = it },
                label = "Company / Contractor Organization",
                placeholder = "Company Name",
                trailingIcon = Icons.Default.Apartment,
                testTag = "profile_company_input"
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                LiquidPillTextField(
                  value = city,
                  onValueChange = { city = it },
                  label = "Primary City / Hub",
                  placeholder = "City",
                  trailingIcon = Icons.Default.LocationCity,
                  modifier = Modifier.weight(1f),
                  testTag = "profile_city_input"
                )
                LiquidPillTextField(
                  value = phone,
                  onValueChange = { phone = it },
                  label = "Contact Phone",
                  placeholder = "Phone",
                  trailingIcon = Icons.Default.Phone,
                  modifier = Modifier.weight(1f),
                  testTag = "profile_phone_input"
                )
              }

              LiquidPrimaryButton(
                text = if (isSubmitting) "Saving..." else "Save Profile Changes",
                icon = Icons.Default.Save,
                enabled = !isSubmitting,
                onClick = {
                  onSaveProfile(
                    fullName.trim(),
                    companyName.trim(),
                    phone.trim(),
                    city.trim(),
                    accountType
                  )
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "save_profile_button"
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SellerRfqResponseCard(
  order: QuotationOrder,
  onSubmitSellerQuote: (String, Double, Double, Double, Double, Long, String) -> Unit,
) {
  var unitPriceText by rememberSaveable(order.id) { mutableStateOf(order.unitPrice.toString()) }
  var transportCostText by rememberSaveable(order.id) { mutableStateOf(order.transportCost.toString()) }
  var sellerNotes by rememberSaveable(order.id) {
    mutableStateOf(order.sellerNotes.ifBlank { "Material tested and ready for immediate tipper loading." })
  }

  LiquidGlassCard(cornerRadius = 22.dp) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${order.orderRef} • ${order.buyerName} (${order.quantityTonnes.toInt()} t)",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = TextDark
        )
        StatusPillBadge(status = order.orderStatus)
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        LiquidPillTextField(
          value = unitPriceText,
          onValueChange = { unitPriceText = it },
          label = "Unit Price ($/t)",
          placeholder = "16.50",
          modifier = Modifier.weight(1f)
        )
        LiquidPillTextField(
          value = transportCostText,
          onValueChange = { transportCostText = it },
          label = "Haulage ($)",
          placeholder = "120.00",
          modifier = Modifier.weight(1f)
        )
      }
      LiquidPillTextField(
        value = sellerNotes,
        onValueChange = { sellerNotes = it },
        label = "Seller Dispatch Notes",
        placeholder = "Dispatch window & loading notes..."
      )
      LiquidSecondaryButton(
        text = "Send Official Quotation to Buyer",
        icon = Icons.Default.ReceiptLong,
        onClick = {
          val uPrice = unitPriceText.toDoubleOrNull() ?: order.unitPrice
          val tCost = transportCostText.toDoubleOrNull() ?: order.transportCost
          onSubmitSellerQuote(
            order.id,
            order.quantityTonnes,
            uPrice,
            tCost,
            order.processingFee,
            14L,
            sellerNotes
          )
        },
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}
