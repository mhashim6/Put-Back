package mhashim6.android.putback.ui

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import mhashim6.android.putback.R
import mhashim6.android.putback.data.Notion
import mhashim6.android.putback.formatDate
import mhashim6.android.putback.isAboutToRun
import java.util.*


/**
 * Created by mhashim6 on 22/08/2018.
 */

/*
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
*/

fun Fragment.launchUrl(urlString: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlString)))
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
    val colorRes = when (interval) {
        in 0..13 -> R.color.muted_red
        in 14..30 -> R.color.muted_green
        in 31..179 -> R.color.muted_blue
        else -> R.color.muted_gray
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

private const val DATE_METADATA_TEMPLATE = "%s: %s       %s: %s"

fun dateMetaDataString(createdAt: Long, lastRunAt: Long, resources: Resources): String {
    return String.format(Locale.getDefault(),
            DATE_METADATA_TEMPLATE,
            resources.getString(R.string.last_run_at),
            formatDate(lastRunAt),
            resources.getString(R.string.created_at),
            formatDate(createdAt))
}


@DrawableRes
fun statusIconSelector(notion: Notion): Int {
    return when {
        notion.isArchived -> R.drawable.ic_archive_grey600_18dp
        notion.isAboutToRun() -> R.drawable.ic_progress_clock_grey600_18dp
        else -> 0
    }
}

val Boolean.visibility
    get() = if (this) View.VISIBLE else View.GONE