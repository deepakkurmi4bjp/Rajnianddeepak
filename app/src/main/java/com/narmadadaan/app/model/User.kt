package com.narmadadaan.app.model

import com.google.firebase.Timestamp
import java.util.Date

data class User(
    val uid: String = "",
    val name: String = "",
    val mobile: String = "",
    val role: String = "collector", // "super_admin", "admin", "collector"
    val email: String? = null,
    val isActive: Boolean = true
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "name" to name,
        "mobile" to mobile,
        "role" to role,
        "email" to email,
        "isActive" to isActive,
        "createdAt" to Timestamp.now()
    )

    companion object {
        fun fromMap(uid: String, map: Map<String, Any?>): User {
            return User(
                uid = uid,
                name = (map["name"] as? String).orEmpty(),
                mobile = (map["mobile"] as? String).orEmpty(),
                role = (map["role"] as? String) ?: "collector",
                email = map["email"] as? String,
                isActive = (map["isActive"] as? Boolean) ?: true
            )
        }
    }
}
