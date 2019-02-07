package mhashim6.android.putback

import mhashim6.android.putback.data.Notion
import mhashim6.android.putback.data.Notion.TimeUnits.DAY
import mhashim6.android.putback.data.NotionsRealm.isHot
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*
import java.util.concurrent.TimeUnit

class ExampleUnitTest {

    @Test
    fun testHotNotionPredicate() {
        val pastDayInMillis = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7) //last run 7 intervalSpinnerIndex ago

        println(Date(pastDayInMillis))

        assertTrue(
                Notion(
                        lastRunAt = pastDayInMillis,
                        interval = 8,
                        timeUnit = DAY
                ).isHot.not())
    }

}
