package com.atu.campus.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
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
import com.atu.campus.data.ChatMessage
import com.atu.campus.data.StudentProfile
import com.atu.campus.ui.theme.AtuBorder
import com.atu.campus.ui.theme.AtuColors
import com.atu.campus.ui.theme.AtuDanger
import com.atu.campus.ui.theme.AtuPrimary
import com.atu.campus.ui.theme.AtuRadius
import com.atu.campus.ui.theme.AtuSpacing
import com.atu.campus.ui.theme.AtuSuccess
import com.atu.campus.ui.theme.AtuTextPrimary
import com.atu.campus.ui.theme.AtuTextSecondary
import com.atu.campus.ui.theme.AtuTint
import com.atu.campus.ui.theme.AtuWhite

@Stable
data class AtuPalette(
    val background: Color,
    val surface: Color,
    val softSurface: Color,
    val border: Color,
    val text: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val primary: Color,
    val primaryDark: Color,
    val softPrimary: Color
) {
    val muted: Color get() = textSecondary
    val surfaceSoft: Color get() = softSurface
}

@Composable
fun atuPalette(darkMode: Boolean): AtuPalette = if (darkMode) {
    AtuPalette(
        background = AtuColors.DarkBackground,
        surface = AtuColors.DarkSurface,
        softSurface = AtuColors.DarkSoftSurface,
        border = AtuColors.DarkBorder,
        text = AtuColors.DarkText,
        textSecondary = AtuColors.DarkTextSecondary,
        textMuted = AtuColors.DarkTextSecondary.copy(alpha = 0.78f),
        primary = Color(0xFFD55A7B),
        primaryDark = AtuColors.Primary,
        softPrimary = Color(0xFF3D1E28)
    )
} else {
    AtuPalette(
        background = AtuColors.Background,
        surface = AtuColors.Surface,
        softSurface = AtuColors.SoftSurface,
        border = AtuColors.Border,
        text = AtuColors.TextPrimary,
        textSecondary = AtuColors.TextSecondary,
        textMuted = AtuColors.TextMuted,
        primary = AtuColors.Primary,
        primaryDark = AtuColors.PrimaryDark,
        softPrimary = AtuColors.SoftPrimary
    )
}

object AtuMotion {
    const val Fast = 140
    const val Normal = 220
    const val Slow = 360
    const val fastMillis = Fast
    const val normalMillis = Normal
    const val slowMillis = Slow
}

fun Modifier.atuPressScale(
    enabled: Boolean = true,
    targetScale: Float = 0.985f
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (enabled && pressed) targetScale else 1f,
        animationSpec = tween(AtuMotion.Fast),
        label = "atuPressScale"
    )
    graphicsLayer {
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
        modifier = modifier,
        enter = fadeIn(tween(AtuMotion.Normal)) + slideInVertically(tween(AtuMotion.Normal)) { it / 16 },
        exit = fadeOut(tween(AtuMotion.Fast)) + slideOutVertically(tween(AtuMotion.Fast)) { it / 20 }
    ) {
        content()
    }
}

@Composable
fun AtuScreen(
    darkMode: Boolean = false,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(AtuSpacing.xl),
    gradientBackground: Boolean = true,
    content: @Composable ColumnScope.(AtuPalette) -> Unit
) {
    val palette = atuPalette(darkMode)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
    ) {
        if (gradientBackground) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            palette.softPrimary.copy(alpha = if (darkMode) 0.30f else 0.78f),
                            Color.Transparent
                        )
                    ),
                    radius = size.minDimension * 0.48f,
                    center = Offset(size.width * 0.82f, size.height * 0.06f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AtuColors.SoftPurple.copy(alpha = if (darkMode) 0.14f else 0.48f),
                            Color.Transparent
                        )
                    ),
                    radius = size.minDimension * 0.42f,
                    center = Offset(size.width * 0.12f, size.height * 0.18f)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            verticalArrangement = verticalArrangement,
            content = { content(palette) }
        )
    }
}

