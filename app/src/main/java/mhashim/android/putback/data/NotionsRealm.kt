package mhashim.android.putback.data

import io.reactivex.Flowable
import io.realm.Realm
import io.realm.RealmResults
import mhashim.android.putback.debug
import mhashim.android.putback.hotNotionPredicate

/**
 * Created by mhashim6 on 31/08/2018.
 */

object NotionsRealm {

	fun findAllWithIdleStatus(status: Boolean): Flowable<RealmResults<Notion>> {
		val realm = Realm.getDefaultInstance()
		return realm.where(Notion::class.java)
				.equalTo("isArchived", status)
				.findAllAsync()
				.asFlowable()
				.filter { it.isLoaded && it.isValid }
				.doFinally { closeRealm(realm) }
	}

	fun changeIdleStatus(notion: Notion, status: Boolean) {
		val realm = Realm.getDefaultInstance()

		realm.executeTransactionAsync {
			it.copyToRealmOrUpdate(notion.apply { isArchived = status })
		}

		closeRealm(realm)
	}

	fun changeIdleStatus(notions: Iterable<Notion>, status: Boolean) {
		val realm = Realm.getDefaultInstance()

		realm.executeTransactionAsync({
			it.copyToRealmOrUpdate(
					notions.map { it.apply { it.isArchived = status } }
			)
		}, { e ->
			error("archive error: ${e.message}")
		})

		closeRealm(realm)
	}

	fun updateLastRunAt(notion: Notion, lastRunAt: Long) {
		val realm = Realm.getDefaultInstance()
		notion.lastRunAt = lastRunAt

		realm.executeTransactionAsync {
			it.copyToRealmOrUpdate(notion)
		}
		realm.close()
	}

	fun loadHottestNotion(): Notion? {
		val realm = Realm.getDefaultInstance()
		val activeNotions = realm.where(Notion::class.java).equalTo("isArchived", false).findAll()
		var notion = activeNotions.firstOrNull(hotNotionPredicate)
		if (notion != null)
			notion = realm.copyFromRealm(notion)
		closeRealm(realm)
		return notion
	}

	fun closeRealm(realm: Realm) {
		try {
			realm.close()
		} catch (e: Exception) {
		} finally {
			debug("Realm has been closed.")
		}
	}
}