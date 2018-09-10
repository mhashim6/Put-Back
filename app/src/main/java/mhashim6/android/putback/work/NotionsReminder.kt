package mhashim6.android.putback.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.PeriodicWorkRequest
import androidx.work.Worker
import mhashim6.android.putback.R
import mhashim6.android.putback.RandomStrings.randomTitle
import mhashim6.android.putback.data.Notion
import mhashim6.android.putback.data.NotionsRealm.findHottestNotion
import mhashim6.android.putback.data.NotionsRealm.updateLastRunAt
import mhashim6.android.putback.notificationAction
import mhashim6.android.putback.notificationContentAction
import mhashim6.android.putback.ui.MainActivity.Companion.MAIN_ACTIVITY_SHOW_NOTION_ACTION
import mhashim6.android.putback.ui.colorSelector
import mhashim6.android.putback.work.NotificationBroadcastReceiver.Companion.ACTION_TYPE_ARCHIVE
import mhashim6.android.putback.work.NotificationBroadcastReceiver.Companion.ACTION_TYPE_PUTBACK
import mhashim6.android.putback.work.NotificationBroadcastReceiver.Companion.ACTION_TYPE_SHOW_CONTENT
import java.util.concurrent.TimeUnit


/**
 * Created by mhashim6 on 31/08/2018.
 */
class NotionsReminder : Worker() {

    override fun doWork(): Result {
        val notion = findHottestNotion()

        notion?.let {
            updateLastRunAt(it)
            showNotification(it)
        }
        return Result.SUCCESS
    }

    private fun showNotification(notion: Notion) {
        val color = colorSelector(notion, applicationContext.resources)

        val putbackAction = notificationAction(applicationContext, notion.id, ACTION_TYPE_PUTBACK)
		val archiveAction = notificationAction(applicationContext, notion.id, ACTION_TYPE_ARCHIVE)

        val showAction = notificationContentAction(applicationContext, notion.id, ACTION_TYPE_SHOW_CONTENT, MAIN_ACTIVITY_SHOW_NOTION_ACTION)
        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_format_list_bulleted_type_white_18dp)
                .setContentTitle(randomTitle(applicationContext.resources))
                .setContentText(notion.content)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(showAction)
                .addAction(0, applicationContext.getString(R.string.putback), putbackAction) //TODO icon
				.addAction(0, applicationContext.getString(R.string.archive), archiveAction)

                .setStyle(NotificationCompat.BigTextStyle().bigText(notion.content))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setLights(color, 500, 2000)
//				.setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setColor(color)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build()

        notification.flags = NotificationCompat.FLAG_ONLY_ALERT_ONCE or NotificationCompat.FLAG_AUTO_CANCEL or NotificationCompat.FLAG_SHOW_LIGHTS

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTION_NOTIFICATION_ID, notification)
    }

    companion object Factory {
        const val NOTION_NOTIFICATION_ID = 17
        const val NOTIFICATION_CHANNEL_ID = "NOTIONS_REMINDER_CHANNEL"
        const val NOTIONS_REMINDER_TAG = "NOTIONS_REMINDER_TAG"

        fun createNotionsReminder(): PeriodicWorkRequest {
            return PeriodicWorkRequest
                    .Builder(NotionsReminder::class.java, 15, TimeUnit.MINUTES, 5, TimeUnit.MINUTES) //temp for testing.
                    .build()
        }

        fun createNotionsReminderNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.channel_name)
                val description = context.getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
                channel.description = description
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager!!.createNotificationChannel(channel)
            }
        }
    }

}