@Composable
fun AtuPremiumCard(
    modifier: Modifier = Modifier,
    darkMode: Boolean = false,
    radius: Dp = AtuRadius.card,
    contentPadding: PaddingValues = PaddingValues(AtuSpacing.lg),
    content: @Composable ColumnScope.() -> Unit
) {
    val palette = atuPalette(darkMode)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (darkMode) 0.dp else 7.dp,
                shape = RoundedCornerShape(radius),
                ambientColor = Color(0x0F2B1230),
                spotColor = Color(0x122B1230)
            ),
        shape = RoundedCornerShape(radius),
        colors = CardDefaults.cardColors(containerColor = palette.surface),
        border = BorderStroke(1.dp, palette.border),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(AtuSpacing.md),
            content = content
        )
    }
}

@Composable
fun AtuSoftCard(
    modifier: Modifier = Modifier,
    darkMode: Boolean = false,
    radius: Dp = AtuRadius.cardLarge,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
    content: @Composable ColumnScope.() -> Unit
) = AtuPremiumCard(
    modifier = modifier,
    darkMode = darkMode,
    radius = radius,
    contentPadding = contentPadding,
    content = content
)

@Composable
fun AtuHeroCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false,
    minHeight: Dp = 172.dp,
    trailing: @Composable BoxScope.() -> Unit = {}
) {
    val palette = atuPalette(darkMode)
    val transition = rememberInfiniteTransition(label = "heroGlow")
    val drift by transition.animateFloat(
        initialValue = -0.2f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(4200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "drift"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(minHeight)
            .clip(RoundedCornerShape(AtuRadius.hero))
            .background(
                Brush.linearGradient(
                    listOf(
                        if (darkMode) palette.softPrimary else Color(0xFFFFFFFF),
                        if (darkMode) palette.surface else Color(0xFFFFF7FB),
                        if (darkMode) palette.background else Color(0xFFF4F1FF)
                    )
                )
            )
            .border(1.dp, palette.border, RoundedCornerShape(AtuRadius.hero))
            .padding(22.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        palette.primary.copy(alpha = if (darkMode) 0.20f else 0.10f),
                        Color.Transparent
                    )
                ),
                radius = size.width * 0.32f,
                center = Offset(size.width * drift, size.height * 0.15f)
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.7f),
            verticalArrangement = Arrangement.spacedBy(AtuSpacing.sm)
        ) {
            Text(
                text = title,
                color = palette.text,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = palette.textSecondary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        trailing()
    }
}

@Composable
fun AtuLogoHeader(
    modifier: Modifier = Modifier,
    subtitle: String = "Təhsil. Texnologiya. Gələcək."
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(R.drawable.atu_logo),
            contentDescription = "ATU loqosu",
            modifier = Modifier.size(58.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(AtuSpacing.xs)) {
            Text(
                text = "AZƏRBAYCAN\nTEXNOLOGİYA\nUNİVERSİTETİ",
                color = AtuPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                lineHeight = MaterialTheme.typography.titleMedium.lineHeight
            )
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    color = AtuTextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun AtuTopHeader(
    greeting: String,
    title: String,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false,
    subtitle: String? = null,
    action: @Composable RowScope.() -> Unit = {}
) {
    val palette = atuPalette(darkMode)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(AtuSpacing.xxs)
        ) {
            Text(
                text = greeting,
                color = palette.text,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle ?: title,
                color = palette.textSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(AtuSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            content = action
        )
    }
}

@Composable
fun AtuSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false,
    placeholder: String = "Xidmət, xəbər və ya kurs axtar...",
    trailingIcon: ImageVector? = null,
    onTrailingClick: (() -> Unit)? = null,
    readOnly: Boolean = false
) {
    val palette = atuPalette(darkMode)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        readOnly = readOnly,
        placeholder = {
            Text(
                text = placeholder,
                color = palette.textMuted,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = palette.textMuted
            )
        },
        trailingIcon = if (trailingIcon != null) {
            {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = palette.textSecondary,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(enabled = onTrailingClick != null) { onTrailingClick?.invoke() }
                        .padding(2.dp)
                )
            }
        } else null,
        singleLine = true,
        shape = RoundedCornerShape(22.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = palette.surface,
            unfocusedContainerColor = palette.surface,
            disabledContainerColor = palette.surface,
            focusedBorderColor = palette.primary.copy(alpha = 0.28f),
            unfocusedBorderColor = palette.border,
            disabledBorderColor = palette.border,
            focusedTextColor = palette.text,
            unfocusedTextColor = palette.text,
            cursorColor = palette.primary
        )
    )
}

