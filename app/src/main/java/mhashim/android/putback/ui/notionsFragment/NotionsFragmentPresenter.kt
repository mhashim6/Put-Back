package mhashim.android.putback.ui

import android.content.res.Resources
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.realm.Realm
import mhashim.android.putback.data.Notion
import mhashim.android.putback.data.NotionsRealm

/**
 * Created by mhashim6 on 01/09/2018.
 */

class ViewModel(val notions: Flowable<List<NotionCompactViewModel>>, val emptyNotionsVisibility: Flowable<Int>, val successfulArchives: Disposable)

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

object NotionsPresenter {

	fun present(
			idleStates: PublishSubject<Pair<NotionCompactViewModel, Boolean>>,
			resources: Resources,
			isIdle: Boolean): ViewModel {

		val realm = Realm.getDefaultInstance()

		val notions = NotionsRealm.findAllWithIdleStatus(isIdle)
				.map { realm.copyFromRealm(it).map { notion -> NotionCompactViewModel(resources, notion) } }
//				.map { emptyList<NotionCompactViewModel>() } //for debugging empty results.


		val emptyNotionsVisibility = notions.map { if (it.isEmpty()) VISIBLE else GONE }

//		successful archives
		val successfulArchives = idleStates
				.subscribeOn(Schedulers.computation())
				.observeOn(AndroidSchedulers.mainThread())
				.doFinally { NotionsRealm.closeRealm(realm) }
				.subscribe {
					val notion = it.first.model
					val idleState = it.second
					NotionsRealm.changeIdleStatus(notion, idleState)
				}

		return ViewModel(notions, emptyNotionsVisibility, successfulArchives)
	}

}