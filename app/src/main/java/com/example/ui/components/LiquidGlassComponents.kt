package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AccentBluePurple
import com.example.ui.theme.BgCoolBlueGrey
import com.example.ui.theme.BgSoftLavenderGrey
import com.example.ui.theme.BgWarmCreamPink
import com.example.ui.theme.DangerCoral
import com.example.ui.theme.FeatureCardGradientBrush
import com.example.ui.theme.FeatureCyan
import com.example.ui.theme.FeatureLavender
import com.example.ui.theme.FeaturePink
import com.example.ui.theme.FeatureSkyBlue
import com.example.ui.theme.GlassBorderWhite
import com.example.ui.theme.GlassCardFill25
import com.example.ui.theme.GlassInnerShine
import com.example.ui.theme.GlassPanelFill
import com.example.ui.theme.GlassWhiteFill
import com.example.ui.theme.IridescentBorderBrush
import com.example.ui.theme.LiquidGlassBackdropBox
import com.example.ui.theme.LocalLiquidGlassSpec
import com.example.ui.theme.NexcoreEmerald
import com.example.ui.theme.PrimaryPurpleBrush
import com.example.ui.theme.PrimaryPurpleEnd
import com.example.ui.theme.PrimaryPurpleStart
import com.example.ui.theme.SecondaryTealBrush
import com.example.ui.theme.SecondaryTealEnd
import com.example.ui.theme.SecondaryTealStart
import com.example.ui.theme.SolidSoftWhite
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TopRightGlassCardBrush
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.liquidGlassSurface

/**
 * 0. Liquid Glass Background Canvas
 * Soft gradient from warm cream-green (top-left) to cool blue-grey (top-right and bottom),
 * with a blurred ambient light backdrop and subtle diagonal surface shadow.
 */
@Composable
fun LiquidGlassBackground(
  modifier: Modifier = Modifier,
  content: @Composable BoxScope.() -> Unit,
) {
  LiquidGlassBackdropBox(
    modifier = modifier,
    content = content
  )
}

/**
 * MAIN GLASS PANEL
 * Big frosted glass container, 28.dp rounded corners, rgba(255, 255, 255, 0.25) glassmorphism fill,
 * 1.5.dp white border with thin top highlight, soft shadow underneath,
 * and window chrome header.
 */
@Composable
fun MainGlassPanel(
  title: String,
  subtitle: String? = null,
  modifier: Modifier = Modifier,
  headerTrailing: (@Composable () -> Unit)? = null,
  content: @Composable () -> Unit,
) {
  val glassSpec = LocalLiquidGlassSpec.current
  Box(
    modifier = modifier
      .fillMaxWidth()
      .liquidGlassSurface(
        cornerRadius = glassSpec.surfaceCornerRadius, // 28.dp rounded corners
        backgroundColor = glassSpec.cardBackgroundColor, // rgba(255, 255, 255, 0.25)
        shadowElevation = 14.dp
      )
      .padding(18.dp)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = TextDark
          )
          if (!subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = subtitle,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }
        }
        if (headerTrailing != null) {
          headerTrailing()
        } else {
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(TextMuted.copy(alpha = 0.35f))
                .border(1.dp, Color.White.copy(alpha = 0.8f), CircleShape)
            )
            Icon(
              imageVector = Icons.Default.Remove,
              contentDescription = null,
              tint = TextMuted.copy(alpha = 0.65f),
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
      content()
    }
  }
}

/**
 * Frosted Glass Card (reusable liquid glass surface for items, forms, and panels)
 * Default 28.dp rounded corners, rgba(255, 255, 255, 0.25) glassmorphism card background,
 * and crisp white border.
 */
