package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Role
import com.example.model.User
import com.example.ui.components.SmartCampusBrandLogo
import com.example.ui.theme.BentoBackgroundLight
import com.example.ui.theme.BentoBluePrimary
import com.example.ui.theme.BentoBorderLight
import com.example.ui.theme.BentoLavenderContainer
import com.example.ui.theme.BentoLavenderOnContainer
import com.example.ui.theme.BentoNavyDark
import com.example.ui.theme.BentoNeutralCard
import com.example.ui.theme.BentoPeachContainer
import com.example.ui.theme.BentoPeachOnContainer
import com.example.ui.theme.BentoSageContainer
import com.example.ui.theme.BentoSageOnContainer
import com.example.ui.theme.BentoSkyContainer
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.PriorityUrgent
import com.example.viewmodel.AppScreen
import com.example.viewmodel.SmartCampusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: SmartCampusViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }

    val user = currentUser ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Institutional Profile",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoNavyDark
                        )
                        Text(
                            text = "Identity & Role Hub",
                            fontSize = 11.sp,
                            color = BentoTextSecondary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier.testTag("btn_logout_topbar")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = PriorityUrgent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BentoBackgroundLight)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(BentoBackgroundLight)
                .padding(padding)
                .testTag("profile_screen"),
            contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Bento Profile Avatar & Identity Hero Card
            item {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BentoBorderLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.size(68.dp),
                            shape = CircleShape,
                            color = if (user.role == Role.STUDENT) BentoSkyContainer else BentoPeachContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = user.name.take(2).uppercase(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (user.role == Role.STUDENT) BentoBluePrimary else BentoPeachOnContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = user.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoNavyDark
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            color = if (user.role == Role.STUDENT) BentoSkyContainer else BentoPeachContainer,
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text(
                                text = user.role.displayName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (user.role == Role.STUDENT) BentoBluePrimary else BentoPeachOnContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = BentoBorderLight)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Details Rows
                        ProfileDetailRow(label = "Institutional Email", value = user.email)
                        ProfileDetailRow(label = "Registration / Staff ID", value = user.registrationNumber)
                        ProfileDetailRow(label = "Department", value = user.department)
                        if (user.role == Role.STUDENT) {
                            ProfileDetailRow(label = "Academic Cohort", value = "${user.year} • ${user.section}")
                        }
                    }
                }
            }

            // 2. Cloud Storage Vault & Attachments Quick Access Tile
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { viewModel.navigateTo(AppScreen.CLOUD_STORAGE) }
                        .testTag("btn_open_cloud_vault"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoNavyDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = BentoSkyContainer,
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CloudQueue,
                                        contentDescription = null,
                                        tint = BentoNavyDark,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Cloud Storage Vault",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Explore PDFs, documents & cloud attachments",
                                    fontSize = 11.sp,
                                    color = BentoSkyContainer
                                )
                            }
                        }

                        Surface(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text(
                                text = "VIEW",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            // 3. Security & Authorization Authority Info Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BentoBorderLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = BentoBluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Security & Access Authorization",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoNavyDark
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        ProfileDetailRow(label = "Session Authority", value = "Backend Database Verified")
                        ProfileDetailRow(label = "Authorized Scope", value = user.role.displayName)
                        ProfileDetailRow(label = "Account Status", value = if (user.active) "Active / Authorized" else "Suspended")
                    }
                }
            }

            // 4. Log Out Bento Button Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { showLogoutDialog = true }
                        .testTag("btn_logout_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = PriorityUrgent.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, PriorityUrgent.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = PriorityUrgent.copy(alpha = 0.15f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Logout,
                                        contentDescription = null,
                                        tint = PriorityUrgent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Log Out of Session",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PriorityUrgent
                                )
                                Text(
                                    text = "End session & return to institutional sign-in",
                                    fontSize = 11.sp,
                                    color = BentoTextSecondary
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Sign Out",
                            tint = PriorityUrgent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 5. Official Smart Campus Crest Footer
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SmartCampusBrandLogo(size = 46.dp, showContainer = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "SMART CAMPUS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoNavyDark,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "CONNECT • LEARN • STAY INFORMED",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BentoTextSecondary,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Sign Out from Smart Campus?",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoNavyDark
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to end your current session? You will need to sign in with your email and password to access the portal again.",
                    fontSize = 13.sp,
                    color = BentoTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PriorityUrgent),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("btn_confirm_logout")
                ) {
                    Text("Log Out", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Cancel", fontSize = 13.sp, color = BentoNavyDark)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = BentoTextSecondary
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = BentoNavyDark
        )
    }
}
