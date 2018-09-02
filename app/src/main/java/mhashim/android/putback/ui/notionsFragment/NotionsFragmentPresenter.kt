package mhashim.android.putback.ui.notionsFragment

import android.content.res.Resources
import android.view.View
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.realm.OrderedCollectionChangeSet
import mhashim.android.putback.data.Notion
import mhashim.android.putback.data.NotionsRealm
import mhashim.android.putback.ui.colorSelector
import mhashim.android.putback.ui.visibility

/**
 * Created by mhashim6 on 01/09/2018.
 */

class ViewModel(
		val notions: Observable<Pair<List<NotionCompactViewModel>, OrderedCollectionChangeSet?>>,
		val emptyNotionsVisibility: Observable<Int>,
		val archives: Disposable)

class NotionCompactViewModel(
		resources: Resources,
		val model: Notion,
		val content: String = model.content,
		val interval: Int = model.interval, //TODO
		val createdAt: Long = model.createdAt,
		val modifiedAt: Long = createdAt,
		val lastRunAt: Long = createdAt,
		val archivedIconVisibility: Int = if (model.isArchived) View.VISIBLE else View.GONE,
		val color: Int = colorSelector(model, resources)
)

fun present(
		idleStates: PublishSubject<Pair<NotionCompactViewModel, Boolean>>,
		resources: Resources,
		isIdle: Boolean): ViewModel {

	val fillerViewVisibility = PublishSubject.create<Int>()

	val notions = NotionsRealm.notionsChangeSet(isIdle)
			.doOnNext { fillerViewVisibility.onNext(it.first.isEmpty().visibility) }
			.map { it.first.map { notion -> NotionCompactViewModel(resources, notion) } to it.second }
//				.map { emptyList<NotionCompactViewModel>() } //for debugging empty results.


//		successful archives
	val archives = idleStates
			.subscribe {
				val (notion, idleState) = it
				NotionsRealm.changeIdleState(notion.model, idleState)
			}

	return ViewModel(notions,
			fillerViewVisibility.subscribeOn(
					Schedulers.computation())
					.observeOn(AndroidSchedulers.mainThread()),
			archives)
}