@Composable
fun AtuInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false,
    label: String? = null,
    placeholder: String = "",
    leadingIcon: ImageVector,
    trailingIcon: ImageVector? = null,
    onTrailingClick: (() -> Unit)? = null,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None
) {
    val palette = atuPalette(darkMode)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AtuSpacing.xs)
    ) {
        if (label != null) {
            Text(
                text = label,
                color = palette.text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = palette.textMuted) },
            singleLine = singleLine,
            shape = RoundedCornerShape(AtuRadius.input),
            visualTransformation = visualTransformation,
            leadingIcon = {
                Icon(leadingIcon, contentDescription = null, tint = palette.textSecondary)
            },
            trailingIcon = if (trailingIcon != null) {
                {
                    Icon(
                        trailingIcon,
                        contentDescription = null,
                        tint = palette.textSecondary,
                        modifier = Modifier
                            .clickable(enabled = onTrailingClick != null) { onTrailingClick?.invoke() }
                    )
                }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = palette.surface,
                unfocusedContainerColor = palette.surface,
                focusedBorderColor = palette.border,
                unfocusedBorderColor = palette.border,
                focusedTextColor = palette.text,
                unfocusedTextColor = palette.text,
                cursorColor = palette.primary
            )
        )
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
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(AtuSpacing.xxs)
        ) {
            Text(
                text = title,
                color = palette.text,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = palette.textSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        if (action != null) {
            Text(
                text = action,
                color = palette.primary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(enabled = onActionClick != null) { onActionClick?.invoke() }
            )
        }
    }
}

