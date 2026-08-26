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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Role
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
import com.example.viewmodel.AppScreen
import com.example.viewmodel.SmartCampusViewModel
import org.json.JSONArray
import org.json.JSONObject

data class CloudFileItem(
    val id: String,
    val fileName: String,
    val fileType: String,
    val fileSize: String,
    val noticeTitle: String,
    val noticeId: String,
    val cloudBucketPath: String,
    val cdnUrl: String,
    val uploadedBy: String,
    val uploadedAt: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudStorageVaultScreen(
    viewModel: SmartCampusViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allNotices by viewModel.adminAllNotices.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterType by remember { mutableStateOf("ALL") }
    var previewFile by remember { mutableStateOf<CloudFileItem?>(null) }
    var showUploadDialog by remember { mutableStateOf(false) }

    val isAdminOrFaculty = currentUser?.let {
        it.role == Role.ADMIN || it.role == Role.SUPER_ADMIN || it.role == Role.FACULTY
    } ?: false

    // Extract all attachments from notice records
    val cloudFiles = remember(allNotices) {
        val list = mutableListOf<CloudFileItem>()
        // Default seed cloud files
        list.add(
            CloudFileItem(
                id = "cld_01",
                fileName = "IA_II_Timetable_CSE2A.pdf",
                fileType = "PDF",
                fileSize = "1.2 MB",
                noticeTitle = "Internal Assessment II Timetable",
                noticeId = "not_001",
                cloudBucketPath = "gs://smart-campus-vault/exams/2026/IA_II_Timetable_CSE2A.pdf",
                cdnUrl = "https://storage.googleapis.com/smart-campus-vault/exams/IA_II_Timetable_CSE2A.pdf",
                uploadedBy = "CSE Examination Cell",
                uploadedAt = "2 hours ago"
            )
        )
        list.add(
            CloudFileItem(
                id = "cld_02",
                fileName = "Amazon_SDE_JD_2026.pdf",
                fileType = "PDF",
                fileSize = "2.8 MB",
                noticeTitle = "Amazon SDE Recruitment Drive",
                noticeId = "not_002",
                cloudBucketPath = "gs://smart-campus-vault/placements/2026/Amazon_SDE_JD_2026.pdf",
                cdnUrl = "https://storage.googleapis.com/smart-campus-vault/placements/Amazon_SDE_JD.pdf",
                uploadedBy = "Campus Placement Cell",
                uploadedAt = "5 hours ago"
            )
        )
        list.add(
            CloudFileItem(
                id = "cld_03",
                fileName = "Eligible_Candidates_List_CSE.xlsx",
                fileType = "XLSX",
                fileSize = "840 KB",
                noticeTitle = "Amazon SDE Recruitment Drive",
                noticeId = "not_002",
                cloudBucketPath = "gs://smart-campus-vault/placements/2026/Eligible_Candidates_List_CSE.xlsx",
                cdnUrl = "https://storage.googleapis.com/smart-campus-vault/placements/Eligible_List.xlsx",
                uploadedBy = "Campus Placement Cell",
                uploadedAt = "5 hours ago"
            )
        )
        list.add(
            CloudFileItem(
                id = "cld_04",
                fileName = "Hackathon_Rules_and_Themes.pdf",
                fileType = "PDF",
                fileSize = "3.4 MB",
                noticeTitle = "National Smart Hackathon 2026",
                noticeId = "not_003",
                cloudBucketPath = "gs://smart-campus-vault/events/hackathon_2026_rules.pdf",
                cdnUrl = "https://storage.googleapis.com/smart-campus-vault/events/hackathon_2026_rules.pdf",
                uploadedBy = "Centre for Innovation",
                uploadedAt = "1 day ago"
            )
        )
        list.add(
            CloudFileItem(
                id = "cld_05",
                fileName = "Hackathon_Poster_HD.png",
                fileType = "PNG",
                fileSize = "4.1 MB",
                noticeTitle = "National Smart Hackathon 2026",
                noticeId = "not_003",
                cloudBucketPath = "gs://smart-campus-vault/events/Hackathon_Poster_HD.png",
                cdnUrl = "https://storage.googleapis.com/smart-campus-vault/events/Hackathon_Poster_HD.png",
                uploadedBy = "Centre for Innovation",
                uploadedAt = "1 day ago"
            )
        )
        list.add(
            CloudFileItem(
                id = "cld_06",
                fileName = "Academic_Calendar_Even_Semester_2026.pdf",
                fileType = "PDF",
                fileSize = "1.8 MB",
                noticeTitle = "Even Semester Academic Calendar",
                noticeId = "not_004",
                cloudBucketPath = "gs://smart-campus-vault/academics/Academic_Calendar_2026.pdf",
                cdnUrl = "https://storage.googleapis.com/smart-campus-vault/academics/Academic_Calendar_2026.pdf",
                uploadedBy = "Dr. R. Thirunavukkarasu (Dean)",
                uploadedAt = "3 days ago"
            )
        )

        // Parse any additional attachments from notices
        allNotices.forEach { notice ->
            if (notice.attachmentsJson.isNotBlank() && notice.attachmentsJson != "[]") {
                try {
                    val array = JSONArray(notice.attachmentsJson)
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        val fileName = obj.optString("fileName", "Document.pdf")
                        if (list.none { it.fileName == fileName }) {
                            list.add(
                                CloudFileItem(
                                    id = obj.optString("id", "cld_${System.currentTimeMillis()}_$i"),
                                    fileName = fileName,
                                    fileType = obj.optString("fileType", "PDF"),
                                    fileSize = obj.optString("fileSize", "1.5 MB"),
                                    noticeTitle = notice.title,
                                    noticeId = notice.id,
                                    cloudBucketPath = "gs://smart-campus-vault/documents/${fileName.replace(" ", "_")}",
                                    cdnUrl = "https://storage.googleapis.com/smart-campus-vault/documents/${fileName.replace(" ", "_")}",
                                    uploadedBy = notice.postedBy,
                                    uploadedAt = "Recent"
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    // Ignore parsing error
                }
            }
        }
        list
    }

    val filteredFiles = cloudFiles.filter { file ->
        val matchesQuery = searchQuery.isBlank() ||
                file.fileName.contains(searchQuery, ignoreCase = true) ||
                file.noticeTitle.contains(searchQuery, ignoreCase = true) ||
                file.uploadedBy.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilterType) {
            "ALL" -> true
            "PDF" -> file.fileType.equals("PDF", ignoreCase = true)
            "IMAGES" -> file.fileType.equals("PNG", ignoreCase = true) || file.fileType.equals("JPG", ignoreCase = true)
            "SHEETS" -> file.fileType.equals("XLSX", ignoreCase = true) || file.fileType.equals("CSV", ignoreCase = true)
            else -> true
        }

        matchesQuery && matchesFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Cloud Storage Vault",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoNavyDark
                        )
                        Text(
                            text = "Google Cloud & Firebase Storage Explorer",
                            fontSize = 11.sp,
                            color = BentoTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = BentoNavyDark
                        )
                    }
                },
                actions = {
                    if (isAdminOrFaculty) {
                        IconButton(
                            onClick = { showUploadDialog = true },
                            modifier = Modifier.testTag("btn_upload_cloud_file")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = "Upload Document",
                                tint = BentoNavyDark
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BentoBackgroundLight)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BentoBackgroundLight)
                .padding(padding)
                .testTag("cloud_storage_screen"),
            contentPadding = PaddingValues(bottom = 100.dp, start = 16.dp, end = 16.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Cloud Storage Architecture & Tier Status Bento Card
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
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = BentoSkyContainer,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.CloudDone,
                                            contentDescription = null,
                                            tint = BentoNavyDark,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Cloud Storage Bucket",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "gs://smart-campus-vault",
                                        fontSize = 11.sp,
                                        color = BentoSkyContainer
                                    )
                                }
                            }

                            Surface(
                                color = BentoSageContainer,
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(
                                    text = "LIVE SYNC ACTIVE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoSageOnContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Storage Usage Meter
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Storage Quota: 14.1 MB used of 5.0 GB",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "0.28%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoSkyContainer
                                )
                            }

                            LinearProgressIndicator(
                                progress = { 0.0028f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = BentoSkyContainer,
                                trackColor = Color.White.copy(alpha = 0.15f)
                            )
                        }

                        // Architecture Explanation Pill
                        Surface(
                            color = Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "How Cloud Storage works: Circular attachments and timetables are securely stored in the cloud bucket with automated CDN caching and tokenized URL downloads for students and faculty.",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            // 2. Bento Metrics Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Tile 1: Total Cloud Attachments
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(95.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoSkyContainer)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "STORED FILES",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoBluePrimary,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "${cloudFiles.size} Docs",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = BentoNavyDark
                            )
                        }
                    }

                    // Tile 2: Cache & Bandwidth
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(95.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoLavenderContainer)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "CDN BANDWIDTH",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoLavenderOnContainer,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "99.8% Hit Rate",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = BentoLavenderOnContainer
                            )
                        }
                    }
                }
            }

            // 3. Search & Category Filters
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search cloud documents, PDFs, circulars...", fontSize = 13.sp, color = BentoTextSecondary) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = BentoNavyDark,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cloud_search_input"),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = BentoNeutralCard,
                        focusedBorderColor = BentoBluePrimary,
                        unfocusedBorderColor = BentoBorderLight
                    ),
                    singleLine = true
                )
            }

            // Filter Chips Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "ALL" to "All Files",
                        "PDF" to "PDFs",
                        "IMAGES" to "Images",
                        "SHEETS" to "Sheets"
                    ).forEach { (key, label) ->
                        val isSelected = selectedFilterType == key
                        Surface(
                            color = if (isSelected) BentoNavyDark else BentoNeutralCard,
                            shape = RoundedCornerShape(20.dp),
                            border = if (!isSelected) BorderStroke(1.dp, BentoBorderLight) else null,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { selectedFilterType = key }
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else BentoTextSecondary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // 4. File List Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CLOUD VAULT OBJECTS (${filteredFiles.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoNavyDark,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // 5. Cloud File Cards
            items(filteredFiles, key = { it.id }) { file ->
                val icon: ImageVector = when (file.fileType.uppercase()) {
                    "PDF" -> Icons.Default.PictureAsPdf
                    "PNG", "JPG", "JPEG" -> Icons.Default.Image
                    "XLSX", "CSV" -> Icons.Default.TableChart
                    else -> Icons.Default.InsertDriveFile
                }
                val iconBg = when (file.fileType.uppercase()) {
                    "PDF" -> BentoPeachContainer
                    "PNG", "JPG" -> BentoLavenderContainer
                    "XLSX", "CSV" -> BentoSageContainer
                    else -> BentoSkyContainer
                }
                val iconTint = when (file.fileType.uppercase()) {
                    "PDF" -> BentoPeachOnContainer
                    "PNG", "JPG" -> BentoLavenderOnContainer
                    "XLSX", "CSV" -> BentoSageOnContainer
                    else -> BentoNavyDark
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { previewFile = file }
                        .testTag("cloud_file_${file.id}"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BentoBorderLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // File Type Icon
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = iconBg,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = file.fileType,
                                    tint = iconTint,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        // File Metadata
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = file.fileName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoNavyDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Linked: ${file.noticeTitle}",
                                fontSize = 11.sp,
                                color = BentoTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = BentoNeutralCard,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = file.fileSize,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoNavyDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = file.uploadedAt,
                                    fontSize = 10.sp,
                                    color = BentoTextSecondary
                                )
                            }
                        }

                        // View Action
                        IconButton(
                            onClick = { previewFile = file },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download or preview",
                                tint = BentoBluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // File Preview Modal Dialog
    previewFile?.let { file ->
        AlertDialog(
            onDismissRequest = { previewFile = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudQueue,
                        contentDescription = null,
                        tint = BentoNavyDark,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cloud File Details",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoNavyDark
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = BentoNeutralCard,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = file.fileName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoNavyDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Size: ${file.fileSize} • Type: ${file.fileType}",
                                fontSize = 12.sp,
                                color = BentoTextSecondary
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "CLOUD BUCKET OBJECT PATH",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextSecondary
                        )
                        Text(
                            text = file.cloudBucketPath,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = BentoBluePrimary
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "PUBLIC CDN URL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextSecondary
                        )
                        Text(
                            text = file.cdnUrl,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = BentoTextSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Surface(
                        color = BentoSageContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = BentoSageOnContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Cached on Cloud Edge & available offline",
                                fontSize = 11.sp,
                                color = BentoSageOnContainer,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        previewFile = null
                        viewModel.clearSnackbar()
                        // simulate download complete feedback
                        viewModel.navigateTo(AppScreen.NOTICE_DETAILS, file.noticeId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoNavyDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Open Linked Notice", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { previewFile = null },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Close", fontSize = 13.sp, color = BentoNavyDark)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Upload New Cloud Document Dialog
    if (showUploadDialog) {
        var uploadFileName by remember { mutableStateOf("") }
        var uploadFileType by remember { mutableStateOf("PDF") }
        var uploadFileSize by remember { mutableStateOf("1.5 MB") }

        AlertDialog(
            onDismissRequest = { showUploadDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = BentoNavyDark,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Upload to Cloud Bucket",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoNavyDark
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Upload PDF circular, syllabus document, or timetable spreadsheet to Google Cloud Storage.",
                        fontSize = 12.sp,
                        color = BentoTextSecondary
                    )

                    OutlinedTextField(
                        value = uploadFileName,
                        onValueChange = { uploadFileName = it },
                        label = { Text("File Name") },
                        placeholder = { Text("e.g. Lab_Schedule_Even_Sem.pdf") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BentoNeutralCard,
                            unfocusedContainerColor = BentoNeutralCard,
                            focusedBorderColor = BentoBluePrimary,
                            unfocusedBorderColor = BentoBorderLight
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("PDF", "PNG", "XLSX").forEach { type ->
                            val isSelected = uploadFileType == type
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { uploadFileType = type },
                                color = if (isSelected) BentoNavyDark else BentoNeutralCard,
                                shape = RoundedCornerShape(12.dp),
                                border = if (!isSelected) BorderStroke(1.dp, BentoBorderLight) else null
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = type,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else BentoTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (uploadFileName.isNotBlank()) {
                            showUploadDialog = false
                            // Feedback
                        }
                    },
                    enabled = uploadFileName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoNavyDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Upload to Bucket", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showUploadDialog = false },
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
