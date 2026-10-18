package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CrusherBooking
import com.example.data.MarketplaceListing
import com.example.data.NexcoreRepository
import com.example.data.QuotationOrder
import com.example.data.TrustReport
import com.example.data.UiState
import com.example.data.UserProfile
import com.example.ui.NexcoreDestination
import com.example.ui.NexcoreViewModel
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.RegistrationDraft
import com.example.ui.auth.signOut
import com.example.ui.components.ClearGlassCircleButton
import com.example.ui.components.LiquidGlassBackground
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidGlassChip
import com.example.ui.components.LiquidPrimaryButton
import com.example.ui.components.StatusPillBadge
import com.example.ui.screens.BookCrusherScreen
import com.example.ui.screens.BuyMaterialsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SellWasteScreen
import com.example.ui.screens.TrackOrdersScreen
import com.example.ui.theme.AccentBluePurple
import com.example.ui.theme.LiquidGlassTheme
import com.example.ui.theme.PrimaryPurpleBrush
import com.example.ui.theme.SolidSoftWhite
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextSecondary
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      LiquidGlassTheme {
        NexcoreAppRoot()
      }
    }
  }
}

@Composable
fun NexcoreAppRoot() {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val credentialManager = remember { CredentialManager.create(context) }

  var currentUserId by remember {
    mutableStateOf(Firebase.auth.currentUser?.uid)
  }
  var pendingDraft by remember {
    mutableStateOf<RegistrationDraft?>(null)
  }

  val uid = currentUserId
  if (uid == null) {
    AuthScreen(
      onAuthSuccess = { draft ->
        pendingDraft = draft
        currentUserId = Firebase.auth.currentUser?.uid
      }
    )
  } else {
    val viewModel = remember(uid) {
      NexcoreViewModel(
        repository = NexcoreRepository(context = context),
        currentUserId = uid
      )
    }

    LaunchedEffect(uid, pendingDraft) {
      viewModel.ensureUserProfileInitialized(pendingDraft)
    }

    NexcoreAuthenticatedShell(
      viewModel = viewModel,
      onSignOut = {
        signOut(
          context = context,
          credentialManager = credentialManager,
          onSignOutComplete = {
            currentUserId = null
          },
          scope = scope
        )
      }
    )
  }
}