@Composable
fun AtuStatusBadge(
    text: String,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false,
    icon: ImageVector = Icons.Outlined.Badge,
    success: Boolean = false
) {
    val palette = atuPalette(darkMode)
    val tint = if (success) AtuSuccess else palette.primary
    Surface(
        modifier = modifier,
        color = tint.copy(alpha = if (darkMode) 0.24f else 0.10f),
        shape = RoundedCornerShape(AtuRadius.pill),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.18f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
            Text(text, color = tint, style = MaterialTheme.typography.labelLarge)
        }
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
        color = if (darkMode) palette.softSurface else Color(0xFFFFFBFD),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, palette.border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = palette.primary, modifier = Modifier.size(18.dp))
            Text(text, color = palette.textSecondary, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun AtuPermissionCard(
    permissionDenied: Boolean,
    onRequestPermission: () -> Unit,
    securityNote: String,
    modifier: Modifier = Modifier
) {
    AtuSoftCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(AtuTint, AtuColors.SoftPurple))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = AtuPrimary, modifier = Modifier.size(28.dp))
            }
            Text(
                text = "Kamera icazəsi",
                color = AtuTextPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Tələbə vəsiqəsinin ön və arxa tərəfini təhlükəsiz şəkildə skan etmək üçün kamera icazəsi tələb olunur.",
                color = AtuTextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            AtuInlineNote(text = securityNote)
            if (permissionDenied) {
                Text(
                    text = "Kamera icazəsi verilmədi. Davam etmək üçün icazəni yenidən aktiv edin.",
                    color = AtuDanger,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
            AtuPrimaryButton(
                text = "Kameraya icazə ver",
                onClick = onRequestPermission,
                showArrow = true
            )
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
    AtuSoftCard(modifier = modifier, darkMode = darkMode) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.linearGradient(listOf(AtuTint, AtuColors.SoftPurple))),
                contentAlignment = Alignment.Center
            ) {
                if (student.photoUrl.isNotBlank()) {
                    AsyncImage(
                        model = student.photoUrl,
                        contentDescription = student.fullName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = student.fullName.firstOrNull()?.uppercase() ?: "A",
                        color = palette.primary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AtuSpacing.xxs)
            ) {
                Text(
                    text = student.fullName.ifBlank { "ATU tələbəsi" },
                    color = palette.text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "ID: ${student.id.ifBlank { "Təyin edilməyib" }}",
                    color = palette.textSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = student.specialty.ifBlank { "İxtisas məlumatı əlavə edilməyib" },
                    color = palette.textSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            AtuStatusBadge(text = "Aktiv", darkMode = darkMode, success = true)
        }
    }
}

@Immutable
data class AtuQuickActionItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun AtuQuickActionGrid(
    items: List<AtuQuickActionItem>,
    darkMode: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items.chunked(4).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowItems.forEach { item ->
                    Column(
                        modifier = Modifier.width(72.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            onClick = item.onClick,
                            shape = RoundedCornerShape(20.dp),
                            color = atuPalette(darkMode).surface,
                            border = BorderStroke(1.dp, atuPalette(darkMode).border),
                            shadowElevation = if (darkMode) 0.dp else 4.dp,
                            modifier = Modifier.size(60.dp).atuPressScale()
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    item.icon,
                                    contentDescription = item.title,
                                    tint = atuPalette(darkMode).primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Text(
                            text = item.title,
                            color = atuPalette(darkMode).textSecondary,
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                    }
                }
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
    AtuSoftCard(
        modifier = modifier.clickable(onClick = onClick).atuPressScale(),
        darkMode = darkMode
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AtuStatusBadge(text = if (item.type == "EVENT") "TƏDBİR" else "YENİ", darkMode = darkMode)
                Text(
                    text = item.title,
                    color = palette.text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = if (featured) 3 else 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.summary.ifBlank { item.body },
                    color = palette.textSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = if (featured) 4 else 3,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.date.ifBlank { "2 saat əvvəl" },
                    color = palette.textMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (item.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    modifier = Modifier
                        .size(if (featured) 110.dp else 92.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun AtuScheduleCard(
    lessonName: String,
    time: String,
    room: String,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false,
    dotColor: Color = AtuSuccess
) {
    val palette = atuPalette(darkMode)
    AtuSoftCard(modifier = modifier, darkMode = darkMode) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(lessonName, color = palette.text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(time, color = palette.textSecondary, style = MaterialTheme.typography.bodySmall)
            }
            Surface(
                color = AtuColors.SoftPurple,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = room,
                    color = palette.primary,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun AtuCategoryItem(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val palette = atuPalette(darkMode)
    Column(
        modifier = modifier.width(72.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            onClick = { onClick?.invoke() },
            modifier = Modifier.size(56.dp).atuPressScale(enabled = onClick != null),
            shape = RoundedCornerShape(18.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            shadowElevation = if (darkMode) 0.dp else 3.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = title, tint = palette.primary)
            }
        }
        Text(
            text = title,
            color = palette.textSecondary,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
fun AtuRecentSearchItem(
    text: String,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false,
    onClick: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null
) {
    val palette = atuPalette(darkMode)
    Surface(
        onClick = { onClick?.invoke() },
        modifier = modifier.fillMaxWidth(),
        color = palette.surface,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, palette.border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Schedule, contentDescription = null, tint = palette.textMuted, modifier = Modifier.size(18.dp))
            Text(text, color = palette.text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            Icon(
                Icons.Outlined.Close,
                contentDescription = null,
                tint = palette.textMuted,
                modifier = Modifier.clickable(enabled = onRemove != null) { onRemove?.invoke() }
            )
        }
    }
}

@Composable
fun AtuRecommendationCard(
    category: String,
    title: String,
    description: String,
    time: String,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false
) {
    val palette = atuPalette(darkMode)
    AtuSoftCard(
        modifier = modifier.width(180.dp),
        darkMode = darkMode,
        radius = AtuRadius.card
    ) {
        AtuStatusBadge(text = category, darkMode = darkMode)
        Text(title, color = palette.text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 2)
        Text(description, color = palette.textSecondary, style = MaterialTheme.typography.bodySmall, maxLines = 3, overflow = TextOverflow.Ellipsis)
        Text(time, color = palette.textMuted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun AtuSuggestionChip(
    label: String,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false,
    onClick: () -> Unit
) {
    val palette = atuPalette(darkMode)
    Surface(
        onClick = onClick,
        modifier = modifier.atuPressScale(),
        shape = RoundedCornerShape(AtuRadius.pill),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border)
    ) {
        Text(
            text = label,
            color = palette.textSecondary,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )
    }
}

@Composable
fun AtuChatBubble(
    message: ChatMessage,
    darkMode: Boolean,
    modifier: Modifier = Modifier,
    content: (@Composable ColumnScope.() -> Unit)? = null
) {
    val palette = atuPalette(darkMode)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (message.fromUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (message.fromUser) AtuColors.SoftPurple else palette.surface,
            shape = RoundedCornerShape(
                topStart = 22.dp,
                topEnd = 22.dp,
                bottomStart = if (message.fromUser) 22.dp else 8.dp,
                bottomEnd = if (message.fromUser) 8.dp else 22.dp
            ),
            border = if (message.fromUser) null else BorderStroke(1.dp, palette.border),
            shadowElevation = if (darkMode) 0.dp else 3.dp,
            modifier = Modifier.fillMaxWidth(0.86f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = message.text,
                    color = palette.text,
                    style = MaterialTheme.typography.bodyMedium
                )
                content?.invoke(this)
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
    AtuSoftCard(modifier = modifier, darkMode = darkMode) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.softPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = palette.primary)
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(title, color = palette.text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = palette.textSecondary, style = MaterialTheme.typography.bodySmall)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = AtuWhite,
                    checkedTrackColor = palette.primary,
                    uncheckedThumbColor = AtuWhite,
                    uncheckedTrackColor = palette.border
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
    val glow by transition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(3600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(154.dp)
            .clip(RoundedCornerShape(AtuRadius.cardLarge))
            .background(
                Brush.linearGradient(
                    listOf(
                        AtuColors.PrimaryDark,
                        AtuColors.Primary,
                        Color(0xFFA10D3B)
                    )
                )
            )
            .padding(18.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = size.width * 0.38f,
                center = Offset(size.width * glow, size.height * 0.1f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.06f),
                radius = size.width * 0.28f,
                center = Offset(size.width * 0.82f, size.height * 0.82f)
            )
        }

        Column(
            modifier = Modifier.align(Alignment.TopStart),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("ATU Pass", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(
                student.fullName.ifBlank { "ATU tələbəsi" },
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Text(
                "${student.specialty.ifBlank { "İxtisas məlumatı" }}${student.course.takeIf { it.isNotBlank() }?.let { "\n$it kurs" } ?: ""}",
                color = Color.White.copy(alpha = 0.82f),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
            Text(
                "ID: ${student.id.ifBlank { "0000000" }}",
                color = Color.White.copy(alpha = 0.80f),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Column(
            modifier = Modifier.align(Alignment.TopEnd),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.atu_logo),
                contentDescription = "ATU loqosu",
                modifier = Modifier.size(42.dp)
            )
            if (approved) {
                AtuStatusBadge(text = "Aktiv", darkMode = true, success = true)
            }
        }

        Surface(
            modifier = Modifier.align(Alignment.BottomEnd),
            shape = RoundedCornerShape(14.dp),
            color = Color.White.copy(alpha = 0.96f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.QrCode2, contentDescription = null, tint = AtuColors.Primary, modifier = Modifier.size(16.dp))
                Text("QR kod", color = AtuColors.Primary, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun AtuScanOverlay(
    progressText: String,
    instruction: String,
    modifier: Modifier = Modifier,
    darkScrim: Color = Color.Black.copy(alpha = 0.44f)
) {
    val transition = rememberInfiniteTransition(label = "scanOverlay")
    val scan by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1700), RepeatMode.Restart),
        label = "scan"
    )
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val frameWidth = size.width * 0.82f
            val frameHeight = frameWidth * 0.62f
            val left = (size.width - frameWidth) / 2f
            val top = (size.height - frameHeight) / 2f - 36.dp.toPx()
            val radius = 28.dp.toPx()
            drawRect(color = darkScrim)
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(frameWidth, frameHeight),
                cornerRadius = CornerRadius(radius, radius),
                blendMode = androidx.compose.ui.graphics.BlendMode.Clear
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.9f),
                topLeft = Offset(left, top),
                size = Size(frameWidth, frameHeight),
                cornerRadius = CornerRadius(radius, radius),
                style = Stroke(width = 2.dp.toPx(), join = StrokeJoin.Round)
            )
            drawLine(
                brush = Brush.horizontalGradient(listOf(Color.Transparent, Color.White, Color.Transparent)),
                start = Offset(left + 20.dp.toPx(), top + frameHeight * scan),
                end = Offset(left + frameWidth - 20.dp.toPx(), top + frameHeight * scan),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp, start = 20.dp, end = 20.dp),
            color = Color.Black.copy(alpha = 0.34f),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Kart skanı", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                AtuStatusBadge(text = progressText, darkMode = true)
            }
        }
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 160.dp),
            color = Color.Black.copy(alpha = 0.34f),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f))
        ) {
            Text(
                text = instruction,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
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
    AtuSoftCard(
        modifier = modifier
            .width(160.dp)
            .clickable(onClick = onClick)
            .atuPressScale(),
        darkMode = darkMode
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accent)
        }
        Text(title, color = palette.text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(subtitle, color = palette.textSecondary, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Immutable
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
    darkMode: Boolean,
    modifier: Modifier = Modifier,
    expandedContent: (@Composable () -> Unit)? = null
) {
    val palette = atuPalette(darkMode)
    val selectedIndex = items.indexOfFirst { it.key == selectedKey }.coerceAtLeast(0)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (expandedContent != null) {
            expandedContent()
        }

        Surface(
            shape = RoundedCornerShape(28.dp),
            color = if (darkMode) palette.surface else AtuWhite.copy(alpha = 0.96f),
            border = BorderStroke(1.dp, palette.border),
            shadowElevation = if (darkMode) 0.dp else 10.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                val itemWidth = remember(items.size) { 1f / items.size.coerceAtLeast(1) }
                val offsetFraction by animateFloatAsState(
                    targetValue = selectedIndex * itemWidth,
                    animationSpec = spring(),
                    label = "bottomIndicatorFraction"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(itemWidth)
                        .offset(x = (320.dp * offsetFraction))
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEachIndexed { index, item ->
                        val selected = selectedKey == item.key
                        val topPadding by animateDpAsState(
                            targetValue = if (selected) 0.dp else 2.dp,
                            animationSpec = tween(AtuMotion.Normal),
                            label = "topPadding"
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(24.dp))
                                .clickable { onSelect(item.key) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.padding(top = topPadding),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            if (selected) palette.softPrimary else Color.Transparent
                                        )
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = if (selected) palette.primary else palette.textMuted,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = item.title,
                                    color = if (selected) palette.primary else palette.textMuted,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AtuFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false
) = AtuSuggestionChip(
    label = label,
    modifier = modifier,
    darkMode = darkMode,
    onClick = onClick
)

@Composable
fun AtuEmptyState(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    darkMode: Boolean = false
) {
    val palette = atuPalette(darkMode)
    AtuSoftCard(modifier = modifier, darkMode = darkMode) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(palette.softPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = palette.primary)
            }
            Text(title, color = palette.text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = palette.textSecondary, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
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
    val alpha by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "alpha"
    )
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp)
                    .clip(RoundedCornerShape(AtuRadius.card))
                    .background(palette.softSurface.copy(alpha = alpha))
            )
        }
    }
}

// Backward-compatible aliases while the rest of the project keeps migrating.
typealias PremiumPalette = AtuPalette

object PremiumSpace {
    val xs = AtuSpacing.xs
    val sm = AtuSpacing.sm
    val md = AtuSpacing.lg
    val lg = AtuSpacing.xl
    val xl = AtuSpacing.xxl
}

object PremiumRadius {
    val input = AtuRadius.input
    val card = AtuRadius.card
    val hero = AtuRadius.hero
    val dock = AtuRadius.hero
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
) = AtuSectionHeader(
    title = title,
    subtitle = subtitle,
    action = action,
    darkMode = darkMode,
    modifier = modifier
)

@Composable
fun PremiumCard(
    darkMode: Boolean = false,
    modifier: Modifier = Modifier,
    radius: Dp = AtuRadius.card,
    content: @Composable ColumnScope.(AtuPalette) -> Unit
) {
    val palette = atuPalette(darkMode)
    AtuPremiumCard(
        darkMode = darkMode,
        modifier = modifier,
        radius = radius
    ) {
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
        Box(modifier = Modifier.align(Alignment.TopEnd)) { trailing() }
    }
}

@Composable
fun StatusBadge(
    text: String,
    darkMode: Boolean = false,
    icon: ImageVector = Icons.Outlined.Badge,
    modifier: Modifier = Modifier
) = AtuStatusBadge(text = text, darkMode = darkMode, icon = icon, modifier = modifier)

@Composable
fun PremiumNote(
    text: String,
    darkMode: Boolean = false,
    modifier: Modifier = Modifier
) = AtuInlineNote(text = text, darkMode = darkMode, modifier = modifier)
