package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Notice
import com.example.model.NoticePriority
import com.example.model.NoticeStatus
import com.example.service.AudienceEngine
import com.example.ui.components.AudienceChip
import com.example.ui.components.SmartCampusBrandLogo
import com.example.ui.components.PriorityBadge
import com.example.ui.components.ReadAnalyticsDialog
import com.example.ui.components.StatCard
import com.example.ui.components.getCategoryColor
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.BentoBluePrimary
import com.example.ui.theme.BentoLavenderContainer
import com.example.ui.theme.BentoLavenderOnContainer
import com.example.ui.theme.BentoNavyDark
import com.example.ui.theme.BentoPeachContainer
import com.example.ui.theme.BentoPeachOnContainer
import com.example.ui.theme.BentoSageContainer
import com.example.ui.theme.BentoSageOnContainer
import com.example.ui.theme.BentoSkyContainer
import com.example.ui.theme.BentoSkyOnContainer
import com.example.ui.theme.CategoryAcademic
import com.example.ui.theme.CategoryPlacement
import com.example.ui.theme.PriorityImportant
import com.example.ui.theme.PriorityUrgent
import com.example.viewmodel.AppScreen
import com.example.viewmodel.SmartCampusViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: SmartCampusViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val adminNotices by viewModel.adminAllNotices.collectAsStateWithLifecycle()
    val activeStudents by viewModel.activeStudentCount.collectAsStateWithLifecycle()
    val analyticsModalData by viewModel.analyticsModalData.collectAsStateWithLifecycle()

    val activeNoticesCount = adminNotices.count { it.status == NoticeStatus.ACTIVE }
    val urgentNoticesCount = adminNotices.count { it.priority == NoticePriority.URGENT && it.status == NoticeStatus.ACTIVE }
    val scheduledCount = adminNotices.count { it.status == NoticeStatus.SCHEDULED }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SmartCampusBrandLogo(
                            size = 36.dp,
                            showContainer = true,
                            modifier = Modifier.testTag("admin_header_logo")
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Admin Control Console",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${currentUser?.name ?: "Admin"} (${currentUser?.role?.displayName ?: ""})",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.CLOUD_STORAGE) },
                        modifier = Modifier.testTag("admin_topbar_cloud_vault")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudQueue,
                            contentDescription = "Cloud Storage Vault",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.AUDIT_LOGS) },
                        modifier = Modifier.testTag("admin_topbar_audit_logs")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Audit Logs",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("admin_topbar_logout")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = PriorityUrgent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.navigateTo(AppScreen.CREATE_NOTICE) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("admin_fab_create_notice")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Announcement")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Publish Notice", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // 1. KPI Statistics Grid
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "INSTITUTIONAL METRICS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "Active Notices",
                            value = "$activeNoticesCount",
                            subtitle = "Across 14 Depts",
                            icon = Icons.Default.Campaign,
                            accentColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Urgent Alerts",
                            value = "$urgentNoticesCount",
                            subtitle = "High Priority",
                            icon = Icons.Default.Warning,
                            accentColor = PriorityUrgent,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "Total Enrolled",
                            value = "~2,880",
                            subtitle = "Targetable Cohorts",
                            icon = Icons.Default.Groups,
                            accentColor = CategoryAcademic,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Avg Read Rate",
                            value = "88.4%",
                            subtitle = "Delivered Scope",
                            icon = Icons.Default.BarChart,
                            accentColor = CategoryPlacement,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 2. Admin Quick Action Navigation Hub
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "ADMINISTRATIVE WORKFLOWS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(
                                    text = "Quick Hub",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Row 1: Publish & Cloud Vault
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AdminWorkflowTile(
                                title = "Publish Notice",
                                subtitle = "Draft & broadcast",
                                icon = Icons.Default.Campaign,
                                iconBgColor = BentoSkyContainer,
                                iconTint = BentoBluePrimary,
                                onClick = { viewModel.navigateTo(AppScreen.CREATE_NOTICE) },
                                testTag = "admin_workflow_post",
                                modifier = Modifier.weight(1f)
                            )

                            AdminWorkflowTile(
                                title = "Cloud Vault",
                                subtitle = "Media & files",
                                icon = Icons.Default.CloudQueue,
                                iconBgColor = BentoLavenderContainer,
                                iconTint = BentoLavenderOnContainer,
                                onClick = { viewModel.navigateTo(AppScreen.CLOUD_STORAGE) },
                                testTag = "admin_workflow_vault",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Row 2: Students & Audit Logs
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AdminWorkflowTile(
                                title = "Student Roster",
                                subtitle = "Cohorts & status",
                                icon = Icons.Default.People,
                                iconBgColor = BentoSageContainer,
                                iconTint = BentoSageOnContainer,
                                onClick = { viewModel.navigateTo(AppScreen.STUDENT_MANAGEMENT) },
                                testTag = "admin_workflow_students",
                                modifier = Modifier.weight(1f)
                            )

                            AdminWorkflowTile(
                                title = "Audit Logs",
                                subtitle = "Activity timeline",
                                icon = Icons.Default.History,
                                iconBgColor = BentoPeachContainer,
                                iconTint = BentoPeachOnContainer,
                                onClick = { viewModel.navigateTo(AppScreen.AUDIT_LOGS) },
                                testTag = "admin_workflow_audit",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. Notice Management Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PUBLISHED ANNOUNCEMENTS (${adminNotices.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // 4. Admin Notice Items List
            items(adminNotices, key = { "admin_notice_" + it.id }) { notice ->
                AdminNoticeManagementCard(
                    notice = notice,
                    onViewAnalytics = { viewModel.openAnalyticsModal(notice) },
                    onTogglePin = { viewModel.toggleNoticePin(notice) },
                    onArchive = { viewModel.archiveNotice(notice.id) },
                    onDelete = { viewModel.deleteNotice(notice.id) },
                    onClick = { viewModel.navigateTo(AppScreen.NOTICE_DETAILS, notice.id) }
                )
            }
        }
    }

    // Analytics Modal
    analyticsModalData?.let { data ->
        ReadAnalyticsDialog(
            analyticsData = data,
            onDismiss = { viewModel.closeAnalyticsModal() }
        )
    }
}

@Composable
fun AdminNoticeManagementCard(
    notice: Notice,
    onViewAnalytics: () -> Unit,
    onTogglePin: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val catColor = getCategoryColor(notice.category)
    val catIcon = getCategoryIcon(notice.category)
    val targets = remember(notice.targetAudience) { AudienceEngine.parseTargets(notice.targetAudience) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Category, Priority, Target Scope & Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = catColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = catIcon, contentDescription = null, tint = catColor, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = notice.category.displayName, color = catColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    PriorityBadge(priority = notice.priority)

                    if (notice.pinned) {
                        Icon(imageVector = Icons.Default.PushPin, contentDescription = "Pinned", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                    }
                }

                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Actions", modifier = Modifier.size(18.dp))
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("View Read Analytics") },
                            leadingIcon = { Icon(Icons.Default.BarChart, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onViewAnalytics()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (notice.pinned) "Unpin Notice" else "Pin to Top") },
                            leadingIcon = { Icon(Icons.Default.PushPin, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onTogglePin()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Archive Notice") },
                            leadingIcon = { Icon(Icons.Default.Archive, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onArchive()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Notice", color = PriorityUrgent) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = PriorityUrgent) },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = notice.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Targets chips & Quick Analytics Trigger Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val primaryTarget = targets.firstOrNull() ?: "ALL"
                    AudienceChip(target = primaryTarget)
                    if (targets.size > 1) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "+${targets.size - 1}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable { onViewAnalytics() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Analytics",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminWorkflowTile(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = iconBgColor,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
