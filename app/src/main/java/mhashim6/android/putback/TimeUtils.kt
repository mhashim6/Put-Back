package mhashim6.android.putback

import mhashim6.android.putback.data.Notion


fun intervalString(interval: Int, unit: Int): String {
    return "$interval ${unitStringSelector(interval, unit)}"
}

fun unitStringSelector(interval: Int, unit: Int): String {
    val suffix = if (interval > 1) "S" else ""

    return when (unit) {
        Notion.YEAR -> "YEAR$suffix"
        Notion.MONTH -> "MONTH$suffix"
        Notion.WEEK -> "WEEK$suffix"
        else -> "DAY$suffix"
    }
}