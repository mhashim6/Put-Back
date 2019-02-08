package mhashim6.android.putback.ui.notionsDetailFragment

import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import mhashim6.android.putback.data.Notion
import mhashim6.android.putback.data.NotionsRealm
import mhashim6.android.putback.ui.*
import mhashim6.android.putback.ui.notionsDetailFragment.NotionDetailFragment.Companion.NOTION_DETAIL_NOTION_CONTENT
import mhashim6.android.putback.withNewLine
import java.util.*

class ViewModel(
        val notion: NotionDetailViewModel,
        val backgroundColor: Observable<ColorDrawable>,
        val update: Disposable
)

class NotionUpdate(
        var content: String,
        val intervalSpinnerIndex: Int,
        val timeUnitIndex: Int
)

class NotionDetailViewModel(
        notion: Notion,
        resources: Resources,
        val notionId: String = notion.id,
        val content: String = notion.content.withNewLine(), //leave a space for the user to touch in case of a url.
        val intervalSpinnerIndex: Int = notion.interval.intervalSpinnerIndex,
        val timeUnit: Int = notion.timeUnit.unitSpinnerIndex,
        val backgroundColor: ColorDrawable = ColorDrawable(colorSelector(notion, resources)),
        val dateMetaData: String = dateMetaDataString(notion.createdAt, notion.lastRunAt, resources)
)

fun present(args: Bundle?,
            intervals: PublishSubject<Pair<Int, Int>>,
            updates: Observable<NotionUpdate>,
            resources: Resources): ViewModel {


    val notionId = args?.getString(NotionDetailFragment.NOTION_DETAIL_NOTION_ID)
            ?: UUID.randomUUID().toString()


    var notion = NotionsRealm.findOne(notionId)
    val isNew = notion == null
    if (isNew)
        notion = Notion(id = notionId, content = args?.getString(NOTION_DETAIL_NOTION_CONTENT) ?: "")

    val colors = intervals.map { (intervalIndex, unitIndex) ->
        val interval = intervalIndex.interval
        val unit = unitIndex.unit
        ColorDrawable(colorSelector(interval, unit, resources))
    }

    val updateDisposable =
            updates.map { update -> update.apply { content = content.trim() } }.subscribe { update ->
                if (update.isBlank(isNew))
                    NotionsRealm.delete(notionId)
                else
                    NotionsRealm.update(notionId,
                            update.content,
                            update.intervalSpinnerIndex.interval,
                            update.timeUnitIndex.unit)
            }

    args?.putString(NotionDetailFragment.NOTION_DETAIL_NOTION_ID, notionId) // retain id in case of rotation.

    return ViewModel(NotionDetailViewModel(notion!!, resources), colors, updateDisposable)
}

/** blank and new. */
fun NotionUpdate.isBlank(isNew: Boolean) =
        content.isBlank() && isNew

val Int.minutes: Long
    get() = this * 60L * 1000L
