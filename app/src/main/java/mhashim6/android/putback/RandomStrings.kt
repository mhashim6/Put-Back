package mhashim6.android.putback

import android.content.res.Resources
import androidx.annotation.StringRes
import java.util.*

/**
 * Created by mhashim6 on 05/09/2018.
 */

object RandomStrings {
    private val random = Random()

    fun randomTitle(resources: Resources): String = resources.getString(randomTitle())

    @StringRes
    fun randomTitle(): Int = randomString(R.string.title_1, R.string.title_2, R.string.title_3)

    fun randomComment(resources: Resources): String = resources.getString(randomComment())

    @StringRes
    fun randomComment(): Int = randomString(R.string.comment_1, R.string.comment_2, R.string.comment_3, R.string.comment_4)

    @StringRes
    private fun randomString(vararg strings: Int): Int = strings.random()

    private fun IntArray.random(): Int {
        return this[random.nextInt(this.size)]
    }

}