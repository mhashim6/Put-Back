package mhashim.android.putback

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import mhashim.android.putback.data.NotionsRealm
import mhashim.android.putback.work.NotionsReminder

/**
 * Created by mhashim6 on 02/09/2018.
 */
class NotificationBroadcastReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent) {

		val notionId = intent.getStringExtra(NOTION_ID_EXTRA)
		val actionType = intent.getIntExtra(ACTION_TYPE, ACTION_TYPE_PUTBACK)

		when (actionType) {
			ACTION_TYPE_PUTBACK -> {
				Toast.makeText(context, R.string.notion_is_putback, Toast.LENGTH_SHORT).show()
			}

			ACTION_TYPE_ARCHIVE -> {
				val notion = NotionsRealm.findOne(notionId)
				notion?.let {
					NotionsRealm.changeIdleState(it, true)
				}
			}
		}

		val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.cancel(NotionsReminder.NOTION_NOTIFICATION_ID)

	}

	companion object {
		const val ACTION = "mhashim6.putback.NOTIFICATION"
		const val NOTION_ID_EXTRA = "NOTION_ID"
		const val ACTION_TYPE = "ACTION_TYPE"

		const val ACTION_TYPE_PUTBACK = 0
		const val ACTION_TYPE_ARCHIVE = 1
		const val ACTION_TYPE_SHOW_CONTENT = 2
	}

}