@Composable
fun LiquidGlassCard(
  modifier: Modifier = Modifier,
  cornerRadius: Dp = 28.dp,
  iridescentBorder: Boolean = false,
  onClick: (() -> Unit)? = null,
  content: @Composable () -> Unit,
) {
  val glassSpec = LocalLiquidGlassSpec.current
  val shape = RoundedCornerShape(cornerRadius)
  val borderBrush = if (iridescentBorder) {
    IridescentBorderBrush
  } else {
    Brush.verticalGradient(
      colors = listOf(
        glassSpec.borderHighlightColor,
        glassSpec.borderColor,
        Color.White.copy(alpha = 0.45f)
      )
    )
  }
  val fillBrush = if (iridescentBorder) {
    TopRightGlassCardBrush
  } else {
    Brush.verticalGradient(
      colors = listOf(
        glassSpec.cardBackgroundColor.copy(alpha = 0.32f),
        glassSpec.cardBackgroundColor, // rgba(255, 255, 255, 0.25)
        glassSpec.cardBackgroundColor.copy(alpha = 0.22f)
      )
    )
  }

  val clickableMod = if (onClick != null) {
    Modifier.clickable(onClick = onClick)
  } else {
    Modifier
  }

  Box(
    modifier = modifier
      .shadow(
        elevation = 10.dp,
        shape = shape,
        ambientColor = Color.Black.copy(alpha = 0.08f),
        spotColor = AccentBluePurple.copy(alpha = 0.12f)
      )
      .clip(shape)
      .background(brush = fillBrush, shape = shape)
      .border(width = glassSpec.borderWidth, brush = borderBrush, shape = shape)
      .then(clickableMod)
      .padding(16.dp)
  ) {
    content()
  }
}

/**
 * 1. Primary Button: full pill shape, purple gradient (#8B6CF0 to #A78BFA),
 * white text, glossy white shine on top edge, soft purple shadow below.
 */
@Composable
fun LiquidPrimaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  enabled: Boolean = true,
  testTag: String? = null,
) {
  val shape = RoundedCornerShape(50)
  val tagMod = if (testTag != null) Modifier.testTag(testTag) else Modifier
  Box(
    modifier = modifier
      .then(tagMod)
      .minimumInteractiveComponentSize()
      .shadow(
        elevation = if (enabled) 10.dp else 2.dp,
        shape = shape,
        ambientColor = PrimaryPurpleStart,
        spotColor = PrimaryPurpleStart
      )
      .clip(shape)
      .background(
        brush = if (enabled) PrimaryPurpleBrush else SolidColor(TextMuted.copy(alpha = 0.4f))
      )
      .border(
        width = 1.2.dp,
        brush = Brush.verticalGradient(
          colors = listOf(Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0.15f))
        ),
        shape = shape
      )
      .clickable(enabled = enabled, onClick = onClick)
      .padding(horizontal = 20.dp, vertical = 12.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = TextLight,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = TextLight,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

/**
 * 2. Secondary Button: full pill shape, teal gradient (#5CC8C0 to #8EE0D6),
 * white text, glossy shine on top edge, soft teal shadow below.
 */
@Composable
fun LiquidSecondaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  enabled: Boolean = true,
  testTag: String? = null,
) {
  val shape = RoundedCornerShape(50)
  val tagMod = if (testTag != null) Modifier.testTag(testTag) else Modifier
  Box(
    modifier = modifier
      .then(tagMod)
      .minimumInteractiveComponentSize()
      .shadow(
        elevation = if (enabled) 10.dp else 2.dp,
        shape = shape,
        ambientColor = SecondaryTealStart,
        spotColor = SecondaryTealStart
      )
      .clip(shape)
      .background(
        brush = if (enabled) SecondaryTealBrush else SolidColor(TextMuted.copy(alpha = 0.4f))
      )
      .border(
        width = 1.2.dp,
        brush = Brush.verticalGradient(
          colors = listOf(Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0.2f))
        ),
        shape = shape
      )
      .clickable(enabled = enabled, onClick = onClick)
      .padding(horizontal = 20.dp, vertical = 12.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = TextLight,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = TextLight,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

/**
 * 3. Round Icon Button: solid soft-white circle (#F8F5F7) with a "+" icon and soft drop shadow.
 */
@Composable
fun SoftWhiteRoundIconButton(
  icon: ImageVector = Icons.Default.Add,
  contentDescription: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String? = null,
) {
  val tagMod = if (testTag != null) Modifier.testTag(testTag) else Modifier
  Box(
    modifier = modifier
      .then(tagMod)
      .minimumInteractiveComponentSize()
      .size(48.dp)
      .shadow(8.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.12f))
      .clip(CircleShape)
      .background(SolidSoftWhite)
      .border(1.dp, Color.White, CircleShape)
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = contentDescription,
      tint = TextDark,
      modifier = Modifier.size(22.dp)
    )
  }
}

/**
 * 4. Clear Glass Circle Button: see-through glass bubble with a small icon, light border and inner shine.
 */
@Composable
fun ClearGlassCircleButton(
  icon: ImageVector,
  contentDescription: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  iridescentRing: Boolean = false,
  testTag: String? = null,
) {
  val tagMod = if (testTag != null) Modifier.testTag(testTag) else Modifier
  val borderBrush = if (iridescentRing) {
    IridescentBorderBrush
  } else {
    Brush.verticalGradient(
      colors = listOf(Color.White.copy(alpha = 0.9f), Color.White.copy(alpha = 0.35f))
    )
  }
  Box(
    modifier = modifier
      .then(tagMod)
      .minimumInteractiveComponentSize()
      .size(48.dp)
      .shadow(6.dp, CircleShape, ambientColor = AccentBluePurple.copy(alpha = 0.1f))
      .clip(CircleShape)
      .background(
        brush = Brush.radialGradient(
          colors = listOf(Color.White.copy(alpha = 0.55f), Color.White.copy(alpha = 0.22f))
        )
      )
      .border(width = 1.5.dp, brush = borderBrush, shape = CircleShape)
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = contentDescription,
      tint = TextDark,
      modifier = Modifier.size(20.dp)
    )
  }
}

