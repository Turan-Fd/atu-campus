package com.atu.campus.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.atu.campus.R
import com.atu.campus.data.AtuNews
import com.atu.campus.data.StudentProfile
import com.atu.campus.ui.theme.AtuBackground
import com.atu.campus.ui.theme.AtuBorder
import com.atu.campus.ui.theme.AtuDanger
import com.atu.campus.ui.theme.AtuDarkBackground
import com.atu.campus.ui.theme.AtuDarkBorder
import com.atu.campus.ui.theme.AtuDarkElevated
import com.atu.campus.ui.theme.AtuDarkMuted
import com.atu.campus.ui.theme.AtuDarkSurface
import com.atu.campus.ui.theme.AtuDarkText
import com.atu.campus.ui.theme.AtuMagenta
import com.atu.campus.ui.theme.AtuNavy
import com.atu.campus.ui.theme.AtuPrimary
import com.atu.campus.ui.theme.AtuSuccess
import com.atu.campus.ui.theme.AtuSurfaceSoft
import com.atu.campus.ui.theme.AtuTextPrimary
import com.atu.campus.ui.theme.AtuTextSecondary
import com.atu.campus.ui.theme.AtuTint
import com.atu.campus.ui.theme.AtuWhite

object AtuColors {
    val backgroundLight = AtuBackground
    val surfaceLight = AtuWhite
    val surfaceSoft = AtuSurfaceSoft
    val textPrimary = AtuTextPrimary
    val textSecondary = AtuTextSecondary
    val primary = AtuPrimary
    val magenta = AtuMagenta
    val navy = AtuNavy
    val softPink = AtuTint
    val softLavender = Color(0xFFEEF0FF)
    val softBlue = Color(0xFFEAF1FF)
    val backgroundDark = AtuDarkBackground
    val surfaceDark = AtuDarkSurface
    val elevatedDark = AtuDarkElevated
    val textDark = AtuDarkText
    val textDarkSecondary = AtuDarkMuted
    val success = AtuSuccess
    val error = AtuDanger
}

object AtuSpacing {
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 20.dp
    val xl = 24.dp
    val xxl = 32.dp
}

object AtuRadius {
    val chip = 999.dp
    val input = 20.dp
    val button = 20.dp
    val card = 24.dp
    val hero = 28.dp
    val nav = 30.dp
}

object AtuElevation {
    val none = 0.dp
    val card = 4.dp
    val floating = 12.dp
    val nav = 18.dp
}

object AtuMotion {
    const val fastMillis = 150
    const val normalMillis = 250
    const val slowMillis = 450
    const val screenMillis = 320
    const val pressMillis = 100
    val fastTween @Composable get() = tween<Float>(fastMillis)
    val normalTween @Composable get() = tween<Float>(normalMillis)
    val softSpring @Composable get() = spring<Float>(
        dampingRatio = 0.75f,
        stiffness = Spring.StiffnessMediumLow
    )
}

@Stable
data class AtuPalette(
    val background: Color,
    val surface: Color,
    val surfaceSoft: Color,
    val elevated: Color,
    val border: Color,
    val text: Color,
    val muted: Color,
    val primary: Color,
    val accent: Color,
)

@Composable
fun atuPalette(darkMode: Boolean): AtuPalette = if (darkMode) {
    AtuPalette(
        background = AtuDarkBackground,
        surface = AtuDarkSurface,
        surfaceSoft = AtuDarkElevated,
        elevated = AtuDarkElevated,
        border = AtuDarkBorder,
        text = AtuDarkText,
        muted = AtuDarkMuted,
        primary = AtuMagenta,
        accent = Color(0xFFE67AA5)
    )
} else {
    AtuPalette(
        background = AtuBackground,
        surface = AtuWhite,
        surfaceSoft = AtuSurfaceSoft,
        elevated = AtuWhite,
        border = AtuBorder,
        text = AtuTextPrimary,
        muted = AtuTextSecondary,
        primary = AtuPrimary,
        accent = AtuMagenta
    )
}

fun Modifier.atuPressScale(enabled: Boolean = true, targetScale: Float = 0.98f): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (enabled && pressed) targetScale else 1f,
        animationSpec = tween(AtuMotion.pressMillis),
        label = "pressScale"
    )
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

