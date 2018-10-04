package mhashim6.android.putback.ui.notionsFragment

import android.content.res.Resources
import androidx.annotation.DrawableRes
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.realm.OrderedCollectionChangeSet
import mhashim6.android.putback.data.Notion
import mhashim6.android.putback.data.NotionsRealm
import mhashim6.android.putback.ui.colorSelector
import mhashim6.android.putback.ui.intervalString
import mhashim6.android.putback.ui.statusIconSelector
import mhashim6.android.putback.wtf

/**
 * Created by mhashim6 on 01/09/2018.
 */

class ViewModel(
        val notionsChanges: Observable<Pair<List<NotionCompactViewModel>, OrderedCollectionChangeSet>>,
        val emptyNotionsVisibility: Observable<Boolean>,
        val archives: Disposable,
        val deletes: Disposable)

class NotionCompactViewModel(
        resources: Resources,
        val model: Notion,
        val notionId: String = model.id,
        val content: String = model.content,
        val interval: String = intervalString(model.interval, model.timeUnit, resources),
        @DrawableRes val statusIcon: Int = statusIconSelector(model),
        val color: Int = colorSelector(model, resources)
)

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

fun present(
        idleStates: PublishSubject<Pair<NotionCompactViewModel, Boolean>>,
        deletes: PublishSubject<Pair<NotionCompactViewModel, Boolean>>,
        resources: Resources,
        isIdle: Boolean): ViewModel {

    val fillerViewVisibility = PublishSubject.create<Boolean>()

    val notionsChanges = NotionsRealm.notionsChanges(isIdle)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { fillerViewVisibility.onNext(it.first.isEmpty()) }
            .map { it.first.map { notion -> NotionCompactViewModel(resources, notion) } to it.second }

//		successful archives
    val idleStatesDisposable = idleStates
            .subscribe {
                val (notion, idleState) = it
                NotionsRealm.changeIdleState(notion.notionId, idleState)
            }

    val deletesDisposable = deletes.observeOn(Schedulers.io()).subscribe {
        if (it.second) //undo
            NotionsRealm.add(it.first.model)
        else
            NotionsRealm.delete(it.first.notionId)
    }

    return ViewModel(notionsChanges,
            fillerViewVisibility.observeOn(AndroidSchedulers.mainThread()),
            idleStatesDisposable, deletesDisposable)
}
