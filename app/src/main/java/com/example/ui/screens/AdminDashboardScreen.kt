package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ModerationStatus
import com.example.data.model.PostType
import com.example.ui.CampusViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: CampusViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pendingVerifications by viewModel.pendingVerifications.collectAsState()
    val reports by viewModel.reports.collectAsState()

    var activeAdminTab by remember { mutableIntStateOf(0) } // 0: Verifications, 1: Content Reports, 2: Announcement
    var announcementTitle by remember { mutableStateOf("") }
    var announcementBody by remember { mutableStateOf("") }
    var announcementSuccess by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Moderation & Admin Hub",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("admin_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("admin_dashboard_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Admin Tabs Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "Verify Students (${pendingVerifications.size})",
                        "Reports (${reports.size})",
                        "Broadcast Notice"
                    ).forEachIndexed { idx, label ->
                        val isSelected = activeAdminTab == idx
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) CampusPurpleAccent else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) CampusPurpleAccent else CampusCardBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { activeAdminTab = idx },
                            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Tab 0: Student Verification Queue
            if (activeAdminTab == 0) {
                if (pendingVerifications.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier.padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "All student verifications are caught up! ✨",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = CampusSuccess)
                                )
                            }
                        }
                    }
                } else {
                    items(pendingVerifications, key = { it.id }) { pendingUser ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CampusSurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = pendingUser.displayName,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        Text(
                                            text = pendingUser.email,
                                            style = MaterialTheme.typography.bodySmall.copy(color = CampusSecondaryPink)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = CampusWarning.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "PENDING ID REVIEW",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = CampusWarning,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "Department: ${pendingUser.department} • Semester ${pendingUser.semester}\nCommutes from: ${pendingUser.commuteCity}",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = CampusTextSecondary)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = { viewModel.reviewVerification(pendingUser.id, approve = false) }
                                    ) {
                                        Text("Reject", color = CampusError)
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        onClick = { viewModel.reviewVerification(pendingUser.id, approve = true) },
                                        colors = ButtonDefaults.buttonColors(containerColor = CampusSuccess)
                                    ) {
                                        Text("Approve Student")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tab 1: Moderation Reports Queue
            if (activeAdminTab == 1) {
                if (reports.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier.padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No active moderation reports! Campus feed is clean.",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = CampusSuccess)
                                )
                            }
                        }
                    }
                } else {
                    items(reports, key = { it.id }) { report ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CampusError.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = CampusError.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = report.reason.label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = CampusError,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Text(
                                        text = "Status: ${report.status.name}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = CampusTextMuted)
                                    )
                                }

                                Text(
                                    text = "Target Content: \"${report.targetSummary}\"",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )

                                if (report.details.isNotBlank()) {
                                    Text(
                                        text = "Report Context: ${report.details}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = CampusTextSecondary)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = { viewModel.resolveReport(report.id, ModerationStatus.DISMISSED) }
                                    ) {
                                        Text("Dismiss Flag")
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        onClick = { viewModel.resolveReport(report.id, ModerationStatus.RESOLVED_REMOVED) },
                                        colors = ButtonDefaults.buttonColors(containerColor = CampusError)
                                    ) {
                                        Text("Remove Content")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tab 2: Broadcast Notice Composer
            if (activeAdminTab == 2) {
                item {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CampusSurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Broadcast Official Notice to Campus",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CampusPurpleAccent
                                )
                            )

                            if (announcementSuccess) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CampusSuccess.copy(alpha = 0.2f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Announcement posted successfully to General Campus feed!",
                                        color = CampusSuccess,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = announcementTitle,
                                onValueChange = { announcementTitle = it },
                                label = { Text("Announcement Title") },
                                placeholder = { Text("e.g. GTU Winter 2026 Exam Schedule Released") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_announcement_title"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = announcementBody,
                                onValueChange = { announcementBody = it },
                                label = { Text("Announcement Details") },
                                placeholder = { Text("Enter official notice text for GEC Bharuch students...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_announcement_body"),
                                shape = RoundedCornerShape(12.dp),
                                minLines = 4
                            )

                            Button(
                                onClick = {
                                    if (announcementTitle.isNotBlank()) {
                                        viewModel.createPost(
                                            communityId = "general",
                                            title = "[OFFICIAL] $announcementTitle",
                                            content = announcementBody,
                                            isAnonymous = false,
                                            postType = PostType.ANNOUNCEMENT,
                                            tags = listOf("OfficialNotice", "GECBharuch", "AdminBroadcast")
                                        )
                                        announcementTitle = ""
                                        announcementBody = ""
                                        announcementSuccess = true
                                    }
                                },
                                enabled = announcementTitle.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = CampusPurpleAccent),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("publish_announcement_button")
                            ) {
                                Text("Publish Official Announcement", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
