package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ============================================================================
// NEXCORE 'Liquid Glass' Color Palette
// Soft Warm Cream-Green to Cool Blue-Grey Background Gradient +
// rgba(255, 255, 255, 0.25) Glassmorphism Surfaces + Logo Emerald & Navy
// ============================================================================

// Primary Brand & Interactive Tokens (NEXCORE Logo Forest Emerald & Leaf Green)
val PrimaryPurpleStart = Color(0xFF047857) // Deep Forest Emerald
val PrimaryPurpleEnd = Color(0xFF10B981)   // NEXCORE Vibrant Emerald
val AccentBluePurple = Color(0xFF059669)   // Primary Logo Emerald Accent

// Secondary Brand Tokens (Dark Industrial Navy & Muted Teal)
val SecondaryTealStart = Color(0xFF0F172A) // NEXCORE Dark Industrial Navy
val SecondaryTealEnd = Color(0xFF0F766E)   // Muted Industrial Teal
val NexcoreEmerald = Color(0xFF16A34A)     // Logo Leaf Green
val NexcoreDeepNavy = Color(0xFF0F172A)    // Primary Dark Navy
val NexcoreLimeFacet = Color(0xFF22C55E)   // Logo Bright Facet Green
val NexcoreMintFacet = Color(0xFF4ADE80)   // Logo Top-Right Wing Highlight

// Semantic Status Colors
val SuccessGreen = Color(0xFF16A34A)
val WarningAmber = Color(0xFFD97706)
val DangerCoral = Color(0xFFDC2626)

// Liquid Glass Surface Tokens — Exact rgba(255, 255, 255, 0.25) Glassmorphism
val GlassCardFill25 = Color(255, 255, 255, 64)   // Exact rgba(255, 255, 255, 0.25)
val GlassWhiteFill = Color(255, 255, 255, 64)    // rgba(255, 255, 255, 0.25) for cards
val GlassPanelFill = Color(255, 255, 255, 72)    // ~28% white for main glass panel container
val GlassBorderWhite = Color(255, 255, 255, 178) // Crisp white border (~70% opacity)
val GlassInnerShine = Color(255, 255, 255, 230)  // Specular top white border highlight (~90%)
val SolidSoftWhite = Color(0xFFF6F9F7)           // Soft warm cream-green tinted white for pills/knobs

// Background Gradient: Soft Warm Cream-Green (top-left) -> Cool Blue-Grey (bottom-right)
val BgWarmCreamGreen = Color(0xFFEFF7EC)         // Soft Warm Cream-Green
val BgWarmCreamPink = BgWarmCreamGreen           // Alias for theme compatibility
val BgSoftSageBlue = Color(0xFFE4EFEA)           // Transitional Soft Sage-Mist
val BgSoftLavenderGrey = BgSoftSageBlue          // Alias for theme compatibility
val BgCoolBlueGrey = Color(0xFFD8E3F0)           // Cool Blue-Grey

// Text Colors (High-contrast Dark Charcoal & Slate on translucent glass)
val TextDark = Color(0xFF0F172A)                 // Dark Charcoal / Navy
val TextSecondary = Color(0xFF334155)            // Industrial Slate
val TextMuted = Color(0xFF64748B)                // Muted Cool Grey
val TextLight = Color(0xFFFFFFFF)                // Pure White

// Backdrop Ambient Light Orb Colors (diffused behind the 25% white glass surfaces)
val FeaturePink = Color(0xFFC8EED9)              // Warm Cream-Green Ambient Orb
val FeatureLavender = Color(0xFFB5E5CF)          // Soft Emerald-Mint Orb
val FeatureSkyBlue = Color(0xFFC5DCF2)           // Cool Sky Blue-Grey Orb
val FeatureCyan = Color(0xFFB8E2DC)              // Muted Teal-Mist Orb

// Reusable Brand & Glass Brushes
val BackgroundLiquidGradientBrush = Brush.linearGradient(
  colors = listOf(
    BgWarmCreamGreen,
    BgSoftSageBlue,
    BgCoolBlueGrey
  )
)

val PrimaryPurpleBrush = Brush.horizontalGradient(
  colors = listOf(Color(0xFF047857), Color(0xFF059669), Color(0xFF10B981))
)

val SecondaryTealBrush = Brush.horizontalGradient(
  colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0F766E))
)

val IridescentBorderBrush = Brush.linearGradient(
  colors = listOf(
    Color.White.copy(alpha = 0.95f),
    Color(0xFF4ADE80).copy(alpha = 0.85f),
    Color(0xFF10B981).copy(alpha = 0.85f),
    Color(0xFF93C5FD).copy(alpha = 0.85f),
    Color.White.copy(alpha = 0.90f)
  )
)

val FeatureCardGradientBrush = Brush.linearGradient(
  colors = listOf(
    Color(255, 255, 255, 90),
    Color(0xFFDDF5E6).copy(alpha = 0.55f),
    Color(0xFFD6E6F5).copy(alpha = 0.55f),
    Color(255, 255, 255, 76)
  )
)

val TopRightGlassCardBrush = Brush.linearGradient(
  colors = listOf(
    Color(255, 255, 255, 76),
    Color(0xFFE2F5EA).copy(alpha = 0.35f),
    Color(0xFFDCE7F3).copy(alpha = 0.35f)
  )
)
