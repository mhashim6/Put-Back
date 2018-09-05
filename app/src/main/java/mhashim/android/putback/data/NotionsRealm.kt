package mhashim.android.putback.data

import android.os.HandlerThread
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.realm.OrderedCollectionChangeSet
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery
import mhashim.android.putback.debug
import java.util.concurrent.TimeUnit

/**
 * Created by mhashim6 on 31/08/2018.
 */

object NotionsRealm {

	fun findAll(state: Boolean): Flowable<List<Notion>> {
		val realm = Realm.getDefaultInstance()
		return realm.where<Notion>()
				.equalTo("isArchived", state)
				.findAllAsync()
				.asFlowable()
				.filter { it.isLoaded && it.isValid }
				.map { realm.copyFromRealm(it) }
				.doFinally { closeRealm(realm) }
	}

	fun notionsChanges(state: Boolean): Observable<Pair<MutableList<Notion>, OrderedCollectionChangeSet?>> {
		val looperScheduler: Scheduler
		val x: Int
		val thread = HandlerThread("looper")
		thread.start()
		looperScheduler = AndroidSchedulers.from(thread.looper)

		return Observable.create<Pair<MutableList<Notion>, OrderedCollectionChangeSet?>> { source ->
			val realm = Realm.getDefaultInstance()
			val queryResult = realm.where<Notion>()
					.equalTo("isArchived", state)
					.findAll()

			queryResult.addChangeListener { realmResults, changeSet ->
				if (!source.isDisposed)
					if (realmResults.isValid && realmResults.isLoaded) {
						debug("well shit")
						val results: MutableList<Notion> = realm.copyFromRealm(realmResults)
						source.onNext(results to changeSet)
					}
			}
			source.setDisposable(Disposables.fromRunnable {
				queryResult.removeAllChangeListeners()
				realm.close()
			})
		}.subscribeOn(looperScheduler).unsubscribeOn(looperScheduler)
	}


	fun notionsChangeSet(state: Boolean): Observable<Pair<MutableList<Notion>, OrderedCollectionChangeSet?>> {
		val realm = Realm.getDefaultInstance()
		return realm.where<Notion>()
				.equalTo("isArchived", state)
				.findAllAsync()
				.asChangesetObservable()
				.map { Pair(realm.copyFromRealm(it.collection), it.changeset) }
				.doFinally { closeRealm(realm) }
	}

	fun findOne(id: String): Notion? {
		val realm = Realm.getDefaultInstance()

		var notion = realm.where<Notion>().equalTo("id", id).findFirst()
		if (notion != null)
			notion = realm.copyFromRealm(notion)

		closeRealm(realm)
		return notion
	}

	fun changeIdleState(notion: Notion, state: Boolean) {
		changeIdleState(notion.id,state)
	}

	fun changeIdleState(id: String, state: Boolean) {
		val realm = Realm.getDefaultInstance()
		realm.executeTransactionAsync {
			val notion = it.where<Notion>().equalTo("id", id).findFirst()

			notion?.isArchived = state
			debug("here bich ${notion?.isArchived}")
		}
		closeRealm(realm)
	}

	fun changeIdleState(notions: Iterable<Notion>, state: Boolean) {
		val realm = Realm.getDefaultInstance()

		realm.executeTransactionAsync({
			it.copyToRealmOrUpdate(
					notions.map { it.apply { it.isArchived = state } }
			)
		}, { e ->
			error("archive error: ${e.message}")
		})

		closeRealm(realm)
	}

	fun updateLastRunAt(notion: Notion) {
		val realm = Realm.getDefaultInstance()
		notion.lastRunAt = System.currentTimeMillis()

		realm.executeTransaction {
			it.copyToRealmOrUpdate(notion)
		}
		closeRealm(realm)
	}

	fun loadHottestNotion(): Notion? {
		val realm = Realm.getDefaultInstance()

		val activeNotions = realm.where<Notion>().equalTo("isArchived", false).findAll()
		var notion = activeNotions.firstOrNull(hotNotionPredicate)
		debug("idle: ${notion?.isArchived}")
		if (notion != null)
			notion = realm.copyFromRealm(notion)

		closeRealm(realm)
		return notion
	}

	private val hotNotionPredicate: (Notion) -> Boolean = { notion ->
		val lastRunDay = TimeUnit.MILLISECONDS.toDays(notion.lastRunAt)
		val today = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())

//	notion.interval >= (today - lastRunDay)
		true //TODO
	}

	private fun closeRealm(realm: Realm) {
		try {
			realm.close()
		} catch (e: Exception) {
			error(e)
		} finally {
			debug("realm closed")
		}
	}

	private inline fun <reified T : RealmObject> Realm.where(): RealmQuery<T> = this.where(T::class.java)
}