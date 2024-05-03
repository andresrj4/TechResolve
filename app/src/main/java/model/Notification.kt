package model

import android.text.format.DateUtils
import java.util.Locale
import java.util.UUID

data class Notification(
    val id: String = UUID.randomUUID().toString(),
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    var isRead: Boolean = false
)