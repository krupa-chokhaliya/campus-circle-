package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.CampusBrandGradient
import com.example.ui.theme.CampusPrimaryPink
import com.example.ui.theme.CampusPurpleAccent
import com.example.ui.theme.CampusSecondaryPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusTopBar(
    title: String = "CampusCircle",
    unreadCount: Int = 0,
    userRole: UserRole = UserRole.STUDENT,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onAdminClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Glowing star emblem
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CampusBrandGradient)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✦",
                            color = CampusPrimaryPink,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }
                    Text(
                        text = "GEC Bharuch Exclusive",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CampusSecondaryPink,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        },
        actions = {
            if (userRole == UserRole.ADMIN || userRole == UserRole.MODERATOR) {
                IconButton(
                    onClick = onAdminClick,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("top_bar_admin_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin and Moderation Dashboard",
                        tint = CampusPurpleAccent
                    )
                }
            }

            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("top_bar_search_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search CampusCircle",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("top_bar_notifications_button")
            ) {
                BadgedBox(
                    badge = {
                        if (unreadCount > 0) {
                            Badge(
                                containerColor = CampusPrimaryPink,
                                contentColor = Color.White
                            ) {
                                Text(text = if (unreadCount > 9) "9+" else unreadCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}