/**
 * 6. Search Bar with Checkbox & Rainbow Arrow Button:
 * Glass pill, purple filled circle with a white tick on the left, search input,
 * and rainbow-border circle arrow button on the right.
 */
@Composable
fun LiquidGlassSearchBar(
  query: String,
  onQueryChange: (String) -> Unit,
  placeholder: String = "Search materials, waste, or city...",
  onSearchAction: () -> Unit = {},
  modifier: Modifier = Modifier,
  testTag: String = "marketplace_search_input",
) {
  val shape = RoundedCornerShape(50)
  Row(
    modifier = modifier
      .fillMaxWidth()
      .height(54.dp)
      .shadow(8.dp, shape, ambientColor = AccentBluePurple.copy(alpha = 0.12f))
      .clip(shape)
      .background(
        brush = Brush.horizontalGradient(
          colors = listOf(Color.White.copy(alpha = 0.72f), Color.White.copy(alpha = 0.48f))
        )
      )
      .border(
        width = 1.5.dp,
        brush = Brush.verticalGradient(
          colors = listOf(Color.White.copy(alpha = 0.9f), Color.White.copy(alpha = 0.4f))
        ),
        shape = shape
      )
      .padding(horizontal = 10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Purple filled circle with white tick on the left
    Box(
      modifier = Modifier
        .size(34.dp)
        .clip(CircleShape)
        .background(brush = PrimaryPurpleBrush),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Check,
        contentDescription = null,
        tint = TextLight,
        modifier = Modifier.size(18.dp)
      )
    }

    Spacer(modifier = Modifier.width(10.dp))

    Box(
      modifier = Modifier.weight(1f),
      contentAlignment = Alignment.CenterStart
    ) {
      if (query.isEmpty()) {
        Text(
          text = placeholder,
          style = MaterialTheme.typography.bodyMedium,
          color = TextMuted,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
      BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextDark),
        cursorBrush = SolidColor(AccentBluePurple),
        modifier = Modifier
          .fillMaxWidth()
          .testTag(testTag)
      )
    }

    Spacer(modifier = Modifier.width(8.dp))

    // Rainbow-border circle arrow button on the right
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape)
        .background(SolidSoftWhite)
        .border(width = 2.dp, brush = IridescentBorderBrush, shape = CircleShape)
        .clickable(onClick = onSearchAction),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = "Search",
        tint = TextDark,
        modifier = Modifier.size(18.dp)
      )
    }
  }
}

/**
 * 7. Soft-White Pill Input Field with label, placeholder, optional trailing icon, and soft shadow.
 */
@Composable
fun LiquidPillTextField(
  value: String,
  onValueChange: (String) -> Unit,
  label: String,
  placeholder: String,
  modifier: Modifier = Modifier,
  singleLine: Boolean = true,
  trailingIcon: ImageVector? = null,
  testTag: String? = null,
) {
  val shape = if (singleLine) RoundedCornerShape(50) else RoundedCornerShape(22.dp)
  val tagMod = if (testTag != null) Modifier.testTag(testTag) else Modifier
  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelMedium,
      color = TextDark,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
    )
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .shadow(6.dp, shape, ambientColor = Color.Black.copy(alpha = 0.08f))
        .clip(shape)
        .background(SolidSoftWhite.copy(alpha = 0.92f))
        .border(1.2.dp, Color.White, shape)
        .padding(horizontal = 16.dp, vertical = 13.dp),
      verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top
    ) {
      Box(
        modifier = Modifier.weight(1f),
        contentAlignment = Alignment.TopStart
      ) {
        if (value.isEmpty()) {
          Text(
            text = placeholder,
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted
          )
        }
        BasicTextField(
          value = value,
          onValueChange = onValueChange,
          singleLine = singleLine,
          textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextDark),
          cursorBrush = SolidColor(AccentBluePurple),
          modifier = Modifier
            .fillMaxWidth()
            .then(tagMod)
        )
      }
      if (trailingIcon != null) {
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
          imageVector = trailingIcon,
          contentDescription = null,
          tint = TextMuted,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}

