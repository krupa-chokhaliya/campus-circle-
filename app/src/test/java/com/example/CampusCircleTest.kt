package com.example

import com.example.data.model.*
import com.example.data.repository.CampusRepository
import org.junit.Assert.*
import org.junit.Test

class CampusCircleTest {

    @Test
    fun testAnonymousPostingSeparation() {
        val post = CampusRepository.createPost(
            communityId = "confessions",
            title = "Anonymous Test Post",
            content = "Testing private author ID masking",
            isAnonymous = true,
            postType = PostType.GENERAL,
            tags = listOf("Test")
        )

        assertTrue("Post must have isAnonymous set to true", post.isAnonymous)
        assertEquals("Author name displayed publicly must be 'Anonymous Student'", "Anonymous Student", post.authorName)
        assertNotEquals("Author ID must not expose the real user id", CampusRepository.currentUser.value.id, post.authorId)
    }

    @Test
    fun testVotingSingleVoteAndToggle() {
        val testPost = CampusRepository.createPost(
            communityId = "general",
            title = "Voting Test Post",
            content = "Testing vote calculations",
            isAnonymous = false,
            postType = PostType.GENERAL,
            tags = emptyList()
        )

        // Initial state created by user has 1 upvote
        val initialPost = CampusRepository.posts.value.first { it.id == testPost.id }
        assertEquals(1, initialPost.upvotes)
        assertEquals(1, initialPost.userVote)

        // Clicking upvote again should toggle vote back to 0
        CampusRepository.votePost(testPost.id, 1)
        val toggledPost = CampusRepository.posts.value.first { it.id == testPost.id }
        assertEquals(0, toggledPost.userVote)
        assertEquals(0, toggledPost.upvotes)

        // Voting downvote
        CampusRepository.votePost(testPost.id, -1)
        val downvotedPost = CampusRepository.posts.value.first { it.id == testPost.id }
        assertEquals(-1, downvotedPost.userVote)
        assertEquals(1, downvotedPost.downvotes)
        assertEquals(-1, downvotedPost.netScore)
    }

    @Test
    fun testBookmarkToggle() {
        val post = CampusRepository.posts.value.first()
        val initialSaved = post.isSaved

        CampusRepository.toggleSavePost(post.id)
        val updatedPost = CampusRepository.posts.value.first { it.id == post.id }
        assertEquals(!initialSaved, updatedPost.isSaved)
    }

    @Test
    fun testCommentCreationAndAcceptedAnswer() {
        val post = CampusRepository.createPost(
            communityId = "study",
            title = "Academic Help Question",
            content = "Question about GTU paper",
            isAnonymous = false,
            postType = PostType.QUESTION,
            tags = listOf("GTU")
        )

        val comment = CampusRepository.addComment(
            postId = post.id,
            parentId = null,
            content = "Here is the verified solution step by step.",
            isAnonymous = false
        )

        val comments = CampusRepository.comments.value[post.id]
        assertNotNull(comments)
        assertTrue(comments!!.any { it.id == comment.id })

        // Mark accepted answer
        CampusRepository.markAcceptedAnswer(post.id, comment.id)
        val postAfterAccept = CampusRepository.posts.value.first { it.id == post.id }
        val commentAfterAccept = CampusRepository.comments.value[post.id]!!.first { it.id == comment.id }

        assertEquals(comment.id, postAfterAccept.acceptedCommentId)
        assertTrue(commentAfterAccept.isAcceptedAnswer)
    }

    @Test
    fun testCommuteReportCreation() {
        val report = CampusRepository.addCommuteReport(
            origin = "Vadodara",
            destination = "Bharuch",
            transportName = "Gujarat Queen",
            scheduledTime = "07:45 AM",
            delayMinutes = 8,
            crowdLevel = CrowdLevel.MODERATE,
            travelExperience = "Seats available in coach C2"
        )

        val reports = CampusRepository.commuteReports.value
        assertTrue("Newly created commute report must be in state", reports.any { it.id == report.id })
    }

    @Test
    fun testModerationReportWorkflow() {
        val initialCount = CampusRepository.reports.value.size
        CampusRepository.submitReport(
            targetType = ReportTargetType.POST,
            targetId = "test_post_flag",
            summary = "Spam advertisement",
            reason = ReportReason.SPAM,
            details = "Commercial ad without permission"
        )

        val updatedReports = CampusRepository.reports.value
        assertEquals(initialCount + 1, updatedReports.size)

        val newReport = updatedReports.first()
        CampusRepository.resolveReport(newReport.id, ModerationStatus.RESOLVED_REMOVED)
        val resolved = CampusRepository.reports.value.first { it.id == newReport.id }
        assertEquals(ModerationStatus.RESOLVED_REMOVED, resolved.status)
    }
}
