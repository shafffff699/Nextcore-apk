package com.example.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class LiquidGlassThemeSpec(
  val surfaceCornerRadius: Dp = 28.dp,
  val backdropBlurRadius: Dp = 28.dp,
  val cardBackgroundColor: Color = GlassCardFill25, // rgba(255, 255, 255, 0.25)
  val panelBackgroundColor: Color = GlassPanelFill,
  val borderColor: Color = GlassBorderWhite,
  val borderHighlightColor: Color = GlassInnerShine,
  val borderWidth: Dp = 1.5.dp,
  val gradientStartWarmCreamGreen: Color = BgWarmCreamGreen,
  val gradientMidSageBlue: Color = BgSoftSageBlue,
  val gradientEndCoolBlueGrey: Color = BgCoolBlueGrey,
)

val LocalLiquidGlassSpec = staticCompositionLocalOf { LiquidGlassThemeSpec() }

private val LiquidGlassMaterialColorScheme = lightColorScheme(
  primary = AccentBluePurple,
  onPrimary = TextLight,
  primaryContainer = Color(0xFFD1FAE5),
  onPrimaryContainer = Color(0xFF064E3B),
  secondary = NexcoreDeepNavy,
  onSecondary = TextLight,
  secondaryContainer = BgCoolBlueGrey,
  onSecondaryContainer = TextDark,
  tertiary = NexcoreLimeFacet,
  onTertiary = TextLight,
  background = BgWarmCreamGreen,
  onBackground = TextDark,
  surface = GlassCardFill25,
  onSurface = TextDark,
  surfaceVariant = GlassPanelFill,
  onSurfaceVariant = TextSecondary,
  outline = GlassBorderWhite,
  outlineVariant = GlassInnerShine,
  error = DangerCoral,
  onError = TextLight
)

val LiquidGlassShapes = Shapes(
  extraSmall = RoundedCornerShape(14.dp),
  small = RoundedCornerShape(20.dp),
  medium = RoundedCornerShape(28.dp),
  large = RoundedCornerShape(28.dp),
  extraLarge = RoundedCornerShape(50)
)

fun Modifier.liquidGlassSurface(
  cornerRadius: Dp = 28.dp,
  backgroundColor: Color = GlassCardFill25,
  borderBrush: Brush? = null,
  shadowElevation: Dp = 12.dp,
): Modifier {
  val shape = RoundedCornerShape(cornerRadius)
  val resolvedBorderBrush = borderBrush ?: Brush.verticalGradient(
    colors = listOf(
      GlassInnerShine,
      GlassBorderWhite,
      Color.White.copy(alpha = 0.45f)
    )
  )
  val glassFillBrush = Brush.verticalGradient(
    colors = listOf(
      backgroundColor.copy(alpha = (backgroundColor.alpha + 0.08f).coerceAtMost(1f)),
      backgroundColor,
      backgroundColor.copy(alpha = (backgroundColor.alpha - 0.03f).coerceAtLeast(0.18f))
    )
  )
  return this
    .shadow(
      elevation = shadowElevation,
      shape = shape,
      ambientColor = Color.Black.copy(alpha = 0.08f),
      spotColor = AccentBluePurple.copy(alpha = 0.12f)
    )
    .clip(shape)
    .background(brush = glassFillBrush, shape = shape)
    .border(width = 1.5.dp, brush = resolvedBorderBrush, shape = shape)
}

@Composable
fun LiquidGlassTheme(
  spec: LiquidGlassThemeSpec = LiquidGlassThemeSpec(),
  wrapWithLiquidBackdrop: Boolean = false,
  content: @Composable () -> Unit,
) {
  CompositionLocalProvider(LocalLiquidGlassSpec provides spec) {
    MaterialTheme(
      colorScheme = LiquidGlassMaterialColorScheme,
      typography = Typography,
      shapes = LiquidGlassShapes
    ) {
      if (wrapWithLiquidBackdrop) {
        LiquidGlassBackdropBox(spec = spec) {
          content()
        }
      } else {
        content()
      }
    }
  }
}

@Composable
fun LiquidGlassBackdropBox(
  modifier: Modifier = Modifier,
  spec: LiquidGlassThemeSpec = LocalLiquidGlassSpec.current,
  content: @Composable BoxScope.() -> Unit,
) {
  Box(modifier = modifier.fillMaxSize()) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .drawWithCache {
          val baseGradient = Brush.linearGradient(
            colors = listOf(
              spec.gradientStartWarmCreamGreen,
              spec.gradientMidSageBlue,
              spec.gradientEndCoolBlueGrey
            ),
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height)
          )
          val creamGreenOrb = Brush.radialGradient(
            colors = listOf(FeaturePink.copy(alpha = 0.50f), Color.Transparent),
            center = Offset(size.width * 0.18f, size.height * 0.16f),
            radius = (size.minDimension * 0.58f).coerceAtLeast(1f)
          )
          val coolBlueGreyOrb = Brush.radialGradient(
            colors = listOf(FeatureSkyBlue.copy(alpha = 0.48f), Color.Transparent),
            center = Offset(size.width * 0.84f, size.height * 0.28f),
            radius = (size.minDimension * 0.54f).coerceAtLeast(1f)
          )
          val sageTealOrb = Brush.radialGradient(
            colors = listOf(FeatureLavender.copy(alpha = 0.42f), Color.Transparent),
            center = Offset(size.width * 0.48f, size.height * 0.78f),
            radius = (size.minDimension * 0.62f).coerceAtLeast(1f)
          )
          val diagonalSurfacePath = Path().apply {
            moveTo(0f, size.height * 0.36f)
            lineTo(size.width, size.height * 0.14f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
          }
          val diagonalShadowBrush = Brush.linearGradient(
            colors = listOf(Color.Black.copy(alpha = 0.03f), Color.Transparent),
            start = Offset(0f, size.height * 0.2f),
            end = Offset(size.width, size.height * 0.8f)
          )

          onDrawBehind {
            drawRect(brush = baseGradient)
            drawCircle(
              brush = creamGreenOrb,
              center = Offset(size.width * 0.18f, size.height * 0.16f),
              radius = size.minDimension * 0.58f
            )
            drawCircle(
              brush = coolBlueGreyOrb,
              center = Offset(size.width * 0.84f, size.height * 0.28f),
              radius = size.minDimension * 0.54f
            )
            drawCircle(
              brush = sageTealOrb,
              center = Offset(size.width * 0.48f, size.height * 0.78f),
              radius = size.minDimension * 0.62f
            )
            drawPath(path = diagonalSurfacePath, brush = diagonalShadowBrush)
          }
        }
    )

    content()
  }
}

@Composable
fun NexcoreTheme(
  content: @Composable () -> Unit,
) {
  LiquidGlassTheme(content = content)
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  LiquidGlassTheme(content = content)
}
