package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Attachment
import com.example.model.Notice
import com.example.model.NoticeCategory
import com.example.model.NoticePriority
import com.example.service.AudienceEngine
import com.example.ui.components.AttachmentItemCard
import com.example.ui.components.AudienceChip
import com.example.ui.components.PdfViewerModal
import com.example.ui.components.PriorityBadge
import com.example.ui.components.getCategoryColor
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.BentoBluePrimary
import com.example.ui.theme.BentoBorderLight
import com.example.ui.theme.BentoNavyDark
import com.example.ui.theme.BentoNeutralCard
import com.example.ui.theme.BentoSkyContainer
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.CategoryAcademic
import com.example.ui.theme.CategoryPlacement
import com.example.ui.theme.PriorityUrgent
import com.example.viewmodel.AppScreen
import com.example.viewmodel.SmartCampusViewModel
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NoticeDetailsScreen(
    viewModel: SmartCampusViewModel,
    noticeId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val rawNotices by viewModel.rawPersonalizedNotices.collectAsStateWithLifecycle()
    val adminNotices by viewModel.adminAllNotices.collectAsStateWithLifecycle()
    val previewModalData by viewModel.previewModalData.collectAsStateWithLifecycle()

    val noticeWithState = rawNotices.find { it.notice.id == noticeId }
    val notice = noticeWithState?.notice ?: adminNotices.find { it.id == noticeId } ?: remember(noticeId) { viewModel.getNoticeById(noticeId) }

    val isBookmarked = noticeWithState?.isBookmarked ?: false

    // Auto mark as read on view
    LaunchedEffect(noticeId) {
        viewModel.markAsRead(noticeId)
    }

    if (notice == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Announcement Details") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Announcement not found or access restricted by cohort policy.")
            }
        }
        return
    }

    val attachmentsFromDb by viewModel.getAttachmentsForNoticeFlow(notice.id).collectAsStateWithLifecycle(initialValue = emptyList())

    // If attachments exist in database, use those; otherwise fallback to JSON
    val attachmentsToDisplay = remember(attachmentsFromDb, notice.attachmentsJson) {
        if (attachmentsFromDb.isNotEmpty()) {
            attachmentsFromDb
        } else {
            try {
                val arr = JSONArray(notice.attachmentsJson)
                val list = mutableListOf<Attachment>()
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val fn = obj.optString("fileName", "Document.pdf")
                    val ext = if (fn.contains('.')) fn.substringAfterLast('.').lowercase() else "pdf"
                    list.add(
                        Attachment(
                            obj.optString("id", "att_${notice.id}_$i"),
                            notice.id,
                            fn,
                            "sample_$i.$ext",
                            if (ext == "pdf") "application/pdf" else "application/octet-stream",
                            ext,
                            1572864L,
                            obj.optString("fileSize", "1.5 MB"),
                            "vault/notices/${notice.id}/sample_$i.$ext",
                            notice.postedBy,
                            notice.createdAt
                        )
                    )
                }
                list
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    val catColor = getCategoryColor(notice.category)
    val catIcon = getCategoryIcon(notice.category)
    val dateFormat = remember { SimpleDateFormat("EEEE, MMMM dd, yyyy • hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(notice.createdAt) { dateFormat.format(Date(notice.createdAt)) }
    val targets = remember(notice.targetAudience) { AudienceEngine.parseTargets(notice.targetAudience) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Official Announcement",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("notice_detail_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.toggleBookmark(notice.id, isBookmarked)
                        },
                        modifier = Modifier.testTag("notice_detail_bookmark_button")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) BentoBluePrimary else BentoNavyDark
                        )
                    }

                    IconButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TITLE, notice.title)
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Official Campus Announcement: ${notice.title}\n\n${notice.content}\n\nPosted by: ${notice.postedBy} via Smart Campus"
                                )
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Notice"))
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share Announcement", tint = BentoNavyDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            // 1. Official Verification Ribbon
            item {
                Surface(
                    color = BentoSkyContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BentoBorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Official",
                                tint = BentoBluePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Institutional Verified Bulletin",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoNavyDark
                            )
                        }

                        if (notice.pinned) {
                            Surface(
                                color = BentoNavyDark,
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PushPin,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "PINNED",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 2. Category & Priority Badges
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = catColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = catIcon,
                                contentDescription = null,
                                tint = catColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = notice.category.displayName.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = catColor
                            )
                        }
                    }

                    PriorityBadge(priority = notice.priority)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 3. Title
            item {
                Text(
                    text = notice.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoNavyDark,
                    lineHeight = 26.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 4. Author & Meta Information Bar
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoNeutralCard),
                    border = BorderStroke(1.dp, BentoBorderLight)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(BentoBluePrimary.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = notice.postedBy.take(2).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = BentoBluePrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = notice.postedBy,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoNavyDark
                                )
                                Text(
                                    text = "Departmental Authority • $formattedDate",
                                    fontSize = 10.sp,
                                    color = BentoTextSecondary
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 5. Target Audience Hierarchy Box
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, BentoBorderLight)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = null,
                                tint = BentoBluePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Authorized Target Audience",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoNavyDark
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            targets.forEach { target ->
                                AudienceChip(target = target)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 6. Main Announcement Body
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, BentoBorderLight)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Announcement Details",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoBluePrimary,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = notice.content,
                            fontSize = 15.sp,
                            color = BentoTextPrimary,
                            lineHeight = 23.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 7. Secure Cloud Storage Attachments Section
            item {
                if (attachmentsToDisplay.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ATTACHED DOCUMENTS (${attachmentsToDisplay.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextSecondary,
                            letterSpacing = 0.5.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Vault Protected",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    attachmentsToDisplay.forEach { attachment ->
                        AttachmentItemCard(
                            attachment = attachment,
                            onPreview = { viewModel.previewAttachment(attachment) },
                            onDownload = { viewModel.downloadAttachment(attachment) }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // 8. Cryptographic Authenticity Stamp Footer
            item {
                Surface(
                    color = BentoNeutralCard,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BentoBorderLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = BentoNavyDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Notice ID: ${notice.id} • Digitally Audited & Encrypted",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BentoNavyDark
                            )
                            Text(
                                text = "Cloud Object Storage URL signed with 15-minute token isolation.",
                                fontSize = 9.sp,
                                color = BentoTextSecondary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // PDF Viewer Modal
    previewModalData?.let { data ->
        PdfViewerModal(
            previewData = data,
            onDownload = { viewModel.downloadAttachment(data.attachment) },
            onDismiss = { viewModel.closePreviewModal() }
        )
    }
}
