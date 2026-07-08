package com.atu.campus.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.atu.campus.data.StudentDirectoryEntry
import com.atu.campus.services.AdminAttachmentEncoder
import com.atu.campus.services.AdminSelectedAttachment
import com.atu.campus.ui.components.AtuEmptyState
import com.atu.campus.ui.components.AtuFilterChip
import com.atu.campus.ui.components.AtuHeroCard
import com.atu.campus.ui.components.AtuInlineNote
import com.atu.campus.ui.components.AtuPrimaryButton
import com.atu.campus.ui.components.AtuScreen
import com.atu.campus.ui.components.AtuTopHeader
import com.atu.campus.ui.components.PremiumCard
import kotlinx.coroutines.delay

@Composable
fun SmsAdminScreen(
    results: List<StudentDirectoryEntry>,
    searching: Boolean,
    message: String,
    sending: Boolean,
    onSearch: (String) -> Unit,
    onSend: (List<String>, String, String, String, AdminSelectedAttachment?) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("DIRECT") }
    var attachment by remember { mutableStateOf<AdminSelectedAttachment?>(null) }
    var pendingAttachmentUri by remember { mutableStateOf<Uri?>(null) }
    val selectedIds = remember { mutableStateListOf<String>() }
    val attachmentPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        pendingAttachmentUri = uri
    }

    LaunchedEffect(query) {
        if (query.length < 2) return@LaunchedEffect
        delay(260)
        onSearch(query)
    }

    LaunchedEffect(pendingAttachmentUri) {
        val uri = pendingAttachmentUri ?: return@LaunchedEffect
        attachment = AdminAttachmentEncoder.encode(context, uri)
        pendingAttachmentUri = null
    }

    AtuScreen(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) { palette ->
        AtuTopHeader(greeting = "ATU Campus", title = "SMS Admin") {
            IconButton(onClick = onLogout) {
                Icon(Icons.Outlined.Logout, contentDescription = "x", tint = palette.primary)
            }
        }
        AtuHeroCard(
            title = "Hdfli bildiri gndr",
            subtitle = "Tlbni axtarn, sein v ona birbaa mesaj, snd v ya elan gndrin.",
            minHeight = 184.dp
        ) {
            Icon(
                imageVector = Icons.Outlined.PersonSearch,
                contentDescription = null,
                tint = palette.primary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(34.dp)
            )
        }

        PremiumCard(radius = 26.dp) {
            Text(
                text = "Tlb axtar",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = palette.text
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it.take(48) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ad, soyad, i nmrsi v ya qrup") },
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = palette.primary,
                    unfocusedBorderColor = palette.border,
                    focusedContainerColor = palette.surfaceSoft,
                    unfocusedContainerColor = palette.surfaceSoft
                )
            )
            Spacer(Modifier.height(14.dp))
            when {
                searching -> Text("Axtarlr...", color = palette.muted, style = MaterialTheme.typography.bodyMedium)
                results.isEmpty() && query.length >= 2 -> {
                    AtuEmptyState(
                        title = "Ntic taplmad",
                        subtitle = "Axtar szn dyiib yenidn yoxlayn.",
                        icon = Icons.Outlined.PersonSearch
                    )
                }
                else -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        results.forEach { student ->
                            val selected = selectedIds.contains(student.id)
                            StudentTargetRow(
                                student = student,
                                selected = selected,
                                onToggle = {
                                    if (selected) selectedIds.remove(student.id) else selectedIds.add(student.id)
                                }
                            )
                        }
                    }
                }
            }
        }

        PremiumCard(radius = 26.dp) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(
                    "DIRECT" to "Mesaj",
                    "ANNOUNCEMENT" to "Elan",
                    "EVENT" to "Tdbir"
                ).forEach { item ->
                    AtuFilterChip(
                        label = item.second,
                        selected = type == item.first,
                        onClick = { type = item.first }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it.take(120) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ba?l?q") },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = palette.primary,
                    unfocusedBorderColor = palette.border,
                    focusedContainerColor = palette.surfaceSoft,
                    unfocusedContainerColor = palette.surfaceSoft
                )
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = body,
                onValueChange = { body = it.take(2000) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("M?tn") },
                minLines = 4,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = palette.primary,
                    unfocusedBorderColor = palette.border,
                    focusedContainerColor = palette.surfaceSoft,
                    unfocusedContainerColor = palette.surfaceSoft
                )
            )
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = { attachmentPicker.launch("*/*") }, shape = RoundedCornerShape(18.dp)) {
                Icon(Icons.Outlined.AttachFile, contentDescription = null, tint = palette.primary)
                Text(
                    text = attachment?.fileName ?: "Cihazdan s?n?d ?lav? et",
                    modifier = Modifier.padding(start = 8.dp),
                    color = palette.text,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        AtuInlineNote(
            text = if (message.isBlank()) {
                "Se?il?n t?l?b?l?r bildiri?i birba?a t?tbiq daxilind? g?r?c?k."
            } else {
                message
            }
        )

        AtuPrimaryButton(
            text = "Bildiri?i g?nd?r",
            enabled = selectedIds.isNotEmpty() && title.length >= 3 && body.length >= 4,
            loading = sending,
            onClick = { onSend(selectedIds.toList(), title, body, type, attachment) }
        )
    }
}

@Composable
private fun StudentTargetRow(
    student: StudentDirectoryEntry,
    selected: Boolean,
    onToggle: () -> Unit
) {
    PremiumCard(
        radius = 22.dp,
        modifier = Modifier.clickable(onClick = onToggle)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (student.photoUrl.isNotBlank()) {
                AsyncImage(
                    model = student.photoUrl,
                    contentDescription = student.fullName,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(18.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = student.fullName.firstOrNull()?.uppercase() ?: "T",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(student.fullName, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                Text("ID ${student.id}  ${student.group}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                Text(student.specialty.ifBlank { "xtisas tyin edilmyib" }, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(
                        Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
