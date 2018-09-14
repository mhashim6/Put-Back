package mhashim6.android.putback.ui.notionsDetailFragment

import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import mhashim6.android.putback.data.Notion
import mhashim6.android.putback.data.NotionsRealm
import mhashim6.android.putback.ui.colorSelector
import mhashim6.android.putback.ui.dateMetaDataString
import mhashim6.android.putback.ui.indexByUnit
import mhashim6.android.putback.ui.notionsDetailFragment.NotionDetailFragment.Companion.NOTION_DETAIL_ACTION_DISPLAY
import mhashim6.android.putback.ui.notionsDetailFragment.NotionDetailFragment.Companion.NOTION_DETAIL_ACTION_RETAINED
import mhashim6.android.putback.ui.notionsDetailFragment.NotionDetailFragment.Companion.NOTION_DETAIL_ACTION_TYPE
import mhashim6.android.putback.ui.notionsDetailFragment.NotionDetailFragment.Companion.NOTION_DETAIL_NOTION_CONTENT
import mhashim6.android.putback.ui.unitByIndex
import java.util.*

class ViewModel(
        val notion: NotionDetailViewModel,
        val backgroundColor: Observable<ColorDrawable>,
        val update: Disposable
)

class NotionUpdate(
        var content: String,
        val interval: String,
        val timeUnit: Int
)

class NotionDetailViewModel(
        notion: Notion,
        resources: Resources,
        val notionId: String = notion.id,
        val content: String = notion.content + "\n", //leave a space for the user to touch in case of a url.
        val interval: String = notion.interval.toString(),
        val timeUnit: Int = indexByUnit(notion.timeUnit),
        val backgroundColor: ColorDrawable = ColorDrawable(colorSelector(notion, resources)),
        val dateMetaData: String = dateMetaDataString(notion.createdAt, notion.lastRunAt, resources)
)

fun present(args: Bundle?,
            intervals: Observable<Pair<String, Int>>,
            update: Observable<NotionUpdate>,
            resources: Resources): ViewModel {

    val actionType = args?.getInt(NotionDetailFragment.NOTION_DETAIL_ACTION_TYPE)
            ?: NotionDetailFragment.NOTION_DETAIL_ACTION_CREATE
    val notionId = args?.getString(NotionDetailFragment.NOTION_DETAIL_NOTION_ID)
            ?: UUID.randomUUID().toString()
    val notion = when (actionType) {
        NOTION_DETAIL_ACTION_DISPLAY -> NotionsRealm.findOne(notionId)
                ?: Notion(id = notionId) //assuming null is better than a crash.
        NOTION_DETAIL_ACTION_RETAINED -> NotionsRealm.findOne(notionId) ?: Notion(id = notionId)
        else -> Notion(id = notionId, content = args?.getString(NOTION_DETAIL_NOTION_CONTENT) ?: "")
    }
    args?.apply {
        //consume the current action.
        putInt(NOTION_DETAIL_ACTION_TYPE, NOTION_DETAIL_ACTION_RETAINED)
        putString(NotionDetailFragment.NOTION_DETAIL_NOTION_ID, notion.id)
    }

    val colors = intervals.map { pair ->
        val count = pair.first.takeIf(String::isNotEmpty)?.toInt() ?: 1
        val unit = unitByIndex(pair.second)
        ColorDrawable(colorSelector(count, unit, resources))
    }

    val updateDisposable = update.map { it.apply { content = content.trim() } }.subscribe {
        if (it.content.isEmpty())
            NotionsRealm.delete(notionId)
        else
            NotionsRealm.update(notionId,
                    it.content,
                    it.interval.takeIf(String::isNotEmpty)?.toInt() ?: 1,
                    unitByIndex(it.timeUnit))
    }

    return ViewModel(NotionDetailViewModel(notion, resources), colors, updateDisposable)
}
