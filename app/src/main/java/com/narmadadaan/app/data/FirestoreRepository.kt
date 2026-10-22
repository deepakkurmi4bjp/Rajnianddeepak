package com.narmadadaan.app.data

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.narmadadaan.app.R
import com.narmadadaan.app.model.AnalyticsSummary
import com.narmadadaan.app.model.AuditLog
import com.narmadadaan.app.model.Donation
import com.narmadadaan.app.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class FirestoreRepository(val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            FirebaseApp.getInstance(),
            context.applicationContext.getString(R.string.firestore_database_id)
        ).apply {
            firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
        }
    )

    private val donationsCollection = db.collection("donations")
    private val auditLogsCollection = db.collection("audit_logs")
    private val usersCollection = db.collection("users")

    fun getDonationsFlow(): Flow<List<Donation>> = callbackFlow {
        val listener: ListenerRegistration = donationsCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("FirestoreRepository", "Listen failed", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.data?.let { Donation.fromMap(doc.id, it) }
                    }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    fun getAuditLogsFlow(): Flow<List<AuditLog>> = callbackFlow {
        val listener: ListenerRegistration = auditLogsCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("FirestoreRepository", "Audit listen failed", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.data?.let { AuditLog.fromMap(doc.id, it) }
                    }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun addDonation(
        name: String,
        mobile: String,
        amount: Double,
        purpose: String,
        paymentMethod: String,
        transactionId: String?,
        collectorId: String?,
        collectorName: String
    ): Result<Donation> = runCatching {
        val donationId = HashUtils.generateReceiptId()
        val hash = HashUtils.generateDonationHash(donationId, amount)
        val donation = Donation(
            donationId = donationId,
            name = name,
            mobile = mobile,
            amount = amount,
            purpose = purpose,
            paymentMethod = paymentMethod,
            transactionId = transactionId,
            collectorId = collectorId,
            collectorName = collectorName,
            hash = hash,
            timestamp = System.currentTimeMillis()
        )

        val docRef = donationsCollection.document(donationId)
        docRef.set(donation.toMap()).await()

        // Create audit log entry
        addAuditLog(
            userId = collectorId,
            userName = collectorName,
            action = "CREATE_DONATION",
            details = "रसीद संख्या: $donationId, राशि: ₹$amount (${if (paymentMethod == "upi") "UPI" else "नकद"}), दानदाता: $name"
        )

        donation.copy(id = docRef.id)
    }

    suspend fun addAuditLog(
        userId: String?,
        userName: String,
        action: String,
        details: String
    ) {
        runCatching {
            val log = AuditLog(
                userId = userId,
                userName = userName,
                action = action,
                details = details,
                timestamp = System.currentTimeMillis()
            )
            auditLogsCollection.document().set(log.toMap()).await()
        }
    }

    suspend fun verifyDonation(query: String): VerificationResult {
        val cleanQuery = query.trim()
        if (cleanQuery.isEmpty()) {
            return VerificationResult.NotFound
        }

        return try {
            // Check direct document query by donationId
            val doc = donationsCollection.document(cleanQuery).get().await()
            val donation = if (doc.exists() && doc.data != null) {
                Donation.fromMap(doc.id, doc.data!!)
            } else {
                // Check where donationId == query
                val querySnapshot = donationsCollection
                    .whereEqualTo("donationId", cleanQuery)
                    .limit(1)
                    .get()
                    .await()
                if (!querySnapshot.isEmpty) {
                    val d = querySnapshot.documents[0]
                    Donation.fromMap(d.id, d.data!!)
                } else {
                    null
                }
            }

            if (donation == null) {
                VerificationResult.NotFound
            } else {
                val isValid = HashUtils.verifyDonationHash(donation.donationId, donation.amount, donation.hash)
                if (isValid) {
                    VerificationResult.Valid(donation)
                } else {
                    VerificationResult.Tampered(donation)
                }
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error verifying donation", e)
            VerificationResult.NotFound
        }
    }

    suspend fun seedInitialDataIfEmpty() {
        try {
            val countSnapshot = donationsCollection.limit(1).get().await()
            if (countSnapshot.isEmpty) {
                Log.d("FirestoreRepository", "Database empty. Seeding initial records...")
                val seeds = listOf(
                    Triple("दीपक पटेल", "7440771076", 11000.0) to ("श्री मां नर्मदा जन्मोत्सव चुनरी यात्रा" to "upi"),
                    Triple("रमेश शर्मा", "9876543210", 5100.0) to ("श्री राम जन्मोत्सव शोभायात्रा" to "cash"),
                    Triple("सुरेश वर्मा", "9425478323", 2100.0) to ("श्री हनुमान जन्मोत्सव शोभायात्रा" to "upi"),
                    Triple("राकेश गुप्ता", "9826123456", 1100.0) to ("धर्म रक्षा निधि संग्रहण" to "cash"),
                    Triple("अनिता सोनी", "9123456780", 501.0) to ("श्री मां नर्मदा महा आरती सेवा" to "upi"),
                    Triple("मोहन लाल त्रिवेदी", "9893012345", 2500.0) to ("भंडारा प्रसाद सेवा" to "cash")
                )

                val cal = Calendar.getInstance()
                seeds.forEachIndexed { index, pair ->
                    val (info, details) = pair
                    val (donorName, mobile, amount) = info
                    val (purpose, method) = details
                    val receiptId = "ND-2026-000${index + 1}"
                    val hash = HashUtils.generateDonationHash(receiptId, amount)

                    cal.timeInMillis = System.currentTimeMillis() - (index * 3600000L * 4) // Spread over today/yesterday

                    val donation = Donation(
                        donationId = receiptId,
                        name = donorName,
                        mobile = mobile,
                        amount = amount,
                        purpose = purpose,
                        paymentMethod = method,
                        transactionId = if (method == "upi") "UPI${(100000000000L..999999999999L).random()}" else null,
                        collectorId = "collector_001",
                        collectorName = "दीपक पटेल",
                        hash = hash,
                        timestamp = cal.timeInMillis
                    )
                    donationsCollection.document(receiptId).set(donation.toMap()).await()
                }

                addAuditLog(
                    userId = "system",
                    userName = "सिस्टम व्यवस्थापक",
                    action = "INITIAL_SEED",
                    details = "प्रारंभिक दान रिकॉर्ड और ऑडिट डेटा सफलतापूर्वक शुरू किया गया"
                )
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Failed to seed initial data", e)
        }
    }

    fun calculateSummary(donations: List<Donation>): AnalyticsSummary {
        val calNow = Calendar.getInstance()
        val todayYear = calNow.get(Calendar.YEAR)
        val todayDay = calNow.get(Calendar.DAY_OF_YEAR)

        var totalAmount = 0.0
        var todayAmount = 0.0
        var todayDonors = 0
        var cashAmount = 0.0
        var cashCount = 0
        var upiAmount = 0.0
        var upiCount = 0

        val calItem = Calendar.getInstance()

        donations.forEach { d ->
            totalAmount += d.amount
            if (d.paymentMethod.equals("upi", ignoreCase = true)) {
                upiAmount += d.amount
                upiCount++
            } else {
                cashAmount += d.amount
                cashCount++
            }

            calItem.timeInMillis = d.timestamp
            if (calItem.get(Calendar.YEAR) == todayYear && calItem.get(Calendar.DAY_OF_YEAR) == todayDay) {
                todayAmount += d.amount
                todayDonors++
            }
        }

        return AnalyticsSummary(
            totalAmount = totalAmount,
            totalDonors = donations.size,
            todayAmount = todayAmount,
            todayDonors = todayDonors,
            cashAmount = cashAmount,
            cashCount = cashCount,
            upiAmount = upiAmount,
            upiCount = upiCount
        )
    }
}

sealed class VerificationResult {
    data class Valid(val donation: Donation) : VerificationResult()
    data class Tampered(val donation: Donation) : VerificationResult()
    object NotFound : VerificationResult()
}
