/**
 * Created by mhashim6 on 29/08/2018.
 */

@file:Suppress("NOTHING_TO_INLINE")

package mhashim6.android.putback

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import mhashim6.android.putback.ui.MainActivity
import mhashim6.android.putback.work.NotificationBroadcastReceiver

fun Any.debug(message: Any?) = Log.d(this::class.java.simpleName, message.toString())
fun Any.info(message: Any?) = Log.i(this::class.java.simpleName, message.toString())
fun Any.verbose(message: Any?) = Log.v(this::class.java.simpleName, message.toString())
fun Any.error(message: Any?) = Log.e(this::class.java.simpleName, message.toString())
fun Any.wtf(message: Any?) = Log.wtf(this::class.java.simpleName, message.toString())

fun notificationAction(context: Context, id: String, actionType: Int): PendingIntent {
    return PendingIntent.getBroadcast(
            context, actionType,
            Intent(context, NotificationBroadcastReceiver::class.java).apply {
                putExtra(NotificationBroadcastReceiver.NOTION_ID_EXTRA, id)
                putExtra(NotificationBroadcastReceiver.ACTION_TYPE, actionType)
            }, 0)
}

fun notificationContentAction(context: Context, id: String, actionType: Int, action: String): PendingIntent {
    return PendingIntent.getActivity(
            context, actionType,
            Intent(context, MainActivity::class.java).apply {
                this.action = action
                putExtra(NotificationBroadcastReceiver.NOTION_ID_EXTRA, id)
            }, 0)
}