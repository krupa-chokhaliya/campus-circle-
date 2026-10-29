package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.ui.CampusViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: CampusViewModel,
    onBack: () -> Unit,
    onReopenOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    var notificationsEnabled by remember { mutableStateOf(true) }
    var commuteAlertsEnabled by remember { mutableStateOf(true) }
    var showFirebaseDiagnostics by remember { mutableStateOf(false) }
    var showGuidelinesDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    if (showGuidelinesDialog) {
        AlertDialog(
            onDismissRequest = { showGuidelinesDialog = false },
            title = {
                Text(
                    text = "GEC Bharuch Community Guidelines",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "1. Student Exclusivity: CampusCircle is exclusively for students, alumni, and faculty of Government Engineering College, Bharuch.\n\n" +
                               "2. Anonymity Ethics: When posting anonymously, your public identity is shielded. However, using anonymity for harassment, personal attacks, or doxxing is strictly prohibited and results in immediate account suspension.\n\n" +
                               "3. Accurate Commute Telemetry: Share honest, observed crowd and delay reports for trains and buses. Do not intentionally misinform fellow commuters.\n\n" +
                               "4. Academic Integrity: Discussions on PYQs and coursework are encouraged. Do not post or solicit unpermitted exam materials.\n\n" +
                               "5. Local Recommendations: Post real student rates and genuine experiences for Bharuch PGs, tiffins, and canteens.",
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showGuidelinesDialog = false }) {
                    Text("Understood")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "App Settings",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("settings_back_button")
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Theme Section
            Text(
                text = "Appearance & Interface",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = CampusSecondaryPink
                )
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, CampusSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = CampusPrimaryPink
                        )
                        Column {
                            Text(
                                text = "Dark Mode",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (isDarkTheme) "Deep Charcoal & Magenta Theme" else "Crisp Light Contrast Theme",
                                style = MaterialTheme.typography.bodySmall.copy(color = CampusTextSecondary)
                            )
                        }
                    }

                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { viewModel.setTheme(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = CampusPrimaryPink),
                        modifier = Modifier.testTag("theme_switch")
                    )
                }
            }

            // Notifications Section
            Text(
                text = "Notifications & Alerts",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = CampusSecondaryPink
                )
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, CampusSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Discussion Replies & Answers",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Get notified when classmates reply to your posts",
                                style = MaterialTheme.typography.bodySmall.copy(color = CampusTextSecondary)
                            )
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CampusPrimaryPink)
                        )
                    }

                    Divider(color = CampusCardBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Train Commute Alerts",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Morning Gujarat Queen / MEMU delay broadcasts",
                                style = MaterialTheme.typography.bodySmall.copy(color = CampusTextSecondary)
                            )
                        }
                        Switch(
                            checked = commuteAlertsEnabled,
                            onCheckedChange = { commuteAlertsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CampusCyanAccent)
                        )
                    }
                }
            }

            // Cloud Backend & Firebase Diagnostics Section
            Text(
                text = "Cloud Database & Sync Diagnostics",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = CampusSecondaryPink
                )
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, CampusPurpleAccent.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = CampusPurpleAccent
                        )
                        Column {
                            Text(
                                text = "Data Layer Status",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Persistent Local Repository + Cloud Firestore Schema",
                                style = MaterialTheme.typography.bodySmall.copy(color = CampusSuccess)
                            )
                        }
                    }

                    Text(
                        text = "• Local persistence active: Posts, comments, votes, communities, and commute updates are fully functional.\n" +
                               "• Firestore security rules generated: firestore.rules configured for zero-trust client access.\n" +
                               "• Anonymous identity separation enforced: private user identifiers separated from public display models.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CampusTextSecondary,
                            lineHeight = 18.sp
                        )
                    )

                    Button(
                        onClick = { showFirebaseDiagnostics = !showFirebaseDiagnostics },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = CampusPurpleAccent
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CampusPurpleAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (showFirebaseDiagnostics) "Hide Schema Details" else "View Cloud Schema Details")
                    }

                    if (showFirebaseDiagnostics) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Collections Configured:\n" +
                                        "• /users/{userId}\n" +
                                        "• /communities/{communityId}\n" +
                                        "• /posts/{postId}\n" +
                                        "• /posts/{postId}/comments/{commentId}\n" +
                                        "• /commuteReports/{reportId}\n" +
                                        "• /localListings/{listingId}\n" +
                                        "• /reports/{reportId} (Admin only)\n" +
                                        "• /identityMappings/{privateId} (Strict Backend Read-Only)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CampusSecondaryPink,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                ),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            // Legal & College Policies
            Text(
                text = "Community Policies",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = CampusSecondaryPink
                )
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, CampusSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TextButton(
                        onClick = { showGuidelinesDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Community Guidelines & Anti-Harassment", color = MaterialTheme.colorScheme.onSurface)
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CampusTextMuted)
                        }
                    }

                    Divider(color = CampusCardBorder)

                    TextButton(
                        onClick = onReopenOnboarding,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Reset Onboarding / Change Branch", color = MaterialTheme.colorScheme.onSurface)
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CampusTextMuted)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
