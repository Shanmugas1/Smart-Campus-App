package com.example.ui.screens

import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.NoticeCategory
import com.example.model.NoticePriority
import com.example.service.AudienceEngine
import com.example.ui.components.AudienceTreeSelector
import com.example.ui.components.CategoryChip
import com.example.ui.components.CreateNoticeAttachmentSection
import com.example.ui.components.FilePickerModal
import com.example.ui.theme.BentoNavyDark
import com.example.ui.theme.PriorityImportant
import com.example.ui.theme.PriorityNormal
import com.example.ui.theme.PriorityUrgent
import com.example.viewmodel.AppScreen
import com.example.viewmodel.SmartCampusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoticeScreen(
    viewModel: SmartCampusViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(NoticeCategory.ACADEMIC) }
    var selectedPriority by remember { mutableStateOf(NoticePriority.NORMAL) }
    var selectedTargets by remember { mutableStateOf(setOf("ALL")) }
    var isPinned by remember { mutableStateOf(false) }

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showFilePicker by remember { mutableStateOf(false) }

    val pendingAttachments by viewModel.pendingAttachments.collectAsStateWithLifecycle()
    val isUploading by viewModel.isUploading.collectAsStateWithLifecycle()
    val uploadProgress by viewModel.uploadProgress.collectAsStateWithLifecycle()

    val compressedTargets = remember(selectedTargets) { AudienceEngine.compressTargets(selectedTargets) }
    val estimatedRecipients = remember(compressedTargets) { AudienceEngine.estimateRecipients(compressedTargets) }

    val context = LocalContext.current
    val contentResolver = context.contentResolver

    val allowedMimeTypes = remember {
        arrayOf(
            "application/pdf",
            "image/*",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain",
            "text/csv",
            "*/*"
        )
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            uris.forEach { uri ->
                try {
                    var fileName = "document_${System.currentTimeMillis()}"
                    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            if (nameIndex != -1) {
                                val name = cursor.getString(nameIndex)
                                if (!name.isNullOrBlank()) fileName = name
                            }
                        }
                    }
                    if (!fileName.contains(".")) {
                        val mime = contentResolver.getType(uri)
                        val ext = mime?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
                        if (ext != null) fileName = "$fileName.$ext"
                    }
                    val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    if (bytes != null && bytes.isNotEmpty()) {
                        viewModel.addPendingAttachment(fileName, bytes)
                    }
                } catch (e: Exception) {
                    // Handled gracefully
                }
            }
        }
    }

    fun executePublish() {
        viewModel.publishNoticeWithAttachments(
            title,
            content,
            selectedCategory,
            selectedPriority,
            compressedTargets,
            isPinned,
            {
                viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Announcement",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("create_notice_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Title input
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Announcement Title *",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = { Text("e.g. End Semester Examination Schedule & Hall Tickets") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("notice_title_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = false,
                            maxLines = 2
                        )
                    }
                }
            }

            // 2. Content input
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Announcement Body *",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            placeholder = { Text("Enter the full announcement text, instructions, and official details...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .testTag("notice_content_input"),
                            shape = RoundedCornerShape(10.dp),
                            maxLines = 8
                        )
                    }
                }
            }

            // 3. Category & Priority Selector
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Category",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            NoticeCategory.values().forEach { cat ->
                                CategoryChip(
                                    category = cat,
                                    isSelected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Priority Level",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NoticePriority.values().forEach { p ->
                                val (label, color) = when (p) {
                                    NoticePriority.NORMAL -> "Normal" to PriorityNormal
                                    NoticePriority.IMPORTANT -> "Important" to PriorityImportant
                                    NoticePriority.URGENT -> "Urgent Alert" to PriorityUrgent
                                }
                                val isSelected = selectedPriority == p
                                Surface(
                                    color = if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) color else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedPriority = p }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = color,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                        }
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Hierarchical Audience Selector Tree
            item {
                AudienceTreeSelector(
                    selectedTargets = selectedTargets,
                    onTargetsChanged = { selectedTargets = it }
                )
            }

            // 5. Cloud Object Storage Attachments Section
            item {
                CreateNoticeAttachmentSection(
                    pendingAttachments = pendingAttachments,
                    isUploading = isUploading,
                    uploadProgress = uploadProgress,
                    onAddFilesClick = { filePickerLauncher.launch(allowedMimeTypes) },
                    onOpenTemplatesClick = { showFilePicker = true },
                    onRemoveAttachment = { viewModel.removePendingAttachment(it) }
                )
            }

            // 6. Pin Option
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = null,
                                    tint = BentoNavyDark,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Pin to Top of Feed",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Keep at the top of target students' inbox",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = isPinned,
                                onCheckedChange = { isPinned = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = BentoNavyDark)
                            )
                        }
                    }
                }
            }

            // 7. Action Submit Button
            item {
                Button(
                    onClick = {
                        if (selectedPriority == NoticePriority.URGENT || selectedTargets.contains("ALL")) {
                            showConfirmationDialog = true
                        } else {
                            executePublish()
                        }
                    },
                    enabled = !isUploading && title.isNotBlank() && content.isNotBlank() && selectedTargets.isNotEmpty(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoNavyDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("publish_notice_button")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isUploading) "Uploading Attachments..." else "Publish Announcement (~$estimatedRecipients Students)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Attachment Picker Modal
    if (showFilePicker) {
        FilePickerModal(
            onFileSelected = { fileName, bytes ->
                showFilePicker = false
                viewModel.addPendingAttachment(fileName, bytes)
            },
            onBrowseDeviceFiles = {
                filePickerLauncher.launch(allowedMimeTypes)
            },
            onDismiss = { showFilePicker = false }
        )
    }

    // High Impact Confirmation Dialog
    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (selectedPriority == NoticePriority.URGENT) PriorityUrgent else BentoNavyDark,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm Broadcast Impact")
                }
            },
            text = {
                Text(
                    text = "You are about to publish an ${if (selectedPriority == NoticePriority.URGENT) "URGENT" else "Official"} announcement with ${pendingAttachments.size} attachment(s) targeting ${if (selectedTargets.contains("ALL")) "the ENTIRE COLLEGE (~2,880 students)" else "${compressedTargets.joinToString(", ")} (~$estimatedRecipients students)"}.\n\nPlease ensure all attachments and instructions have been verified by the department authority.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmationDialog = false
                        executePublish()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedPriority == NoticePriority.URGENT) PriorityUrgent else BentoNavyDark
                    )
                ) {
                    Text("Confirm & Publish")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
