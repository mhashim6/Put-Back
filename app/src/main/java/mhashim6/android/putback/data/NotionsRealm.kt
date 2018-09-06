package mhashim6.android.putback.data

import android.os.HandlerThread
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.realm.*
import mhashim6.android.putback.debug
import java.util.concurrent.TimeUnit

/**
 * Created by mhashim6 on 31/08/2018.
 */

object NotionsRealm {

    fun findAll(state: Boolean): Flowable<List<Notion>> {
        val realm = Realm.getDefaultInstance()
        return realm.where<Notion>()
                .equalTo("isArchived", state)
                .sort("createdAt")
                .findAllAsync()
                .asFlowable()
                .filter { it.isLoaded && it.isValid }
                .map { realm.copyFromRealm(it) }
                .doFinally { closeRealm(realm) }
    }

    fun notionsChanges(state: Boolean): Observable<Pair<MutableList<Notion>, OrderedCollectionChangeSet>> {
        val scheduler = looperScheduler()
        return Observable.create<Pair<MutableList<Notion>, OrderedCollectionChangeSet>> { source ->
            val realm = Realm.getDefaultInstance()
            val queryResult = realm.where<Notion>()
                    .equalTo("isArchived", state)
                    .sort("createdAt")
                    .findAllAsync()

            val listener: OrderedRealmCollectionChangeListener<RealmResults<Notion>> = OrderedRealmCollectionChangeListener { realmResults, changeSet ->
                if (realmResults.isValid && realmResults.isLoaded) {
                    val results: MutableList<Notion> = realm.copyFromRealm(realmResults)
                    source.onNext(results to changeSet)
                }
            }
            queryResult.addChangeListener(listener)
            source.setDisposable(Disposables.fromRunnable {
                queryResult.removeChangeListener(listener)
                realm.close()
            })
        }.subscribeOn(scheduler).unsubscribeOn(scheduler)
    }

    private fun looperScheduler(): Scheduler {
        var looperScheduler: Scheduler? = null
        val thread = HandlerThread("looper")
        thread.start()
        synchronized(thread) {
            looperScheduler = AndroidSchedulers.from(thread.looper)
        }
        return looperScheduler!!
    }

    fun notionsChangesSubject(state: Boolean): Observable<Pair<MutableList<Notion>, OrderedCollectionChangeSet>> {

        val notionsChanges = BehaviorSubject.create<Pair<MutableList<Notion>, OrderedCollectionChangeSet>>()

        val realm = Realm.getDefaultInstance()
        val queryResult = realm.where<Notion>()
                .equalTo("isArchived", state)
                .sort("createdAt")
                .findAllAsync()
        val listener: OrderedRealmCollectionChangeListener<RealmResults<Notion>> = OrderedRealmCollectionChangeListener { realmResults, changeSet ->
            if (realmResults.isValid && realmResults.isLoaded) {
                val results: MutableList<Notion> = realm.copyFromRealm(realmResults)
                notionsChanges.onNext(results to changeSet)
            }
        }
        queryResult.addChangeListener(listener)
        notionsChanges.doFinally {
            queryResult.removeChangeListener(listener)
            closeRealm(realm)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

        return notionsChanges
    }

    fun notionsChangeSetMainThread(state: Boolean): Observable<Pair<MutableList<Notion>, OrderedCollectionChangeSet?>> {
        val realm = Realm.getDefaultInstance()
        return realm.where<Notion>()
                .equalTo("isArchived", state)
                .sort("createdAt")
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
        changeIdleState(notion.id, state)
    }

    fun changeIdleState(id: String, state: Boolean) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransactionAsync {
            val notion = it.where<Notion>().equalTo("id", id).findFirst()

            notion?.isArchived = state
        }
        closeRealm(realm)
    }

    fun changeIdleState(notions: Iterable<Notion>, state: Boolean) {
        val realm = Realm.getDefaultInstance()

        realm.executeTransactionAsync({
            it.copyToRealmOrUpdate(
                    notions.map { list -> list.apply { isArchived = state } }
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

//	(today - lastRunDay) >= notion.interval*notion.timeUnit
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