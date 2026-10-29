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
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import com.example.ui.CampusViewModel
import com.example.ui.components.PostCard
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: CampusViewModel,
    onPostClick: (String) -> Unit,
    onCommunityClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onAdminClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allPosts by viewModel.allPosts.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0: My Posts, 1: Saved Posts
    var showEditProfileDialog by remember { mutableStateOf(false) }

    val myPosts = allPosts.filter { it.authorId == currentUser.id }
    val savedPosts = allPosts.filter { it.isSaved }

    if (showEditProfileDialog) {
        EditProfileDialog(
            user = currentUser,
            onDismiss = { showEditProfileDialog = false },
            onSave = { displayName, dept, sem, city, bio ->
                viewModel.updateUserProfile(displayName, dept, sem, city, bio)
                showEditProfileDialog = false
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Card
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, CampusSurfaceBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(CampusBrandGradient)
                                    .border(2.dp, CampusSecondaryPink, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentUser.displayName.take(1),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = currentUser.displayName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    if (currentUser.verificationStatus == VerificationStatus.VERIFIED) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Verified GEC Student",
                                            tint = CampusSuccess,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "u/${currentUser.username}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = CampusSecondaryPink)
                                )
                            }
                        }

                        // Settings Icon
                        IconButton(
                            onClick = onSettingsClick,
                            modifier = Modifier.testTag("profile_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // College affiliation & Branch Details
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "🏛️ ${currentUser.college}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "🎓 ${currentUser.department} • Sem ${currentUser.semester} • Daily commuter from ${currentUser.commuteCity}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CampusTextSecondary
                            )
                        )
                        if (currentUser.bio.isNotBlank()) {
                            Text(
                                text = currentUser.bio,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    // Karma & Role Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CampusCardBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${currentUser.karma}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CampusPrimaryPink
                                    )
                                )
                                Text(
                                    text = "Campus Karma",
                                    style = MaterialTheme.typography.labelSmall.copy(color = CampusTextMuted)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CampusCardBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = currentUser.role.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CampusPurpleAccent
                                    )
                                )
                                Text(
                                    text = "Privilege Level",
                                    style = MaterialTheme.typography.labelSmall.copy(color = CampusTextMuted)
                                )
                            }
                        }
                    }

                    // Action Buttons Row: Edit Profile & Admin Panel
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showEditProfileDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = CampusSecondaryPink
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CampusSurfaceBorder),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("edit_profile_button")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Profile", fontWeight = FontWeight.SemiBold)
                        }

                        if (currentUser.role == UserRole.ADMIN || currentUser.role == UserRole.MODERATOR) {
                            Button(
                                onClick = onAdminClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CampusPurpleAccent.copy(alpha = 0.2f),
                                    contentColor = CampusPurpleAccent
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CampusPurpleAccent),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("admin_panel_button")
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Moderation", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // Sub-tabs: My Posts / Saved Posts
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (activeTab == 0) CampusPrimaryPink else MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (activeTab == 0) CampusPrimaryPink else CampusCardBorder
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeTab = 0 }
                        .testTag("profile_tab_my_posts"),
                    contentColor = if (activeTab == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "My Posts (${myPosts.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (activeTab == 1) CampusPrimaryPink else MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (activeTab == 1) CampusPrimaryPink else CampusCardBorder
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeTab = 1 }
                        .testTag("profile_tab_saved_posts"),
                    contentColor = if (activeTab == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Saved Posts (${savedPosts.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Tab Content
        val displayedList = if (activeTab == 0) myPosts else savedPosts
        if (displayedList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (activeTab == 0) "You haven't posted yet." else "No bookmarked posts.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = CampusTextSecondary)
                    )
                }
            }
        } else {
            items(displayedList, key = { it.id }) { post ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    PostCard(
                        post = post,
                        onPostClick = { onPostClick(post.id) },
                        onCommunityClick = onCommunityClick,
                        onVote = { dir -> viewModel.votePost(post.id, dir) },
                        onBookmarkToggle = { viewModel.toggleSavePost(post.id) },
                        onReportClick = {}
                    )
                }
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (displayName: String, dept: String, sem: Int, city: String, bio: String) -> Unit
) {
    var displayName by remember { mutableStateOf(user.displayName) }
    var department by remember { mutableStateOf(user.department) }
    var semesterText by remember { mutableStateOf(user.semester.toString()) }
    var commuteCity by remember { mutableStateOf(user.commuteCity) }
    var bio by remember { mutableStateOf(user.bio) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Profile",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = CampusPrimaryPink
                )
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Branch / Department") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = semesterText,
                    onValueChange = { semesterText = it },
                    label = { Text("Current Semester (1-8)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = commuteCity,
                    onValueChange = { commuteCity = it },
                    label = { Text("Daily Commute City") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        displayName,
                        department,
                        semesterText.toIntOrNull() ?: user.semester,
                        commuteCity,
                        bio
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = CampusPrimaryPink)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
