package mhashim6.android.putback

import mhashim6.android.putback.data.Notion
import org.junit.Test
import java.util.concurrent.TimeUnit

class ExampleUnitTest {

	@Test
	fun testHotNotionPredicate() {
		val pastDayInMillis = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7) //last run 7 interval ago

		assert(hotNotionPredicate(
				Notion(
						lastRunAt = pastDayInMillis,
						interval = 6
				)
		))

		assert(hotNotionPredicate(
				Notion(
						lastRunAt = pastDayInMillis,
						interval = 8
				)
		).not())
	}

}
