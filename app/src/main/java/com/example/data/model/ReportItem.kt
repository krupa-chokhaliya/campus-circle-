package com.example.data.model

enum class ReportTargetType {
    POST,
    COMMENT,
    USER,
    COMMUNITY,
    LISTING
}

enum class ReportReason(val label: String) {
    HARASSMENT("Harassment or bullying"),
    PERSONAL_INFO("Exposing personal information (doxxing)"),
    SPAM("Spam or promotional abuse"),
    MISLEADING("Misleading / fake information"),
    INAPPROPRIATE("Inappropriate or abusive language"),
    OTHER("Other community guideline breach")
}

enum class ModerationStatus {
    PENDING,
    UNDER_REVIEW,
    RESOLVED_REMOVED,
    DISMISSED
}

data class ReportItem(
    val id: String,
    val targetType: ReportTargetType,
    val targetId: String,
    val targetSummary: String,
    val reason: ReportReason,
    val details: String,
    val reportedBy: String,
    val reportedAt: Long = System.currentTimeMillis(),
    val status: ModerationStatus = ModerationStatus.PENDING
)
