package com.example.data.model

enum class NotificationType {
    REPLY,
    UPVOTE,
    ANNOUNCEMENT,
    VERIFICATION,
    COMMUNITY
}

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val targetPostId: String? = null
)
