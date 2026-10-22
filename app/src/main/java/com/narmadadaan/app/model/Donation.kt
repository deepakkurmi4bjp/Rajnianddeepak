package com.narmadadaan.app.model

import com.google.firebase.Timestamp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Donation(
    val id: String = "",
    val donationId: String = "",
    val name: String = "",
    val mobile: String = "",
    val amount: Double = 0.0,
    val purpose: String = "श्री मां नर्मदा जन्मोत्सव चुनरी यात्रा",
    val paymentMethod: String = "cash", // "cash" or "upi"
    val transactionId: String? = null,
    val collectorId: String? = null,
    val collectorName: String = "दीपक पटेल",
    val hash: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    val formattedAmount: String
        get() {
            val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
            format.maximumFractionDigits = 0
            return format.format(amount)
        }

    val maskedMobile: String
        get() = if (mobile.length >= 4) {
            "******" + mobile.takeLast(4)
        } else {
            "******"
        }

    val maskedTransactionId: String?
        get() = transactionId?.let {
            if (it.length > 4) "••••" + it.takeLast(4) else it
        }

    val formattedDate: String
        get() {
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale("hi", "IN"))
            return sdf.format(Date(timestamp))
        }

    fun toMap(): Map<String, Any?> = mapOf(
        "donationId" to donationId,
        "name" to name,
        "mobile" to mobile,
        "amount" to amount,
        "purpose" to purpose,
        "paymentMethod" to paymentMethod,
        "transactionId" to transactionId,
        "collectorId" to collectorId,
        "collectorName" to collectorName,
        "hash" to hash,
        "createdAt" to Timestamp(Date(timestamp))
    )

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): Donation {
            val ts = when (val c = map["createdAt"]) {
                is Timestamp -> c.toDate().time
                is Number -> c.toLong()
                else -> System.currentTimeMillis()
            }
            return Donation(
                id = id,
                donationId = (map["donationId"] as? String).orEmpty(),
                name = (map["name"] as? String).orEmpty(),
                mobile = (map["mobile"] as? String).orEmpty(),
                amount = (map["amount"] as? Number)?.toDouble() ?: 0.0,
                purpose = (map["purpose"] as? String).orEmpty().ifEmpty { "धर्म रक्षा निधि संग्रहण" },
                paymentMethod = (map["paymentMethod"] as? String) ?: "cash",
                transactionId = map["transactionId"] as? String,
                collectorId = map["collectorId"] as? String,
                collectorName = (map["collectorName"] as? String) ?: "दीपक पटेल",
                hash = (map["hash"] as? String).orEmpty(),
                timestamp = ts
            )
        }
    }
}
