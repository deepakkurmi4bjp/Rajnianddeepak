package com.narmadadaan.app.model

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AuditLog(
    val id: String = "",
    val userId: String? = null,
    val userName: String = "सिस्टम",
    val action: String = "",
    val details: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    val formattedDate: String
        get() {
            val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale("hi", "IN"))
            return sdf.format(Date(timestamp))
        }

    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "userName" to userName,
        "action" to action,
        "details" to details,
        "createdAt" to Timestamp(Date(timestamp))
    )

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): AuditLog {
            val ts = when (val c = map["createdAt"]) {
                is Timestamp -> c.toDate().time
                is Number -> c.toLong()
                else -> System.currentTimeMillis()
            }
            return AuditLog(
                id = id,
                userId = map["userId"] as? String,
                userName = (map["userName"] as? String) ?: "सिस्टम",
                action = (map["action"] as? String).orEmpty(),
                details = (map["details"] as? String).orEmpty(),
                timestamp = ts
            )
        }
    }
}

data class AnalyticsSummary(
    val totalAmount: Double = 0.0,
    val totalDonors: Int = 0,
    val todayAmount: Double = 0.0,
    val todayDonors: Int = 0,
    val cashAmount: Double = 0.0,
    val cashCount: Int = 0,
    val upiAmount: Double = 0.0,
    val upiCount: Int = 0
)
