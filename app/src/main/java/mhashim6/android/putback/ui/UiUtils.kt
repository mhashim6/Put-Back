package mhashim6.android.putback.ui

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import mhashim6.android.putback.R
import mhashim6.android.putback.data.Notion
import mhashim6.android.putback.formatDate
import mhashim6.android.putback.isAboutToRun
import java.util.*


/**
 * Created by mhashim6 on 22/08/2018.
 */

fun View.captureBitmap(): Bitmap {
    val empty = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(empty)
    this.background?.draw(canvas)
    this.draw(canvas)
    return empty
}

fun Bitmap.crop(startX: Int, startY: Int, endX: Int, endY: Int): Bitmap {
    return Bitmap.createBitmap(this, startX, startY, endX - startX, endY - startY)
}


fun TextView.marquee() {
    isSelected = true
    setSingleLine(true)
    ellipsize = TextUtils.TruncateAt.MARQUEE
}

fun colorSelector(notion: Notion, resources: Resources): Int {
    return colorSelector(notion.interval, notion.timeUnit, resources)
}

fun colorSelector(count: Int, unit: Int, resources: Resources): Int {
    val interval = count * unit
    val colorRes = when {
        interval in 0..13 -> R.color.muted_red
        interval in 14..30 -> R.color.muted_green
        else -> R.color.muted_blue
    }

    return ResourcesCompat.getColor(resources, colorRes, null)
}


private const val INTERVAL_TEMPLATE = "%d %s"

fun intervalString(interval: Int, unit: Int, resources: Resources): String {
    return String.format(Locale.getDefault(),
            INTERVAL_TEMPLATE,
            interval,
            resources.getString(unitStringSelector(interval, unit)))
}

@StringRes
fun unitStringSelector(interval: Int, unit: Int): Int {
    val isPlural = (interval > 1)

    return when (unit) {
        Notion.WEEK -> if (isPlural) R.string.week_plural else R.string.week
        Notion.MONTH -> if (isPlural) R.string.month_plural else R.string.month
        Notion.YEAR -> if (isPlural) R.string.year_plural else R.string.year
        else -> if (isPlural) R.string.day_plural else R.string.day
    }
}

fun unitByIndex(index: Int): Int {
    return when (index) {
        0 -> Notion.WEEK
        1 -> Notion.MONTH
        2 -> Notion.YEAR
        else -> Notion.DAY
    }
}

fun indexByUnit(unit: Int): Int {
    return when (unit) {
        Notion.WEEK -> 0
        Notion.MONTH -> 1
        Notion.YEAR -> 2
        else -> 3
    }
}

private const val DATE_METADATA_TEMPLATE = "%s %s - %s %s"

fun dateMetaDataString(createdAt: Long, lastRunAt: Long, resources: Resources): String {
    return String.format(Locale.getDefault(),
            DATE_METADATA_TEMPLATE,
            resources.getString(R.string.created_at),
            formatDate(createdAt),
            resources.getString(R.string.last_run_at),
            formatDate(lastRunAt))
}


@DrawableRes
fun statusIconSelector(notion: Notion): Int {
    return when {
        notion.isArchived -> R.drawable.ic_archive_grey600_18dp
        isAboutToRun(notion) -> R.drawable.ic_progress_clock_grey600_18dp
        else -> 0
    }
}

private const val RED_DUST = "red_dust"
private const val GRACE = "grace"
private const val COPPER = "copper"
private const val PREDAWN = "predawn"
private const val DUSK = "dusk"
private const val BRONZE_ATMOSPHERE = "bronze_atmosphere"
private const val PINK_HORIZON = "pink_horizon"

@DrawableRes
fun themeSelector(themeString: String): Int {
    return when (themeString) {
        GRACE -> R.drawable.window_background_grace
        COPPER -> R.drawable.window_background_copper
        PREDAWN -> R.drawable.window_background_predawn
        DUSK -> R.drawable.window_background_dusk
        BRONZE_ATMOSPHERE -> R.drawable.window_background_bronze_atmosphere
        PINK_HORIZON -> R.drawable.window_background_pink_horizon
        else -> R.drawable.window_background_red_dust
    }
}

val Boolean.visibility
    get() = if (this) View.VISIBLE else View.GONE