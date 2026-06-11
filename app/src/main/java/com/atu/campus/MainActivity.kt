package com.atu.campus

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.atu.campus.data.NotificationOpenPayload
import com.atu.campus.navigation.AppNavigation
import com.atu.campus.ui.theme.AtuCampusTheme

class MainActivity : ComponentActivity() {
    private var launchNotification by mutableStateOf<NotificationOpenPayload?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_ATUCampus)
        super.onCreate(savedInstanceState)
        launchNotification = intent.toNotificationPayload()
        setContent {
            AtuCampusTheme {
                AppNavigation(
                    launchNotification = launchNotification,
                    onLaunchNotificationConsumed = { launchNotification = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        launchNotification = intent.toNotificationPayload()
    }
}

private fun Intent?.toNotificationPayload(): NotificationOpenPayload? {
    val extras = this?.extras ?: return null
    val id = extras.getString("notification_id").orEmpty()
    if (id.isBlank()) return null
    return NotificationOpenPayload(
        id = id,
        type = extras.getString("notification_type").orEmpty(),
        title = extras.getString("notification_title").orEmpty(),
        body = extras.getString("notification_body").orEmpty(),
        imageUrl = extras.getString("notification_image_url").orEmpty(),
        attachmentName = extras.getString("notification_attachment_name").orEmpty(),
        createdAt = extras.getLong("notification_created_at", 0L),
        roomId = extras.getString("notification_room_id").orEmpty(),
        messageId = extras.getString("notification_message_id").orEmpty(),
        mediaType = extras.getString("notification_media_type").orEmpty()
    )
}
