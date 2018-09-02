package mhashim.android.putback.work

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import mhashim.android.putback.App
import mhashim.android.putback.NotificationBroadcastReceiver.Companion.ACTION_TYPE_ARCHIVE
import mhashim.android.putback.NotificationBroadcastReceiver.Companion.ACTION_TYPE_PUTBACK
import mhashim.android.putback.NotificationBroadcastReceiver.Companion.ACTION_TYPE_SHOW_CONTENT
import mhashim.android.putback.R
import mhashim.android.putback.data.Notion
import mhashim.android.putback.data.NotionsRealm.loadHottestNotion
import mhashim.android.putback.data.NotionsRealm.updateLastRunAt
import mhashim.android.putback.debug
import mhashim.android.putback.notificationAction


/**
 * Created by mhashim6 on 31/08/2018.
 */
class NotionsReminder : Worker() {

	override fun doWork(): Result {
		val notion = loadHottestNotion()
		debug("idle: ${notion?.isArchived}")

		notion?.let {
			updateLastRunAt(it)
			showNotification(it)
		}
		return Result.SUCCESS
	}

	private fun showNotification(notion: Notion) {
		debug("new shit is always welcome")

		val putbackAction = notificationAction(applicationContext, notion.id, ACTION_TYPE_PUTBACK)
		val archiveAction = notificationAction(applicationContext, notion.id, ACTION_TYPE_ARCHIVE)
		val showAction = notificationAction(applicationContext, notion.id, ACTION_TYPE_SHOW_CONTENT)

		val notification = NotificationCompat.Builder(applicationContext, App.NOTIFICATION_CHANNEL_ID)
				.setSmallIcon(R.drawable.ic_format_list_bulleted_type_white_18dp)
				.setContentTitle("Feeling like it?")
				.setContentText(notion.content)
				.setCategory(NotificationCompat.CATEGORY_REMINDER)

				.addAction(0, applicationContext.getString(R.string.putback), putbackAction)
				.addAction(0, applicationContext.getString(R.string.archive), archiveAction)

				.setStyle(NotificationCompat.BigTextStyle().bigText(notion.content))
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setAutoCancel(true)
				.setOnlyAlertOnce(true)
				.build()

		notification.flags = NotificationCompat.FLAG_ONLY_ALERT_ONCE or NotificationCompat.FLAG_AUTO_CANCEL

		val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.notify(NOTION_NOTIFICATION_ID, notification)
	}

	companion object {
		const val NOTION_NOTIFICATION_ID = 17
	}

}

