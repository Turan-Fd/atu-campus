package com.atu.campus.ui.screens

import android.Manifest
import android.media.MediaPlayer
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MarkChatRead
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.atu.campus.R
import com.atu.campus.data.AtuNews
import com.atu.campus.data.CampusChatMessage
import com.atu.campus.data.CampusChatReaction
import com.atu.campus.data.CampusChatRoom
import com.atu.campus.data.CampusNotificationItem
import com.atu.campus.data.ChatMessage
import com.atu.campus.data.NotificationOpenPayload
import com.atu.campus.data.StudentProfile
import com.atu.campus.services.AiChatService
import com.atu.campus.services.BackendConfigStore
import com.atu.campus.services.BackendHealthService
import com.atu.campus.services.CampusCommunityService
import com.atu.campus.services.CampusContentService
import com.atu.campus.services.CampusNotificationService
import com.atu.campus.services.FcmTokenSyncService
import com.atu.campus.ui.components.AtuBottomNavItem
import com.atu.campus.ui.components.AtuBottomNavigation
import com.atu.campus.ui.components.AtuEmptyState
import com.atu.campus.ui.components.AtuFilterChip
import com.atu.campus.ui.components.AtuHeroCard
import com.atu.campus.ui.components.AtuModuleCard
import com.atu.campus.ui.components.AtuMotion
import com.atu.campus.ui.components.AtuNewsCard
import com.atu.campus.ui.components.AtuPassCard
import com.atu.campus.ui.components.AtuPremiumCard
import com.atu.campus.ui.components.AtuScreen
import com.atu.campus.ui.components.AtuSearchBar
import com.atu.campus.ui.components.AtuSectionHeader
import com.atu.campus.ui.components.AtuSettingsRow
import com.atu.campus.ui.components.AtuSkeletonLoader
import com.atu.campus.ui.components.AtuStatusBadge
import com.atu.campus.ui.components.AtuStudentCard
import com.atu.campus.ui.components.AtuTopHeader
import com.atu.campus.ui.components.atuPalette
import com.atu.campus.ui.components.atuPressScale
import com.atu.campus.ui.theme.AtuDanger
import com.atu.campus.ui.theme.AtuMagenta
import com.atu.campus.ui.theme.AtuPrimary
import com.atu.campus.ui.theme.AtuSuccess
import com.atu.campus.ui.theme.AtuTint
import com.atu.campus.ui.theme.AtuWhite
import com.atu.campus.ui.theme.AtuWine
import com.atu.campus.services.AdminSelectedAttachment
import com.atu.campus.services.AdminAttachmentEncoder
import com.atu.campus.services.VoiceMessageEncoder
import com.atu.campus.services.VoiceMessageRecorder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class CampusTab(val key: String, val title: String, val icon: ImageVector) {
    Home("home", "Ana", Icons.Outlined.Home),
    Chat("chat", "Chat", Icons.Outlined.ChatBubbleOutline),
    Assistant("assistant", "AI", Icons.Outlined.AutoAwesome),
    Pass("pass", "Pass", Icons.Outlined.CreditCard),
    Settings("settings", "Parametr", Icons.Outlined.Settings)
}

private data class CampusModule(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accent: Color,
    val action: CampusTab? = null
)

private data class AccessLog(
    val title: String,
    val subtitle: String,
    val time: String,
    val type: String,
    val success: Boolean
)

