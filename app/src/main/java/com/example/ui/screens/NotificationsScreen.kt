package com.example.ui.screens

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationType
import com.example.ui.CampusViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: CampusViewModel,
    onBack: () -> Unit,
    onPostClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notifications",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("notifications_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.markNotificationsRead() }) {
                        Text("Mark all read", color = CampusSecondaryPink, fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No notifications right now.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = CampusTextSecondary)
                )
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("notifications_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications, key = { it.id }) { notif ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (notif.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (!notif.isRead) CampusPrimaryPink.copy(alpha = 0.5f) else CampusCardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (notif.targetPostId != null) {
                                    onPostClick(notif.targetPostId)
                                }
                            }
                            .testTag("notification_item_${notif.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (notif.type) {
                                            NotificationType.UPVOTE -> CampusSuccess.copy(alpha = 0.2f)
                                            NotificationType.REPLY -> CampusPrimaryPink.copy(alpha = 0.2f)
                                            NotificationType.ANNOUNCEMENT -> CampusWarning.copy(alpha = 0.2f)
                                            NotificationType.VERIFICATION -> CampusPurpleAccent.copy(alpha = 0.2f)
                                            NotificationType.COMMUNITY -> CampusCyanAccent.copy(alpha = 0.2f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (notif.type) {
                                        NotificationType.UPVOTE -> Icons.Default.CheckCircle
                                        NotificationType.REPLY -> Icons.Default.ChatBubble
                                        NotificationType.ANNOUNCEMENT -> Icons.Default.Campaign
                                        NotificationType.VERIFICATION -> Icons.Default.Verified
                                        NotificationType.COMMUNITY -> Icons.Default.Groups
                                    },
                                    contentDescription = null,
                                    tint = when (notif.type) {
                                        NotificationType.UPVOTE -> CampusSuccess
                                        NotificationType.REPLY -> CampusPrimaryPink
                                        NotificationType.ANNOUNCEMENT -> CampusWarning
                                        NotificationType.VERIFICATION -> CampusPurpleAccent
                                        NotificationType.COMMUNITY -> CampusCyanAccent
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = notif.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = notif.message,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }

                            if (!notif.isRead) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(CampusPrimaryPink)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
