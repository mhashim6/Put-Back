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
import mhashim.android.putback.debug
import java.util.concurrent.TimeUnit

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

fun NotionCompactView.render(notion: NotionCompactViewModel) {
	content.text = notion.content
	archiveIcon.visibility = notion.archivedIconVisibility
	setCardBackgroundColor(notion.color)
}

object NotionsPresenter {

	fun present(archiveAttempts: PublishSubject<NotionCompactViewModel>, resources: Resources, isIdle: Boolean): ViewModel {
		val realm = Realm.getDefaultInstance()

		val notions = NotionsRealm.findAllWithIdleStatus(isIdle)
				.map { realm.copyFromRealm(it).map { notion -> NotionCompactViewModel(resources, notion) } }
//				.map { emptyList<NotionCompactViewModel>() } //for debugging empty results.


		val emptyNotionsVisibility = notions.map { if (it.isEmpty()) VISIBLE else GONE }

//		successful archives
		val successfulArchives = archiveAttempts.buffer(5, TimeUnit.SECONDS)
				.map {
					val models = it.map { it.model }
					models.map {
						it.apply { isArchived = isIdle.not() }
					}
				}
				.filter { it.isNotEmpty() }
				.subscribeOn(Schedulers.computation())
				.observeOn(AndroidSchedulers.mainThread())
				.doFinally { NotionsRealm.closeRealm(realm) }
				.subscribe { list ->
					debug("buffer period has passed")
					NotionsRealm.changeIdleStatus(list, isIdle.not())
				}

		return ViewModel(notions, emptyNotionsVisibility, successfulArchives)
	}

}