/**
 * 8 & 10. Liquid Glass Chip & Success Chip
 * Small glass pill with text and an icon or green verification tick.
 */
@Composable
fun LiquidGlassChip(
  text: String,
  selected: Boolean = false,
  isSuccessChip: Boolean = false,
  leadingIcon: ImageVector? = null,
  onClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier,
  testTag: String? = null,
) {
  val shape = RoundedCornerShape(50)
  val tagMod = if (testTag != null) Modifier.testTag(testTag) else Modifier
  val bgBrush = when {
    selected -> PrimaryPurpleBrush
    else -> Brush.horizontalGradient(
      colors = listOf(Color.White.copy(alpha = 0.68f), Color.White.copy(alpha = 0.42f))
    )
  }
  val textColor = if (selected) TextLight else TextDark
  val clickMod = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier

  Row(
    modifier = modifier
      .then(tagMod)
      .shadow(4.dp, shape, ambientColor = Color.Black.copy(alpha = 0.06f))
      .clip(shape)
      .background(brush = bgBrush)
      .border(1.2.dp, Color.White.copy(alpha = 0.85f), shape)
      .then(clickMod)
      .padding(horizontal = 14.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
  ) {
    if (leadingIcon != null) {
      Icon(
        imageVector = leadingIcon,
        contentDescription = null,
        tint = if (selected) TextLight else AccentBluePurple,
        modifier = Modifier.size(15.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
    }
    Text(
      text = text,
      style = MaterialTheme.typography.bodySmall.copy(
        fontWeight = FontWeight.SemiBold,
        color = textColor
      )
    )
    if (isSuccessChip) {
      Spacer(modifier = Modifier.width(6.dp))
      Box(
        modifier = Modifier
          .size(16.dp)
          .clip(CircleShape)
          .background(SuccessGreen),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = "Verified",
          tint = TextLight,
          modifier = Modifier.size(11.dp)
        )
      }
    }
  }
}

/**
 * 9. Toggle Switch: glass pill track with a soft-white round knob holding a "+" icon.
 */
@Composable
fun LiquidGlassToggle(
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  label: String,
  modifier: Modifier = Modifier,
  testTag: String? = null,
) {
  val shape = RoundedCornerShape(50)
  val tagMod = if (testTag != null) Modifier.testTag(testTag) else Modifier
  Row(
    modifier = modifier
      .then(tagMod)
      .clip(shape)
      .clickable { onCheckedChange(!checked) }
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      color = TextDark,
      modifier = Modifier.weight(1f)
    )
    Spacer(modifier = Modifier.width(12.dp))
    Box(
      modifier = Modifier
        .width(56.dp)
        .height(32.dp)
        .clip(shape)
        .background(
          brush = if (checked) SecondaryTealBrush else Brush.horizontalGradient(
            listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0.3f))
          )
        )
        .border(1.2.dp, Color.White.copy(alpha = 0.8f), shape)
        .padding(3.dp),
      contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
      Box(
        modifier = Modifier
          .size(26.dp)
          .shadow(4.dp, CircleShape)
          .clip(CircleShape)
          .background(SolidSoftWhite),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (checked) Icons.Default.Check else Icons.Default.Add,
          contentDescription = null,
          tint = if (checked) SecondaryTealStart else AccentBluePurple,
          modifier = Modifier.size(15.dp)
        )
      }
    }
  }
}

/**
 * 11. Tabs / Segmented Control: glass pill background.
 * Active tab is a solid soft-white pill. Inactive tab is see-through with a short purple underline.
 */
