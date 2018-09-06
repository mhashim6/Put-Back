package mhashim6.android.putback.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Notion(
		@PrimaryKey var id: String = UUID.randomUUID().toString(),
		var content: String = "",
		var interval: Int = 14, //moderate priority.
		var createdAt: Long = System.currentTimeMillis(),
		var modifiedAt: Long = createdAt,
		/**last date this Notion has passed it's interval.*/
		var lastRunAt: Long = createdAt,
		var isArchived: Boolean = false
) : RealmObject() {

	override fun equals(other: Any?): Boolean {
		return if (other == null || other !is Notion) false
		else {
			id == other.id
			&& content == other.content
			&& interval == other.interval
			&& createdAt == other.createdAt
			&& modifiedAt == other.modifiedAt
			&& lastRunAt == other.lastRunAt
			&& isArchived == other.isArchived
		}

	}

	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + content.hashCode()
		result = 31 * result + interval
		result = 31 * result + createdAt.hashCode()
		result = 31 * result + modifiedAt.hashCode()
		result = 31 * result + lastRunAt.hashCode()
		result = 31 * result + isArchived.hashCode()
		return result
	}
}