package com.narmadadaan.app.data

import java.util.Locale
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HashUtils {
    private const val SECRET_KEY = "donation-platform-secret-key-2024"

    fun generateDonationHash(donationId: String, amount: Double): String {
        val amountStr = String.format(Locale.US, "%.2f", amount)
        val data = """{"id":"$donationId","amount":"$amountStr"}"""
        val mac = Mac.getInstance("HmacSHA256")
        val secretKeySpec = SecretKeySpec(SECRET_KEY.toByteArray(Charsets.UTF_8), "HmacSHA256")
        mac.init(secretKeySpec)
        val bytes = mac.doFinal(data.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyDonationHash(donationId: String, amount: Double, hash: String): Boolean {
        val expected = generateDonationHash(donationId, amount)
        return expected.equals(hash, ignoreCase = true)
    }

    fun generateReceiptId(): String {
        val randomNum = (1000..9999).random()
        val year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        return "ND-$year-$randomNum"
    }

    fun numberToHindiWords(amount: Long): String {
        if (amount == 0L) return "शून्य रुपये"
        val units = arrayOf(
            "", "एक", "दो", "तीन", "चार", "पाँच", "छह", "सात", "आठ", "नौ", "दस",
            "ग्यारह", "बारह", "तेरह", "चौदह", "पंद्रह", "सोलह", "सत्रह", "अठारह", "उन्नीस"
        )
        val tens = arrayOf(
            "", "दस", "बीस", "तीस", "चालीस", "पचास", "साठ", "सत्तर", "अस्सी", "नब्बे"
        )

        fun convertUnderHundred(n: Int): String {
            return when {
                n < 20 -> units[n]
                n % 10 == 0 -> tens[n / 10]
                else -> "${tens[n / 10]} ${units[n % 10]}"
            }
        }

        var num = amount
        var result = ""

        if (num >= 10000000) {
            val crore = (num / 10000000).toInt()
            result += "${convertUnderHundred(crore)} करोड़ "
            num %= 10000000
        }
        if (num >= 100000) {
            val lakh = (num / 100000).toInt()
            result += "${convertUnderHundred(lakh)} लाख "
            num %= 100000
        }
        if (num >= 1000) {
            val thousand = (num / 1000).toInt()
            result += "${convertUnderHundred(thousand)} हज़ार "
            num %= 1000
        }
        if (num >= 100) {
            val hundred = (num / 100).toInt()
            result += "${units[hundred]} सौ "
            num %= 100
        }
        if (num > 0) {
            result += convertUnderHundred(num.toInt())
        }

        return "${result.trim()} रुपये मात्र"
    }
}
