package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MarketplaceListing
import com.example.data.UserProfile
import com.example.ui.NexcoreDestination
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidGlassChip
import com.example.ui.components.LiquidGlassSearchBar
import com.example.ui.components.LiquidPrimaryButton
import com.example.ui.components.LiquidSecondaryButton
import com.example.ui.components.MainGlassPanel
import com.example.ui.components.MaterialVisualBanner
import com.example.ui.components.StatusPillBadge
import com.example.ui.theme.AccentBluePurple
import com.example.ui.theme.SolidSoftWhite
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
  listings: List<MarketplaceListing>,
  searchQuery: String,
  userProfile: UserProfile?,
  onSearchQueryChange: (String) -> Unit,
  onNavigate: (NexcoreDestination, String?, String?) -> Unit,
  onSelectListing: (MarketplaceListing) -> Unit,
  onToggleSaveListing: (String) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("home_screen_container"),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    // Concise Quick Actions + Search Bar inside Liquid Glass Panel
    MainGlassPanel(
      title = "Explore NEXCORE Marketplace",
      subtitle = "Find graded recycled aggregates, list C&D debris, or book on-site mobile crushing"
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        LiquidGlassSearchBar(
          query = searchQuery,
          onQueryChange = onSearchQueryChange,
          placeholder = "Search RCA 20mm, crushed brick, steel scrap, or site location...",
          onSearchAction = {
            onNavigate(NexcoreDestination.BUY_MATERIALS, null, null)
          },
          testTag = "home_search_bar"
        )

        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          LiquidPrimaryButton(
            text = "Buy Materials",
            icon = Icons.Default.Storefront,
            onClick = { onNavigate(NexcoreDestination.BUY_MATERIALS, "All", "MATERIAL") },
            testTag = "home_cta_buy_materials"
          )
          LiquidSecondaryButton(
            text = "Sell Waste",
            icon = Icons.Default.AddBusiness,
            onClick = { onNavigate(NexcoreDestination.SELL_WASTE, null, null) },
            testTag = "home_cta_sell_waste"
          )
          LiquidGlassChip(
            text = "Book Mobile Crusher",
            leadingIcon = Icons.Default.Factory,
            onClick = { onNavigate(NexcoreDestination.BOOK_CRUSHER, null, null) },
            testTag = "home_cta_book_crusher"
          )
          LiquidGlassChip(
            text = "Explore Nearby Listings",
            leadingIcon = Icons.Default.LocationOn,
            onClick = { onNavigate(NexcoreDestination.BUY_MATERIALS, "All", "ALL") },
            testTag = "home_cta_explore_nearby"
          )
        }
      }
    }

    // Featured C&D Listings
    MainGlassPanel(
      title = "Featured C&D Listings",
      subtitle = "Verified recycled materials and segregated demolition debris ready for quote or pickup",
      headerTrailing = {
        LiquidGlassChip(
          text = "View All (${listings.size})",
          onClick = { onNavigate(NexcoreDestination.BUY_MATERIALS, "All", "ALL") },
          testTag = "home_view_all_listings_chip"
        )
      }
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        listings.take(4).forEach { listing ->
          val isSaved = userProfile?.savedListingIds?.contains(listing.id) == true
          MarketplaceListingCard(
            listing = listing,
            isSaved = isSaved,
            onCardClick = { onSelectListing(listing) },
            onToggleSave = { onToggleSaveListing(listing.id) }
          )
        }
      }
    }

    // Concise Mobile Crusher Service Card
    LiquidGlassCard(
      cornerRadius = 24.dp,
      iridescentBorder = true
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
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
              contentDescription = "NEXCORE Emblem"
            )
          }
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "On-Site Mobile Crusher Service",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = TextDark
            )
            Text(
              text = "Jaw & Impact Crushing • Magnetic Separation • Multi-Deck Screening",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          LiquidPrimaryButton(
            text = "Book Crusher",
            icon = Icons.Default.Factory,
            onClick = { onNavigate(NexcoreDestination.BOOK_CRUSHER, null, null) },
            modifier = Modifier.weight(1f),
            testTag = "highlight_book_crusher_button"
          )
          LiquidSecondaryButton(
            text = "Post Waste",
            icon = Icons.Default.Eco,
            onClick = { onNavigate(NexcoreDestination.SELL_WASTE, null, null) },
            modifier = Modifier.weight(1f),
            testTag = "highlight_post_waste_button"
          )
        }
      }
    }
  }
}

@Composable
fun MarketplaceListingCard(
  listing: MarketplaceListing,
  isSaved: Boolean,
  onCardClick: () -> Unit,
  onToggleSave: () -> Unit,
  modifier: Modifier = Modifier,
) {
  LiquidGlassCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("listing_card_${listing.id}"),
    cornerRadius = 28.dp,
    onClick = onCardClick
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
          Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            StatusPillBadge(status = if (listing.listingType == "WASTE") "Debris / Waste" else "Recycled Material")
            StatusPillBadge(status = listing.priceType)
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = listing.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextDark,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }

        IconButton(
          onClick = onToggleSave,
          modifier = Modifier.testTag("save_listing_btn_${listing.id}")
        ) {
          Icon(
            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
            contentDescription = if (isSaved) "Remove from saved" else "Save listing",
            tint = if (isSaved) AccentBluePurple else TextSecondary
          )
        }
      }

      Text(
        text = listing.description,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "₹${String.format("%,.0f", listing.pricePerTonne)} ${listing.unit}",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.ExtraBold,
              fontSize = 18.sp
            ),
            color = AccentBluePurple
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Scale,
              contentDescription = null,
              tint = TextMuted,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${listing.quantityTonnes.toInt()} tonnes available",
              style = MaterialTheme.typography.labelMedium,
              color = TextSecondary
            )
          }
        }

        Column(horizontalAlignment = Alignment.End) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = AccentBluePurple,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = listing.location,
              style = MaterialTheme.typography.labelMedium,
              color = TextDark,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            if (listing.sellerVerified) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Verified Seller",
                tint = SuccessGreen,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
              text = listing.sellerName,
              style = MaterialTheme.typography.bodySmall,
              color = TextMuted,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Delivery: ${listing.deliveryOptions}",
          style = MaterialTheme.typography.labelMedium,
          color = TextSecondary
        )
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(SolidSoftWhite)
            .border(1.dp, Color.White, RoundedCornerShape(50))
            .clickable(onClick = onCardClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.RequestQuote,
            contentDescription = null,
            tint = AccentBluePurple,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Inspect & Quote",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = TextDark
          )
        }
      }
    }
  }
}
