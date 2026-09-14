package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Role
import com.example.ui.components.SmartCampusEmblemHero
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
import com.example.viewmodel.SmartCampusViewModel

enum class AuthTab {
    LOGIN,
    REGISTER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: SmartCampusViewModel,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(AuthTab.LOGIN) }
    val allDepartments by viewModel.allDepartments.collectAsStateWithLifecycle()

    // Login Form State
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var loginErrorMessage by remember { mutableStateOf<String?>(null) }
    var isAuthenticating by remember { mutableStateOf(false) }

    // Register Form State
    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var regDepartment by remember { mutableStateOf("CSE") }
    var regYear by remember { mutableStateOf("2nd Year") }
    var regSection by remember { mutableStateOf("Section A") }
    var regIdNumber by remember { mutableStateOf("") }
    var isRegPasswordVisible by remember { mutableStateOf(false) }
    var registerErrorMessage by remember { mutableStateOf<String?>(null) }

    // Dropdown expanded states
    var deptExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }
    var sectionExpanded by remember { mutableStateOf(false) }

    val departmentsList = remember(allDepartments) {
        if (allDepartments.isNotEmpty()) allDepartments.map { it.code }
        else listOf("CSE", "ECE", "EEE", "AI & DS", "CSBS", "Information Technology", "Mechanical", "Civil", "Biomedical")
    }

    val yearsList = listOf("1st Year", "2nd Year", "3rd Year", "4th Year")
    val sectionsList = listOf("Section A", "Section B", "Section C")

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackgroundLight)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BentoBackgroundLight)
                .padding(innerPadding)
                .testTag("auth_screen"),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Bento Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoNavyDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SmartCampusEmblemHero(
                            logoSize = 110.dp,
                            showTagline = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Institutional Portal",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Role-based noticeboard & administrative management",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.75f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // 2. Tab Switcher: Sign In vs Register
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BentoNeutralCard, RoundedCornerShape(24.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Sign In Tab
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                currentTab = AuthTab.LOGIN
                                loginErrorMessage = null
                            }
                            .testTag("auth_tab_login"),
                        color = if (currentTab == AuthTab.LOGIN) BentoBluePrimary else Color.Transparent,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sign In",
                                fontSize = 13.sp,
                                fontWeight = if (currentTab == AuthTab.LOGIN) FontWeight.Bold else FontWeight.Medium,
                                color = if (currentTab == AuthTab.LOGIN) Color.White else BentoTextSecondary
                            )
                        }
                    }

                    // Register Tab
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                currentTab = AuthTab.REGISTER
                                registerErrorMessage = null
                            }
                            .testTag("auth_tab_register"),
                        color = if (currentTab == AuthTab.REGISTER) BentoBluePrimary else Color.Transparent,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Create Account",
                                fontSize = 13.sp,
                                fontWeight = if (currentTab == AuthTab.REGISTER) FontWeight.Bold else FontWeight.Medium,
                                color = if (currentTab == AuthTab.REGISTER) Color.White else BentoTextSecondary
                            )
                        }
                    }
                }
            }

            // TAB CONTENTS: LOGIN FORM
            if (currentTab == AuthTab.LOGIN) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BentoBorderLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Sign In with Institutional ID",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoNavyDark
                            )

                            // Error banner if any
                            AnimatedVisibility(
                                visible = loginErrorMessage != null,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Surface(
                                    color = PriorityUrgent.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, PriorityUrgent.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ErrorOutline,
                                            contentDescription = null,
                                            tint = PriorityUrgent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = loginErrorMessage ?: "",
                                            fontSize = 12.sp,
                                            color = PriorityUrgent,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            // Email text field
                            OutlinedTextField(
                                value = loginEmail,
                                onValueChange = {
                                    loginEmail = it
                                    loginErrorMessage = null
                                },
                                label = { Text("College Email Address") },
                                placeholder = { Text("e.g. karthik.selvam@college.edu") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = BentoNavyDark
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                shape = RoundedCornerShape(18.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = BentoNeutralCard,
                                    unfocusedContainerColor = BentoNeutralCard,
                                    focusedBorderColor = BentoBluePrimary,
                                    unfocusedBorderColor = BentoBorderLight
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_email_input")
                            )

                            // Password text field
                            OutlinedTextField(
                                value = loginPassword,
                                onValueChange = {
                                    loginPassword = it
                                    loginErrorMessage = null
                                },
                                label = { Text("Password") },
                                placeholder = { Text("••••••••") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = BentoNavyDark
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                            tint = BentoTextSecondary
                                        )
                                    }
                                },
                                singleLine = true,
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                shape = RoundedCornerShape(18.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = BentoNeutralCard,
                                    unfocusedContainerColor = BentoNeutralCard,
                                    focusedBorderColor = BentoBluePrimary,
                                    unfocusedBorderColor = BentoBorderLight
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_password_input")
                            )

                            // Sign In Button
                            Button(
                                onClick = {
                                    isAuthenticating = true
                                    loginErrorMessage = null
                                    viewModel.login(loginEmail, loginPassword) { success, errorMsg ->
                                        isAuthenticating = false
                                        if (!success) {
                                            loginErrorMessage = errorMsg
                                        }
                                    }
                                },
                                enabled = !isAuthenticating && loginEmail.isNotBlank() && loginPassword.isNotBlank(),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BentoNavyDark),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("login_submit_button")
                            ) {
                                if (isAuthenticating) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = "Sign In to Campus",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // Role routing indicator helper
                            Surface(
                                color = BentoSageContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = BentoSageOnContainer,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Students are routed to the Noticeboard; Faculty & Admins launch directly to the Management Dashboard.",
                                        fontSize = 11.sp,
                                        color = BentoSageOnContainer,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. TAB CONTENTS: REGISTER FORM
            if (currentTab == AuthTab.REGISTER) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BentoBorderLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Register Student Account",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoNavyDark
                            )

                            // Error banner if any
                            AnimatedVisibility(
                                visible = registerErrorMessage != null,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Surface(
                                    color = PriorityUrgent.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, PriorityUrgent.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ErrorOutline,
                                            contentDescription = null,
                                            tint = PriorityUrgent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = registerErrorMessage ?: "",
                                            fontSize = 12.sp,
                                            color = PriorityUrgent,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            // Full Name
                            OutlinedTextField(
                                value = regName,
                                onValueChange = {
                                    regName = it
                                    registerErrorMessage = null
                                },
                                label = { Text("Full Name") },
                                placeholder = { Text("e.g. Karthik Selvam") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = BentoNavyDark
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(18.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = BentoNeutralCard,
                                    unfocusedContainerColor = BentoNeutralCard,
                                    focusedBorderColor = BentoBluePrimary,
                                    unfocusedBorderColor = BentoBorderLight
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_name_input")
                            )

                            // Institutional Email
                            OutlinedTextField(
                                value = regEmail,
                                onValueChange = {
                                    regEmail = it
                                    registerErrorMessage = null
                                },
                                label = { Text("College Email Address") },
                                placeholder = { Text("e.g. karthik.selvam@college.edu") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = BentoNavyDark
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                shape = RoundedCornerShape(18.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = BentoNeutralCard,
                                    unfocusedContainerColor = BentoNeutralCard,
                                    focusedBorderColor = BentoBluePrimary,
                                    unfocusedBorderColor = BentoBorderLight
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_email_input")
                            )

                            // Password
                            OutlinedTextField(
                                value = regPassword,
                                onValueChange = {
                                    regPassword = it
                                    registerErrorMessage = null
                                },
                                label = { Text("Password") },
                                placeholder = { Text("Create password") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = BentoNavyDark
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { isRegPasswordVisible = !isRegPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isRegPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = null,
                                            tint = BentoTextSecondary
                                        )
                                    }
                                },
                                singleLine = true,
                                visualTransformation = if (isRegPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                shape = RoundedCornerShape(18.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = BentoNeutralCard,
                                    unfocusedContainerColor = BentoNeutralCard,
                                    focusedBorderColor = BentoBluePrimary,
                                    unfocusedBorderColor = BentoBorderLight
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_password_input")
                            )

                            // Registration ID Number
                            OutlinedTextField(
                                value = regIdNumber,
                                onValueChange = {
                                    regIdNumber = it
                                    registerErrorMessage = null
                                },
                                label = { Text("Student Registration No.") },
                                placeholder = { Text("e.g. 2024CSE199") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Badge,
                                        contentDescription = null,
                                        tint = BentoNavyDark
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(18.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = BentoNeutralCard,
                                    unfocusedContainerColor = BentoNeutralCard,
                                    focusedBorderColor = BentoBluePrimary,
                                    unfocusedBorderColor = BentoBorderLight
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_id_input")
                            )

                            // Department Dropdown
                            ExposedDropdownMenuBox(
                                expanded = deptExpanded,
                                onExpandedChange = { deptExpanded = !deptExpanded }
                            ) {
                                OutlinedTextField(
                                    value = regDepartment,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Department") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Business,
                                            contentDescription = null,
                                            tint = BentoNavyDark
                                        )
                                    },
                                    shape = RoundedCornerShape(18.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = BentoNeutralCard,
                                        unfocusedContainerColor = BentoNeutralCard,
                                        focusedBorderColor = BentoBluePrimary,
                                        unfocusedBorderColor = BentoBorderLight
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                        .testTag("reg_department_dropdown")
                                )
                                ExposedDropdownMenu(
                                    expanded = deptExpanded,
                                    onDismissRequest = { deptExpanded = false }
                                ) {
                                    departmentsList.forEach { dept ->
                                        DropdownMenuItem(
                                            text = { Text(dept) },
                                            onClick = {
                                                regDepartment = dept
                                                deptExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Student Cohort (Year & Section)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Year Dropdown
                                ExposedDropdownMenuBox(
                                    expanded = yearExpanded,
                                    onExpandedChange = { yearExpanded = !yearExpanded },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    OutlinedTextField(
                                        value = regYear,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Year") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded) },
                                        shape = RoundedCornerShape(18.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = BentoNeutralCard,
                                            unfocusedContainerColor = BentoNeutralCard,
                                            focusedBorderColor = BentoBluePrimary,
                                            unfocusedBorderColor = BentoBorderLight
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor()
                                            .testTag("reg_year_dropdown")
                                    )
                                    ExposedDropdownMenu(
                                        expanded = yearExpanded,
                                        onDismissRequest = { yearExpanded = false }
                                    ) {
                                        yearsList.forEach { yr ->
                                            DropdownMenuItem(
                                                text = { Text(yr) },
                                                onClick = {
                                                    regYear = yr
                                                    yearExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                // Section Dropdown
                                ExposedDropdownMenuBox(
                                    expanded = sectionExpanded,
                                    onExpandedChange = { sectionExpanded = !sectionExpanded },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    OutlinedTextField(
                                        value = regSection,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Section") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sectionExpanded) },
                                        shape = RoundedCornerShape(18.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = BentoNeutralCard,
                                            unfocusedContainerColor = BentoNeutralCard,
                                            focusedBorderColor = BentoBluePrimary,
                                            unfocusedBorderColor = BentoBorderLight
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor()
                                            .testTag("reg_section_dropdown")
                                    )
                                    ExposedDropdownMenu(
                                        expanded = sectionExpanded,
                                        onDismissRequest = { sectionExpanded = false }
                                    ) {
                                        sectionsList.forEach { sec ->
                                            DropdownMenuItem(
                                                text = { Text(sec) },
                                                onClick = {
                                                    regSection = sec
                                                    sectionExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Register Button
                            Button(
                                onClick = {
                                    if (regName.isBlank()) {
                                        registerErrorMessage = "Please enter your full name"
                                        return@Button
                                    }
                                    if (regEmail.isBlank() || !regEmail.contains("@")) {
                                        registerErrorMessage = "Please enter a valid email address"
                                        return@Button
                                    }
                                    if (regPassword.length < 4) {
                                        registerErrorMessage = "Password should be at least 4 characters long"
                                        return@Button
                                    }

                                    viewModel.register(
                                        regName,
                                        regEmail,
                                        regPassword,
                                        regDepartment,
                                        regYear,
                                        regSection,
                                        regIdNumber
                                    ) { success, errorMsg ->
                                        if (!success) {
                                            registerErrorMessage = errorMsg
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BentoNavyDark),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("reg_submit_button")
                            ) {
                                Text(
                                    text = "Create Student Account",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
