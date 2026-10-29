package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Post
import com.example.data.model.ReportReason
import com.example.data.model.ReportTargetType
import com.example.ui.CampusViewModel
import com.example.ui.FeedFilter
import com.example.ui.components.PostCard
import com.example.ui.components.ReportDialog
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: CampusViewModel,
    onPostClick: (String) -> Unit,
    onCommunityClick: (String) -> Unit,
    onQuickAnonymousPost: () -> Unit,
    onCommuteClick: () -> Unit,
    onLocalGuideClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val posts by viewModel.feedPosts.collectAsState()
    val currentFilter by viewModel.feedFilter.collectAsState()
    var reportingPost by remember { mutableStateOf<Post?>(null) }

    if (reportingPost != null) {
        ReportDialog(
            targetSummary = reportingPost!!.title,
            onDismiss = { reportingPost = null },
            onSubmitReport = { reason, details ->
                viewModel.submitReport(
                    targetType = ReportTargetType.POST,
                    targetId = reportingPost!!.id,
                    summary = reportingPost!!.title,
                    reason = reason,
                    details = details
                )
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_feed_list"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Quick Action Shortcuts Bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Quick Shortcuts Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Anonymous Post shortcut
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CampusSurfaceBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onQuickAnonymousPost() }
                            .testTag("quick_anonymous_action")
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🎭", fontSize = 18.sp)
                            Column {
                                Text(
                                    text = "Post Secret",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = "100% Anonymous",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CampusSecondaryPink,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    // Commute Hub shortcut
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CampusSurfaceBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onCommuteClick() }
                            .testTag("quick_commute_action")
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🚆", fontSize = 18.sp)
                            Column {
                                Text(
                                    text = "Trains & Delay",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = "Vadodara / Surat",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CampusCyanAccent,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    // Local Guide shortcut
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CampusSurfaceBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onLocalGuideClick() }
                            .testTag("quick_local_guide_action")
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "📍", fontSize = 18.sp)
                            Column {
                                Text(
                                    text = "Bharuch Guide",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = "Tiffin & PGs",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CampusPurpleAccent,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Feed Filter Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FeedFilter.entries.forEach { filter ->
                        val isSelected = filter == currentFilter
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) CampusPrimaryPink else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) CampusPrimaryPink else CampusCardBorder
                            ),
                            modifier = Modifier
                                .clickable { viewModel.setFeedFilter(filter) }
                                .testTag("feed_filter_${filter.name.lowercase()}"),
                            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ) {
                            Text(
                                text = filter.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }
        }

        // Feed Post Items or Empty State
        if (posts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "🌿", fontSize = 44.sp)
                        Text(
                            text = "No posts found",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Be the first student to start a discussion or switch to another feed filter.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CampusTextSecondary
                            )
                        )
                    }
                }
            }
        } else {
            items(posts, key = { it.id }) { post ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    PostCard(
                        post = post,
                        onPostClick = { onPostClick(post.id) },
                        onCommunityClick = onCommunityClick,
                        onVote = { direction -> viewModel.votePost(post.id, direction) },
                        onBookmarkToggle = { viewModel.toggleSavePost(post.id) },
                        onReportClick = { reportingPost = post },
                        onPollVote = { optionId -> viewModel.votePoll(post.id, optionId) }
                    )
                }
            }
        }
    }
}
