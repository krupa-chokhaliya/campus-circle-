package com.example.data.model

enum class VerificationStatus {
    UNVERIFIED,
    PENDING,
    VERIFIED
}

enum class UserRole {
    STUDENT,
    MODERATOR,
    ADMIN
}

data class User(
    val id: String,
    val username: String,
    val displayName: String,
    val email: String,
    val college: String = "Government Engineering College, Bharuch",
    val department: String = "Computer Engineering",
    val semester: Int = 5,
    val commuteCity: String = "Vadodara",
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
    val role: UserRole = UserRole.MODERATOR,
    val karma: Int = 342,
    val bio: String = "Passionate developer & daily Vadodara-Bharuch commuter at GEC Bharuch.",
    val joinedCommunities: List<String> = listOf("general", "commute", "food", "study"),
    val avatarSeed: String = "krups",
    val createdAt: Long = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 120
)