@Composable
fun HomeScreen(
    profile: StudentProfile?,
    launchNotification: NotificationOpenPayload? = null,
    onLaunchNotificationConsumed: () -> Unit = {},
    onReset: () -> Unit
) {
    val student = profile ?: return
    val context = LocalContext.current
    val backendConfigStore = remember { BackendConfigStore(context.applicationContext) }
    val backendHealthService = remember { BackendHealthService(context.applicationContext) }
    val contentService = remember { CampusContentService(context.applicationContext) }
    val communityService = remember { CampusCommunityService(context.applicationContext) }
    val notificationService = remember { CampusNotificationService(context.applicationContext) }
    val aiChatService = remember { AiChatService(context.applicationContext) }
    val voiceRecorder = remember { VoiceMessageRecorder(context.applicationContext) }
    val coroutineScope = rememberCoroutineScope()
    val contentPreferences = remember {
        context.getSharedPreferences("atu_campus_content", android.content.Context.MODE_PRIVATE)
    }

    var selectedTab by remember { mutableStateOf(CampusTab.Home) }
    var darkMode by remember { mutableStateOf(false) }
    var news by remember { mutableStateOf<List<AtuNews>>(emptyList()) }
    var loadingNews by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Ham\u0131s\u0131") }
    var selectedNews by remember { mutableStateOf<AtuNews?>(null) }
    var showSearchScreen by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var backendUrl by remember { mutableStateOf(backendConfigStore.getPreferredBaseUrl()) }
    var showBackendDialog by remember { mutableStateOf(false) }
    var backendStatusMessage by remember { mutableStateOf("") }
    var backendTesting by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf<List<CampusNotificationItem>>(emptyList()) }
    var selectedNotification by remember { mutableStateOf<CampusNotificationItem?>(null) }
    var rooms by remember { mutableStateOf<List<CampusChatRoom>>(emptyList()) }
    var selectedRoom by remember { mutableStateOf<CampusChatRoom?>(null) }
    var highlightedMessageId by remember { mutableStateOf("") }
    var showProfileScreen by remember { mutableStateOf(false) }
    var pendingRoomId by remember { mutableStateOf("") }
    var pendingMessageId by remember { mutableStateOf("") }
    var roomMessages by remember { mutableStateOf<List<CampusChatMessage>>(emptyList()) }
    var chatInput by remember { mutableStateOf("") }
    var recordingVoice by remember { mutableStateOf(false) }
    var editingMessage by remember { mutableStateOf<CampusChatMessage?>(null) }
    var editText by remember { mutableStateOf("") }
    var aiInput by remember { mutableStateOf("") }
    var aiSending by remember { mutableStateOf(false) }
    var aiMessages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    "Salam. M\u0259n ATU Campus AI k\u00f6m\u0259k\u00e7isiy\u0259m. Universitet, x\u0259b\u0259rl\u0259r v\u0259 t\u0259l\u0259b\u0259 xidm\u0259tl\u0259ri il\u0259 ba\u011fl\u0131 sual\u0131n\u0131 yaz.",
                    false
                )
            )
        )
    }
    val roomCache = remember { mutableStateMapOf<String, List<CampusChatMessage>>() }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        notificationsEnabled = granted
    }
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            recordingVoice = voiceRecorder.start()
        }
    }
    val chatAttachmentPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null || selectedRoom == null) return@rememberLauncherForActivityResult
        coroutineScope.launch {
            val attachment = AdminAttachmentEncoder.encode(context, uri)
            if (attachment != null) {
                val room = selectedRoom ?: return@launch
                communityService.sendMessage(
                    roomId = room.id,
                    studentId = student.id,
                    text = chatInput.trim(),
                    attachment = attachment
                )
                chatInput = ""
                val items = communityService.fetchMessages(room.id)
                roomCache[room.id] = items
                roomMessages = items
            }
        }
    }

    fun filteredNews(): List<AtuNews> {
        return news.filter { item ->
            val textMatch = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.summary.contains(searchQuery, ignoreCase = true) ||
                item.body.contains(searchQuery, ignoreCase = true)
            val filterMatch = when (selectedFilter) {
                "X\u0259b\u0259rl\u0259r" -> item.type == "NEWS"
                "Elanlar" -> item.type == "ANNOUNCEMENT"
                "T\u0259dbirl\u0259r" -> item.type == "EVENT"
                else -> true
            }
            textMatch && filterMatch
        }
    }

    fun refreshRoomMessages(room: CampusChatRoom) {
        coroutineScope.launch {
            val items = communityService.fetchMessages(room.id)
            roomCache[room.id] = items
            roomMessages = items
        }
    }

    fun openRoom(roomId: String, messageId: String = "") {
        val room = rooms.firstOrNull { it.id == roomId }
        if (room == null) {
            pendingRoomId = roomId
            pendingMessageId = messageId
            selectedTab = CampusTab.Chat
            return
        }
        selectedTab = CampusTab.Chat
        selectedRoom = room
        highlightedMessageId = messageId
        pendingRoomId = ""
        pendingMessageId = ""
        roomMessages = roomCache[room.id] ?: emptyList()
        refreshRoomMessages(room)
    }

    fun submitAi(text: String = aiInput) {
        val question = text.trim()
        if (question.isBlank() || aiSending) return
        aiInput = ""
        aiMessages = aiMessages + ChatMessage(question, true)
        aiSending = true
        coroutineScope.launch {
            aiMessages = aiMessages + ChatMessage(aiChatService.sendMessage(question), false)
            aiSending = false
        }
    }

    fun toggleVoiceRecordingForRoom() {
        if (recordingVoice) {
            recordingVoice = false
            val file = voiceRecorder.stop()
            if (file != null && selectedRoom != null) {
                coroutineScope.launch {
                    val attachment = VoiceMessageEncoder.encode(file)
                    if (attachment != null) {
                        communityService.sendMessage(
                            roomId = selectedRoom!!.id,
                            studentId = student.id,
                            text = "",
                            attachment = attachment
                        )
                        refreshRoomMessages(selectedRoom!!)
                    }
                    file.delete()
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            } else {
                recordingVoice = voiceRecorder.start()
            }
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        notificationService.ensureChannel()
        loadingNews = true
        news = contentService.fetchContent()
        notifications = communityService.fetchNotifications(student.id)
        rooms = communityService.fetchRooms(student.id)
        loadingNews = false
        val newest = news.maxOfOrNull { it.createdAt } ?: 0L
        val latestNotification = notifications.maxOfOrNull { it.createdAt } ?: 0L
        if (contentPreferences.getLong("latest_content", 0L) == 0L && newest > 0L) {
            contentPreferences.edit().putLong("latest_content", newest).apply()
        }
        if (contentPreferences.getLong("latest_notification", 0L) == 0L && latestNotification > 0L) {
            contentPreferences.edit().putLong("latest_notification", latestNotification).apply()
        }
        while (true) {
            delay(2_500)
            val lastSeen = contentPreferences.getLong("latest_content", 0L)
            val freshItems = contentService.fetchContent(lastSeen)
            if (freshItems.isNotEmpty()) {
                freshItems.sortedBy { it.createdAt }.forEach(notificationService::show)
                news = contentService.fetchContent()
                contentPreferences.edit().putLong("latest_content", freshItems.maxOf { it.createdAt }).apply()
            }
            val lastNotificationSeen = contentPreferences.getLong("latest_notification", 0L)
            notifications = communityService.fetchNotifications(student.id)
            val freshNotifications = notifications.filter { it.createdAt > lastNotificationSeen }
            if (freshNotifications.isNotEmpty()) {
                freshNotifications.sortedBy { it.createdAt }.forEach(notificationService::show)
                contentPreferences.edit().putLong("latest_notification", freshNotifications.maxOf { it.createdAt }).apply()
            }
            rooms = communityService.fetchRooms(student.id)
            selectedRoom?.let { room ->
                val updates = communityService.fetchMessages(room.id)
                roomCache[room.id] = updates
                roomMessages = updates
            }
        }
    }

    LaunchedEffect(selectedRoom?.id) {
        val room = selectedRoom ?: return@LaunchedEffect
        while (selectedRoom?.id == room.id) {
            val updates = communityService.fetchMessages(room.id)
            roomCache[room.id] = updates
            roomMessages = updates
            delay(1200)
        }
    }

    LaunchedEffect(rooms, pendingRoomId) {
        if (pendingRoomId.isBlank()) return@LaunchedEffect
        val room = rooms.firstOrNull { it.id == pendingRoomId } ?: return@LaunchedEffect
        openRoom(room.id, pendingMessageId)
    }

    LaunchedEffect(launchNotification?.id) {
        val payload = launchNotification ?: return@LaunchedEffect
        showNotifications = false
        selectedNews = null
        showSearchScreen = false
        if (recordingVoice) {
            voiceRecorder.stopAndDelete()
            recordingVoice = false
        }
        selectedRoom = null
        if (payload.roomId.isNotBlank()) {
            openRoom(payload.roomId, payload.messageId)
        } else {
            selectedNotification = CampusNotificationItem(
                id = payload.id,
                type = payload.type,
                title = payload.title,
                body = payload.body,
                imageUrl = payload.imageUrl,
                attachmentName = payload.attachmentName,
                createdAt = payload.createdAt,
                roomId = payload.roomId,
                messageId = payload.messageId,
                mediaType = payload.mediaType
            )
        }
        onLaunchNotificationConsumed()
    }

    BackHandler(
        enabled = selectedNews != null || selectedNotification != null || selectedRoom != null || showSearchScreen || showNotifications || showProfileScreen || selectedTab != CampusTab.Home
    ) {
        when {
            selectedNews != null -> selectedNews = null
            selectedNotification != null -> selectedNotification = null
            selectedRoom != null -> {
                if (recordingVoice) {
                    voiceRecorder.stopAndDelete()
                    recordingVoice = false
                }
                selectedRoom = null
            }
            showProfileScreen -> showProfileScreen = false
            showSearchScreen -> showSearchScreen = false
            showNotifications -> showNotifications = false
            selectedTab != CampusTab.Home -> selectedTab = CampusTab.Home
        }
    }

    Scaffold(
        containerColor = atuPalette(darkMode).background,
        bottomBar = {
            AnimatedVisibility(visible = selectedNews == null) {
                AtuBottomNavigation(
                    items = CampusTab.entries.map { AtuBottomNavItem(it.key, it.title, it.icon) },
                    selectedKey = selectedTab.key,
                    onSelect = { key ->
                        if (recordingVoice) {
                            voiceRecorder.stopAndDelete()
                            recordingVoice = false
                        }
                        selectedNews = null
                        selectedNotification = null
                        selectedRoom = null
                        showProfileScreen = false
                        showSearchScreen = false
                        showNotifications = false
                        selectedTab = CampusTab.entries.first { it.key == key }
                    },
                    darkMode = darkMode,
                    expandedContent = if (selectedTab == CampusTab.Assistant) {
                        {
                            AiDockInput(
                                input = aiInput,
                                onInputChange = { aiInput = it },
                                isSending = aiSending,
                                onSend = { submitAi() },
                                darkMode = darkMode
                            )
                        }
                    } else if (selectedRoom != null && !selectedRoom!!.readOnly) {
                        {
                            PromptDockInput(
                                input = chatInput,
                                onInputChange = { chatInput = it },
                                isSending = false,
                                onSend = {
                                    val room = selectedRoom
                                    val text = chatInput.trim()
                                    if (room != null && text.isNotBlank()) {
                                        chatInput = ""
                                        coroutineScope.launch {
                                            communityService.sendMessage(room.id, student.id, text)
                                            refreshRoomMessages(room)
                                        }
                                    }
                                },
                                onAttachmentAction = { chatAttachmentPicker.launch(arrayOf("*/*")) },
                                onVoiceAction = { toggleVoiceRecordingForRoom() },
                                isRecording = recordingVoice,
                                darkMode = darkMode,
                                placeholder = "Qrupa mesaj yaz"
                            )
                        }
                    } else {
                        null
                    }
                )
            }
        }
    ) { padding ->
        when {
            selectedNews != null -> NewsDetailContent(
                item = selectedNews!!,
                darkMode = darkMode,
                onBack = { selectedNews = null },
                modifier = Modifier.padding(padding)
            )

            selectedNotification != null -> NotificationDetailScreen(
                item = selectedNotification!!,
                darkMode = darkMode,
                onBack = { selectedNotification = null },
                modifier = Modifier.padding(padding)
            )

            showNotifications -> NotificationsScreen(
                items = notifications,
                darkMode = darkMode,
                onNotificationClick = {
                    showNotifications = false
                    if (it.roomId.isNotBlank()) openRoom(it.roomId, it.messageId) else selectedNotification = it
                },
                onBack = { showNotifications = false },
                modifier = Modifier.padding(padding)
            )

            showSearchScreen -> SearchScreen(
                news = filteredNews(),
                loading = loadingNews,
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                selectedFilter = selectedFilter,
                onFilterChange = { selectedFilter = it },
                onNewsClick = { selectedNews = it },
                darkMode = darkMode,
                onBack = { showSearchScreen = false },
                modifier = Modifier.padding(padding)
            )

            selectedRoom != null -> ChatRoomScreen(
                student = student,
                room = selectedRoom!!,
                messages = roomMessages,
                highlightedMessageId = highlightedMessageId,
                darkMode = darkMode,
                onBack = { selectedRoom = null },
                onReact = { messageId, emoji ->
                    coroutineScope.launch {
                        communityService.toggleReaction(messageId, student.id, emoji)
                        refreshRoomMessages(selectedRoom!!)
                    }
                },
                onEdit = { message ->
                    editingMessage = message
                    editText = message.text
                },
                onDelete = { message ->
                    coroutineScope.launch {
                        communityService.updateMessage(message.id, student.id, "delete")
                        refreshRoomMessages(selectedRoom!!)
                    }
                },
                modifier = Modifier.padding(padding)
            )

            else -> {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        (fadeIn(tween(AtuMotion.normalMillis)) + scaleIn(initialScale = 0.985f))
                            .togetherWith(fadeOut(tween(AtuMotion.fastMillis)) + scaleOut(targetScale = 1.01f))
                    },
                    label = "campusTabs"
                ) { tab ->
                    when (tab) {
                        CampusTab.Home -> HomeTab(
                            student = student,
                            darkMode = darkMode,
                            onOpenNotifications = { showNotifications = true },
                            onOpenProfile = { showProfileScreen = true },
                            onOpenSearch = { showSearchScreen = true },
                            onSelectModule = { selectedTab = it },
                            modifier = Modifier.padding(padding)
                        )

                        CampusTab.Chat -> ChatListTab(
                            rooms = rooms,
                            darkMode = darkMode,
                            onOpenRoom = { room ->
                                selectedRoom = room
                                highlightedMessageId = ""
                                roomMessages = roomCache[room.id] ?: emptyList()
                                refreshRoomMessages(room)
                            },
                            modifier = Modifier.padding(padding)
                        )

                        CampusTab.Assistant -> AssistantTab(
                            messages = aiMessages,
                            isSending = aiSending,
                            onSubmit = ::submitAi,
                            darkMode = darkMode,
                            modifier = Modifier.padding(padding)
                        )

                        CampusTab.Pass -> PassTab(student, darkMode, Modifier.padding(padding))
                        CampusTab.Settings -> SettingsTab(
                            student = student,
                            darkMode = darkMode,
                            notificationsEnabled = notificationsEnabled,
                            backendUrl = backendUrl,
                            onDarkModeChange = { darkMode = it },
                            onNotificationsChange = { notificationsEnabled = it },
                            onBackendClick = { showBackendDialog = true },
                            onReset = onReset,
                            modifier = Modifier.padding(padding)
                        )
                    }
                }
            }
        }
    }

    if (showProfileScreen) {
        AlertDialog(
            onDismissRequest = { showProfileScreen = false },
            confirmButton = {
                TextButton(onClick = { showProfileScreen = false }) {
                    Text("Bağla")
                }
            },
            text = {
                ProfileOverviewContent(student = student, darkMode = darkMode)
            }
        )
    }

    if (editingMessage != null) {
        AlertDialog(
            onDismissRequest = { editingMessage = null },
            title = { Text("Mesajı redaktə et") },
            text = {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it.take(2000) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(20.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val message = editingMessage ?: return@TextButton
                        coroutineScope.launch {
                            communityService.updateMessage(message.id, student.id, "edit", editText)
                            selectedRoom?.let { refreshRoomMessages(it) }
                        }
                        editingMessage = null
                    }
                ) {
                    Text("Yadda saxla")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingMessage = null }) {
                    Text("Ləğv et")
                }
            }
        )
    }

    if (showBackendDialog) {
        AlertDialog(
            onDismissRequest = { showBackendDialog = false },
            title = { Text("Backend ünvanı") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Telefonlar eyni Wi‑Fi-də olanda kompüterinin lokal ünvanını yaz. Nümunə: http://192.168.1.25:8080",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = backendUrl,
                        onValueChange = { backendUrl = it.take(120) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("http://192.168.x.x:8080") },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp)
                    )
                    if (backendStatusMessage.isNotBlank()) {
                        Text(
                            backendStatusMessage,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        backendConfigStore.savePreferredBaseUrl(backendUrl)
                        backendStatusMessage = "Ünvan yadda saxlandı."
                        showBackendDialog = false
                    }
                ) {
                    Text("Yadda saxla")
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            backendTesting = true
                            coroutineScope.launch {
                                val result = backendHealthService.testConnection()
                                backendTesting = false
                                backendStatusMessage = result.second
                            }
                        }
                    ) {
                        Text(if (backendTesting) "Yoxlanır..." else "Test et")
                    }
                    TextButton(onClick = { showBackendDialog = false }) {
                        Text("Bağla")
                    }
                }
            }
        )
    }
}

