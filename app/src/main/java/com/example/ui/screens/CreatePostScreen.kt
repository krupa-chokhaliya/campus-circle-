package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.data.model.PostType
import com.example.data.model.User
import com.example.ui.CampusViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    viewModel: CampusViewModel,
    currentUser: User,
    preselectedCommunityId: String? = null,
    startAsAnonymous: Boolean = false,
    onPostCreated: (String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val communities by viewModel.communities.collectAsState()

    var selectedCommunityId by remember {
        mutableStateOf(preselectedCommunityId ?: communities.firstOrNull()?.id ?: "general")
    }
    var postType by remember { mutableStateOf(PostType.GENERAL) }
    var isAnonymous by remember { mutableStateOf(startAsAnonymous) }

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tagInput by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf(listOf<String>()) }

    var pollOption1 by remember { mutableStateOf("") }
    var pollOption2 by remember { mutableStateOf("") }
    var pollOption3 by remember { mutableStateOf("") }

    var communityDropdownExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Post",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier.testTag("cancel_create_post_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                errorMessage = "Please enter a post title."
                                return@Button
                            }
                            if (postType == PostType.POLL && (pollOption1.isBlank() || pollOption2.isBlank())) {
                                errorMessage = "Poll requires at least 2 options."
                                return@Button
                            }

                            isSubmitting = true
                            val pollOptionsList = if (postType == PostType.POLL) {
                                listOf(pollOption1, pollOption2, pollOption3).filter { it.isNotBlank() }
                            } else emptyList()

                            val newPost = viewModel.createPost(
                                communityId = selectedCommunityId,
                                title = title,
                                content = content,
                                isAnonymous = isAnonymous,
                                postType = postType,
                                tags = tags,
                                pollOptions = pollOptionsList
                            )
                            isSubmitting = false
                            onPostCreated(newPost.id)
                        },
                        enabled = title.isNotBlank() && !isSubmitting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CampusPrimaryPink
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("publish_post_button")
                    ) {
                        Text(
                            text = "Publish",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error banner if any
            if (errorMessage != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CampusError.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CampusError),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage!!,
                        color = CampusError,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // CRITICAL IDENTITY SELECTOR
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isAnonymous) Color(0xFF23101E) else MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isAnonymous) CampusPrimaryPink else CampusCardBorder
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Publishing Identity",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isAnonymous) CampusSecondaryPink else MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Public Profile Button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (!isAnonymous) CampusPrimaryPink.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (!isAnonymous) CampusPrimaryPink else CampusCardBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isAnonymous = false }
                                .testTag("identity_public_option")
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (!isAnonymous) CampusPrimaryPink else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = "@${currentUser.username}",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = "Verified Student",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CampusSuccess,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }

                        // Anonymous Button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isAnonymous) CampusPrimaryPink.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isAnonymous) CampusPrimaryPink else CampusCardBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isAnonymous = true }
                                .testTag("identity_anonymous_option")
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = if (isAnonymous) CampusPrimaryPink else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = "Anonymous",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = "Alias: Anonymous Student",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CampusSecondaryPink,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    if (isAnonymous) {
                        Text(
                            text = "🛡️ Your identity and username are stripped from public view. Other students and regular community moderators will only see 'Anonymous Student'.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CampusSecondaryPink,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Community Selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Select Community",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                )

                val selectedCommObj = communities.find { it.id == selectedCommunityId }
                ExposedDropdownMenuBox(
                    expanded = communityDropdownExpanded,
                    onExpandedChange = { communityDropdownExpanded = !communityDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = "${selectedCommObj?.iconEmoji ?: "🏛️"} ${selectedCommObj?.name ?: "Select Circle"}",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = communityDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("select_community_dropdown"),
                        shape = RoundedCornerShape(14.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = communityDropdownExpanded,
                        onDismissRequest = { communityDropdownExpanded = false }
                    ) {
                        communities.forEach { comm ->
                            DropdownMenuItem(
                                text = { Text("${comm.iconEmoji} ${comm.name}") },
                                onClick = {
                                    selectedCommunityId = comm.id
                                    communityDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Post Type Selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Post Format",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        PostType.GENERAL to "Discussion",
                        PostType.QUESTION to "Academic Question",
                        PostType.POLL to "Campus Poll",
                        PostType.COMMUTE_REPORT to "Commute Report",
                        PostType.LOCAL_RECOMMENDATION to "Local Place"
                    ).forEach { (type, label) ->
                        val isSelected = type == postType
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) CampusPrimaryPink else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) CampusPrimaryPink else CampusCardBorder
                            ),
                            modifier = Modifier.clickable { postType = type }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }

            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    errorMessage = null
                },
                label = { Text("Title") },
                placeholder = { Text("What would you like to discuss or ask?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("post_title_input"),
                shape = RoundedCornerShape(14.dp),
                maxLines = 3
            )

            // Content Body Input
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content / Details (Optional)") },
                placeholder = { Text("Share context, train timings, course PYQ details, or questions...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("post_content_input"),
                shape = RoundedCornerShape(14.dp),
                minLines = 5,
                maxLines = 10
            )

            // Poll Inputs if Poll selected
            if (postType == PostType.POLL) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CampusCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Poll Options",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        OutlinedTextField(
                            value = pollOption1,
                            onValueChange = { pollOption1 = it },
                            placeholder = { Text("Option 1 (e.g. 19033 Gujarat Queen)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("poll_option_1"),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = pollOption2,
                            onValueChange = { pollOption2 = it },
                            placeholder = { Text("Option 2 (e.g. Surat-Vadodara MEMU)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("poll_option_2"),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = pollOption3,
                            onValueChange = { pollOption3 = it },
                            placeholder = { Text("Option 3 (Optional)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("poll_option_3"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Tags input
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = tagInput,
                        onValueChange = { tagInput = it },
                        placeholder = { Text("Add tag (e.g. Sem5, Vadodara, GTU)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            val clean = tagInput.trim().replace("#", "")
                            if (clean.isNotBlank() && !tags.contains(clean)) {
                                tags = tags + clean
                                tagInput = ""
                            }
                        },
                        enabled = tagInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = CampusPurpleAccent)
                    ) {
                        Text("Add")
                    }
                }

                if (tags.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        tags.forEach { tag ->
                            InputChip(
                                selected = true,
                                onClick = { tags = tags.filter { it != tag } },
                                label = { Text("#$tag") },
                                trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Remove") }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
