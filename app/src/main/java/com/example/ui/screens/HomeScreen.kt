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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.MarketplaceListing
import com.example.data.SampleMarketplaceData
import com.example.ui.NexcoreDestination
import com.example.ui.components.ClearGlassCircleButton
import com.example.ui.components.LiquidFeatureGradientCard
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidGlassChip
import com.example.ui.components.LiquidPrimaryButton
import com.example.ui.components.LiquidSecondaryButton
import com.example.ui.components.MainGlassPanel
import com.example.ui.components.MaterialVisualBanner
import com.example.ui.components.StatusPillBadge
import com.example.ui.theme.AccentBluePurple
import com.example.ui.theme.IridescentBorderBrush
import com.example.ui.theme.PrimaryPurpleBrush
import com.example.ui.theme.SecondaryTealStart
import com.example.ui.theme.SolidSoftWhite
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
  featuredListings: List<MarketplaceListing>,
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  onNavigate: (NexcoreDestination, String?, String?) -> Unit,
  onSelectListing: (MarketplaceListing) -> Unit,
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(20.dp)
  ) {
    // 1. HERO SECTION (Liquid Glass Feature Banner + Primary CTAs)
    MainGlassPanel(
      title = "Give Waste a Second Life.",
      subtitle = "Construction Waste. Renewed Value."
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        MaterialVisualBanner(
          imageKey = "mobile_crusher",
          category = "C&D Waste Management Digital Marketplace",
          isSample = false
        )

        Text(
          text = "Buy recycled construction materials, sell construction debris, or book mobile crushing services through one platform.",
          style = MaterialTheme.typography.bodyLarge,
          color = TextDark
        )

        // Primary Hero Buttons
        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          LiquidPrimaryButton(
            text = "Buy Materials",
            icon = Icons.Default.ShoppingBag,
            onClick = { onNavigate(NexcoreDestination.BUY_MATERIALS, "All", "MATERIAL") },
            testTag = "hero_buy_materials_button"
          )
          LiquidSecondaryButton(
            text = "Sell Waste",
            icon = Icons.Default.Recycling,
            onClick = { onNavigate(NexcoreDestination.SELL_WASTE, null, null) },
            testTag = "hero_sell_waste_button"
          )
          LiquidPrimaryButton(
            text = "Book Mobile Crusher",
            icon = Icons.Default.Factory,
            onClick = { onNavigate(NexcoreDestination.BOOK_CRUSHER, null, null) },
            testTag = "hero_book_crusher_button"
          )
        }

        // Secondary Action: Explore Nearby Listings
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          LiquidGlassChip(
            text = "Explore Nearby Listings",
            leadingIcon = Icons.Default.Explore,
            isSuccessChip = true,
            onClick = { onNavigate(NexcoreDestination.BUY_MATERIALS, "All", "ALL") },
            testTag = "hero_explore_nearby_chip"
          )
          Text(
            text = "${featuredListings.size} Active / Sample Listings",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
        }
      }
    }

    // 2. COMPONENT #5: Iridescent Top-Right Style Glass Discovery Card
    LiquidGlassCard(
      iridescentBorder = true,
      cornerRadius = 24.dp
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // App logo in a white rounded square
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SolidSoftWhite)
                .border(1.dp, Color.White, RoundedCornerShape(12.dp))
                .padding(6.dp),
              contentAlignment = Alignment.Center
            ) {
              Image(
                painter = painterResource(id = R.drawable.ic_nexcore_logo),
                contentDescription = "NEXCORE Logo",
                modifier = Modifier.size(28.dp)
              )
            }
            Column {
              Text(
                text = "Quick Material Discovery",
                style = MaterialTheme.typography.titleMedium,
                color = TextDark
              )
              Text(
                text = "Search aggregates, concrete debris, or city",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
            }
          }

          // Ring-style icon button with rainbow border
          ClearGlassCircleButton(
            icon = Icons.Default.Explore,
            contentDescription = "Browse Marketplace",
            iridescentRing = true,
            onClick = { onNavigate(NexcoreDestination.BUY_MATERIALS, "All", "ALL") }
          )
        }

        // White pill search field + purple circle "+" button at its right
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .clip(RoundedCornerShape(50))
              .background(SolidSoftWhite)
              .border(1.dp, Color.White, RoundedCornerShape(50))
              .clickable { onNavigate(NexcoreDestination.BUY_MATERIALS, "All", "ALL") }
              .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = null,
              tint = TextMuted,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = searchQuery.ifBlank { "Search projects, 20mm RCA, crushed brick..." },
              style = MaterialTheme.typography.bodyMedium,
              color = if (searchQuery.isBlank()) TextMuted else TextDark
            )
          }

          // Purple circle "+" button at its right (Post a Listing shortcut)
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(brush = PrimaryPurpleBrush)
              .border(1.dp, Color.White.copy(alpha = 0.8f), CircleShape)
              .clickable { onNavigate(NexcoreDestination.SELL_WASTE, null, null) }
              .testTag("quick_post_listing_circle_btn"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Post a Listing",
              tint = TextLight,
              modifier = Modifier.size(22.dp)
            )
          }
        }
      }
    }

    // 3. SECTION A: HOW NEXCORE WORKS
    MainGlassPanel(
      title = "How NEXCORE Works",
      subtitle = "End-to-end circular C&D material recovery workflow"
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        val steps = listOf(
          Triple(
            "Step 1",
            "List or Search",
            "List construction waste or search for graded recycled materials by location."
          ),
          Triple(
            "Step 2",
            "Connect Directly",
            "Connect with verified buyers, demolition sellers, or recycling service providers."
          ),
          Triple(
            "Step 3",
            "Quotations & Mobile Crushing",
            "Request an itemized quotation or book on-site mobile crushing and screening services."
          ),
          Triple(
            "Step 4",
            "Recover & Coordinate",
            "Process suitable segregated debris and coordinate pickup or site delivery."
          )
        )
        steps.forEach { (badge, heading, desc) ->
          LiquidGlassCard(cornerRadius = 18.dp) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              LiquidGlassChip(text = badge, selected = true)
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = heading,
                  style = MaterialTheme.typography.titleMedium,
                  color = TextDark
                )
                Text(
                  text = desc,
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondary
                )
              }
            }
          }
        }
      }
    }

    // 4. SECTION B: MARKETPLACE CATEGORIES
    MainGlassPanel(
      title = "Marketplace Categories",
      subtitle = "Tap any category to browse available materials or services"
    ) {
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        SampleMarketplaceData.materialCategories.forEach { category ->
          val icon = when {
            category.contains("Crushing") -> Icons.Default.Factory
            category.contains("Metal") -> Icons.Default.Hardware
            category.contains("Brick") -> Icons.Default.Construction
            category.contains("Road") -> Icons.Default.LocalShipping
            else -> Icons.Default.Landscape
          }
          LiquidGlassChip(
            text = category,
            leadingIcon = icon,
            onClick = {
              if (category == "Mobile Crushing Services") {
                onNavigate(NexcoreDestination.BOOK_CRUSHER, null, null)
              } else {
                onNavigate(NexcoreDestination.BUY_MATERIALS, category, "ALL")
              }
            },
            testTag = "category_card_${category.take(10).lowercase().replace(" ", "_")}"
          )
        }
      }
    }

    // 5. SECTION C: FEATURED LISTINGS (Clearly labeled Sample Data + Live Data)
    MainGlassPanel(
      title = "Featured Marketplace Listings",
      subtitle = "Demonstration listings are clearly marked as Sample Listing until replaced by live user inventory"
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        featuredListings.take(4).forEach { listing ->
          FeaturedListingCard(
            listing = listing,
            onViewDetails = { onSelectListing(listing) }
          )
        }

        LiquidSecondaryButton(
          text = "View All Marketplace Listings",
          icon = Icons.Default.ShoppingBag,
          onClick = { onNavigate(NexcoreDestination.BUY_MATERIALS, "All", "ALL") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    // 6. SECTION D: MOBILE CRUSHER SERVICE (Component #12 Feature Card)
    LiquidFeatureGradientCard(
      title = "On-Site Mobile Crusher & Screening Service",
      subtitle = "Request mobile jaw crushing and screening for suitable, segregated concrete and masonry debris at or near your site — subject to site access, equipment availability, and service feasibility.",
      badgeText = "Proposed Service",
      buttonText = "Book a Mobile Crusher",
      onButtonClick = { onNavigate(NexcoreDestination.BOOK_CRUSHER, null, null) },
      testTag = "home_book_crusher_feature_card"
    )

    // 7. SECTION E: WHY NEXCORE?
    MainGlassPanel(
      title = "Why NEXCORE?",
      subtitle = "Built for construction engineers, demolition contractors & recyclers"
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        val benefits = listOf(
          "Convenient digital material discovery with clear grading & condition disclosures.",
          "Direct connections between C&D waste generators and recycled aggregate buyers.",
          "Streamlined coordination of on-site mobile crushing and screening requests.",
          "Transparent quotation breakdowns separating material, transport, and processing fees.",
          "Potential reduction in unnecessary haulage by matching nearby construction sites."
        )
        benefits.forEach { point ->
          Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = SuccessGreen,
              modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
            )
            Text(
              text = point,
              style = MaterialTheme.typography.bodyMedium,
              color = TextDark
            )
          }
        }
      }
    }

    // 8. SECTION F: CALL TO ACTION
    MainGlassPanel(
      title = "Turn Construction Waste into a Resource.",
      subtitle = "Start listing C&D debris or sourcing recycled aggregates today."
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        LiquidPrimaryButton(
          text = "List Your Waste",
          icon = Icons.Default.Recycling,
          onClick = { onNavigate(NexcoreDestination.SELL_WASTE, null, null) },
          modifier = Modifier.weight(1f),
          testTag = "cta_list_waste_button"
        )
        LiquidSecondaryButton(
          text = "Explore Materials",
          icon = Icons.Default.ShoppingBag,
          onClick = { onNavigate(NexcoreDestination.BUY_MATERIALS, "All", "MATERIAL") },
          modifier = Modifier.weight(1f),
          testTag = "cta_explore_materials_button"
        )
      }
    }
  }
}

@Composable
private fun FeaturedListingCard(
  listing: MarketplaceListing,
  onViewDetails: () -> Unit,
) {
  LiquidGlassCard(
    cornerRadius = 22.dp,
    onClick = onViewDetails
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
          Text(
            text = listing.title,
            style = MaterialTheme.typography.titleLarge,
            color = TextDark
          )
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = AccentBluePurple,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${listing.location} • ${listing.sellerName}",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }
        }
        StatusPillBadge(status = listing.status)
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = String.format(Locale.US, "$%.2f %s", listing.pricePerTonne, listing.unit),
            style = MaterialTheme.typography.titleLarge.copy(
              color = AccentBluePurple,
              fontWeight = FontWeight.Bold
            )
          )
          Text(
            text = "${listing.quantityTonnes.toInt()} tonnes available • ${listing.priceType}",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
        }

        LiquidPrimaryButton(
          text = "View Details",
          onClick = onViewDetails,
          testTag = "featured_view_details_${listing.id}"
        )
      }
    }
  }
}
