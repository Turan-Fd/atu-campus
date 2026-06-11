package com.atu.campus.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.atu.campus.MainActivity
import com.atu.campus.R
import com.atu.campus.data.AtuNews
import com.atu.campus.data.CampusNotificationItem

class CampusNotificationService(private val context: Context) {
    private val manager = context.getSystemService(NotificationManager::class.java)

    fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "ATU Campus bildiri\u015Fl\u0259ri",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "X\u0259b\u0259r, elan v\u0259 t\u0259dbir bildiri\u015Fl\u0259ri"
                }
            )
        }
    }

    fun show(item: AtuNews) {
        ensureChannel()
        val typeLabel = when (item.type) {
            "EVENT" -> "Yeni t\u0259dbir"
            "ANNOUNCEMENT" -> "Yeni elan"
            else -> "Yeni x\u0259b\u0259r"
        }
        val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.atu_logo)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_atu)
            .setLargeIcon(largeIcon)
            .setColor(0xFFA80F3E.toInt())
            .setSubText("ATU Campus")
            .setContentTitle("$typeLabel: ${item.title}")
            .setContentText(item.summary)
            .setStyle(NotificationCompat.BigTextStyle().bigText(item.summary))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setContentIntent(pendingIntent(item))
            .setAutoCancel(true)
            .build()
        manager.notify(item.id.hashCode(), notification)
    }

    fun show(item: CampusNotificationItem) {
        ensureChannel()
        val typeLabel = when (item.type) {
            "EVENT" -> "Yeni tədbir"
            "ANNOUNCEMENT" -> "Yeni elan"
            "DIRECT" -> "Yeni mesaj"
            else -> "Yeni xəbər"
        }
        val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.atu_logo)
        val contentText = buildString {
            append(item.body)
            if (item.attachmentName.isNotBlank()) append(" • ").append(item.attachmentName)
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_atu)
            .setLargeIcon(largeIcon)
            .setColor(0xFFA80F3E.toInt())
            .setSubText("ATU Campus")
            .setContentTitle("$typeLabel: ${item.title}")
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(pendingIntent(item))
            .setAutoCancel(true)
            .build()
        manager.notify(item.id.hashCode(), notification)
    }

    private fun pendingIntent(item: AtuNews): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notification_id", item.id)
            putExtra("notification_type", item.type)
            putExtra("notification_title", item.title)
            putExtra("notification_body", item.summary)
            putExtra("notification_image_url", item.imageUrl)
            putExtra("notification_attachment_name", "")
            putExtra("notification_created_at", item.createdAt)
            putExtra("notification_room_id", "")
            putExtra("notification_message_id", "")
            putExtra("notification_media_type", "")
        }
        return PendingIntent.getActivity(
            context,
            item.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun pendingIntent(item: CampusNotificationItem): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notification_id", item.id)
            putExtra("notification_type", item.type)
            putExtra("notification_title", item.title)
            putExtra("notification_body", item.body)
            putExtra("notification_image_url", item.imageUrl)
            putExtra("notification_attachment_name", item.attachmentName)
            putExtra("notification_created_at", item.createdAt)
            putExtra("notification_room_id", item.roomId)
            putExtra("notification_message_id", item.messageId)
            putExtra("notification_media_type", item.mediaType)
        }
        return PendingIntent.getActivity(
            context,
            item.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        private const val CHANNEL_ID = "atu_campus_content"
    }
}
