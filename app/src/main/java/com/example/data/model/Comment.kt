package com.example.data.model

data class Comment(
    val id: String,
    val postId: String,
    val parentId: String? = null, // for threaded replies
    val authorId: String,
    val authorName: String,
    val isAnonymous: Boolean = false,
    val content: String,
    val upvotes: Int = 0,
    val downvotes: Int = 0,
    val userVote: Int = 0,
    val isAcceptedAnswer: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val netScore: Int get() = upvotes - downvotes
}