@Composable
fun LiquidSegmentedTabs(
  tabs: List<String>,
  selectedIndex: Int,
  onTabSelected: (Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  val outerShape = RoundedCornerShape(50)
  Row(
    modifier = modifier
      .fillMaxWidth()
      .shadow(6.dp, outerShape, ambientColor = Color.Black.copy(alpha = 0.06f))
      .clip(outerShape)
      .background(Color.White.copy(alpha = 0.38f))
      .border(1.5.dp, Color.White.copy(alpha = 0.75f), outerShape)
      .padding(4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    tabs.forEachIndexed { index, title ->
      val isSelected = index == selectedIndex
      val pillShape = RoundedCornerShape(50)
      Box(
        modifier = Modifier
          .weight(1f)
          .minimumInteractiveComponentSize()
          .then(
            if (isSelected) {
              Modifier
                .shadow(4.dp, pillShape, ambientColor = Color.Black.copy(alpha = 0.08f))
                .clip(pillShape)
                .background(SolidSoftWhite)
            } else {
              Modifier.clip(pillShape)
            }
          )
          .clickable { onTabSelected(index) }
          .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) TextDark else TextSecondary
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          if (!isSelected) {
            Spacer(modifier = Modifier.height(3.dp))
            Box(
              modifier = Modifier
                .width(18.dp)
                .height(2.dp)
                .clip(RoundedCornerShape(50))
                .background(PrimaryPurpleStart.copy(alpha = 0.7f))
            )
          }
        }
      }
    }
  }
}

/**
 * 12. Feature Card (bottom-right style):
 * Big gradient card (pink -> lavender -> sky blue -> cyan), 24.dp radius,
 * bold dark title, subtitle, white pill button with up-arrow icon, and small pill indicator at top-right.
 */
@Composable
fun LiquidFeatureGradientCard(
  title: String,
  subtitle: String,
  badgeText: String,
  buttonText: String,
  onButtonClick: () -> Unit,
  modifier: Modifier = Modifier,
  secondaryButtonText: String? = null,
  onSecondaryButtonClick: (() -> Unit)? = null,
  testTag: String? = null,
) {
  val glassSpec = LocalLiquidGlassSpec.current
  val shape = RoundedCornerShape(glassSpec.surfaceCornerRadius) // 28.dp rounded corners
  val tagMod = if (testTag != null) Modifier.testTag(testTag) else Modifier
  Box(
    modifier = modifier
      .fillMaxWidth()
      .then(tagMod)
      .shadow(
        elevation = 16.dp,
        shape = shape,
        ambientColor = FeaturePink,
        spotColor = FeatureCyan
      )
      .clip(shape)
      .background(brush = FeatureCardGradientBrush)
      .border(glassSpec.borderWidth, Color.White.copy(alpha = 0.85f), shape)
      .padding(20.dp)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(SolidSoftWhite)
              .padding(5.dp),
            contentAlignment = Alignment.Center
          ) {
            Image(
              painter = painterResource(id = R.drawable.ic_nexcore_logo),
              contentDescription = "NEXCORE Logo",
              modifier = Modifier.fillMaxSize()
            )
          }
          Text(
            text = "NEXCORE",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            ),
            color = TextDark
          )
        }
        // Small pill indicator at top-right
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.65f))
            .border(1.dp, Color.White, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
          Text(
            text = badgeText,
            style = MaterialTheme.typography.labelMedium.copy(
              color = TextDark,
              fontWeight = FontWeight.SemiBold
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        color = TextDark
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = TextDark.copy(alpha = 0.78f)
      )

      Spacer(modifier = Modifier.height(16.dp))

      Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // White pill button with up-arrow icon
        Row(
          modifier = Modifier
            .minimumInteractiveComponentSize()
            .shadow(8.dp, RoundedCornerShape(50), ambientColor = Color.Black.copy(alpha = 0.12f))
            .clip(RoundedCornerShape(50))
            .background(SolidSoftWhite)
            .border(1.dp, Color.White, RoundedCornerShape(50))
            .clickable(onClick = onButtonClick)
            .padding(horizontal = 18.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = buttonText,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.Bold,
              color = TextDark
            )
          )
          Spacer(modifier = Modifier.width(6.dp))
          Icon(
            imageVector = Icons.Default.ArrowUpward,
            contentDescription = null,
            tint = AccentBluePurple,
            modifier = Modifier.size(16.dp)
          )
        }

        if (secondaryButtonText != null && onSecondaryButtonClick != null) {
          LiquidSecondaryButton(
            text = secondaryButtonText,
            onClick = onSecondaryButtonClick
          )
        }
      }
    }
  }
}

