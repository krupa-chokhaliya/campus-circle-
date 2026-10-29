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
import com.example.data.model.CrowdLevel
import com.example.ui.CampusViewModel
import com.example.ui.theme.*

@Composable
fun CommuteScreen(
    viewModel: CampusViewModel,
    modifier: Modifier = Modifier
) {
    val reports by viewModel.filteredCommuteReports.collectAsState()
    val selectedOrigin by viewModel.selectedCommuteOrigin.collectAsState()
    var showAddReportDialog by remember { mutableStateOf(false) }

    val originFilters = listOf("All", "Vadodara", "Surat", "Ankleshwar")

    if (showAddReportDialog) {
        AddCommuteReportDialog(
            onDismiss = { showAddReportDialog = false },
            onSubmit = { origin, dest, train, time, delay, crowd, exp ->
                viewModel.addCommuteReport(origin, dest, train, time, delay, crowd, exp)
                showAddReportDialog = false
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("commute_hub_screen"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero / Notice Banner
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Disclaimer Banner
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CampusCyanAccent.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
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
                                .background(CampusCyanAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsTransit,
                                contentDescription = null,
                                tint = CampusCyanAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "GEC Bharuch Commuter Hub",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "Crowdsourced by daily up-down students. Not official IRCTC live telemetry.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CampusTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                // Origin City Filter Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    originFilters.forEach { origin ->
                        val isSelected = origin == selectedOrigin
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) CampusCyanAccent else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) CampusCyanAccent else CampusCardBorder
                            ),
                            modifier = Modifier
                                .clickable { viewModel.setCommuteOrigin(origin) }
                                .testTag("commute_filter_$origin"),
                            contentColor = if (isSelected) CampusBlack else MaterialTheme.colorScheme.onSurfaceVariant
                        ) {
                            Text(
                                text = origin,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Row with Post Commute Report CTA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Commute Updates (${reports.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )

                    Button(
                        onClick = { showAddReportDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CampusCyanAccent,
                            contentColor = CampusBlack
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("post_commute_update_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Report Delay",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Reports list
        if (reports.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No commute reports for this route yet today.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = CampusTextSecondary)
                    )
                }
            }
        } else {
            items(reports, key = { it.id }) { report ->
                val hoursOld = (System.currentTimeMillis() - report.reportedAt) / (1000 * 60 * 60)
                val isStale = hoursOld >= 2

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CampusSurfaceBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("commute_report_${report.id}")
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
                                    text = report.routeTitle,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = "${report.transportName} • Scheduled ${report.scheduledTime}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = CampusTextSecondary
                                    )
                                )
                            }

                            // Delay Tag
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (report.delayMinutes > 15) CampusError.copy(alpha = 0.2f)
                                else if (report.delayMinutes > 0) CampusWarning.copy(alpha = 0.2f)
                                else CampusSuccess.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (report.delayMinutes > 15) CampusError
                                    else if (report.delayMinutes > 0) CampusWarning
                                    else CampusSuccess
                                )
                            ) {
                                Text(
                                    text = if (report.delayMinutes == 0) "On Time" else "+${report.delayMinutes} mins delay",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (report.delayMinutes > 15) CampusError
                                        else if (report.delayMinutes > 0) CampusWarning
                                        else CampusSuccess
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Crowd Meter & Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Groups,
                                        contentDescription = null,
                                        tint = CampusPurpleAccent,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = report.crowdLevel.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CampusSuccess.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Confirmed by Commuter",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CampusSuccess,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = report.travelExperience,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        // Stale Warning or Freshness Indicator
                        if (isStale) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = CampusWarning,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "Reported > 2h ago. Railway delay conditions may have changed.",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CampusWarning,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        } else {
                            Text(
                                text = "Reported recently by @${report.reportedBy}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CampusTextMuted,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddCommuteReportDialog(
    onDismiss: () -> Unit,
    onSubmit: (origin: String, dest: String, train: String, time: String, delay: Int, crowd: CrowdLevel, exp: String) -> Unit
) {
    var origin by remember { mutableStateOf("Vadodara") }
    var destination by remember { mutableStateOf("Bharuch") }
    var transport by remember { mutableStateOf("") }
    var scheduledTime by remember { mutableStateOf("07:45 AM") }
    var delayText by remember { mutableStateOf("0") }
    var crowdLevel by remember { mutableStateOf(CrowdLevel.MODERATE) }
    var experience by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Report Commute / Delay",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = CampusCyanAccent
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
                    value = origin,
                    onValueChange = { origin = it },
                    label = { Text("Origin Station / City") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = transport,
                    onValueChange = { transport = it },
                    label = { Text("Train / Transport Name") },
                    placeholder = { Text("e.g. Gujarat Queen (19034) or MEMU") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("commute_train_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = scheduledTime,
                        onValueChange = { scheduledTime = it },
                        label = { Text("Time") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = delayText,
                        onValueChange = { delayText = it },
                        label = { Text("Delay (mins)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Text(
                    text = "Observed Crowd Level",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CrowdLevel.entries.forEach { level ->
                        val isSelected = level == crowdLevel
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) CampusCyanAccent else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { crowdLevel = level }
                        ) {
                            Text(
                                text = level.label.take(8),
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) CampusBlack else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = experience,
                    onValueChange = { experience = it },
                    label = { Text("Travel Experience / Advice") },
                    placeholder = { Text("Which coach has seats? Auto availability at station?") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (transport.isNotBlank()) {
                        onSubmit(
                            origin,
                            destination,
                            transport,
                            scheduledTime,
                            delayText.toIntOrNull() ?: 0,
                            crowdLevel,
                            experience
                        )
                    }
                },
                enabled = transport.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CampusCyanAccent, contentColor = CampusBlack)
            ) {
                Text("Submit Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
