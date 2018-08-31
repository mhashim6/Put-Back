package mhashim.android.putback.work

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import mhashim.android.putback.App
import mhashim.android.putback.R
import mhashim.android.putback.data.Notion
import mhashim.android.putback.data.NotionsRealm.loadHotNotion
import mhashim.android.putback.data.NotionsRealm.updateLastRunAt
import mhashim.android.putback.debug


/**
 * Created by mhashim6 on 31/08/2018.
 */
class NotionsReminder : Worker() {

	override fun doWork(): Result {
		val notion = loadHotNotion()
		notion?.let {
			updateLastRunAt(it, System.currentTimeMillis())
			showNotification(it)
		}
		return Result.SUCCESS
	}

	private fun showNotification(notion: Notion) {
		debug("new shit is always welcome")

		val notification = NotificationCompat.Builder(applicationContext, App.CHANNEL_ID)
				.setSmallIcon(R.drawable.ic_format_list_bulleted_type_white_18dp)
				.setContentTitle("Feeling like it?")
				.setContentText(notion.content)
				.setStyle(NotificationCompat.BigTextStyle().bigText(notion.content))
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_LIGHTS)
				.build()
		notification.flags = Notification.FLAG_ONLY_ALERT_ONCE

		val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.notify(NOTION_NOTIFICATION_ID, notification)
	}

	companion object {
		const val NOTION_NOTIFICATION_ID = 17
	}

}