/**
 * Status Badge for Orders, Quotations, Listings, and Mobile Crusher Bookings
 */
@Composable
fun StatusPillBadge(
  status: String,
  modifier: Modifier = Modifier,
) {
  val (bgColor, textColor) = when (status) {
    "Active", "Accepted", "Completed", "Delivered", "Ready for Collection", "Resolved" ->
      SuccessGreen.copy(alpha = 0.18f) to Color(0xFF15803D)
    "Quote Received", "Quotation Sent", "Scheduled", "In Progress", "Dispatched", "Processing" ->
      PrimaryPurpleStart.copy(alpha = 0.20f) to AccentBluePurple
    "Cancelled", "Removed" ->
      DangerCoral.copy(alpha = 0.18f) to Color(0xFFB91C1C)
    else ->
      WarningAmber.copy(alpha = 0.20f) to Color(0xFFB45309)
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(50))
      .background(bgColor)
      .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(50))
      .padding(horizontal = 10.dp, vertical = 4.dp)
  ) {
    Text(
      text = status,
      style = MaterialTheme.typography.labelMedium.copy(
        color = textColor,
        fontWeight = FontWeight.Bold
      )
    )
  }
}

/**
 * Custom Industrial Material & Mobile Crusher Illustration Banner
 * Renders rich architectural/industrial visuals for each C&D material or crusher category.
 */
@Composable
fun MaterialVisualBanner(
  imageKey: String,
  category: String,
  isSample: Boolean,
  modifier: Modifier = Modifier,
) {
  val (gradStart, gradEnd, icon) = when (imageKey) {
    "recycled_concrete", "crushed_concrete" -> Triple(
      Color(0xFF334155),
      Color(0xFF64748B),
      Icons.Default.Landscape
    )
    "crushed_brick" -> Triple(
      Color(0xFF9A3412),
      Color(0xFFEA580C),
      Icons.Default.Construction
    )
    "road_base" -> Triple(
      Color(0xFF1E293B),
      Color(0xFF0F766E),
      Icons.Default.LocalShipping
    )
    "scrap_metal" -> Triple(
      Color(0xFF0F172A),
      Color(0xFF334155),
      Icons.Default.Hardware
    )
    "mobile_crusher" -> Triple(
      Color(0xFF064E3B),
      Color(0xFF059669),
      Icons.Default.Factory
    )
    else -> Triple(
      Color(0xFF0F172A),
      Color(0xFF047857),
      Icons.Default.Recycling
    )
  }

  val shape = RoundedCornerShape(18.dp)
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(130.dp)
      .clip(shape)
      .background(
        brush = Brush.linearGradient(
          colors = listOf(gradStart, gradEnd)
        )
      )
      .border(1.dp, Color.White.copy(alpha = 0.55f), shape)
  ) {
    // Geometric aggregate/industrial texture overlay
    Canvas(modifier = Modifier.fillMaxSize()) {
      drawCircle(
        color = Color.White.copy(alpha = 0.08f),
        radius = size.minDimension * 0.45f,
        center = Offset(size.width * 0.82f, size.height * 0.25f)
      )
      drawRoundRect(
        color = Color.White.copy(alpha = 0.06f),
        topLeft = Offset(size.width * 0.08f, size.height * 0.55f),
        size = Size(size.width * 0.45f, size.height * 0.35f),
        cornerRadius = CornerRadius(18f, 18f)
      )
    }

    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Bottom
    ) {
      Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxSize().weight(1f)
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(50))
              .background(Color.Black.copy(alpha = 0.35f))
              .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(50))
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = category,
              style = MaterialTheme.typography.labelMedium.copy(
                color = TextLight,
                fontWeight = FontWeight.SemiBold
              )
            )
          }

          if (isSample) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(WarningAmber.copy(alpha = 0.9f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = "Sample Listing",
                style = MaterialTheme.typography.labelMedium.copy(
                  color = TextDark,
                  fontWeight = FontWeight.Bold
                )
              )
            }
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = icon,
            contentDescription = category,
            tint = TextLight.copy(alpha = 0.92f),
            modifier = Modifier.size(28.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "NEXCORE Verified Spec Format",
            style = MaterialTheme.typography.labelMedium.copy(
              color = TextLight.copy(alpha = 0.85f)
            )
          )
        }
      }
    }
  }
}