@Composable
private fun NexcoreAuthenticatedShell(
  viewModel: NexcoreViewModel,
  onSignOut: () -> Unit,
) {
  val currentDestination by viewModel.currentDestination.collectAsStateWithLifecycle()
  val selectedListing by viewModel.selectedListingForDetail.collectAsStateWithLifecycle()
  val filterState by viewModel.filterState.collectAsStateWithLifecycle()
  val combinedListings by viewModel.combinedListings.collectAsStateWithLifecycle()
  val bannerMessage by viewModel.bannerMessage.collectAsStateWithLifecycle()
  val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()

  val userProfileState by viewModel.userProfileState.collectAsStateWithLifecycle()
  val sellerListingsState by viewModel.sellerListingsState.collectAsStateWithLifecycle()
  val crusherBookingsState by viewModel.crusherBookingsState.collectAsStateWithLifecycle()
  val buyerQuotationsState by viewModel.buyerQuotationsState.collectAsStateWithLifecycle()
  val sellerQuotationsState by viewModel.sellerQuotationsState.collectAsStateWithLifecycle()
  val userReportsState by viewModel.userReportsState.collectAsStateWithLifecycle()

  val userProfile: UserProfile? = (userProfileState as? UiState.Success)?.data
  val sellerListings: List<MarketplaceListing> =
    (sellerListingsState as? UiState.Success)?.data ?: emptyList()
  val crusherBookings: List<CrusherBooking> =
    (crusherBookingsState as? UiState.Success)?.data ?: emptyList()
  val buyerQuotations: List<QuotationOrder> =
    (buyerQuotationsState as? UiState.Success)?.data ?: emptyList()
  val sellerQuotations: List<QuotationOrder> =
    (sellerQuotationsState as? UiState.Success)?.data ?: emptyList()
  val userReports: List<TrustReport> =
    (userReportsState as? UiState.Success)?.data ?: emptyList()

  var mobileMenuExpanded by rememberSaveable { mutableStateOf(false) }

  if (currentDestination != NexcoreDestination.HOME) {
    BackHandler {
      if (selectedListing != null) {
        viewModel.closeListingDetail()
      } else {
        viewModel.navigateTo(NexcoreDestination.HOME)
      }
    }
  }

  LiquidGlassBackground {
    Scaffold(
      containerColor = Color.Transparent,
      contentWindowInsets = WindowInsets.safeDrawing,
      bottomBar = {
        NexcoreBottomGlassBar(
          currentDestination = currentDestination,
          onSelectDestination = { dest ->
            mobileMenuExpanded = false
            viewModel.navigateTo(dest)
          }
        )
      }
    ) { innerPadding ->
      BoxWithConstraints(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        val isWideScreen = maxWidth >= 720.dp

        Row(modifier = Modifier.fillMaxSize()) {
          if (isWideScreen) {
            NexcoreSideGlassRail(
              currentDestination = currentDestination,
              onSelectDestination = { dest ->
                viewModel.navigateTo(dest)
              }
            )
          }

          Column(
            modifier = Modifier
              .weight(1f)
              .fillMaxHeight()
              .verticalScroll(rememberScrollState())
              .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 920.dp),
              verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
              // Top Brand Header Bar with Logo, "Post a Listing" CTA, and Hamburger Menu
              NexcoreTopGlassHeader(
                currentDestination = currentDestination,
                userProfile = userProfile,
                mobileMenuExpanded = mobileMenuExpanded,
                onToggleMobileMenu = { mobileMenuExpanded = !mobileMenuExpanded },
                onNavigate = { dest ->
                  mobileMenuExpanded = false
                  viewModel.navigateTo(dest)
                }
              )

              AnimatedVisibility(visible = mobileMenuExpanded) {
                LiquidGlassCard(cornerRadius = 24.dp, iridescentBorder = true) {
                  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                      text = "NEXCORE Navigation Menu",
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                      color = TextDark
                    )
                    NexcoreDestination.entries.forEach { dest ->
                      LiquidGlassChip(
                        text = dest.title,
                        selected = currentDestination == dest,
                        leadingIcon = dest.icon(),
                        onClick = {
                          mobileMenuExpanded = false
                          viewModel.navigateTo(dest)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "drawer_nav_${dest.name.lowercase()}"
                      )
                    }
                  }
                }
              }

              if (bannerMessage != null) {
                LiquidGlassCard(
                  cornerRadius = 20.dp,
                  iridescentBorder = true,
                  onClick = { viewModel.clearBannerMessage() }
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = bannerMessage ?: "",
                      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                      color = TextDark,
                      modifier = Modifier.weight(1f)
                    )
                    StatusPillBadge(status = "Dismiss")
                  }
                }
              }

              when (currentDestination) {
                NexcoreDestination.HOME -> {
                  HomeScreen(
                    listings = combinedListings,
                    searchQuery = filterState.searchQuery,
                    userProfile = userProfile,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    onNavigate = { dest, cat, type ->
                      viewModel.navigateTo(dest, cat, type)
                    },
                    onSelectListing = { listing ->
                      viewModel.openListingDetail(listing)
                      viewModel.navigateTo(NexcoreDestination.BUY_MATERIALS)
                      viewModel.openListingDetail(listing)
                    },
                    onToggleSaveListing = viewModel::toggleSavedListing
                  )
                }

                NexcoreDestination.BUY_MATERIALS -> {
                  BuyMaterialsScreen(
                    listings = combinedListings,
                    filterState = filterState,
                    userProfile = userProfile,
                    isSubmitting = isSubmitting,
                    selectedListing = selectedListing,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    onCategoryChange = viewModel::updateCategoryFilter,
                    onListingTypeChange = viewModel::updateListingTypeFilter,
                    onDeliveryFilterChange = viewModel::updateDeliveryFilter,
                    onLocationFilterChange = viewModel::updateLocationFilter,
                    onMaxPriceChange = viewModel::updateMaxPriceFilter,
                    onMinQuantityChange = viewModel::updateMinQuantityFilter,
                    onSortByChange = viewModel::updateSortBy,
                    onClearFilters = viewModel::clearAllFilters,
                    onSelectListing = viewModel::openListingDetail,
                    onCloseListingDetail = viewModel::closeListingDetail,
                    onToggleSaveListing = viewModel::toggleSavedListing,
                    onRequestQuotation = { listing, qty, deliveryPref, msg ->
                      viewModel.requestQuotation(
                        listing = listing,
                        quantityTonnes = qty,
                        deliveryPreference = deliveryPref,
                        buyerMessage = msg,
                        onSuccess = {
                          viewModel.closeListingDetail()
                          viewModel.navigateTo(NexcoreDestination.TRACK_ORDERS)
                        }
                      )
                    },
                    onReportListing = { listing, reason, details ->
                      viewModel.submitReport(
                        targetType = "Listing",
                        targetId = listing.id,
                        targetTitle = listing.title,
                        reason = reason,
                        details = details
                      )
                    }
                  )
                }

                NexcoreDestination.SELL_WASTE -> {
                  SellWasteScreen(
                    isSubmitting = isSubmitting,
                    onPublishListing = { type, title, cat, desc, specs, qty, price, priceType, loc, date, deliv, transp, contact, imgKey ->
                      viewModel.publishNewListing(
                        listingType = type,
                        title = title,
                        category = cat,
                        description = desc,
                        conditionSpecs = specs,
                        quantityTonnes = qty,
                        pricePerTonne = price,
                        priceType = priceType,
                        location = loc,
                        preferredCollectionDate = date,
                        deliveryOptions = deliv,
                        transportIncluded = transp,
                        contactPreference = contact,
                        imageKey = imgKey,
                        onSuccess = {
                          viewModel.navigateTo(NexcoreDestination.BUY_MATERIALS, "All", "ALL")
                        }
                      )
                    },
                    onNavigateToCrusher = {
                      viewModel.navigateTo(NexcoreDestination.BOOK_CRUSHER)
                    }
                  )
                }

                NexcoreDestination.BOOK_CRUSHER -> {
                  BookCrusherScreen(
                    userProfile = userProfile,
                    bookings = crusherBookings,
                    isSubmitting = isSubmitting,
                    onSubmitBooking = viewModel::submitCrusherBooking
                  )
                }

                NexcoreDestination.TRACK_ORDERS -> {
                  TrackOrdersScreen(
                    buyerQuotations = buyerQuotations,
                    sellerQuotations = sellerQuotations,
                    crusherBookings = crusherBookings,
                    onUpdateQuotationStatus = viewModel::updateQuotationStatus,
                    onUpdateBookingStatus = viewModel::updateCrusherBookingStatus,
                    onNavigate = { dest -> viewModel.navigateTo(dest) }
                  )
                }

                NexcoreDestination.DASHBOARD -> {
                  DashboardScreen(
                    userProfile = userProfile,
                    allListings = combinedListings,
                    sellerListings = sellerListings,
                    buyerQuotations = buyerQuotations,
                    sellerQuotations = sellerQuotations,
                    crusherBookings = crusherBookings,
                    userReports = userReports,
                    isSubmitting = isSubmitting,
                    onSaveProfile = viewModel::saveProfileUpdates,
                    onUpdateListingStatus = viewModel::updateListingStatus,
                    onDeleteListing = viewModel::deleteListing,
                    onSubmitSellerQuote = viewModel::submitSellerQuoteResponse,
                    onUpdateBookingStatus = viewModel::updateCrusherBookingStatus,
                    onSelectListing = { listing ->
                      viewModel.navigateTo(NexcoreDestination.BUY_MATERIALS)
                      viewModel.openListingDetail(listing)
                    },
                    onSignOut = onSignOut
                  )
                }
              }

              Spacer(modifier = Modifier.height(24.dp))
            }
          }
        }
      }
    }
  }
}

