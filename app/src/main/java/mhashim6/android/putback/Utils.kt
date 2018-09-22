/**
 * Created by mhashim6 on 29/08/2018.
 */

@file:Suppress("NOTHING_TO_INLINE")

package mhashim6.android.putback

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.HandlerThread
import android.util.Log
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import mhashim6.android.putback.data.Notion
import mhashim6.android.putback.ui.MainActivity
import mhashim6.android.putback.work.NotificationBroadcastReceiver
import java.text.DateFormat
import java.util.concurrent.TimeUnit


const val APP_URL = "https://play.google.com/store/apps/details?id=mhashim6.android.putback"
const val GITHUB_URL = "https://github.com/mhashim6"

fun Any.debug(message: Any?) = Log.d(this::class.java.simpleName, message.toString())
fun Any.info(message: Any?) = Log.i(this::class.java.simpleName, message.toString())
fun Any.verbose(message: Any?) = Log.v(this::class.java.simpleName, message.toString())
fun Any.error(message: Any?) = Log.e(this::class.java.simpleName, message.toString())
fun Any.wtf(message: Any?) = Log.wtf(this::class.java.simpleName, message.toString())


fun looperScheduler(): Scheduler {
    var looperScheduler: Scheduler? = null
    val thread = HandlerThread("looper")
    thread.start()
    synchronized(thread) {
        looperScheduler = AndroidSchedulers.from(thread.looper)
    }
    return looperScheduler!!
}

fun notificationAction(context: Context, id: String, actionType: Int): PendingIntent {
    return PendingIntent.getBroadcast(
            context, actionType,
            Intent(context, NotificationBroadcastReceiver::class.java).apply {
                putExtra(NotificationBroadcastReceiver.NOTION_ID_EXTRA, id)
                putExtra(NotificationBroadcastReceiver.ACTION_TYPE, actionType)
            }, PendingIntent.FLAG_UPDATE_CURRENT)
}

fun notificationContentAction(context: Context, id: String, actionType: Int, action: String): PendingIntent {
    return PendingIntent.getActivity(
            context, actionType,
            Intent(context, MainActivity::class.java).apply {
                this.action = action
                putExtra(NotificationBroadcastReceiver.NOTION_ID_EXTRA, id)
            }, PendingIntent.FLAG_UPDATE_CURRENT)
}

//fun <E> List<E>.random(): E = this[Random().nextInt(this.size)]

fun isAboutToRun(notion: Notion): Boolean {
    val interval = notion.interval * notion.timeUnit

    val lastRunDay = TimeUnit.MILLISECONDS.toDays(notion.lastRunAt)
    val today = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())
    return interval - (today - lastRunDay) <= 2
}

fun formatDate(date: Long): String = DateFormat.getDateInstance(DateFormat.MEDIUM).format(date)

fun String.withNewLine(): String {
    return if (isEmpty()) this else (this + "\n")
}