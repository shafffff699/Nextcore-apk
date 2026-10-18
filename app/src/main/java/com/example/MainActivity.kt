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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.NexcoreRepository
import com.example.data.UiState
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
import com.example.ui.components.SoftWhiteRoundIconButton
import com.example.ui.screens.BookCrusherScreen
import com.example.ui.screens.BuyMaterialsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.SellWasteScreen
import com.example.ui.screens.TrackOrdersScreen
import com.example.ui.screens.UserDashboardsScreen
import com.example.ui.theme.AccentBluePurple
import com.example.ui.theme.NexcoreTheme
import com.example.ui.theme.PrimaryPurpleStart
import com.example.ui.theme.SolidSoftWhite
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextSecondary
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      NexcoreTheme {
        AppNavigation()
      }
    }
  }
}

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
  val listener = FirebaseAuth.AuthStateListener { auth ->
    trySend(auth.currentUser)
  }
  addAuthStateListener(listener)
  awaitClose { removeAuthStateListener(listener) }
}

@Composable
fun AppNavigation(auth: FirebaseAuth = Firebase.auth) {
  val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
  var pendingRegistrationDraft by remember { mutableStateOf<RegistrationDraft?>(null) }

  val user = currentUser
  if (user == null) {
    // Unauthenticated session: Render sign-in UI only. Feature ViewModels are NOT instantiated here.
    AuthScreen(
      onAuthSuccess = { draft ->
        pendingRegistrationDraft = draft
      }
    )
  } else {
    NexcoreMainApp(
      currentUserId = user.uid,
      registrationDraft = pendingRegistrationDraft
    )
  }
}

