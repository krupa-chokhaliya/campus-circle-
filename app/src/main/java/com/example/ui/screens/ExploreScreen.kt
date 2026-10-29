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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
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
import com.example.data.model.Community
import com.example.data.model.CommunityCategory
import com.example.data.repository.CampusRepository
import com.example.ui.CampusViewModel
import com.example.ui.theme.*

@Composable
fun ExploreScreen(
    viewModel: CampusViewModel,
    onCommunityClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val communities by viewModel.communities.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var selectedCategory by remember { mutableStateOf<CommunityCategory?>(null) }
    var showCreateCommunityDialog by remember { mutableStateOf(false) }

    val trendingTags = listOf(
        "GujaratQueen", "GTUSem5", "DAA_Notes", "ZadeshwarPG", "AdaniDrive", "CanteenChai"
    )

    val filteredCommunities = communities.filter { comm ->
        val matchesQuery = searchQuery.isBlank() ||
                comm.name.contains(searchQuery, ignoreCase = true) ||
                comm.description.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == null || comm.category == selectedCategory
        matchesQuery && matchesCategory
    }

    if (showCreateCommunityDialog) {
        CreateCommunityDialog(
            onDismiss = { showCreateCommunityDialog = false },
            onCreate = { name, slug, desc, cat, rules ->
                CampusRepository.createCommunity(name, slug, desc, cat, rules)
                showCreateCommunityDialog = false
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("explore_screen"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search Input
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search communities, discussions, trains...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = CampusPurpleAccent)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("explore_search_input"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CampusPrimaryPink,
                        unfocusedBorderColor = CampusCardBorder
                    ),
                    singleLine = true
                )

                // Trending tags
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Trending on Campus",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CampusSecondaryPink
                        )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        trendingTags.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, CampusCardBorder),
                                modifier = Modifier.clickable {
                                    viewModel.setSearchQuery(tag)
                                }
                            ) {
                                Text(
                                    text = "#$tag",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Category Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (selectedCategory == null) CampusPrimaryPink else MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (selectedCategory == null) CampusPrimaryPink else CampusCardBorder
                        ),
                        modifier = Modifier.clickable { selectedCategory = null },
                        contentColor = if (selectedCategory == null) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    ) {
                        Text(
                            text = "All Topics",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    CommunityCategory.entries.forEach { cat ->
                        val isSelected = cat == selectedCategory
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) CampusPrimaryPink else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) CampusPrimaryPink else CampusCardBorder
                            ),
                            modifier = Modifier.clickable { selectedCategory = cat },
                            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ) {
                            Text(
                                text = cat.displayName,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Header with "Create Community" button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Communities (${filteredCommunities.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )

                    TextButton(
                        onClick = { showCreateCommunityDialog = true },
                        modifier = Modifier.testTag("create_community_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = CampusPrimaryPink,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "New Circle",
                            color = CampusPrimaryPink,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Communities List
        items(filteredCommunities, key = { it.id }) { community ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onCommunityClick(community.id) }
                    .testTag("community_item_${community.id}"),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, CampusSurfaceBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Emoji / Icon circle
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, CampusCardBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = community.iconEmoji, fontSize = 22.sp)
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = community.name,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            if (community.isOfficial) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = CampusPrimaryPink.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "OFFICIAL",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CampusPrimaryPink,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = community.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CampusTextSecondary
                            ),
                            maxLines = 2
                        )

                        Text(
                            text = "${community.memberCount} members • c/${community.slug}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CampusPurpleAccent,
                                fontSize = 11.sp
                            )
                        )
                    }

                    // Join / Leave Button
                    Button(
                        onClick = { viewModel.toggleJoinCommunity(community.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (community.isJoined) MaterialTheme.colorScheme.surface else CampusPrimaryPink,
                            contentColor = if (community.isJoined) CampusTextSecondary else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("join_community_${community.id}")
                    ) {
                        Text(
                            text = if (community.isJoined) "Joined" else "Join",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CreateCommunityDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, slug: String, desc: String, category: CommunityCategory, rules: List<String>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var slug by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(CommunityCategory.GENERAL) }
    var rulesText by remember { mutableStateOf("1. Be respectful\n2. Relevant to GEC Bharuch\n3. No spam") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Create New Circle",
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
                    value = name,
                    onValueChange = {
                        name = it
                        if (slug.isEmpty()) slug = it.lowercase().replace(" ", "-")
                    },
                    label = { Text("Community Name") },
                    placeholder = { Text("e.g. Chemical Dept Discussions") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("community_name_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = slug,
                    onValueChange = { slug = it },
                    label = { Text("Unique Slug") },
                    placeholder = { Text("e.g. chem-gec") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Short Description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 2
                )

                OutlinedTextField(
                    value = rulesText,
                    onValueChange = { rulesText = it },
                    label = { Text("Community Rules (line by line)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && slug.isNotBlank()) {
                        onCreate(
                            name,
                            slug,
                            desc,
                            selectedCategory,
                            rulesText.lines()
                        )
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CampusPrimaryPink)
            ) {
                Text("Create Circle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
