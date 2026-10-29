package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Post
import com.example.data.model.PostType
import com.example.ui.theme.*

@Composable
fun PostCard(
    post: Post,
    onPostClick: () -> Unit,
    onCommunityClick: (String) -> Unit,
    onVote: (direction: Int) -> Unit,
    onBookmarkToggle: () -> Unit,
    onReportClick: () -> Unit,
    onPollVote: ((optionId: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("post_card_${post.id}")
            .border(
                width = 1.dp,
                color = if (post.isPinned) CampusPrimaryPink.copy(alpha = 0.5f) else CampusSurfaceBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onPostClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Community, Author / Anonymous badge, Timestamp & More
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Community badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CampusPurpleAccent.copy(alpha = 0.15f),
                        modifier = Modifier.clickable { onCommunityClick(post.communityId) }
                    ) {
                        Text(
                            text = post.communityName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = CampusSecondaryPink
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "•",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )

                    // Author or Anonymous
                    if (post.isAnonymous) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = "Anonymous",
                                tint = CampusTextMuted,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Anonymous Student",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = CampusTextMuted
                                )
                            )
                        }
                    } else {
                        Text(
                            text = "u/${post.authorName}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (post.isPinned) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = CampusPrimaryPink.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "PINNED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CampusPrimaryPink
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Options Menu
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("post_options_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Post Options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Report Content") },
                            onClick = {
                                showMenu = false
                                onReportClick()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Flag, contentDescription = null, tint = CampusError)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (post.isSaved) "Remove Bookmark" else "Save Post") },
                            onClick = {
                                showMenu = false
                                onBookmarkToggle()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (post.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = null,
                                    tint = CampusSecondaryPink
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Share Post") },
                            onClick = {
                                showMenu = false
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "${post.title}\n\nShared via CampusCircle GEC Bharuch")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Post via"))
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Share, contentDescription = null)
                            }
                        )
                    }
                }
            }

            // Post Title
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Content snippet
            if (post.content.isNotBlank()) {
                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Poll Options Display if Poll Type
            if (post.postType == PostType.POLL && post.pollOptions.isNotEmpty()) {
                val totalPollVotes = post.pollOptions.sumOf { it.voteCount }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    post.pollOptions.forEach { opt ->
                        val isSelected = opt.id == post.userSelectedPollOption
                        val percentage = if (totalPollVotes > 0) (opt.voteCount * 100) / totalPollVotes else 0

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) CampusPrimaryPink.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) CampusPrimaryPink else CampusCardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPollVote?.invoke(opt.id) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { onPollVote?.invoke(opt.id) },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = CampusPrimaryPink
                                        )
                                    )
                                    Text(
                                        text = opt.text,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                                Text(
                                    text = "$percentage% (${opt.voteCount})",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CampusTextSecondary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Question Answered Badge
            if (post.postType == PostType.QUESTION && post.acceptedCommentId != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CampusSuccess.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CampusSuccess.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Accepted Answer",
                            tint = CampusSuccess,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Answer Accepted by Author",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CampusSuccess,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            // Tags row
            if (post.tags.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    post.tags.take(3).forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                        ) {
                            Text(
                                text = "#$tag",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CampusPurpleAccent,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Divider(color = CampusCardBorder, thickness = 0.8.dp)

            // Bottom Actions: Upvote / Downvote, Comments, Bookmark, Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Voting Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CampusCardBorder)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        IconButton(
                            onClick = { onVote(1) },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("upvote_button_${post.id}")
                        ) {
                            Icon(
                                imageVector = if (post.userVote == 1) Icons.Filled.ArrowUpward else Icons.Outlined.ArrowUpward,
                                contentDescription = "Upvote",
                                tint = if (post.userVote == 1) CampusPrimaryPink else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = post.netScore.toString(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (post.userVote == 1) CampusPrimaryPink
                                else if (post.userVote == -1) CampusPurpleAccent
                                else MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        IconButton(
                            onClick = { onVote(-1) },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("downvote_button_${post.id}")
                        ) {
                            Icon(
                                imageVector = if (post.userVote == -1) Icons.Filled.ArrowDownward else Icons.Outlined.ArrowDownward,
                                contentDescription = "Downvote",
                                tint = if (post.userVote == -1) CampusPurpleAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Comments Counter Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CampusCardBorder),
                    modifier = Modifier.clickable { onPostClick() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Comments",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${post.commentCount}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }

                // Bookmark & Share Buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("bookmark_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (post.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (post.isSaved) CampusSecondaryPink else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "${post.title}\n\nShared via CampusCircle GEC Bharuch")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Post"))
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("share_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
