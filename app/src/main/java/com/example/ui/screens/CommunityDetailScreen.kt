package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Shield
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
import com.example.data.model.ReportTargetType
import com.example.ui.CampusViewModel
import com.example.ui.components.PostCard
import com.example.ui.components.ReportDialog
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityDetailScreen(
    communityId: String,
    viewModel: CampusViewModel,
    onBack: () -> Unit,
    onPostClick: (String) -> Unit,
    onCreatePostInCommunity: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val communities by viewModel.communities.collectAsState()
    val allPosts by viewModel.allPosts.collectAsState()

    val community = communities.find { it.id == communityId }
    val communityPosts = allPosts.filter { it.communityId == communityId }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = community?.name ?: "Community",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("community_detail_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (community != null) {
                        Button(
                            onClick = { viewModel.toggleJoinCommunity(community.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (community.isJoined) MaterialTheme.colorScheme.surfaceVariant else CampusPrimaryPink,
                                contentColor = if (community.isJoined) CampusTextSecondary else Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(34.dp)
                                .padding(end = 8.dp)
                        ) {
                            Text(if (community.isJoined) "Joined" else "Join", fontSize = 12.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            if (community != null) {
                FloatingActionButton(
                    onClick = { onCreatePostInCommunity(community.id) },
                    containerColor = CampusPrimaryPink,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_post_in_community")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Post in Circle")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (community == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Community not found.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("community_detail_feed"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Banner
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
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(CampusBrandGradient)
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = community.iconEmoji, fontSize = 26.sp)
                                    }
                                }

                                Column {
                                    Text(
                                        text = community.name,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = "c/${community.slug} • ${community.memberCount} members",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CampusPurpleAccent
                                        )
                                    )
                                }
                            }

                            Text(
                                text = community.description,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )

                            // Rules Section
                            if (community.rules.isNotEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CampusCardBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Shield,
                                                contentDescription = null,
                                                tint = CampusSecondaryPink,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "Community Rules",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = CampusSecondaryPink
                                                )
                                            )
                                        }

                                        community.rules.forEachIndexed { idx, rule ->
                                            Text(
                                                text = "${idx + 1}. $rule",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = CampusTextSecondary,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            // Moderators
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Moderators: ",
                                    style = MaterialTheme.typography.labelSmall.copy(color = CampusTextMuted)
                                )
                                Text(
                                    text = community.moderators.joinToString(", ") { "u/$it" },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CampusSecondaryPink,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }

                // Community Feed
                if (communityPosts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No posts in this circle yet. Start the conversation!",
                                style = MaterialTheme.typography.bodyMedium.copy(color = CampusTextSecondary)
                            )
                        }
                    }
                } else {
                    items(communityPosts, key = { it.id }) { post ->
                        PostCard(
                            post = post,
                            onPostClick = { onPostClick(post.id) },
                            onCommunityClick = {},
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
}
