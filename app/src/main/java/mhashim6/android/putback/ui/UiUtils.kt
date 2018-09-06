package mhashim6.android.putback.ui

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import io.realm.OrderedCollectionChangeSet
import mhashim6.android.putback.R
import mhashim6.android.putback.data.Notion
import mhashim6.android.putback.isAboutToRun
import mhashim6.android.putback.ui.notionsFragment.BaseAdapter
import mhashim6.android.putback.ui.notionsFragment.NotionCompactView
import mhashim6.android.putback.ui.notionsFragment.NotionCompactViewModel
import mhashim6.android.putback.wtf
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

fun colorSelector(notion: Notion, resources: Resources): Int {
    val interval = notion.interval * notion.timeUnit
    val colorRes = when {
        interval in 0..13 -> R.color.muted_red
        interval in 14..30 -> R.color.muted_green
        else -> R.color.muted_blue
    }

    return ResourcesCompat.getColor(resources, colorRes, null)
}


fun intervalString(interval: Int, unit: Int, resources: Resources): String {
    return String.format(Locale.getDefault(),
            resources.getString(R.string.time_unit_template),
            interval,
            resources.getString(unitStringSelector(interval, unit)))
}

@StringRes
fun unitStringSelector(interval: Int, unit: Int): Int {
    val isPlural = (interval > 1)

    return when (unit) {
        Notion.YEAR -> if (isPlural) R.string.year_plural else R.string.year
        Notion.MONTH -> if (isPlural) R.string.month_plural else R.string.month
        Notion.WEEK -> if (isPlural) R.string.week_plural else R.string.week
        else -> if (isPlural) R.string.day_plural else R.string.day
    }
}

@DrawableRes
fun statusIconSelector(notion: Notion): Int {
    return when {
        notion.isArchived -> R.drawable.ic_archive_grey600_18dp
        isAboutToRun(notion) -> R.drawable.ic_progress_clock_grey600_18dp
        else -> 0
    }
}


val Boolean.visibility
    get() = if (this) View.VISIBLE else View.GONE


fun BaseAdapter<NotionCompactView, NotionCompactViewModel>.handleChanges(collectionChange: Pair<List<NotionCompactViewModel>, OrderedCollectionChangeSet>) {
    val (collection, changeset) = collectionChange

    if (changeset.state == OrderedCollectionChangeSet.State.ERROR) {
        wtf("changeset state = ERROR")
        return
    }

    replaceAll(collection)
    if (changeset.state == OrderedCollectionChangeSet.State.INITIAL)
        notifyDataSetChanged()
    else {
        for (change in changeset.changeRanges)
            notifyItemRangeChanged(change.startIndex, change.length)

        for (insertion in changeset.insertionRanges)
            notifyItemRangeInserted(insertion.startIndex, insertion.length)

        for (deletion in changeset.deletionRanges)
            notifyItemRangeRemoved(deletion.startIndex, deletion.length)
    }
}