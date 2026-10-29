package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Comment
import com.example.data.model.PostType
import com.example.data.model.ReportTargetType
import com.example.ui.CampusViewModel
import com.example.ui.components.ReportDialog
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String,
    viewModel: CampusViewModel,
    onBack: () -> Unit,
    onCommunityClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allPosts by viewModel.allPosts.collectAsState()
    val commentsMap by viewModel.commentsMap.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val post = allPosts.find { it.id == postId }
    val comments = commentsMap[postId] ?: emptyList()

    var commentInput by remember { mutableStateOf("") }
    var replyingToComment by remember { mutableStateOf<Comment?>(null) }
    var isAnonymousComment by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }

    if (showReportDialog && post != null) {
        ReportDialog(
            targetSummary = post.title,
            onDismiss = { showReportDialog = false },
            onSubmitReport = { reason, details ->
                viewModel.submitReport(
                    targetType = ReportTargetType.POST,
                    targetId = post.id,
                    summary = post.title,
                    reason = reason,
                    details = details
                )
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = post?.communityName ?: "Discussion",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("post_detail_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (post != null) {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "${post.title}\n\nShared via CampusCircle GEC Bharuch")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Post"))
                        }
                    }) {
                        Icon(Icons.Outlined.Share, contentDescription = "Share")
                    }
                    IconButton(onClick = { showReportDialog = true }) {
                        Icon(Icons.Default.Flag, contentDescription = "Report", tint = CampusError)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            // Comment input bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .navigationBarsPadding(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, CampusSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (replyingToComment != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Replying to ${if (replyingToComment!!.isAnonymous) "Anonymous Student" else replyingToComment!!.authorName}",
                                style = MaterialTheme.typography.bodySmall.copy(color = CampusSecondaryPink)
                            )
                            IconButton(
                                onClick = { replyingToComment = null },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Cancel reply", modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Anonymous toggle button
                        IconButton(
                            onClick = { isAnonymousComment = !isAnonymousComment },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isAnonymousComment) CampusPrimaryPink.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, if (isAnonymousComment) CampusPrimaryPink else CampusCardBorder, CircleShape)
                                .testTag("comment_anonymous_toggle")
                        ) {
                            Icon(
                                imageVector = if (isAnonymousComment) Icons.Default.VisibilityOff else Icons.Default.Person,
                                contentDescription = "Toggle Anonymous",
                                tint = if (isAnonymousComment) CampusPrimaryPink else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        OutlinedTextField(
                            value = commentInput,
                            onValueChange = { commentInput = it },
                            placeholder = {
                                Text(if (isAnonymousComment) "Comment anonymously..." else "Add to discussion...")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("comment_input_field"),
                            shape = RoundedCornerShape(20.dp),
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CampusPrimaryPink,
                                unfocusedBorderColor = CampusCardBorder
                            )
                        )

                        IconButton(
                            onClick = {
                                if (commentInput.isNotBlank() && post != null) {
                                    viewModel.addComment(
                                        postId = post.id,
                                        parentId = replyingToComment?.id,
                                        content = commentInput,
                                        isAnonymous = isAnonymousComment
                                    )
                                    commentInput = ""
                                    replyingToComment = null
                                }
                            },
                            enabled = commentInput.isNotBlank(),
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (commentInput.isNotBlank()) CampusPrimaryPink else MaterialTheme.colorScheme.surfaceVariant)
                                .testTag("send_comment_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send Comment",
                                tint = if (commentInput.isNotBlank()) Color.White else CampusTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (post == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Post not found.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("post_detail_content"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Post Header & Body Card
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CampusSurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = CampusPurpleAccent.copy(alpha = 0.2f),
                                        modifier = Modifier.clickable { onCommunityClick(post.communityId) }
                                    ) {
                                        Text(
                                            text = post.communityName,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = CampusSecondaryPink,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Text(text = "•", color = CampusTextMuted)

                                    if (post.isAnonymous) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.VisibilityOff,
                                                contentDescription = null,
                                                tint = CampusTextMuted,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = "Anonymous Student",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = CampusTextMuted,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "u/${post.authorName}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }

                            Text(
                                text = post.title,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            Text(
                                text = post.content,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 24.sp
                                )
                            )

                            // Action Pills Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CampusCardBorder)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        IconButton(onClick = { viewModel.votePost(post.id, 1) }) {
                                            Icon(
                                                imageVector = if (post.userVote == 1) Icons.Filled.ArrowUpward else Icons.Outlined.ArrowUpward,
                                                contentDescription = "Upvote",
                                                tint = if (post.userVote == 1) CampusPrimaryPink else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = post.netScore.toString(),
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                        )
                                        IconButton(onClick = { viewModel.votePost(post.id, -1) }) {
                                            Icon(
                                                imageVector = if (post.userVote == -1) Icons.Filled.ArrowDownward else Icons.Outlined.ArrowDownward,
                                                contentDescription = "Downvote",
                                                tint = if (post.userVote == -1) CampusPurpleAccent else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                IconButton(onClick = { viewModel.toggleSavePost(post.id) }) {
                                    Icon(
                                        imageVector = if (post.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                        contentDescription = "Bookmark",
                                        tint = if (post.isSaved) CampusSecondaryPink else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Comments Header
                item {
                    Text(
                        text = "Discussion & Answers (${comments.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                // Comments List
                if (comments.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier.padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No replies yet. Be the first to share your thoughts!",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = CampusTextSecondary)
                                )
                            }
                        }
                    }
                } else {
                    items(comments, key = { it.id }) { comment ->
                        val isChild = comment.parentId != null

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (comment.isAcceptedAnswer) Color(0xFF142721) else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (comment.isAcceptedAnswer) CampusSuccess else CampusCardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = if (isChild) 24.dp else 0.dp)
                                .testTag("comment_item_${comment.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = if (comment.isAnonymous) "Anonymous Student" else "u/${comment.authorName}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (comment.isAnonymous) CampusTextMuted else CampusSecondaryPink
                                            )
                                        )

                                        if (comment.isAcceptedAnswer) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = CampusSuccess.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = "ACCEPTED ANSWER",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = CampusSuccess,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    // Author or Admin can accept answer for academic questions
                                    if (post.postType == PostType.QUESTION && !comment.isAcceptedAnswer) {
                                        TextButton(
                                            onClick = { viewModel.markAcceptedAnswer(post.id, comment.id) },
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Accept",
                                                tint = CampusSuccess,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "Accept",
                                                color = CampusSuccess,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = comment.content,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { viewModel.voteComment(post.id, comment.id, 1) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.ArrowUpward,
                                                contentDescription = "Upvote",
                                                tint = if (comment.userVote == 1) CampusPrimaryPink else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Text(
                                            text = comment.netScore.toString(),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        IconButton(
                                            onClick = { viewModel.voteComment(post.id, comment.id, -1) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.ArrowDownward,
                                                contentDescription = "Downvote",
                                                tint = if (comment.userVote == -1) CampusPurpleAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    TextButton(
                                        onClick = { replyingToComment = comment },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Reply", fontSize = 11.sp, color = CampusSecondaryPink)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