@Composable
fun AtuAnimatedContentWrapper(
    visible: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(AtuMotion.screenMillis)) + slideInVertically(tween(AtuMotion.screenMillis)) { it / 18 },
        exit = fadeOut(tween(AtuMotion.fastMillis)) + slideOutVertically(tween(AtuMotion.fastMillis)) { it / 24 },
        modifier = modifier
    ) {
        content()
    }
}

@Composable
fun AtuScreen(
    darkMode: Boolean = false,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = AtuSpacing.lg, vertical = AtuSpacing.lg),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(AtuSpacing.md),
    content: @Composable ColumnScope.(AtuPalette) -> Unit
) {
    val palette = atuPalette(darkMode)
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .padding(contentPadding),
        verticalArrangement = verticalArrangement
    ) {
        content(palette)
    }
}

@Composable
fun AtuSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: String? = null,
    darkMode: Boolean = false,
    onActionClick: (() -> Unit)? = null
) {
    val palette = atuPalette(darkMode)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(title, color = palette.text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            if (subtitle != null) {
                Text(subtitle, color = palette.muted, style = MaterialTheme.typography.bodyMedium)
            }
        }
        if (action != null) {
            Text(
                text = action,
                color = palette.primary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                modifier = Modifier
                    .clip(RoundedCornerShape(AtuRadius.chip))
                    .clickable(enabled = onActionClick != null) { onActionClick?.invoke() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun AtuHeroCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false,
    minHeight: Dp = 168.dp,
    trailing: @Composable BoxScope.() -> Unit = {}
) {
    val transition = rememberInfiniteTransition(label = "heroShine")
    val drift by transition.animateFloat(
        initialValue = -0.18f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(tween(3600), RepeatMode.Reverse),
        label = "heroDrift"
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(minHeight)
            .clip(RoundedCornerShape(AtuRadius.hero))
            .background(
                Brush.linearGradient(
                    listOf(
                        if (darkMode) Color(0xFF19131B) else Color(0xFFFFF7FA),
                        if (darkMode) Color(0xFF301426) else Color(0xFFFFEAF2),
                        if (darkMode) Color(0xFF111827) else Color(0xFFEFF3FF)
                    )
                )
            )
            .border(1.dp, if (darkMode) AtuDarkBorder else Color(0xFFFFFFFF), RoundedCornerShape(AtuRadius.hero))
            .padding(AtuSpacing.xl)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                color = (if (darkMode) AtuMagenta else AtuPrimary).copy(alpha = if (darkMode) 0.16f else 0.08f),
                radius = size.width * 0.35f,
                center = Offset(size.width * drift, size.height * 0.08f)
            )
        }
        Column(
            modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth(0.76f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, color = if (darkMode) AtuDarkText else AtuTextPrimary, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            Text(subtitle, color = if (darkMode) AtuDarkMuted else AtuTextSecondary, style = MaterialTheme.typography.bodyLarge)
        }
        trailing()
    }
}

@Composable
fun AtuTopHeader(
    greeting: String,
    title: String,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false,
    action: @Composable RowScope.() -> Unit = {}
) {
    val palette = atuPalette(darkMode)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
            Text(greeting, color = palette.muted, style = MaterialTheme.typography.bodyMedium)
            Text(title, color = palette.text, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically, content = action)
    }
}

@Composable
fun AtuSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false,
    placeholder: String = "Axtar"
) {
    val palette = atuPalette(darkMode)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = palette.muted) },
        singleLine = true,
        shape = RoundedCornerShape(AtuRadius.input),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = palette.primary.copy(alpha = 0.46f),
            unfocusedBorderColor = palette.border,
            focusedContainerColor = palette.surface,
            unfocusedContainerColor = palette.surface,
            cursorColor = palette.primary,
            focusedTextColor = palette.text,
            unfocusedTextColor = palette.text,
            focusedPlaceholderColor = palette.muted,
            unfocusedPlaceholderColor = palette.muted
        )
    )
}

