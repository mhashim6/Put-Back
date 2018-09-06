package mhashim6.android.putback

import mhashim6.android.putback.data.Notion
import mhashim6.android.putback.data.Notion.TimeUnits.DAY
import mhashim6.android.putback.data.NotionsRealm.hotNotionPredicate
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*
import java.util.concurrent.TimeUnit

class ExampleUnitTest {

    @Test
    fun testHotNotionPredicate() {
        val pastDayInMillis = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7) //last run 7 interval ago

        println(Date(pastDayInMillis))

        assertTrue(hotNotionPredicate(
                Notion(
                        lastRunAt = pastDayInMillis,
                        interval = 8,
                        timeUnit = DAY
                )
        ).not())
    }

}
