package com.atu.campus.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.atu.campus.data.LocalProfileStorage

class NotificationSyncWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        val profile = LocalProfileStorage(applicationContext).getProfile() ?: return Result.success()
        val prefs = applicationContext.getSharedPreferences("atu_campus_content", Context.MODE_PRIVATE)
        val notificationService = CampusNotificationService(applicationContext)
        val communityService = CampusCommunityService(applicationContext)

        return try {
            notificationService.ensureChannel()
            val items = communityService.fetchNotifications(profile.id)
            val lastSeen = prefs.getLong("latest_notification", 0L)
            val freshItems = items.filter { it.createdAt > lastSeen }
            if (freshItems.isNotEmpty()) {
                freshItems.sortedBy { it.createdAt }.forEach(notificationService::show)
                prefs.edit().putLong("latest_notification", freshItems.maxOf { it.createdAt }).apply()
            }
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
