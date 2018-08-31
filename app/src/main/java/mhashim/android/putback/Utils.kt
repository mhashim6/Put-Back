/**
 * Created by mhashim6 on 29/08/2018.
 */

@file:Suppress("NOTHING_TO_INLINE")

package mhashim.android.putback

import android.util.Log
import mhashim.android.putback.data.Notion
import java.util.concurrent.TimeUnit

fun Any.debug(message: Any?) = Log.d(this::class.java.simpleName, message.toString())
fun Any.info(message: Any?) = Log.i(this::class.java.simpleName, message.toString())
fun Any.verbose(message: Any?) = Log.v(this::class.java.simpleName, message.toString())
fun Any.error(message: Any?) = Log.e(this::class.java.simpleName, message.toString())
fun Any.wtf(message: Any?) = Log.wtf(this::class.java.simpleName, message.toString())

val hotNotionPredicate: (Notion) -> Boolean = { notion ->
	val lastRunDay = TimeUnit.MILLISECONDS.toDays(notion.lastRunAt)
	val today = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())

//	notion.interval >= (today - lastRunDay)
	true
}