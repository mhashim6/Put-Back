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
import mhashim6.android.putback.data.PreferencesRepository
import mhashim6.android.putback.ui.MainActivity
import mhashim6.android.putback.work.NotificationBroadcastReceiver
import mhashim6.lib.ratemonitor.RateConditionsMonitor
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


const val APP_VERSION = BuildConfig.VERSION_CODE

const val APP_URL = "https://play.google.com/store/apps/details?id=mhashim6.android.putback"

const val GITHUB_URL = "https://github.com/mhashim6"

fun Any.debug(message: Any?) = Log.d(this::class.java.simpleName, message.toString())
fun Any.info(message: Any?) = Log.i(this::class.java.simpleName, message.toString())
fun Any.verbose(message: Any?) = Log.v(this::class.java.simpleName, message.toString())
fun Any.error(message: Any?) = Log.e(this::class.java.simpleName, message.toString())
fun Any.wtf(message: Any?) = Log.wtf(this::class.java.simpleName, message.toString())

val hotNotionPredicate: (Notion) -> Boolean = { notion ->
    val lastRun = notion.lastRunAt
    val now = System.currentTimeMillis()
    val daysPassed = TimeUnit.MILLISECONDS.toDays(now - lastRun)

//    daysPassed >= notion.interval * notion.timeUnit
    true //for testing.
}


fun looperScheduler(): Scheduler {
    var looperScheduler: Scheduler? = null
    val thread = HandlerThread("looper")
    thread.start()
    synchronized(thread) {
        looperScheduler = AndroidSchedulers.from(thread.looper)
    }
    return looperScheduler!!
}

fun notificationAction(context: Context, notion: Notion, actionType: Int): PendingIntent {
    val notificationId = notion.lastRunAt.toInt()

    return PendingIntent.getBroadcast(
            context, notificationId + actionType,
            Intent(context, NotificationBroadcastReceiver::class.java).apply {
                putExtra(NotificationBroadcastReceiver.NOTION_ID_EXTRA, notion.id)
                putExtra(NotificationBroadcastReceiver.NOTIFICATION_ID_EXTRA, notificationId)
                putExtra(NotificationBroadcastReceiver.ACTION_TYPE, actionType)
            }, PendingIntent.FLAG_UPDATE_CURRENT)
}

fun notificationContentAction(context: Context, notion: Notion, actionType: Int, action: String): PendingIntent {
    val notificationId = notion.lastRunAt.toInt()

    return PendingIntent.getActivity(
            context, notificationId,
            Intent(context, MainActivity::class.java).apply {
                this.action = action
                putExtra(NotificationBroadcastReceiver.NOTION_ID_EXTRA, notion.id)
            }, PendingIntent.FLAG_UPDATE_CURRENT)
}

//fun <E> List<E>.random(): E = this[Random().nextInt(this.size)]

fun Notion.isAboutToRun(): Boolean {
    val interval = this.interval * this.timeUnit

    val lastRun = this.lastRunAt
    val now = System.currentTimeMillis()
    val daysPassed = TimeUnit.MILLISECONDS.toDays(now - lastRun)

    return interval - daysPassed <= 2
}

inline fun ifNewUpdate(action: () -> Unit) {
    if (PreferencesRepository.updateVersion < APP_VERSION) {
        PreferencesRepository.updateVersion = APP_VERSION
        action()
    }
}

fun formatDate(date: Long): String = SimpleDateFormat("dd MMMM yyyy hh:mm aa", Locale.getDefault()).format(date)

inline fun String.withNewLine(): String {
    return if (isEmpty()) this else (this + "\n")
}

inline fun Int.toOneIfZero(): Int = if (this == 0) 1 else this

inline fun RateConditionsMonitor.init(context: Context, block: RateConditionsMonitor.() -> Unit) {
    init(context)
    apply(block)
}