package com.atu.campus.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.atu.campus.data.LocalProfileStorage
import com.atu.campus.data.NotificationOpenPayload
import com.atu.campus.data.StudentDirectoryEntry
import com.atu.campus.services.AdminSelectedAttachment
import com.atu.campus.services.AdminSelectedImage
import com.atu.campus.services.BackendStudentService
import com.atu.campus.services.CampusCommunityService
import com.atu.campus.services.CampusContentService
import com.atu.campus.services.FcmTokenSyncService
import com.atu.campus.services.NotificationSyncScheduler
import com.atu.campus.services.StudentLookupStatus
import com.google.firebase.messaging.FirebaseMessaging
import com.atu.campus.ui.screens.AdminLoginScreen
import com.atu.campus.ui.screens.HomeScreen
import com.atu.campus.ui.screens.NewsAdminScreen
import com.atu.campus.ui.screens.SmsAdminScreen
import com.atu.campus.ui.screens.SplashScreen
import com.atu.campus.ui.screens.StudentAccessScreen
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private enum class PendingAdminTarget {
    News,
    Sms
}

@Composable
fun AppNavigation(
    launchNotification: NotificationOpenPayload? = null,
    onLaunchNotificationConsumed: () -> Unit = {}
) {
    val context = LocalContext.current
    val storage = remember { LocalProfileStorage(context.applicationContext) }
    val backendStudentService = remember { BackendStudentService(context.applicationContext) }
    val contentService = remember { CampusContentService(context.applicationContext) }
    val communityService = remember { CampusCommunityService(context.applicationContext) }
    val notificationScheduler = remember { NotificationSyncScheduler(context.applicationContext) }
    val fcmTokenSyncService = remember { FcmTokenSyncService(context.applicationContext) }
    val coroutineScope = rememberCoroutineScope()

    var currentScreen by remember { mutableStateOf(Screen.Splash) }
    var profile by remember { mutableStateOf(storage.getProfile()) }
    var accessMessage by remember { mutableStateOf("") }
    var accessLoading by remember { mutableStateOf(false) }
    var adminLoginMessage by remember { mutableStateOf("") }
    var adminLoginLoading by remember { mutableStateOf(false) }
    var pendingAdminTarget by remember { mutableStateOf(PendingAdminTarget.News) }
    var adminToken by remember { mutableStateOf("") }
    var publishMessage by remember { mutableStateOf("") }
    var publishing by remember { mutableStateOf(false) }
    var smsResults by remember { mutableStateOf<List<StudentDirectoryEntry>>(emptyList()) }
    var smsSearching by remember { mutableStateOf(false) }
    var smsMessage by remember { mutableStateOf("") }
    var smsSending by remember { mutableStateOf(false) }
    var smsSearchJob by remember { mutableStateOf<Job?>(null) }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            (slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(320)
            ) + fadeIn(tween(320))).togetherWith(
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(320)
                ) + fadeOut(tween(220))
            )
        },
        label = "screenTransition"
    ) { screen ->
        when (screen) {
            Screen.Splash -> SplashScreen(
                onFinished = {
                    profile = storage.getProfile()
                    currentScreen = if (profile != null) Screen.Home else Screen.StudentAccess
                }
            )

            Screen.StudentAccess -> StudentAccessScreen(
                message = accessMessage,
                loading = accessLoading,
                onContinue = { cardNumber ->
                    when (cardNumber) {
                        NEWS_ADMIN_ACCESS_CODE -> {
                            pendingAdminTarget = PendingAdminTarget.News
                            accessMessage = ""
                            currentScreen = Screen.AdminLogin
                        }

                        SMS_ADMIN_ACCESS_CODE -> {
                            pendingAdminTarget = PendingAdminTarget.Sms
                            accessMessage = ""
                            currentScreen = Screen.AdminLogin
                        }

                        else -> {
                            accessLoading = true
                            accessMessage = ""
                            coroutineScope.launch {
                                val result = backendStudentService.lookupByCardNumber(cardNumber)
                                accessLoading = false
                                if (result.status == StudentLookupStatus.Verified && result.profile != null) {
                                    storage.saveProfile(result.profile)
                                    profile = result.profile
                                    runCatching { FirebaseMessaging.getInstance().token }
                                        .getOrNull()
                                        ?.addOnSuccessListener { token ->
                                            coroutineScope.launch {
                                                fcmTokenSyncService.registerToken(result.profile.id, token)
                                            }
                                        }
                                    currentScreen = Screen.Home
                                } else {
                                    accessMessage = result.message
                                }
                            }
                        }
                    }
                }
            )

            Screen.AdminLogin -> AdminLoginScreen(
                title = if (pendingAdminTarget == PendingAdminTarget.News) "News Admin" else "SMS Admin",
                subtitle = if (pendingAdminTarget == PendingAdminTarget.News) {
                    "ATU xəbər, elan və tədbirlərini idarə edin."
                } else {
                    "Tələbələri axtarın və onlara birbaşa bildiriş göndərin."
                },
                ctaText = if (pendingAdminTarget == PendingAdminTarget.News) {
                    "News Admin-ə daxil ol"
                } else {
                    "SMS Admin-ə daxil ol"
                },
                message = adminLoginMessage,
                loading = adminLoginLoading,
                onBack = {
                    adminLoginMessage = ""
                    currentScreen = Screen.StudentAccess
                },
                onLogin = { password ->
                    adminLoginLoading = true
                    adminLoginMessage = ""
                    coroutineScope.launch {
                        val accessCode = if (pendingAdminTarget == PendingAdminTarget.News) {
                            NEWS_ADMIN_ACCESS_CODE
                        } else {
                            SMS_ADMIN_ACCESS_CODE
                        }
                        val result = contentService.adminLogin(accessCode, password)
                        adminLoginLoading = false
                        if (result.success) {
                            adminToken = result.token
                            currentScreen = if (pendingAdminTarget == PendingAdminTarget.News) {
                                Screen.NewsAdmin
                            } else {
                                Screen.SmsAdmin
                            }
                        } else {
                            adminLoginMessage = result.message
                        }
                    }
                }
            )

            Screen.NewsAdmin -> NewsAdminScreen(
                publishing = publishing,
                message = publishMessage,
                onPublish = { type, title, summary, body, image ->
                    publishing = true
                    publishMessage = ""
                    coroutineScope.launch {
                        val result = contentService.publishContent(
                            adminToken,
                            type,
                            title,
                            summary,
                            body,
                            image
                        )
                        publishing = false
                        publishMessage = result.message
                    }
                },
                onPostToOfficialGroup = { text, attachment ->
                    coroutineScope.launch {
                        val result = communityService.sendMessage(
                            roomId = "atu-official",
                            studentId = "",
                            text = text,
                            attachment = attachment,
                            adminToken = adminToken
                        )
                        publishMessage = result.message
                    }
                },
                onLogout = {
                    adminToken = ""
                    adminLoginMessage = ""
                    publishMessage = ""
                    currentScreen = Screen.StudentAccess
                }
            )

            Screen.SmsAdmin -> SmsAdminScreen(
                results = smsResults,
                searching = smsSearching,
                message = smsMessage,
                sending = smsSending,
                onSearch = { query ->
                    smsSearchJob?.cancel()
                    smsSearchJob = coroutineScope.launch {
                        smsSearching = true
                        smsResults = communityService.searchStudents(query)
                        smsSearching = false
                    }
                },
                onSend = { studentIds, title, body, type, attachment ->
                    smsSending = true
                    smsMessage = ""
                    coroutineScope.launch {
                        val result = communityService.sendDirectNotification(
                            token = adminToken,
                            studentIds = studentIds,
                            title = title,
                            body = body,
                            type = type,
                            attachment = attachment
                        )
                        smsSending = false
                        smsMessage = result.message
                    }
                },
                onLogout = {
                    adminToken = ""
                    adminLoginMessage = ""
                    smsMessage = ""
                    smsResults = emptyList()
                    currentScreen = Screen.StudentAccess
                }
            )

            Screen.Home -> HomeScreen(
                profile = profile,
                launchNotification = launchNotification,
                onLaunchNotificationConsumed = onLaunchNotificationConsumed,
                onReset = {
                    coroutineScope.launch { fcmTokenSyncService.unregisterStoredToken() }
                    notificationScheduler.cancel()
                    storage.clearProfile()
                    profile = null
                    accessMessage = ""
                    currentScreen = Screen.StudentAccess
                }
            )
        }
    }

    androidx.compose.runtime.LaunchedEffect(profile?.id) {
        val currentProfile = profile
        if (currentProfile != null) {
            notificationScheduler.schedule()
            val storedToken = fcmTokenSyncService.getStoredToken()
            if (storedToken.isNotBlank()) {
                fcmTokenSyncService.registerToken(currentProfile.id, storedToken)
            } else {
                runCatching { FirebaseMessaging.getInstance().token }
                    .getOrNull()
                    ?.addOnSuccessListener { token ->
                        coroutineScope.launch {
                            fcmTokenSyncService.registerToken(currentProfile.id, token)
                        }
                    }
            }
        } else {
            notificationScheduler.cancel()
        }
    }
}

private const val NEWS_ADMIN_ACCESS_CODE = "1970103"
private const val SMS_ADMIN_ACCESS_CODE = "899913"
