package mhashim6.android.putback.data

import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import io.realm.*
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import mhashim6.android.putback.debug
import mhashim6.android.putback.looperScheduler
import java.util.concurrent.TimeUnit

/**
 * Created by mhashim6 on 31/08/2018.
 */

object NotionsRealm {

    val Notion.isHot: Boolean
        get() {
            val lastRun = lastRunAt
            val now = System.currentTimeMillis()
            val daysPassed = TimeUnit.MILLISECONDS.toDays(now - lastRun)

//            return daysPassed >= notion.intervalSpinnerIndex * notion.timeUnitIndex
            return true //for testing.
        }

    val Notion.isAboutToRun: Boolean
        get() {
            val interval = this.interval * this.timeUnit

            val lastRun = this.lastRunAt
            val now = System.currentTimeMillis()
            val daysPassed = TimeUnit.MILLISECONDS.toDays(now - lastRun)

            return interval - daysPassed <= 2
        }

    private val realmScheduler = looperScheduler()

    fun notionsChanges(state: Boolean): Observable<Pair<MutableList<Notion>, OrderedCollectionChangeSet>> {
        return Observable.create<Pair<MutableList<Notion>, OrderedCollectionChangeSet>> { source ->
            val realm = Realm.getDefaultInstance()
            val queryResult = realm.where<Notion>()
                    .equalTo("isArchived", state)
                    .sort("createdAt", Sort.DESCENDING)
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
        }.subscribeOn(realmScheduler).unsubscribeOn(realmScheduler)
    }

    fun findOne(id: String): Notion? {
        val realm = Realm.getDefaultInstance()
        var notion: Notion? = null
        realm.executeTransaction {

            notion = it.where<Notion>()
                    .equalTo("id", id)
                    .findFirst()
            notion?.let { n -> notion = it.copyFromRealm(n) }

        }
        closeRealm(realm)

        return notion
    }

    fun changeIdleState(id: String, state: Boolean) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransactionAsync {
            val notion = it.where<Notion>().equalTo("id", id).findFirst()

            notion?.isArchived = state
        }
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

    fun findHottestNotion(): Notion? {
        val realm = Realm.getDefaultInstance()

        val activeNotions = realm.where<Notion>()
                .equalTo("isArchived", false)
                .sort("createdAt", Sort.ASCENDING)
                .findAll()
        var notion = activeNotions.firstOrNull { it.isHot }
        if (notion != null)
            notion = realm.copyFromRealm(notion)

        closeRealm(realm)
        return notion
    }

    fun add(notion: Notion) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransactionAsync {
            it.copyToRealmOrUpdate(notion)
        }
        closeRealm(realm)
    }

    fun update(notionId: String, content: String, interval: Int, timeUnit: Int) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransactionAsync {
            val notion = it.where<Notion>()
                    .equalTo("id", notionId)
                    .findFirst() ?: it.createObject(notionId)

            notion.content = content
            notion.interval = interval
            notion.timeUnit = timeUnit
            notion.modifiedAt = System.currentTimeMillis()
        }
        closeRealm(realm)
    }

    fun delete(notionId: String?) {
        notionId?.let { id ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                it.where<Notion>().equalTo("id", id).findFirst()?.deleteFromRealm()
            }
            closeRealm(realm)
        }
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
}