@Composable
private fun NexcoreTopGlassHeader(
  currentDestination: NexcoreDestination,
  userProfile: UserProfile?,
  mobileMenuExpanded: Boolean,
  onToggleMobileMenu: () -> Unit,
  onNavigate: (NexcoreDestination) -> Unit,
) {
  LiquidGlassCard(
    cornerRadius = 28.dp,
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier
            .weight(1f)
            .padding(end = 8.dp)
            .clickable { onNavigate(NexcoreDestination.HOME) }
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(SolidSoftWhite)
              .padding(6.dp),
            contentAlignment = Alignment.Center
          ) {
            Image(
              painter = painterResource(id = R.drawable.ic_nexcore_logo),
              contentDescription = "NEXCORE Logo",
              modifier = Modifier.fillMaxSize()
            )
          }
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "NEXCORE",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
              ),
              color = TextDark,
              maxLines = 1
            )
            Text(
              text = "Construction Waste. Renewed Value.",
              style = MaterialTheme.typography.labelMedium,
              color = TextSecondary,
              maxLines = 1,
              overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
          }
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          LiquidPrimaryButton(
            text = "Post",
            icon = Icons.Default.Add,
            onClick = { onNavigate(NexcoreDestination.SELL_WASTE) },
            testTag = "header_post_listing_button"
          )

          ClearGlassCircleButton(
            icon = if (mobileMenuExpanded) Icons.Default.Close else Icons.Default.Menu,
            contentDescription = "Navigation Menu",
            onClick = onToggleMobileMenu,
            testTag = "header_hamburger_menu_button"
          )
        }
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        NexcoreDestination.entries.forEach { dest ->
          LiquidGlassChip(
            text = dest.title,
            selected = currentDestination == dest,
            leadingIcon = dest.icon(),
            onClick = { onNavigate(dest) },
            testTag = "top_nav_chip_${dest.name.lowercase()}"
          )
        }
        if (userProfile != null) {
          StatusPillBadge(status = userProfile.accountType)
        }
      }
    }
  }
}

