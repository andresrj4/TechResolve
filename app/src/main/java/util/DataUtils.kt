package util

import android.text.format.DateUtils

object DateUtils {
    fun getTimeAgo(timestamp: Long, now: Long): CharSequence {
        return DateUtils.getRelativeTimeSpanString(
            timestamp, now, DateUtils.MINUTE_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE
        )
    }
}