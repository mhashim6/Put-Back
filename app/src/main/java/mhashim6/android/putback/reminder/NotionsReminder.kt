package mhashim6.android.putback.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.PeriodicWorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import mhashim6.android.putback.*
import mhashim6.android.putback.data.Notion
import mhashim6.android.putback.data.NotionsRealm.findHottestNotion
import mhashim6.android.putback.data.NotionsRealm.updateLastRunAt
import mhashim6.android.putback.data.PreferencesRepository
import mhashim6.android.putback.ui.MainActivity.Companion.MAIN_ACTIVITY_SHOW_NOTION_ACTION
import mhashim6.android.putback.ui.colorSelector
import mhashim6.android.putback.reminder.NotificationBroadcastReceiver.Companion.ACTION_TYPE_ARCHIVE
import mhashim6.android.putback.reminder.NotificationBroadcastReceiver.Companion.ACTION_TYPE_PUTBACK
import mhashim6.android.putback.reminder.NotificationBroadcastReceiver.Companion.ACTION_TYPE_SHOW_CONTENT
import java.util.concurrent.TimeUnit

/**
 * Created by mhashim6 on 31/08/2018.
 */
class NotionsReminder(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    override fun doWork(): Result {

        info("a new work is invoked")

        findHottestNotion()?.let {
            updateLastRunAt(it)
            showNotification(it)
        }
        return Result.success()
    }

    private fun showNotification(notion: Notion) {
        val color = colorSelector(notion, applicationContext.resources)

        val putbackAction = notificationAction(applicationContext, notion, ACTION_TYPE_PUTBACK)
        val archiveAction = notificationAction(applicationContext, notion, ACTION_TYPE_ARCHIVE)

        val showAction = notificationContentAction(applicationContext, notion, ACTION_TYPE_SHOW_CONTENT, MAIN_ACTIVITY_SHOW_NOTION_ACTION)
        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(randomTitle(applicationContext.resources))
                .setContentText(notion.content)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(showAction)
                .addAction(0, applicationContext.getString(R.string.putback), putbackAction)
                .addAction(0, applicationContext.getString(R.string.archive), archiveAction)
                .setStyle(NotificationCompat.BigTextStyle().bigText(notion.content))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setLights(color, 500, 2000)
                .setSound(PreferencesRepository.soundUri)
                .setColor(color)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build().apply {
                    flags = NotificationCompat.FLAG_ONLY_ALERT_ONCE or NotificationCompat.FLAG_AUTO_CANCEL or NotificationCompat.FLAG_SHOW_LIGHTS
                }

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify((notion.lastRunAt).toInt(), notification)
    }

    companion object Factory {
        const val NOTIFICATION_CHANNEL_ID = "NOTIONS_REMINDER_CHANNEL"
        const val NOTIONS_REMINDER_TAG = "NOTIONS_REMINDER_TAG"

        fun createNotionsReminder(): PeriodicWorkRequest {
            return PeriodicWorkRequest
                    .Builder(NotionsReminder::class.java, 1, TimeUnit.HOURS, 5, TimeUnit.MINUTES)
                    .build()
        }

        fun createNotionsReminderNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.channel_name)
                val description = context.getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply { this.description = description }
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager!!.createNotificationChannel(channel)
            }
        }
    }

}