@Composable
private fun NexcoreBottomGlassBar(
  currentDestination: NexcoreDestination,
  onSelectDestination: (NexcoreDestination) -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .windowInsetsPadding(
        androidx.compose.foundation.layout.WindowInsets.safeDrawing.only(
          androidx.compose.foundation.layout.WindowInsetsSides.Bottom +
            androidx.compose.foundation.layout.WindowInsetsSides.Horizontal
        )
      )
      .padding(horizontal = 12.dp, vertical = 6.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .widthIn(max = 520.dp)
        .height(62.dp)
        .shadow(12.dp, RoundedCornerShape(31.dp), ambientColor = Color.Black.copy(alpha = 0.12f))
        .clip(RoundedCornerShape(31.dp))
        .background(SolidSoftWhite.copy(alpha = 0.92f))
        .border(1.5.dp, Color.White, RoundedCornerShape(31.dp))
        .padding(horizontal = 6.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      NexcoreDestination.entries.forEach { dest ->
        val selected = currentDestination == dest
        Column(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(24.dp))
            .clickable { onSelectDestination(dest) }
            .padding(vertical = 3.dp, horizontal = 2.dp)
            .testTag("bottom_nav_${dest.name.lowercase()}"),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(
                if (selected) PrimaryPurpleBrush else androidx.compose.ui.graphics.SolidColor(Color.Transparent)
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = dest.icon(),
              contentDescription = dest.title,
              tint = if (selected) TextLight else TextSecondary,
              modifier = Modifier.size(16.dp)
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = dest.shortLabel(),
            maxLines = 1,
            softWrap = false,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium.copy(
              fontSize = 10.sp,
              lineHeight = 11.sp,
              fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
              color = if (selected) AccentBluePurple else TextSecondary
            )
          )
        }
      }
    }
  }
}

@Composable
private fun NexcoreSideGlassRail(
  currentDestination: NexcoreDestination,
  onSelectDestination: (NexcoreDestination) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxHeight()
      .padding(start = 12.dp, top = 12.dp, bottom = 12.dp)
      .width(92.dp)
      .clip(RoundedCornerShape(28.dp))
      .background(Color.White.copy(alpha = 0.35f))
      .border(1.5.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(28.dp))
      .padding(vertical = 16.dp, horizontal = 8.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    NexcoreDestination.entries.forEach { dest ->
      val selected = currentDestination == dest
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(if (selected) SolidSoftWhite else Color.Transparent)
          .clickable { onSelectDestination(dest) }
          .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Icon(
          imageVector = dest.icon(),
          contentDescription = dest.title,
          tint = if (selected) AccentBluePurple else TextSecondary,
          modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = dest.shortLabel(),
          style = MaterialTheme.typography.labelMedium.copy(
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) TextDark else TextSecondary
          )
        )
      }
    }
  }
}

private fun NexcoreDestination.icon(): ImageVector = when (this) {
  NexcoreDestination.HOME -> Icons.Default.Home
  NexcoreDestination.BUY_MATERIALS -> Icons.Default.Storefront
  NexcoreDestination.SELL_WASTE -> Icons.Default.Recycling
  NexcoreDestination.BOOK_CRUSHER -> Icons.Default.Factory
  NexcoreDestination.TRACK_ORDERS -> Icons.Default.LocalShipping
  NexcoreDestination.DASHBOARD -> Icons.Default.Dashboard
}

private fun NexcoreDestination.shortLabel(): String = when (this) {
  NexcoreDestination.HOME -> "Home"
  NexcoreDestination.BUY_MATERIALS -> "Buy"
  NexcoreDestination.SELL_WASTE -> "Sell"
  NexcoreDestination.BOOK_CRUSHER -> "Crusher"
  NexcoreDestination.TRACK_ORDERS -> "Orders"
  NexcoreDestination.DASHBOARD -> "Account"
}
