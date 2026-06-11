package com.atu.campus.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.atu.campus.ui.components.AtuFilterChip
import com.atu.campus.ui.components.AtuHeroCard
import com.atu.campus.ui.components.AtuInlineNote
import com.atu.campus.ui.components.AtuPrimaryButton
import com.atu.campus.ui.components.AtuScreen
import com.atu.campus.ui.components.AtuTopHeader
import com.atu.campus.ui.components.PremiumCard
import com.atu.campus.services.AdminAttachmentEncoder
import com.atu.campus.services.AdminSelectedAttachment
import com.atu.campus.services.AdminImageEncoder
import com.atu.campus.services.AdminSelectedImage
import kotlinx.coroutines.launch

@Composable
fun NewsAdminScreen(
    publishing: Boolean,
    message: String,
    onPublish: (String, String, String, String, AdminSelectedImage?) -> Unit,
    onPostToOfficialGroup: (String, AdminSelectedAttachment?) -> Unit,
    onLogout: () -> Unit
) {
    var type by remember { mutableStateOf("NEWS") }
    var title by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<AdminSelectedImage?>(null) }
    var officialMessage by remember { mutableStateOf("") }
    var selectedOfficialAttachment by remember { mutableStateOf<AdminSelectedAttachment?>(null) }
    var imageMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val encoded = AdminImageEncoder.encode(context, uri)
            if (encoded != null) {
                selectedImage = encoded
                imageMessage = "Şəkil hazırdır və cihazdan yüklənəcək."
            } else {
                imageMessage = "Şəkil oxunmadı. Başqa şəkil seçin."
            }
        }
    }
    val officialAttachmentPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            selectedOfficialAttachment = AdminAttachmentEncoder.encode(context, uri)
        }
    }
    val types = listOf(
        "NEWS" to "X\u0259b\u0259r",
        "ANNOUNCEMENT" to "Elan",
        "EVENT" to "T\u0259dbir"
    )

    AtuScreen(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) { palette ->
        AtuTopHeader(greeting = "ATU Campus", title = "News Admin") {
            IconButton(onClick = onLogout) {
                Icon(Icons.Outlined.Logout, contentDescription = "\u00C7\u0131x\u0131\u015F", tint = palette.primary)
            }
        }
        AtuHeroCard(
            title = "Yeni m\u0259zmun yarat",
            subtitle = "Yay\u0131mland\u0131\u011F\u0131 anda t\u0259l\u0259b\u0259l\u0259rin ATU Campus lentind\u0259 g\u00F6r\u00FCn\u0259c\u0259k.",
            minHeight = 180.dp
        )

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            types.forEach { item ->
                AtuFilterChip(
                    label = item.second,
                    selected = type == item.first,
                    onClick = { type = item.first }
                )
            }
        }

        PremiumCard(radius = 26.dp) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(
                    when (type) {
                        "EVENT" -> Icons.Outlined.Event
                        "ANNOUNCEMENT" -> Icons.Outlined.Campaign
                        else -> Icons.Outlined.Newspaper
                    },
                    contentDescription = null,
                    tint = palette.primary
                )
                Text(
                    text = types.first { it.first == type }.second,
                    color = palette.text,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black
                )
            }
            AdminField("Ba\u015Fl\u0131q", title, { title = it.take(120) }, singleLine = true)
            AdminField("Q\u0131sa t\u0259svir", summary, { summary = it.take(220) }, singleLine = false)
            AdminField("Tam m\u0259tn", body, { body = it.take(4000) }, singleLine = false, minLines = 5)
            OutlinedButton(
                onClick = { imagePicker.launch("image/*") },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.outlinedButtonColors()
            ) {
                Icon(Icons.Outlined.Collections, contentDescription = null, tint = palette.primary)
                Text(
                    text = if (selectedImage == null) "Cihazdan şəkil seç" else "Şəkli dəyiş",
                    modifier = Modifier.padding(start = 8.dp),
                    color = palette.text,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (selectedImage != null) {
                AsyncImage(
                    model = selectedImage?.uri,
                    contentDescription = "Seçilmiş şəkil",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(palette.surfaceSoft),
                    contentScale = ContentScale.Crop
                )
            }
            if (imageMessage.isNotBlank()) {
                Text(
                    text = imageMessage,
                    color = palette.muted,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (message.isNotBlank()) AtuInlineNote(text = message)
        AtuPrimaryButton(
            text = "Yay\u0131mla v\u0259 bildiri\u015F g\u00F6nd\u0259r",
            enabled = title.length >= 4 && body.length >= 8,
            loading = publishing,
            onClick = { onPublish(type, title, summary, body, selectedImage) }
        )
        PremiumCard(radius = 26.dp) {
            Text(
                text = "ATU Rəsmi Qrupa paylaş",
                color = palette.text,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )
            AdminField("Mesaj", officialMessage, { officialMessage = it.take(2000) }, singleLine = false, minLines = 4)
            OutlinedButton(
                onClick = { officialAttachmentPicker.launch(arrayOf("*/*")) },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.outlinedButtonColors()
            ) {
                Icon(Icons.Outlined.Collections, contentDescription = null, tint = palette.primary)
                Text(
                    text = if (selectedOfficialAttachment == null) "Fayl, şəkil, səs və ya video seç" else selectedOfficialAttachment?.fileName.orEmpty(),
                    modifier = Modifier.padding(start = 8.dp),
                    color = palette.text,
                    fontWeight = FontWeight.SemiBold
                )
            }
            AtuPrimaryButton(
                text = "Rəsmi qrupa göndər",
                enabled = officialMessage.isNotBlank() || selectedOfficialAttachment != null,
                loading = false,
                onClick = {
                    onPostToOfficialGroup(officialMessage, selectedOfficialAttachment)
                    officialMessage = ""
                    selectedOfficialAttachment = null
                }
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun AdminField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors()
    )
}