@Composable
private fun HomeTab(
    student: StudentProfile,
    darkMode: Boolean,
    onOpenNotifications: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenSearch: () -> Unit,
    onSelectModule: (CampusTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val topGradient = Brush.verticalGradient(
        colors = if (darkMode) {
            listOf(
                AtuWine.copy(alpha = 0.90f),
                AtuPrimary.copy(alpha = 0.26f),
                AtuMagenta.copy(alpha = 0.08f),
                Color.Transparent
            )
        } else {
            listOf(
                AtuPrimary.copy(alpha = 0.26f),
                AtuPrimary.copy(alpha = 0.11f),
                AtuMagenta.copy(alpha = 0.05f),
                Color.Transparent
            )
        }
    )
    val modules = listOf(
        CampusModule("Qrup chatı", "Canlı söhbət", Icons.Outlined.ChatBubbleOutline, Color(0xFF2563EB), CampusTab.Chat),
        CampusModule("ATU Pass", "Turniket tarixi", Icons.Outlined.CreditCard, AtuMagenta, CampusTab.Pass),
        CampusModule("AI köməkçi", "Sualını yaz", Icons.Outlined.AutoAwesome, Color(0xFF0F766E), CampusTab.Assistant),
        CampusModule("Kampus xəritəsi", "A blok və xidmətlər", Icons.Outlined.Map, Color(0xFF4F46E5))
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(atuPalette(darkMode).background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(258.dp)
                .background(topGradient)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 112.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                AtuTopHeader(
                    greeting = "Salam",
                    title = student.name.ifBlank { "Tələbə" },
                    darkMode = darkMode,
                    modifier = Modifier.statusBarsPadding()
                ) {
                    HeaderStudentPhoto(student, onClick = onOpenProfile)
                    HeaderIconButton(Icons.Outlined.Notifications, darkMode, onOpenNotifications)
                }
            }
            item { SearchEntryRow(darkMode = darkMode, onClick = onOpenSearch) }
            item {
                AtuHeroCard(
                    title = "Profil aktivdir",
                    subtitle = "ID ${student.id.ifBlank { "Təyin edilməyib" }} • Qrup ${student.group.ifBlank { "Təyin edilməyib" }}",
                    darkMode = darkMode
                ) {
                    AtuStatusBadge(
                        text = "Təsdiqlənib",
                        darkMode = darkMode,
                        success = true,
                        icon = Icons.Outlined.VerifiedUser,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                    Image(
                        painter = painterResource(R.drawable.atu_logo),
                        contentDescription = "ATU",
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(58.dp)
                            .clip(RoundedCornerShape(18.dp))
                    )
                }
            }
            item { AtuStudentCard(student, darkMode = darkMode) }
            item { AtuSectionHeader("Campus modulları", action = "Canlı xidmətlər", darkMode = darkMode) }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(modules) { module ->
                        Box(
                            modifier = Modifier.clickable(enabled = module.action != null) {
                                module.action?.let(onSelectModule)
                            }
                        ) {
                            AtuModuleCard(
                                title = module.title,
                                subtitle = module.subtitle,
                                icon = module.icon,
                                accent = module.accent,
                                darkMode = darkMode
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchEntryRow(
    darkMode: Boolean,
    onClick: () -> Unit
) {
    val palette = atuPalette(darkMode)
    Surface(
        onClick = onClick,
        color = palette.surface,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, palette.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = palette.muted)
            Text("ATU xəbərlərində axtar", color = palette.muted, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun SearchScreen(
    news: List<AtuNews>,
    loading: Boolean,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    onNewsClick: (AtuNews) -> Unit,
    darkMode: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(atuPalette(darkMode).background),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderIconButton(Icons.Outlined.ArrowBack, darkMode, onBack)
                Text(
                    text = "Axtarış",
                    color = atuPalette(darkMode).text,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black
                )
            }
        }
        item { AtuSearchBar(searchQuery, onSearchChange, darkMode = darkMode, placeholder = "Xəbər, elan və tədbir axtar") }
        item {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("Hamısı", "Xəbərlər", "Elanlar", "Tədbirlər").forEach { label ->
                    AtuFilterChip(label, selectedFilter == label, { onFilterChange(label) }, darkMode = darkMode)
                }
            }
        }
        when {
            loading -> item { AtuSkeletonLoader(darkMode = darkMode) }
            news.isEmpty() -> item {
                AtuEmptyState(
                    title = "Məzmun tapılmadı",
                    subtitle = "Bu axtarışa uyğun xəbər, elan və ya tədbir yoxdur.",
                    icon = Icons.Outlined.Search,
                    darkMode = darkMode
                )
            }

            else -> {
                item {
                    AtuSectionHeader("Seçilmiş nəticə", action = "${news.size} nəticə", darkMode = darkMode)
                    Spacer(Modifier.height(10.dp))
                    AtuNewsCard(news.first(), onClick = { onNewsClick(news.first()) }, darkMode = darkMode, featured = true)
                }
                items(news.drop(1)) { item ->
                    AtuNewsCard(item, onClick = { onNewsClick(item) }, darkMode = darkMode)
                }
            }
        }
    }
}

@Composable
private fun NotificationsScreen(
    items: List<CampusNotificationItem>,
    darkMode: Boolean,
    onNotificationClick: (CampusNotificationItem) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = atuPalette(darkMode)
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderIconButton(Icons.Outlined.ArrowBack, darkMode, onBack)
                Column {
                    Text("Bildirişlər", color = palette.text, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                    Text("Xəbər, elan və tədbir axını", color = palette.muted, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        if (items.isEmpty()) {
            item {
                AtuEmptyState(
                    title = "Bildiriş yoxdur",
                    subtitle = "Yeni elan və tədbirlər burada görünəcək.",
                    icon = Icons.Outlined.Notifications,
                    darkMode = darkMode
                )
            }
        } else {
            items(items) { item ->
                NotificationRow(item = item, darkMode = darkMode, onClick = { onNotificationClick(item) })
            }
        }
    }
}

@Composable
private fun NotificationRow(item: CampusNotificationItem, darkMode: Boolean, onClick: () -> Unit) {
    val palette = atuPalette(darkMode)
    val icon = when (item.type) {
        "ANNOUNCEMENT", "DIRECT" -> Icons.Outlined.Campaign
        "EVENT" -> Icons.Outlined.Event
        else -> Icons.Outlined.Newspaper
    }
    val accent = when (item.type) {
        "ANNOUNCEMENT", "DIRECT" -> AtuPrimary
        "EVENT" -> Color(0xFF2563EB)
        else -> Color(0xFF0F766E)
    }
    AtuPremiumCard(
        darkMode = darkMode,
        modifier = Modifier
            .clickable(onClick = onClick)
            .atuPressScale()
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(item.title, color = palette.text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                Text(item.body, color = palette.muted, style = MaterialTheme.typography.bodyMedium, maxLines = 3, overflow = TextOverflow.Ellipsis)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(formatDateTime(item.createdAt), color = palette.primary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    if (item.attachmentName.isNotBlank()) {
                        Text(item.attachmentName, color = palette.muted, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationDetailScreen(
    item: CampusNotificationItem,
    darkMode: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = atuPalette(darkMode)
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderIconButton(Icons.Outlined.ArrowBack, darkMode, onBack)
                Column {
                    Text("Bildiriş detalları", color = palette.text, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                    Text(formatDateTime(item.createdAt), color = palette.muted, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        item {
            AtuPremiumCard(darkMode = darkMode, radius = 28.dp) {
                AtuStatusBadge(
                    text = when (item.type) {
                        "ANNOUNCEMENT" -> "Elan"
                        "EVENT" -> "Tədbir"
                        "DIRECT" -> "Mesaj"
                        else -> "Xəbər"
                    },
                    darkMode = darkMode
                )
                Spacer(Modifier.height(14.dp))
                Text(item.title, color = palette.text, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(10.dp))
                Text(item.body, color = palette.muted, style = MaterialTheme.typography.bodyLarge)
                if (item.attachmentName.isNotBlank()) {
                    Spacer(Modifier.height(14.dp))
                    Surface(
                        color = palette.surfaceSoft,
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, palette.border)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Badge, contentDescription = null, tint = palette.primary)
                            Column {
                                Text("Əlavə fayl", color = palette.muted, style = MaterialTheme.typography.labelLarge)
                                Text(item.attachmentName, color = palette.text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
        if (item.imageUrl.isNotBlank()) {
            item {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(28.dp))
                )
            }
        }
    }
}

@Composable
private fun ChatListTab(
    rooms: List<CampusChatRoom>,
    darkMode: Boolean,
    onOpenRoom: (CampusChatRoom) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = atuPalette(darkMode)
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AtuSectionHeader(
                title = "Chat",
                subtitle = "Rəsmi qrup və qrup söhbətiniz",
                action = "${rooms.size} otaq",
                darkMode = darkMode
            )
        }
        if (rooms.isEmpty()) {
            item {
                AtuEmptyState(
                    title = "Söhbət otağı tapılmadı",
                    subtitle = "Profiliniz yüklənəndən sonra qrup otaqları burada görünəcək.",
                    icon = Icons.Outlined.ChatBubbleOutline,
                    darkMode = darkMode
                )
            }
        } else {
            items(rooms) { room ->
                ChatRoomRow(room = room, darkMode = darkMode, onClick = { onOpenRoom(room) })
            }
        }
    }
}

@Composable
private fun ChatRoomRow(
    room: CampusChatRoom,
    darkMode: Boolean,
    onClick: () -> Unit
) {
    val palette = atuPalette(darkMode)
    AtuPremiumCard(
        darkMode = darkMode,
        modifier = Modifier
            .clickable(onClick = onClick)
            .atuPressScale()
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (room.kind == "OFFICIAL") AtuTint else Color(0xFFEAF1FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (room.kind == "OFFICIAL") Icons.Outlined.VerifiedUser else Icons.Outlined.MarkChatRead,
                    contentDescription = null,
                    tint = if (room.kind == "OFFICIAL") AtuPrimary else Color(0xFF2563EB)
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(room.title, color = palette.text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                    if (room.lastMessageTime > 0L) {
                        Text(formatTime(room.lastMessageTime), color = palette.muted, style = MaterialTheme.typography.labelLarge)
                    }
                }
                Text(room.subtitle, color = palette.primary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                Text(room.lastMessagePreview, color = palette.muted, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun ChatRoomScreen(
    student: StudentProfile,
    room: CampusChatRoom,
    messages: List<CampusChatMessage>,
    highlightedMessageId: String,
    darkMode: Boolean,
    onBack: () -> Unit,
    onReact: (String, String) -> Unit,
    onEdit: (CampusChatMessage) -> Unit,
    onDelete: (CampusChatMessage) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = atuPalette(darkMode)
    val listState = rememberLazyListState()

    LaunchedEffect(highlightedMessageId, messages.size) {
        if (highlightedMessageId.isBlank()) return@LaunchedEffect
        val index = messages.indexOfFirst { it.id == highlightedMessageId }
        if (index >= 0) listState.animateScrollToItem(index)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderIconButton(Icons.Outlined.ArrowBack, darkMode, onBack)
            Column(modifier = Modifier.weight(1f)) {
                Text(room.title, color = palette.text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Text(if (room.readOnly) "Yalnız reaksiyalar açıqdır" else "Canlı qrup söhbəti", color = palette.muted, style = MaterialTheme.typography.bodyMedium)
            }
            if (room.kind == "OFFICIAL") {
                AtuStatusBadge("Rəsmi", darkMode = darkMode)
            }
        }

        if (messages.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
            ) {
                AtuEmptyState(
                    title = "Mesaj yoxdur",
                    subtitle = "Bu otaqda ilk paylaşım görünəndə burada açılacaq.",
                    icon = Icons.Outlined.ChatBubbleOutline,
                    darkMode = darkMode
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { message ->
                    CommunityMessageBubble(
                        message = message,
                        fromCurrentUser = message.senderId == student.id,
                        highlighted = message.id == highlightedMessageId,
                        darkMode = darkMode,
                        readOnly = room.readOnly,
                        onReact = { onReact(message.id, it) },
                        onEdit = { onEdit(message) },
                        onDelete = { onDelete(message) }
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CommunityMessageBubble(
    message: CampusChatMessage,
    fromCurrentUser: Boolean,
    highlighted: Boolean,
    darkMode: Boolean,
    readOnly: Boolean,
    onReact: (String) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val palette = atuPalette(darkMode)
    var actionSheetVisible by remember { mutableStateOf(false) }
    val groupedReactions = remember(message.reactions) { message.reactions.groupBy { it.emoji } }
    val reactionOptions = listOf("\uD83D\uDC4D", "\u2764\uFE0F", "\uD83D\uDD25", "\uD83D\uDC4F", "\uD83D\uDE04")
    val bubbleShape = RoundedCornerShape(
        topStart = 24.dp,
        topEnd = 24.dp,
        bottomStart = if (fromCurrentUser) 24.dp else 8.dp,
        bottomEnd = if (fromCurrentUser) 8.dp else 24.dp
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (fromCurrentUser) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (fromCurrentUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            if (!fromCurrentUser) {
                if (message.senderPhotoUrl.isNotBlank()) {
                    AsyncImage(
                        model = message.senderPhotoUrl,
                        contentDescription = message.senderName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(end = 8.dp, bottom = 2.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp, bottom = 2.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(palette.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = message.senderName.firstOrNull()?.uppercase() ?: "A",
                            color = palette.primary,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Surface(
                color = if (fromCurrentUser) palette.primary else palette.surface,
                shape = bubbleShape,
                border = if (highlighted) BorderStroke(1.5.dp, palette.primary.copy(alpha = 0.32f)) else if (fromCurrentUser) null else BorderStroke(1.dp, palette.border),
                shadowElevation = if (darkMode) 0.dp else if (highlighted) 4.dp else 2.dp,
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .combinedClickable(
                        onClick = {},
                        onLongClick = { actionSheetVisible = true }
                    )
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!fromCurrentUser) {
                        Text(
                            message.senderName,
                            color = palette.primary,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (message.text.isNotBlank()) {
                        Text(
                            message.text,
                            color = if (fromCurrentUser) AtuWhite else palette.text,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    if (message.mediaUrl.isNotBlank() && message.mediaType.startsWith("audio")) {
                        AudioAttachmentView(
                            mediaUrl = message.mediaUrl,
                            darkMode = darkMode,
                            fromCurrentUser = fromCurrentUser
                        )
                    } else if (message.mediaUrl.isNotBlank() && message.mediaType.startsWith("image")) {
                        AsyncImage(
                            model = message.mediaUrl,
                            contentDescription = message.attachmentName.ifBlank { "Media" },
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .clip(RoundedCornerShape(22.dp))
                        )
                    } else if (message.mediaUrl.isNotBlank()) {
                        FileAttachmentView(
                            fileName = message.attachmentName.ifBlank { "Əlavə fayl" },
                            mediaType = message.mediaType,
                            fromCurrentUser = fromCurrentUser,
                            darkMode = darkMode
                        )
                    }
                    Text(
                        buildString {
                            append(formatTime(message.createdAt))
                            if (message.editedAt > 0L) append(" \u2022 redakt\u0259 edildi")
                        },
                        color = if (fromCurrentUser) AtuWhite.copy(alpha = 0.72f) else palette.muted,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        if (groupedReactions.isNotEmpty()) {
            Row(
                modifier = Modifier.padding(top = 6.dp, start = if (fromCurrentUser) 0.dp else 40.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                groupedReactions.forEach { (emoji, entries) ->
                    ReactionChip(emoji = emoji, entries = entries, darkMode = darkMode) {
                        onReact(emoji)
                    }
                }
            }
        }
    }

    if (actionSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { actionSheetVisible = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = palette.surface,
            tonalElevation = 0.dp,
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Mesaj \u0259m\u0259liyyatlar\u0131", color = palette.text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    reactionOptions.forEach { emoji ->
                        Surface(
                            onClick = {
                                onReact(emoji)
                                actionSheetVisible = false
                            },
                            color = palette.surfaceSoft,
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, palette.border),
                            modifier = Modifier.atuPressScale()
                        ) {
                            Text(
                                text = emoji,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (!readOnly && fromCurrentUser) {
                        MessageActionButton(
                            text = "Redakt\u0259 et",
                            icon = Icons.Outlined.Edit,
                            darkMode = darkMode,
                            onClick = {
                                onEdit()
                                actionSheetVisible = false
                            }
                        )
                    }
                    if (fromCurrentUser) {
                        MessageActionButton(
                            text = "Sil",
                            icon = Icons.Outlined.DeleteOutline,
                            darkMode = darkMode,
                            accent = AtuDanger,
                            onClick = {
                                onDelete()
                                actionSheetVisible = false
                            }
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}


@Composable
private fun ReactionChip(
    emoji: String,
    entries: List<CampusChatReaction>,
    darkMode: Boolean,
    onClick: () -> Unit
) {
    val palette = atuPalette(darkMode)
    Surface(
        onClick = onClick,
        color = palette.surface,
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, palette.border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji)
            Text(entries.size.toString(), color = palette.text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            entries.take(2).forEach { entry ->
                if (entry.photoUrl.isNotBlank()) {
                    AsyncImage(
                        model = entry.photoUrl,
                        contentDescription = entry.userName,
                        modifier = Modifier.size(18.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageActionButton(
    text: String,
    icon: ImageVector,
    darkMode: Boolean,
    accent: Color = AtuPrimary,
    onClick: () -> Unit
) {
    val palette = atuPalette(darkMode)
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().atuPressScale(),
        color = palette.surfaceSoft,
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, palette.border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent)
            }
            Text(text, color = accent.takeIf { accent == AtuDanger } ?: palette.text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AudioAttachmentView(
    mediaUrl: String,
    darkMode: Boolean,
    fromCurrentUser: Boolean
) {
    val palette = atuPalette(darkMode)
    var playing by remember(mediaUrl) { mutableStateOf(false) }
    val player = remember(mediaUrl) {
        MediaPlayer().apply {
            setDataSource(mediaUrl)
            prepareAsync()
            setOnCompletionListener { playing = false }
        }
    }

    androidx.compose.runtime.DisposableEffect(mediaUrl) {
        onDispose {
            try {
                player.release()
            } catch (_: Exception) {
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (fromCurrentUser) AtuWhite.copy(alpha = 0.14f) else palette.surfaceSoft,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = {
                    try {
                        if (player.isPlaying) {
                            player.pause()
                            playing = false
                        } else {
                            player.start()
                            playing = true
                        }
                    } catch (_: Exception) {
                    }
                },
                modifier = Modifier.size(40.dp).atuPressScale(),
                color = if (fromCurrentUser) AtuWhite.copy(alpha = 0.2f) else palette.primary.copy(alpha = 0.12f),
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (playing) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                        contentDescription = null,
                        tint = if (fromCurrentUser) AtuWhite else palette.primary
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    "Səsli mesaj",
                    color = if (fromCurrentUser) AtuWhite else palette.text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    if (playing) "Oxudulur..." else "Dinləmək üçün bas",
                    color = if (fromCurrentUser) AtuWhite.copy(alpha = 0.72f) else palette.muted,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun FileAttachmentView(
    fileName: String,
    mediaType: String,
    fromCurrentUser: Boolean,
    darkMode: Boolean
) {
    val palette = atuPalette(darkMode)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (fromCurrentUser) AtuWhite.copy(alpha = 0.14f) else palette.surfaceSoft,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (fromCurrentUser) AtuWhite.copy(alpha = 0.16f) else palette.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Badge, contentDescription = null, tint = if (fromCurrentUser) AtuWhite else palette.primary)
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    fileName,
                    color = if (fromCurrentUser) AtuWhite else palette.text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    mediaType.ifBlank { "Fayl əlavəsi" },
                    color = if (fromCurrentUser) AtuWhite.copy(alpha = 0.72f) else palette.muted,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun AssistantTab(
    messages: List<ChatMessage>,
    isSending: Boolean,
    onSubmit: (String) -> Unit,
    darkMode: Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().background(atuPalette(darkMode).background),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            AtuHeroCard(
                title = "ATU AI Köməkçi",
                subtitle = "Campus, xəbərlər və tələbə xidmətləri üçün sürətli cavablar.",
                darkMode = darkMode
            ) {
                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = AtuPrimary.copy(alpha = 0.28f),
                    modifier = Modifier.align(Alignment.TopEnd).size(72.dp)
                )
            }
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(listOf("Bugünkü ATU xəbərləri", "Tələbə xidmətləri", "Dərs cədvəli", "Kampus yönləndirməsi")) { prompt ->
                    AtuFilterChip(prompt, selected = false, onClick = { onSubmit(prompt) }, darkMode = darkMode)
                }
            }
        }
        items(messages) { message -> AiBubble(message, darkMode) }
        if (isSending) item { TypingDots(darkMode) }
    }
}

@Composable
private fun AiBubble(message: ChatMessage, darkMode: Boolean) {
    val palette = atuPalette(darkMode)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (message.fromUser) Arrangement.End else Arrangement.Start) {
        Surface(
            color = if (message.fromUser) palette.primary else palette.surface,
            shape = RoundedCornerShape(
                topStart = 22.dp,
                topEnd = 22.dp,
                bottomStart = if (message.fromUser) 22.dp else 7.dp,
                bottomEnd = if (message.fromUser) 7.dp else 22.dp
            ),
            border = if (message.fromUser) null else BorderStroke(1.dp, palette.border),
            shadowElevation = if (darkMode) 0.dp else 2.dp,
            modifier = Modifier.fillMaxWidth(0.84f)
        ) {
            Text(message.text, color = if (message.fromUser) AtuWhite else palette.text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
private fun TypingDots(darkMode: Boolean) {
    val palette = atuPalette(darkMode)
    val transition = rememberInfiniteTransition(label = "typing")
    val alpha by transition.animateFloat(0.35f, 1f, infiniteRepeatable(tween(420), RepeatMode.Reverse), label = "a")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Surface(color = palette.surface, shape = RoundedCornerShape(22.dp), border = BorderStroke(1.dp, palette.border)) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { index ->
                    Box(
                        Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(palette.primary.copy(alpha = (alpha - index * 0.16f).coerceIn(0.25f, 1f)))
                    )
                }
            }
        }
    }
}

@Composable
private fun PromptDockInput(
    input: String,
    onInputChange: (String) -> Unit,
    isSending: Boolean,
    onSend: () -> Unit,
    onAttachmentAction: (() -> Unit)? = null,
    onVoiceAction: (() -> Unit)? = null,
    isRecording: Boolean = false,
    darkMode: Boolean,
    placeholder: String
) {
    val palette = atuPalette(darkMode)
    val supportsVoice = onVoiceAction != null
    val canSend = input.isNotBlank() && !isSending
    Surface(
        modifier = Modifier.fillMaxWidth().imePadding(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onAttachmentAction != null) {
                Surface(
                    onClick = onAttachmentAction,
                    modifier = Modifier
                        .size(46.dp)
                        .atuPressScale(),
                    color = palette.surface,
                    shape = CircleShape,
                    border = BorderStroke(1.dp, palette.border)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.AttachFile, contentDescription = "Fayl əlavə et", tint = palette.primary)
                    }
                }
            }
            Surface(
                modifier = Modifier.weight(1f),
                color = if (darkMode) palette.surface else AtuWhite.copy(alpha = 0.98f),
                shape = RoundedCornerShape(26.dp),
                border = BorderStroke(1.dp, palette.border.copy(alpha = 0.9f)),
                shadowElevation = if (darkMode) 0.dp else 2.dp
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = onInputChange,
                    placeholder = { Text(placeholder, color = palette.muted) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(26.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = if (darkMode) palette.surface else AtuWhite.copy(alpha = 0.98f),
                        unfocusedContainerColor = if (darkMode) palette.surface else AtuWhite.copy(alpha = 0.98f),
                        disabledContainerColor = if (darkMode) palette.surface else AtuWhite.copy(alpha = 0.98f),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        focusedTextColor = palette.text,
                        unfocusedTextColor = palette.text,
                        cursorColor = palette.primary
                    )
                )
            }
            Surface(
                onClick = {
                    if (canSend) onSend() else onVoiceAction?.invoke()
                },
                enabled = canSend || supportsVoice,
                modifier = Modifier
                    .size(52.dp)
                    .atuPressScale(),
                color = when {
                    isRecording -> AtuDanger
                    canSend -> palette.primary
                    !supportsVoice -> palette.primary.copy(alpha = 0.34f)
                    else -> palette.border.copy(alpha = 0.9f)
                },
                shape = CircleShape,
                shadowElevation = if (darkMode) 0.dp else 3.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when {
                            isRecording -> Icons.Outlined.Stop
                            canSend -> Icons.Outlined.Send
                            supportsVoice -> Icons.Outlined.Mic
                            else -> Icons.Outlined.Send
                        },
                        contentDescription = "Göndər",
                        tint = AtuWhite
                    )
                }
            }
        }
    }
}


@Composable
private fun AiDockInput(
    input: String,
    onInputChange: (String) -> Unit,
    isSending: Boolean,
    onSend: () -> Unit,
    darkMode: Boolean
) {
    PromptDockInput(
        input = input,
        onInputChange = onInputChange,
        isSending = isSending,
        onSend = onSend,
        darkMode = darkMode,
        placeholder = "AI köməkçidən soruş"
    )
}

@Composable
private fun PassTab(student: StudentProfile, darkMode: Boolean, modifier: Modifier = Modifier) {
    var ready by remember { mutableStateOf(false) }
    var approved by remember { mutableStateOf(false) }
    val logs = remember(approved) {
        (if (approved) listOf(AccessLog("Əsas turniket", "A korpusuna giriş təsdiqləndi", "İndi", "Giriş", true)) else emptyList()) +
            listOf(
                AccessLog("Əsas turniket", "A korpusuna giriş", "08:42", "Giriş", true),
                AccessLog("Kitabxana keçidi", "Oxu zalından çıxış", "12:18", "Çıxış", true),
                AccessLog("Laboratoriya bloku", "B korpusuna giriş", "14:05", "Giriş", true),
                AccessLog("Əsas turniket", "Campus çıxışı", "18:27", "Çıxış", true)
            )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(atuPalette(darkMode).background),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { AtuSectionHeader("ATU Pass", subtitle = "Rəqəmsal giriş kartı və turniket tarixçəsi", darkMode = darkMode) }
        item { AtuPassCard(student, darkMode = darkMode, approved = approved) }
        item {
            AtuPremiumCard(darkMode = darkMode) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                        Text("Bugünkü status", color = atuPalette(darkMode).muted, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            if (approved) "Giriş təsdiqləndi" else if (ready) "Turniket yoxlaması hazırdır" else "Yoxlama gözləyir",
                            color = atuPalette(darkMode).text,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                    }
                    AtuStatusBadge(if (approved) "Təsdiq" else "Demo", success = approved, darkMode = darkMode)
                }
                Spacer(Modifier.height(12.dp))
                Surface(
                    onClick = { if (!ready) ready = true else approved = true },
                    modifier = Modifier.fillMaxWidth().height(54.dp).atuPressScale(),
                    color = atuPalette(darkMode).primary,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            if (!ready) "Turniket yoxlamasını hazırla" else if (!approved) "Girişi təsdiqlə" else "İcazə verildi",
                            color = AtuWhite,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
        item { AtuSectionHeader("Giriş-çıxış tarixçəsi", action = "Bu gün", darkMode = darkMode) }
        items(logs) { log -> AccessLogRow(log, darkMode) }
    }
}

@Composable
private fun AccessLogRow(log: AccessLog, darkMode: Boolean) {
    val palette = atuPalette(darkMode)
    AtuPremiumCard(darkMode = darkMode) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(50.dp).clip(CircleShape).background(if (log.type == "Giriş") Color(0xFFEAF1FF) else AtuTint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (log.type == "Giriş") Icons.Outlined.Security else Icons.Outlined.CreditCard,
                    contentDescription = null,
                    tint = if (log.type == "Giriş") Color(0xFF4F46E5) else AtuPrimary
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(log.title, color = palette.text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                    Text(log.time, color = palette.muted, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
                Text(log.subtitle, color = palette.muted, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${log.type} • ${if (log.success) "Təsdiqləndi" else "Gözləmədə"}",
                    color = if (log.success) AtuSuccess else palette.primary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SettingsTab(
    student: StudentProfile,
    darkMode: Boolean,
    notificationsEnabled: Boolean,
    backendUrl: String,
    onDarkModeChange: (Boolean) -> Unit,
    onNotificationsChange: (Boolean) -> Unit,
    onBackendClick: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().background(atuPalette(darkMode).background),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { AtuSectionHeader("Parametrl?r", subtitle = "T?tbiq g?r?n???, bildiri? v? ba?lant? ayarlar?", darkMode = darkMode) }
        item { AtuStudentCard(student, darkMode = darkMode) }
        item {
            AtuSettingsRow(
                "Dark mode",
                if (darkMode) "Qaranl?q g?r?n?? aktivdir" else "???ql? g?r?n?? aktivdir",
                if (darkMode) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                darkMode,
                onDarkModeChange,
                darkMode = darkMode
            )
        }
        item {
            AtuSettingsRow(
                "Bildiri?l?r",
                "ATU x?b?rl?ri v? campus yenilikl?ri",
                Icons.Outlined.Notifications,
                notificationsEnabled,
                onNotificationsChange,
                darkMode = darkMode
            )
        }
        item {
            BackendServerRow(
                value = backendUrl.ifBlank { "Avtomatik / USB / emulator" },
                darkMode = darkMode,
                onClick = onBackendClick
            )
        }
        item {
            Surface(
                onClick = onReset,
                modifier = Modifier.fillMaxWidth().height(56.dp).atuPressScale(),
                color = if (darkMode) atuPalette(darkMode).surface else AtuWhite,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, AtuDanger.copy(alpha = if (darkMode) 0.38f else 0.22f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("Profili sil v? yenid?n daxil ol", color = AtuDanger, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun ProfileOverviewContent(
    student: StudentProfile,
    darkMode: Boolean
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Profil", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        AtuStudentCard(student, darkMode = darkMode)
        ProfileInfoRow("Fak?lt?", student.faculty, Icons.Outlined.Badge, darkMode)
        ProfileInfoRow("?xtisas", student.specialty, Icons.Outlined.Campaign, darkMode)
        ProfileInfoRow("T?hsil formas?", student.studyForm, Icons.Outlined.Badge, darkMode)
        ProfileInfoRow("Kurs", student.course, Icons.Outlined.CalendarMonth, darkMode)
        ProfileInfoRow("Qrup", student.group, Icons.Outlined.MarkChatRead, darkMode)
    }
}

@Composable
private fun ProfileInfoRow(title: String, value: String, icon: ImageVector, darkMode: Boolean) {
    val palette = atuPalette(darkMode)
    AtuPremiumCard(darkMode = darkMode) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(46.dp).clip(RoundedCornerShape(16.dp)).background(palette.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = palette.primary)
            }
            Column {
                Text(title, color = palette.muted, style = MaterialTheme.typography.bodyMedium)
                Text(value.ifBlank { "Əlavə olunmayıb" }, color = palette.text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun BackendServerRow(
    value: String,
    darkMode: Boolean,
    onClick: () -> Unit
) {
    val palette = atuPalette(darkMode)
    AtuPremiumCard(
        darkMode = darkMode,
        modifier = Modifier
            .clickable(onClick = onClick)
            .atuPressScale()
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Search, contentDescription = null, tint = palette.primary)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("Backend server", color = palette.text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                Text(value, color = palette.muted, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Text("Dəyiş", color = palette.primary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun NewsDetailContent(
    item: AtuNews,
    darkMode: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = atuPalette(darkMode)
    LazyColumn(
        modifier = modifier.fillMaxSize().background(palette.background),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth().height(330.dp)) {
                AsyncImage(model = item.imageUrl, contentDescription = item.title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.1f), Color.Transparent, Color.Black.copy(alpha = 0.68f))))
                )
                HeaderIconButton(
                    icon = Icons.Outlined.ArrowBack,
                    darkMode = false,
                    onClick = onBack,
                    modifier = Modifier.padding(18.dp)
                )
                Column(modifier = Modifier.align(Alignment.BottomStart).padding(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AtuStatusBadge(item.date.ifBlank { "ATU" }, darkMode = true)
                    Text(item.title, color = AtuWhite, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                }
            }
        }
        item {
            AtuScreen(darkMode = darkMode, contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp)) {
                AtuPremiumCard(darkMode = darkMode, radius = 28.dp) {
                    Text(
                        when (item.type) {
                            "EVENT" -> "ATU tədbiri"
                            "ANNOUNCEMENT" -> "ATU elanı"
                            else -> "ATU rəsmi xəbəri"
                        },
                        color = it.primary,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black
                    )
                    Text(item.body.ifBlank { item.summary }, color = it.text, style = MaterialTheme.typography.bodyLarge)
                    Text("Yayımlayan: ATU News Admin", color = it.muted, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun HeaderStudentPhoto(student: StudentProfile, onClick: () -> Unit) {
    if (student.photoUrl.isNotBlank()) {
        AsyncImage(
            model = student.photoUrl,
            contentDescription = "${student.fullName} profil fotosu",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onClick)
        )
    } else {
        Image(
            painter = painterResource(R.drawable.atu_logo),
            contentDescription = "ATU",
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onClick)
        )
    }
}

@Composable
private fun HeaderIconButton(
    icon: ImageVector,
    darkMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = atuPalette(darkMode)
    Surface(
        modifier = modifier.size(42.dp).atuPressScale(),
        onClick = onClick,
        color = palette.surface,
        shape = CircleShape,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = if (darkMode) 0.dp else 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = palette.text, modifier = Modifier.size(22.dp))
        }
    }
}

private fun formatDateTime(timestamp: Long): String =
    SimpleDateFormat("dd.MM.yyyy • HH:mm", Locale("az")).format(Date(timestamp))

private fun formatTime(timestamp: Long): String =
    SimpleDateFormat("HH:mm", Locale("az")).format(Date(timestamp))
