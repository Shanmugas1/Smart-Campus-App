package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CloudQueue
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Role
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuditLogScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CloudStorageVaultScreen
import com.example.ui.screens.CreateNoticeScreen
import com.example.ui.screens.NoticeDetailsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SavedScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.StudentHomeScreen
import com.example.ui.screens.StudentManagementScreen
import com.example.ui.theme.PriorityUrgent
import com.example.viewmodel.AppScreen
import com.example.viewmodel.SmartCampusViewModel

data class NavItem(
    val screen: AppScreen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasBadge: Boolean = false
)

@Composable
fun SmartCampusApp(viewModel: SmartCampusViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val selectedNoticeId by viewModel.selectedNoticeId.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()

    val isAdminOrFaculty = currentUser?.let {
        it.role == Role.ADMIN || it.role == Role.SUPER_ADMIN || it.role == Role.FACULTY
    } ?: false

    val studentNavItems = listOf(
        NavItem(AppScreen.STUDENT_HOME, "Feed", Icons.Filled.Home, Icons.Outlined.Home, hasBadge = unreadCount > 0),
        NavItem(AppScreen.SEARCH, "Search", Icons.Filled.Search, Icons.Outlined.Search),
        NavItem(AppScreen.SAVED, "Saved", Icons.Filled.Bookmark, Icons.Filled.BookmarkBorder),
        NavItem(AppScreen.CLOUD_STORAGE, "Vault", Icons.Filled.CloudQueue, Icons.Outlined.CloudQueue),
        NavItem(AppScreen.PROFILE, "Profile", Icons.Filled.Person, Icons.Outlined.Person)
    )

    val adminNavItems = listOf(
        NavItem(AppScreen.ADMIN_DASHBOARD, "Dashboard", Icons.Filled.Dashboard, Icons.Filled.Dashboard),
        NavItem(AppScreen.CREATE_NOTICE, "Post", Icons.Filled.AddCircle, Icons.Filled.AddCircle),
        NavItem(AppScreen.CLOUD_STORAGE, "Vault", Icons.Filled.CloudQueue, Icons.Outlined.CloudQueue),
        NavItem(AppScreen.STUDENT_MANAGEMENT, "Students", Icons.Filled.People, Icons.Filled.People),
        NavItem(AppScreen.AUDIT_LOGS, "Audit", Icons.Filled.History, Icons.Filled.History),
        NavItem(AppScreen.PROFILE, "Profile", Icons.Filled.Person, Icons.Outlined.Person)
    )

    val activeNavItems = if (isAdminOrFaculty) adminNavItems else studentNavItems
    val isBottomBarVisible = currentUser != null && currentScreen != AppScreen.NOTICE_DETAILS && currentScreen != AppScreen.AUTH

    // Back handling
    BackHandler(enabled = currentScreen != AppScreen.STUDENT_HOME && currentScreen != AppScreen.ADMIN_DASHBOARD && currentScreen != AppScreen.AUTH) {
        if (currentScreen == AppScreen.NOTICE_DETAILS || currentScreen == AppScreen.CLOUD_STORAGE) {
            viewModel.navigateTo(if (isAdminOrFaculty) AppScreen.ADMIN_DASHBOARD else AppScreen.STUDENT_HOME)
        } else {
            viewModel.navigateTo(if (isAdminOrFaculty) AppScreen.ADMIN_DASHBOARD else AppScreen.STUDENT_HOME)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isBottomBarVisible) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                ) {
                    activeNavItems.forEach { item ->
                        val isSelected = currentScreen == item.screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(item.screen) },
                            icon = {
                                if (item.hasBadge && unreadCount > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge(
                                                containerColor = PriorityUrgent,
                                                contentColor = Color.White
                                            ) {
                                                Text("$unreadCount", fontSize = 9.sp)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = item.label,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 11.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_item_${item.label.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    AppScreen.AUTH -> AuthScreen(viewModel = viewModel)
                    AppScreen.STUDENT_HOME -> StudentHomeScreen(viewModel = viewModel)
                    AppScreen.SEARCH -> SearchScreen(viewModel = viewModel)
                    AppScreen.SAVED -> SavedScreen(viewModel = viewModel)
                    AppScreen.CLOUD_STORAGE -> CloudStorageVaultScreen(
                        viewModel = viewModel,
                        onBack = {
                            viewModel.navigateTo(
                                if (isAdminOrFaculty) AppScreen.ADMIN_DASHBOARD else AppScreen.STUDENT_HOME
                            )
                        }
                    )
                    AppScreen.ADMIN_DASHBOARD -> AdminDashboardScreen(viewModel = viewModel)
                    AppScreen.ADMIN_NOTICES -> AdminDashboardScreen(viewModel = viewModel)
                    AppScreen.CREATE_NOTICE -> CreateNoticeScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }
                    )
                    AppScreen.STUDENT_MANAGEMENT -> StudentManagementScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }
                    )
                    AppScreen.AUDIT_LOGS -> AuditLogScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }
                    )
                    AppScreen.PROFILE -> ProfileScreen(viewModel = viewModel)
                    AppScreen.NOTICE_DETAILS -> {
                        selectedNoticeId?.let { id ->
                            NoticeDetailsScreen(
                                viewModel = viewModel,
                                noticeId = id,
                                onBack = {
                                    viewModel.navigateTo(
                                        if (isAdminOrFaculty) AppScreen.ADMIN_DASHBOARD else AppScreen.STUDENT_HOME
                                    )
                                }
                            )
                        } ?: StudentHomeScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
