package com.example.data.model

enum class PostType {
    GENERAL,
    QUESTION,
    POLL,
    COMMUTE_REPORT,
    LOCAL_RECOMMENDATION,
    ANNOUNCEMENT
}

data class PollOption(
    val id: String,
    val text: String,
    val voteCount: Int = 0
)

data class Post(
    val id: String,
    val communityId: String,
    val communityName: String,
    val title: String,
    val content: String,
    val authorId: String,
    val authorName: String,
    val isAnonymous: Boolean = false,
    val anonymousAlias: String = "Anonymous Student",
    val tags: List<String> = emptyList(),
    val upvotes: Int = 0,
    val downvotes: Int = 0,
    val userVote: Int = 0, // -1: downvoted, 0: none, 1: upvoted
    val commentCount: Int = 0,
    val isSaved: Boolean = false,
    val isPinned: Boolean = false,
    val postType: PostType = PostType.GENERAL,
    val pollOptions: List<PollOption> = emptyList(),
    val userSelectedPollOption: String? = null,
    val acceptedCommentId: String? = null,
    val imageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    val netScore: Int get() = upvotes - downvotes
}