@Composable
fun NexcoreMainApp(
  currentUserId: String,
  registrationDraft: RegistrationDraft?,
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val credentialManager = remember { CredentialManager.create(context) }

  val viewModel: NexcoreViewModel = viewModel(
    key = currentUserId,
    factory = viewModelFactory {
      initializer {
        val app = checkNotNull(this[APPLICATION_KEY]) {
          "APPLICATION_KEY missing from CreationExtras"
        }
        val databaseId = app.getString(R.string.firestore_database_id)
        val db = FirebaseFirestore.getInstance(databaseId)
        NexcoreViewModel(NexcoreRepository(db), currentUserId)
      }
    }
  )

  LaunchedEffect(currentUserId, registrationDraft) {
    viewModel.ensureUserProfileInitialized(registrationDraft)
  }

  val currentDestination by viewModel.currentDestination.collectAsStateWithLifecycle()
  val selectedListing by viewModel.selectedListingForDetail.collectAsStateWithLifecycle()
  val filterState by viewModel.filterState.collectAsStateWithLifecycle()
  val combinedListings by viewModel.combinedListings.collectAsStateWithLifecycle()
  val userProfileState by viewModel.userProfileState.collectAsStateWithLifecycle()
  val sellerListingsState by viewModel.sellerListingsState.collectAsStateWithLifecycle()
  val crusherBookingsState by viewModel.crusherBookingsState.collectAsStateWithLifecycle()
  val buyerQuotationsState by viewModel.buyerQuotationsState.collectAsStateWithLifecycle()
  val sellerQuotationsState by viewModel.sellerQuotationsState.collectAsStateWithLifecycle()
  val userReportsState by viewModel.userReportsState.collectAsStateWithLifecycle()
  val bannerMessage by viewModel.bannerMessage.collectAsStateWithLifecycle()
  val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()

  val userProfile = (userProfileState as? UiState.Success)?.data
  val sellerListings = (sellerListingsState as? UiState.Success)?.data ?: emptyList()
  val crusherBookings = (crusherBookingsState as? UiState.Success)?.data ?: emptyList()
  val buyerQuotations = (buyerQuotationsState as? UiState.Success)?.data ?: emptyList()
  val sellerQuotations = (sellerQuotationsState as? UiState.Success)?.data ?: emptyList()
  val userReports = (userReportsState as? UiState.Success)?.data ?: emptyList()

  // Mandatory BackHandler on secondary/sub-screens
  BackHandler(enabled = selectedListing != null || currentDestination != NexcoreDestination.HOME) {
    if (selectedListing != null) {
      viewModel.closeListingDetail()
    } else {
      viewModel.navigateTo(NexcoreDestination.HOME)
    }
  }

  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
      ModalDrawerSheet(
        drawerContainerColor = SolidSoftWhite
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Image(
              painter = painterResource(id = R.drawable.ic_nexcore_logo),
              contentDescription = "NEXCORE Logo",
              modifier = Modifier.size(36.dp)
            )
            Column {
              Text(
                text = "NEXCORE",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextDark
              )
              Text(
                text = "Construction Waste. Renewed Value.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          NexcoreDestination.entries.forEach { dest ->
            NavigationDrawerItem(
              label = { Text(dest.title) },
              icon = { Icon(destinationIcon(dest), contentDescription = dest.title) },
              selected = currentDestination == dest && selectedListing == null,
              onClick = {
                viewModel.navigateTo(dest)
                coroutineScope.launch { drawerState.close() }
              },
              modifier = Modifier.testTag("drawer_nav_${dest.name.lowercase()}")
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          LiquidPrimaryButton(
            text = "Post a Listing",
            icon = Icons.Default.Add,
            onClick = {
              viewModel.navigateTo(NexcoreDestination.SELL_WASTE)
              coroutineScope.launch { drawerState.close() }
            },
            modifier = Modifier.fillMaxWidth(),
            testTag = "drawer_post_listing_btn"
          )

          Spacer(modifier = Modifier.height(8.dp))

          NavigationDrawerItem(
            label = { Text("Sign Out") },
            icon = { Icon(Icons.Default.Logout, contentDescription = "Sign Out") },
            selected = false,
            onClick = {
              coroutineScope.launch { drawerState.close() }
              signOut(
                context = context,
                credentialManager = credentialManager,
                onSignOutComplete = {},
                scope = coroutineScope
              )
            },
            modifier = Modifier.testTag("drawer_sign_out_btn")
          )
        }
      }
    }
  ) {
    LiquidGlassBackground {
      BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isExpandedScreen = maxWidth >= 720.dp

        Scaffold(
          containerColor = Color.Transparent,
          contentWindowInsets = WindowInsets.safeDrawing,
          topBar = {
            NexcoreLiquidTopBar(
              userName = userProfile?.fullName ?: "Member",
              userRole = userProfile?.accountType ?: "Buyer",
              onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
              onLogoClick = { viewModel.navigateTo(NexcoreDestination.HOME) },
              onPostListingClick = { viewModel.navigateTo(NexcoreDestination.SELL_WASTE) },
              onAccountClick = { viewModel.navigateTo(NexcoreDestination.DASHBOARD) }
            )
          },
          bottomBar = {
            if (!isExpandedScreen) {
              NexcoreLiquidBottomBar(
                currentDestination = currentDestination,
                onNavigate = { viewModel.navigateTo(it) }
              )
            }
          }
        ) { innerPadding ->
          Row(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            if (isExpandedScreen) {
              NavigationRail(
                containerColor = Color.White.copy(alpha = 0.45f),
                modifier = Modifier.fillMaxHeight()
              ) {
                NexcoreDestination.entries.forEach { dest ->
                  NavigationRailItem(
                    selected = currentDestination == dest && selectedListing == null,
                    onClick = { viewModel.navigateTo(dest) },
                    icon = { Icon(destinationIcon(dest), contentDescription = dest.title) },
                    label = { Text(dest.title, style = MaterialTheme.typography.labelMedium) }
                  )
                }
              }
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
                  .widthIn(max = 840.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
              ) {
                // Notification / Action Feedback Banner
                AnimatedVisibility(visible = bannerMessage != null) {
                  if (bannerMessage != null) {
                    LiquidGlassCard(cornerRadius = 18.dp) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(
                          text = bannerMessage ?: "",
                          style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextDark,
                            fontWeight = FontWeight.SemiBold
                          ),
                          modifier = Modifier.weight(1f)
                        )
                        ClearGlassCircleButton(
                          icon = Icons.Default.Close,
                          contentDescription = "Dismiss notification",
                          onClick = { viewModel.clearBannerMessage() }
                        )
                      }
                    }
                  }
                }

                if (selectedListing != null) {
                  val item = selectedListing!!
                  ProductDetailScreen(
                    listing = item,
                    isSaved = userProfile?.savedListingIds?.contains(item.id) == true,
                    onBack = { viewModel.closeListingDetail() },
                    onToggleSaved = { viewModel.toggleSavedListing(item.id) },
                    onRequestQuote = { qty, pref, msg ->
                      viewModel.requestQuotation(item, qty, pref, msg) {
                        viewModel.closeListingDetail()
                        viewModel.navigateTo(NexcoreDestination.TRACK_ORDERS)
                      }
                    },
                    onReportListing = { reason, details ->
                      viewModel.submitReport(
                        targetType = "Listing",
                        targetId = item.id,
                        targetTitle = item.title,
                        reason = reason,
                        details = details
                      )
                    }
                  )
                } else {
                  when (currentDestination) {
                    NexcoreDestination.HOME -> HomeScreen(
                      featuredListings = combinedListings,
                      searchQuery = filterState.searchQuery,
                      onSearchQueryChange = viewModel::updateSearchQuery,
                      onNavigate = { dest, cat, type ->
                        viewModel.navigateTo(dest, cat, type)
                      },
                      onSelectListing = viewModel::openListingDetail
                    )

                    NexcoreDestination.BUY_MATERIALS -> BuyMaterialsScreen(
                      listings = combinedListings,
                      filterState = filterState,
                      savedListingIds = userProfile?.savedListingIds ?: emptyList(),
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
                      onToggleSaved = viewModel::toggleSavedListing,
                      onRequestQuote = { listing, qty, pref, msg ->
                        viewModel.requestQuotation(listing, qty, pref, msg) {
                          viewModel.navigateTo(NexcoreDestination.TRACK_ORDERS)
                        }
                      },
                      onReportItem = viewModel::submitReport
                    )

                    NexcoreDestination.SELL_WASTE -> SellWasteScreen(
                      isSubmitting = isSubmitting,
                      onPublishListing = viewModel::publishNewListing,
                      onNavigateToDashboard = {
                        viewModel.navigateTo(NexcoreDestination.DASHBOARD)
                      }
                    )

                    NexcoreDestination.BOOK_CRUSHER -> BookCrusherScreen(
                      isSubmitting = isSubmitting,
                      defaultCustomerName = userProfile?.companyName?.takeIf { it.isNotBlank() }
                        ?: userProfile?.fullName
                        ?: "",
                      defaultPhone = userProfile?.phone ?: "",
                      defaultCity = userProfile?.city ?: "",
                      onSubmitBooking = viewModel::submitCrusherBooking,
                      onViewTrackOrders = {
                        viewModel.navigateTo(NexcoreDestination.TRACK_ORDERS)
                      }
                    )

                    NexcoreDestination.TRACK_ORDERS -> TrackOrdersScreen(
                      buyerQuotations = buyerQuotations,
                      sellerQuotations = sellerQuotations,
                      crusherBookings = crusherBookings,
                      onUpdateQuotationStatus = viewModel::updateQuotationStatus,
                      onUpdateBookingStatus = viewModel::updateCrusherBookingStatus,
                      onNavigate = { viewModel.navigateTo(it) }
                    )

                    NexcoreDestination.DASHBOARD -> UserDashboardsScreen(
                      userProfile = userProfile,
                      allListings = combinedListings,
                      sellerListings = sellerListings,
                      buyerQuotations = buyerQuotations,
                      sellerQuotations = sellerQuotations,
                      crusherBookings = crusherBookings,
                      userReports = userReports,
                      onSaveProfile = viewModel::saveProfileUpdates,
                      onUpdateListingStatus = viewModel::updateListingStatus,
                      onDeleteListing = viewModel::deleteListing,
                      onSubmitSellerQuote = viewModel::submitSellerQuoteResponse,
                      onUpdateBookingStatus = viewModel::updateCrusherBookingStatus,
                      onNavigate = { viewModel.navigateTo(it) },
                      onSignOut = {
                        signOut(
                          context = context,
                          credentialManager = credentialManager,
                          onSignOutComplete = {},
                          scope = coroutineScope
                        )
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
}

@Composable
private fun NexcoreLiquidTopBar(
  userName: String,
  userRole: String,
  onOpenDrawer: () -> Unit,
  onLogoClick: () -> Unit,
  onPostListingClick: () -> Unit,
  onAccountClick: () -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .windowInsetsPadding(WindowInsets.statusBars)
      .padding(horizontal = 14.dp, vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .shadow(10.dp, RoundedCornerShape(50), ambientColor = Color.Black.copy(alpha = 0.08f))
        .clip(RoundedCornerShape(50))
        .background(Color.White.copy(alpha = 0.68f))
        .border(1.5.dp, Color.White.copy(alpha = 0.9f), RoundedCornerShape(50))
        .padding(horizontal = 10.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left: Hamburger menu + NEXCORE Wordmark & Logo
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ClearGlassCircleButton(
          icon = Icons.Default.Menu,
          contentDescription = "Open Navigation Menu",
          onClick = onOpenDrawer,
          testTag = "top_bar_hamburger_button"
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onLogoClick)
            .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(SolidSoftWhite)
              .border(1.dp, Color.White, RoundedCornerShape(10.dp))
              .padding(4.dp),
            contentAlignment = Alignment.Center
          ) {
            Image(
              painter = painterResource(id = R.drawable.ic_nexcore_logo),
              contentDescription = "NEXCORE Logo",
              modifier = Modifier.fillMaxSize()
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "NEXCORE",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
              ),
              color = TextDark
            )
            Text(
              text = "C&D Marketplace",
              style = MaterialTheme.typography.labelMedium,
              color = TextSecondary
            )
          }
        }
      }

      // Right: Prominent "Post a Listing" + User Account Chip
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        SoftWhiteRoundIconButton(
          icon = Icons.Default.Add,
          contentDescription = "Post a Listing",
          onClick = onPostListingClick,
          testTag = "top_bar_post_listing_btn"
        )
        LiquidGlassChip(
          text = userName.split(" ").firstOrNull()?.take(10) ?: "Account",
          isSuccessChip = true,
          onClick = onAccountClick,
          testTag = "top_bar_account_chip"
        )
      }
    }
  }
}

@Composable
private fun NexcoreLiquidBottomBar(
  currentDestination: NexcoreDestination,
  onNavigate: (NexcoreDestination) -> Unit,
) {
  NavigationBar(
    containerColor = Color.White.copy(alpha = 0.78f),
    tonalElevation = 8.dp,
    modifier = Modifier
      .fillMaxWidth()
      .windowInsetsPadding(WindowInsets.navigationBars)
  ) {
    NexcoreDestination.entries.forEach { dest ->
      val selected = currentDestination == dest
      NavigationBarItem(
        selected = selected,
        onClick = { onNavigate(dest) },
        icon = {
          Icon(
            imageVector = destinationIcon(dest),
            contentDescription = dest.title
          )
        },
        label = {
          Text(
            text = when (dest) {
              NexcoreDestination.BUY_MATERIALS -> "Buy"
              NexcoreDestination.SELL_WASTE -> "Sell"
              NexcoreDestination.BOOK_CRUSHER -> "Crusher"
              NexcoreDestination.TRACK_ORDERS -> "Orders"
              else -> dest.title
            },
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
          )
        },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = AccentBluePurple,
          selectedTextColor = AccentBluePurple,
          indicatorColor = PrimaryPurpleStart.copy(alpha = 0.18f),
          unselectedIconColor = TextSecondary,
          unselectedTextColor = TextSecondary
        ),
        modifier = Modifier.testTag("bottom_nav_${dest.name.lowercase()}")
      )
    }
  }
}

private fun destinationIcon(destination: NexcoreDestination): ImageVector =
  when (destination) {
    NexcoreDestination.HOME -> Icons.Default.Home
    NexcoreDestination.BUY_MATERIALS -> Icons.Default.ShoppingBag
    NexcoreDestination.SELL_WASTE -> Icons.Default.Recycling
    NexcoreDestination.BOOK_CRUSHER -> Icons.Default.Factory
    NexcoreDestination.TRACK_ORDERS -> Icons.Default.LocalShipping
    NexcoreDestination.DASHBOARD -> Icons.Default.Dashboard
  }