@Composable
fun AtuFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false
) {
    val palette = atuPalette(darkMode)
    val bg by animateColorAsState(
        targetValue = when {
            selected && darkMode -> palette.primary.copy(alpha = 0.18f)
            selected -> AtuTint
            else -> palette.surface
        },
        animationSpec = tween(AtuMotion.normalMillis),
        label = "chipBg"
    )
    val text by animateColorAsState(
        targetValue = if (selected) palette.primary else palette.muted,
        animationSpec = tween(AtuMotion.normalMillis),
        label = "chipText"
    )
    Surface(
        onClick = onClick,
        modifier = modifier.atuPressScale(),
        color = bg,
        shape = RoundedCornerShape(AtuRadius.chip),
        border = BorderStroke(1.dp, if (selected) palette.primary.copy(alpha = 0.28f) else palette.border)
    ) {
        Text(
            text = label,
            color = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
        )
    }
}

@Composable
fun AtuStatusBadge(
    text: String,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false,
    icon: ImageVector = Icons.Outlined.CheckCircle,
    success: Boolean = false
) {
    val palette = atuPalette(darkMode)
    Surface(
        modifier = modifier,
        color = if (success) AtuSuccess.copy(alpha = 0.12f) else palette.primary.copy(alpha = if (darkMode) 0.18f else 0.1f),
        shape = RoundedCornerShape(AtuRadius.chip),
        border = BorderStroke(1.dp, if (success) AtuSuccess.copy(alpha = 0.3f) else palette.primary.copy(alpha = 0.24f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = if (success) AtuSuccess else palette.primary, modifier = Modifier.size(17.dp))
            Text(text, color = if (success) AtuSuccess else palette.primary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun AtuStudentCard(
    student: StudentProfile,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false
) {
    val palette = atuPalette(darkMode)
    AtuPremiumCard(darkMode = darkMode, modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(AtuTint, AtuColors.softLavender))),
                contentAlignment = Alignment.Center
            ) {
                if (student.photoUrl.isNotBlank()) {
                    AsyncImage(
                        model = student.photoUrl,
                        contentDescription = "${student.fullName} profil fotosu",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = student.name.firstOrNull()?.uppercase() ?: "A",
                        color = AtuPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(student.fullName.ifBlank { "T\u0259l\u0259b\u0259" }, color = palette.text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("ID ${student.id.ifBlank { "T\u0259yin edilm\u0259yib" }} • Qrup ${student.group.ifBlank { "T\u0259yin edilm\u0259yib" }}", color = palette.muted, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(student.specialty.ifBlank { "\u0130xtisas t\u0259yin edilm\u0259yib" }, color = palette.muted, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            AtuStatusBadge("Aktiv", darkMode = darkMode, success = true)
        }
    }
}

@Composable
fun AtuModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false,
    accent: Color = AtuPrimary,
    onClick: () -> Unit = {}
) {
    val palette = atuPalette(darkMode)
    AtuPremiumCard(
        modifier = modifier
            .width(156.dp)
            .height(128.dp)
            .clip(RoundedCornerShape(AtuRadius.card))
            .clickable { onClick() }
            .atuPressScale(),
        darkMode = darkMode,
        contentPadding = PaddingValues(15.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(accent.copy(alpha = if (darkMode) 0.18f else 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, color = palette.text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, color = palette.muted, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun AtuNewsCard(
    item: AtuNews,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false,
    featured: Boolean = false
) {
    val palette = atuPalette(darkMode)
    AtuPremiumCard(
        darkMode = darkMode,
        modifier = modifier
            .clip(RoundedCornerShape(AtuRadius.card))
            .clickable { onClick() }
            .atuPressScale(),
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(if (featured) 104.dp else 82.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(palette.surfaceSoft)
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(item.date.ifBlank { "ATU" }, color = palette.primary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black)
                Text(item.title, color = palette.text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, maxLines = if (featured) 3 else 2, overflow = TextOverflow.Ellipsis)
                Text(item.summary, color = palette.muted, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = palette.muted, modifier = Modifier.size(20.dp))
        }
    }
}

data class AtuBottomNavItem(
    val key: String,
    val title: String,
    val icon: ImageVector
)

@Composable
fun AtuBottomNavigation(
    items: List<AtuBottomNavItem>,
    selectedKey: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false,
    expandedContent: (@Composable () -> Unit)? = null
) {
    val palette = atuPalette(darkMode)
    val selectedIndex = items.indexOfFirst { it.key == selectedKey }.coerceAtLeast(0)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = palette.surface.copy(alpha = 0.98f),
            shape = RoundedCornerShape(AtuRadius.nav),
            border = BorderStroke(1.dp, palette.border),
            shadowElevation = AtuElevation.nav,
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = 0.86f,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                AnimatedVisibility(
                    visible = expandedContent != null,
                    enter = fadeIn(tween(AtuMotion.normalMillis)) +
                        slideInVertically(tween(AtuMotion.normalMillis)) { it / 3 },
                    exit = fadeOut(tween(AtuMotion.fastMillis)) +
                        slideOutVertically(tween(AtuMotion.fastMillis)) { it / 3 }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    if (darkMode) palette.surface.copy(alpha = 0.92f) else palette.surface.copy(alpha = 0.96f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = palette.border.copy(alpha = if (darkMode) 0.52f else 0.72f),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            expandedContent?.invoke()
                        }
                    }
                }

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    val itemWidth = maxWidth / items.size
                    val indicatorOffset by animateDpAsState(
                        targetValue = itemWidth * selectedIndex,
                        animationSpec = spring(
                            dampingRatio = 0.82f,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        label = "navIndicatorOffset"
                    )

                    Box(
                        modifier = Modifier
                            .offset(x = indicatorOffset)
                            .width(itemWidth)
                            .fillMaxHeight()
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                palette.primary.copy(
                                    alpha = if (darkMode) 0.20f else 0.11f
                                )
                            )
                    )

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items.forEach { item ->
                            val selected = selectedKey == item.key
                            val iconScale by animateFloatAsState(
                                if (selected) 1.08f else 1f,
                                tween(AtuMotion.normalMillis),
                                label = "navScale"
                            )
                            val labelAlpha by animateFloatAsState(
                                targetValue = if (selected) 1f else 0.72f,
                                animationSpec = tween(AtuMotion.normalMillis),
                                label = "navLabelAlpha"
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(24.dp))
                                    .clickable { onSelect(item.key) }
                                    .atuPressScale(targetScale = 0.97f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = if (selected) palette.primary else palette.muted,
                                        modifier = Modifier.size(23.dp).graphicsLayer {
                                            scaleX = iconScale
                                            scaleY = iconScale
                                        }
                                    )
                                    Text(
                                        item.title,
                                        color = if (selected) palette.primary else palette.muted,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = if (selected) FontWeight.Black else FontWeight.SemiBold,
                                        maxLines = 1,
                                        modifier = Modifier.graphicsLayer { alpha = labelAlpha }
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
fun AtuSettingsRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false
) {
    val palette = atuPalette(darkMode)
    AtuPremiumCard(darkMode = darkMode, modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.primary.copy(alpha = if (darkMode) 0.18f else 0.09f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = palette.primary)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(title, color = palette.text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                Text(subtitle, color = palette.muted, style = MaterialTheme.typography.bodyMedium)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = palette.primary,
                    checkedThumbColor = AtuWhite,
                    uncheckedTrackColor = palette.border,
                    uncheckedThumbColor = AtuWhite
                )
            )
        }
    }
}

@Composable
fun AtuPassCard(
    student: StudentProfile,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false,
    approved: Boolean = false
) {
    val transition = rememberInfiniteTransition(label = "passShine")
    val shine by transition.animateFloat(
        initialValue = -0.35f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(tween(3600), RepeatMode.Reverse),
        label = "shine"
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.62f)
            .clip(RoundedCornerShape(AtuRadius.hero))
            .background(Brush.linearGradient(listOf(AtuNavy, AtuPrimary, AtuMagenta)))
            .padding(22.dp)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(AtuWhite.copy(alpha = 0.08f), radius = size.width * 0.32f, center = Offset(size.width * shine, 0f))
            drawLine(
                color = AtuWhite.copy(alpha = 0.18f),
                start = Offset(size.width * shine, 0f),
                end = Offset(size.width * shine - 80.dp.toPx(), size.height),
                strokeWidth = 18.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("ATU Campus", color = AtuWhite.copy(alpha = 0.78f), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text("Digital Pass", color = AtuWhite, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            }
            AtuStatusBadge(if (approved) "Giri\u015F a\u00E7\u0131qd\u0131r" else "Verified", darkMode = true, icon = Icons.Outlined.VerifiedUser, success = approved)
        }
        Column(modifier = Modifier.align(Alignment.BottomStart), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(student.fullName.ifBlank { "T\u0259l\u0259b\u0259" }, color = AtuWhite, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            Text("ID ${student.id} • Qrup ${student.group}", color = AtuWhite.copy(alpha = 0.8f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        Icon(Icons.Outlined.CreditCard, contentDescription = null, tint = AtuWhite.copy(alpha = 0.18f), modifier = Modifier.align(Alignment.BottomEnd).size(58.dp))
    }
}

@Composable
fun AtuPermissionCard(
    permissionDenied: Boolean,
    onRequestPermission: () -> Unit,
    securityNote: String,
    modifier: Modifier = Modifier
) {
    AtuPremiumCard(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier
                    .size(66.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(AtuTint, AtuColors.softLavender))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = AtuPrimary, modifier = Modifier.size(32.dp))
            }
            Text("Kamera icaz\u0259si", color = AtuTextPrimary, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            Text(
                "T\u0259l\u0259b\u0259 v\u0259siq\u0259nizin \u00F6n v\u0259 arxa \u00FCz\u00FCn\u00FC skan etm\u0259k \u00FC\u00E7\u00FCn kamera icaz\u0259si laz\u0131md\u0131r.",
                color = AtuTextSecondary,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            AtuInlineNote(icon = Icons.Outlined.Lock, text = securityNote)
            if (permissionDenied) {
                Text(
                    "Kamera icaz\u0259si verilm\u0259di. Davam etm\u0259k \u00FC\u00E7\u00FCn icaz\u0259ni yenid\u0259n t\u0259sdiql\u0259yin.",
                    color = AtuDanger,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
            AtuPrimaryButton(text = "Kameraya icaz\u0259 ver", onClick = onRequestPermission, modifier = Modifier.atuPressScale())
        }
    }
}

@Composable
fun AtuScanOverlay(
    progressText: String,
    instruction: String,
    modifier: Modifier = Modifier,
    darkScrim: Color = Color.Black.copy(alpha = 0.42f)
) {
    val transition = rememberInfiniteTransition(label = "scanOverlay")
    val scan by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(1500), RepeatMode.Restart), label = "scan")
    val pulse by transition.animateFloat(0.45f, 1f, infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "pulse")
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            val frameWidth = size.width * 0.84f
            val frameHeight = frameWidth * 0.62f
            val left = (size.width - frameWidth) / 2f
            val top = (size.height - frameHeight) / 2f - 24.dp.toPx()
            val corner = 28.dp.toPx()
            drawRect(darkScrim)
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(frameWidth, frameHeight),
                cornerRadius = CornerRadius(corner, corner),
                blendMode = androidx.compose.ui.graphics.BlendMode.Clear
            )
            drawRoundRect(
                color = AtuWhite.copy(alpha = 0.35f + pulse * 0.35f),
                topLeft = Offset(left, top),
                size = Size(frameWidth, frameHeight),
                cornerRadius = CornerRadius(corner, corner),
                style = Stroke(width = 2.5.dp.toPx())
            )
            drawLine(
                brush = Brush.horizontalGradient(listOf(Color.Transparent, AtuWhite, Color.Transparent)),
                start = Offset(left + 22.dp.toPx(), top + frameHeight * scan),
                end = Offset(left + frameWidth - 22.dp.toPx(), top + frameHeight * scan),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        Surface(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 24.dp, start = 20.dp, end = 20.dp).fillMaxWidth(),
            color = Color.Black.copy(alpha = 0.38f),
            shape = RoundedCornerShape(26.dp),
            border = BorderStroke(1.dp, AtuWhite.copy(alpha = 0.16f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Kart skan\u0131", color = AtuWhite, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                AtuStatusBadge(progressText, darkMode = true, icon = Icons.Outlined.Badge)
            }
        }
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).padding(horizontal = 20.dp, vertical = 178.dp).fillMaxWidth(),
            color = Color.Black.copy(alpha = 0.42f),
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.dp, AtuWhite.copy(alpha = 0.13f))
        ) {
            Text(
                instruction,
                color = AtuWhite,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 13.dp)
            )
        }
    }
}

@Composable
fun AtuSkeletonLoader(
    modifier: Modifier = Modifier,
    darkMode: Boolean = false
) {
    val palette = atuPalette(darkMode)
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(0.35f, 0.8f, infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "alpha")
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp)
                    .clip(RoundedCornerShape(AtuRadius.card))
                    .background(palette.surfaceSoft.copy(alpha = alpha))
            )
        }
    }
}

@Composable
fun AtuEmptyState(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false
) {
    val palette = atuPalette(darkMode)
    AtuPremiumCard(darkMode = darkMode, modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(vertical = 10.dp)) {
            Box(
                modifier = Modifier.size(56.dp).clip(CircleShape).background(palette.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = palette.primary)
            }
            Text(title, color = palette.text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
            Text(subtitle, color = palette.muted, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun AtuPremiumCard(
    modifier: Modifier = Modifier,
    darkMode: Boolean = false,
    radius: Dp = AtuRadius.card,
    contentPadding: PaddingValues = PaddingValues(AtuSpacing.md),
    content: @Composable ColumnScope.() -> Unit
) {
    val palette = atuPalette(darkMode)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(if (darkMode) 1.dp else AtuElevation.card, RoundedCornerShape(radius), ambientColor = Color.Black.copy(alpha = 0.06f)),
        shape = RoundedCornerShape(radius),
        colors = CardDefaults.cardColors(containerColor = palette.surface),
        border = BorderStroke(1.dp, palette.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}

@Composable
fun AtuInlineNote(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.Lock,
    darkMode: Boolean = false
) {
    val palette = atuPalette(darkMode)
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = if (darkMode) palette.elevated else Color(0xFFFFF8FB),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, palette.border)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = palette.primary, modifier = Modifier.size(20.dp))
            Text(text, color = palette.muted, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// Backward-compatible aliases while screens are migrated incrementally.
typealias PremiumPalette = AtuPalette

object PremiumSpace {
    val xs = AtuSpacing.xxs
    val sm = AtuSpacing.xs
    val md = AtuSpacing.md
    val lg = AtuSpacing.xl
    val xl = AtuSpacing.xxl
}

object PremiumRadius {
    val input = AtuRadius.input
    val card = AtuRadius.card
    val hero = AtuRadius.hero
    val dock = AtuRadius.nav
}

@Composable
fun premiumPalette(darkMode: Boolean) = atuPalette(darkMode)

@Composable
fun AppScreen(
    darkMode: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(AtuPalette) -> Unit
) = AtuScreen(darkMode = darkMode, modifier = modifier, content = content)

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    action: String? = null,
    darkMode: Boolean = false,
    modifier: Modifier = Modifier
) = AtuSectionHeader(title = title, subtitle = subtitle, action = action, darkMode = darkMode, modifier = modifier)

@Composable
fun PremiumCard(
    darkMode: Boolean = false,
    modifier: Modifier = Modifier,
    radius: Dp = AtuRadius.card,
    content: @Composable ColumnScope.(AtuPalette) -> Unit
) {
    val palette = atuPalette(darkMode)
    AtuPremiumCard(darkMode = darkMode, modifier = modifier, radius = radius) {
        content(palette)
    }
}

@Composable
fun HeroBanner(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null
) = AtuHeroCard(title = title, subtitle = subtitle, modifier = modifier) {
    if (trailing != null) {
        Box(Modifier.align(Alignment.TopEnd)) { trailing() }
    }
}

@Composable
fun StatusBadge(
    text: String,
    darkMode: Boolean = false,
    icon: ImageVector = Icons.Outlined.CheckCircle,
    modifier: Modifier = Modifier
) = AtuStatusBadge(text = text, darkMode = darkMode, icon = icon, modifier = modifier)

@Composable
fun PremiumIconTile(
    icon: ImageVector,
    darkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val palette = atuPalette(darkMode)
    Box(
        modifier = modifier
            .size(62.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(palette.primary.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = palette.primary, modifier = Modifier.size(30.dp))
    }
}

@Composable
fun PremiumNote(
    text: String,
    darkMode: Boolean = false,
    modifier: Modifier = Modifier
) = AtuInlineNote(text = text, darkMode = darkMode, modifier = modifier)
