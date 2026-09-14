package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Attachment
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
import com.example.ui.theme.CategoryAcademic
import com.example.ui.theme.CategoryEmergency
import com.example.ui.theme.CategoryPlacement
import com.example.viewmodel.AttachmentPreviewModalData
import com.example.viewmodel.PendingAttachment

/**
 * Returns icon and container colors depending on document extension
 */
fun getAttachmentVisuals(extension: String): Pair<ImageVector, Color> {
    return when (extension.lowercase()) {
        "pdf" -> Icons.Default.PictureAsPdf to Color(0xFFD32F2F)
        "doc", "docx" -> Icons.Default.Description to Color(0xFF1976D2)
        "ppt", "pptx" -> Icons.Default.Description to Color(0xFFE65100)
        "xls", "xlsx" -> Icons.Default.TableChart to Color(0xFF2E7D32)
        "jpg", "jpeg", "png", "webp" -> Icons.Default.Image to Color(0xFF7B1FA2)
        "csv", "txt" -> Icons.Default.TextFields to Color(0xFF00838F)
        else -> Icons.Outlined.AttachFile to BentoNavyDark
    }
}

/**
 * Upload Section Component for the Create Announcement screen
 */
@Composable
fun CreateNoticeAttachmentSection(
    pendingAttachments: List<PendingAttachment>,
    isUploading: Boolean,
    uploadProgress: Float,
    onAddFilesClick: () -> Unit,
    onOpenTemplatesClick: () -> Unit,
    onRemoveAttachment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BentoNeutralCard),
        border = BorderStroke(1.dp, BentoBorderLight),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(BentoSkyContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AttachFile,
                            contentDescription = null,
                            tint = BentoBluePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Attachments & Documents",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoNavyDark
                        )
                        Text(
                            text = "PDF / DOCX / PPTX / XLSX / Images • Max 20 MB",
                            fontSize = 10.sp,
                            color = BentoTextSecondary
                        )
                    }
                }

                Button(
                    onClick = onAddFilesClick,
                    enabled = !isUploading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoBluePrimary),
                    modifier = Modifier.testTag("add_attachment_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add Files",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Drop & Device Browse Zone
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, BentoBluePrimary.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isUploading) { onAddFilesClick() }
                    .testTag("attachment_drop_zone")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 18.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(BentoSkyContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            tint = BentoBluePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Drag & Drop files here",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoNavyDark
                    )
                    Text(
                        text = "or choose files from your local storage",
                        fontSize = 11.sp,
                        color = BentoTextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onAddFilesClick,
                            enabled = !isUploading,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BentoBluePrimary),
                            modifier = Modifier.testTag("browse_device_files_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Browse Local Files",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = onOpenTemplatesClick,
                            enabled = !isUploading,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BentoBorderLight),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BentoNavyDark),
                            modifier = Modifier.testTag("open_templates_button")
                        ) {
                            Text(
                                text = "📚 Templates",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Supported: PDF, JPG, PNG, WEBP, DOC, DOCX, PPT, PPTX, XLS, XLSX",
                        fontSize = 9.5.sp,
                        color = BentoTextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Upload Progress
            if (isUploading) {
                Spacer(modifier = Modifier.height(12.dp))
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Uploading to Cloud Storage...",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BentoBluePrimary
                        )
                        Text(
                            text = "${(uploadProgress * 100).toInt()}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoBluePrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { uploadProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = BentoBluePrimary,
                        trackColor = BentoSkyContainer
                    )
                }
            }

            // Selected Attachments List
            if (pendingAttachments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ATTACHMENTS (${pendingAttachments.size})",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    val totalMb = pendingAttachments.sumOf { it.fileSizeBytes }.toDouble() / (1024.0 * 1024.0)
                    Text(
                        text = "${"%.1f".format(totalMb)} MB total",
                        fontSize = 10.sp,
                        color = BentoTextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                pendingAttachments.forEach { item ->
                    val isImage = setOf("jpg", "jpeg", "png", "webp").contains(item.fileExtension.lowercase())
                    val (icon, color) = getAttachmentVisuals(item.fileExtension)

                    if (isImage) {
                        // Image Thumbnail Preview Card
                        val imageBitmap = remember(item.bytes) {
                            try {
                                BitmapFactory.decodeByteArray(item.bytes, 0, item.bytes.size)?.asImageBitmap()
                            } catch (e: Throwable) {
                                null
                            }
                        }

                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, BentoBorderLight),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("pending_image_attachment_${item.id}")
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                if (imageBitmap != null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(130.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFF1F5F9)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            bitmap = imageBitmap,
                                            contentDescription = item.fileName,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(30.dp)
                                                .background(color.copy(alpha = 0.12f), RoundedCornerShape(6.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = color,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = item.fileName,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BentoNavyDark,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${item.fileExtension.uppercase()} • ${item.fileSizeFormatted}",
                                                fontSize = 10.sp,
                                                color = BentoTextSecondary
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { onRemoveAttachment(item.id) },
                                        enabled = !isUploading,
                                        modifier = Modifier.size(28.dp).testTag("remove_attachment_${item.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove file",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Standard Document Preview Row
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, BentoBorderLight),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .testTag("pending_doc_attachment_${item.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(color.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = color,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = item.fileName,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BentoNavyDark,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${item.fileExtension.uppercase()} • ${item.fileSizeFormatted}",
                                            fontSize = 10.sp,
                                            color = BentoTextSecondary
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { onRemoveAttachment(item.id) },
                                    enabled = !isUploading,
                                    modifier = Modifier.size(28.dp).testTag("remove_attachment_${item.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove file",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Details Screen Attachment Card Row
 */
@Composable
fun AttachmentItemCard(
    attachment: Attachment,
    onPreview: () -> Unit,
    onDownload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, color) = getAttachmentVisuals(attachment.fileExtension)
    val isPdf = attachment.fileExtension.equals("pdf", ignoreCase = true)
    val isImage = setOf("jpg", "jpeg", "png", "webp").contains(attachment.fileExtension.lowercase())

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BentoBorderLight),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(color.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = attachment.originalFileName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoNavyDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = color.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = attachment.fileExtension.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = color,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = attachment.fileSizeFormatted,
                            fontSize = 11.sp,
                            color = BentoTextSecondary
                        )
                        if (attachment.downloadCount > 0) {
                            Text(
                                text = " • ${attachment.downloadCount} dl",
                                fontSize = 10.sp,
                                color = BentoTextSecondary
                            )
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isPdf || isImage) {
                    OutlinedButton(
                        onClick = onPreview,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BentoBluePrimary.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BentoBluePrimary),
                        modifier = Modifier.testTag("preview_button_${attachment.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("View", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Button(
                    onClick = onDownload,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoNavyDark),
                    modifier = Modifier.testTag("download_button_${attachment.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Download", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Fullscreen Interactive PDF & Document Reader with Multi-Page Simulation
 */
@Composable
fun PdfViewerModal(
    previewData: AttachmentPreviewModalData,
    onDownload: () -> Unit,
    onDismiss: () -> Unit
) {
    var currentPage by remember { mutableIntStateOf(1) }
    val totalPages = 3

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF1E293B)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Action Bar
                Surface(
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = previewData.attachment.originalFileName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = Color(0xFF34D399),
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Cloud Vault Authenticated • ${previewData.attachment.fileSizeFormatted}",
                                        fontSize = 10.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = onDownload,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BentoBluePrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Download", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Page Navigation Pill Toolbar
                Surface(
                    color = Color(0xFF334155),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { if (currentPage > 1) currentPage-- },
                            enabled = currentPage > 1,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF475569)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Text("Previous", fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = "Page $currentPage of $totalPages",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        OutlinedButton(
                            onClick = { if (currentPage < totalPages) currentPage++ },
                            enabled = currentPage < totalPages,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF475569)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Text("Next", fontSize = 11.sp)
                        }
                    }
                }

                // PDF Page Canvas Simulator (Document sheet)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp)
                        ) {
                            // Institutional Letterhead
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "SMART CAMPUS AUTONOMOUS INSTITUTION",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF0F172A),
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "OFFICE OF THE CONTROLLER OF ACADEMIC AFFAIRS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF475569)
                                    )
                                    Text(
                                        text = "Accredited Grade 'A++' by NAAC | Approved by AICTE",
                                        fontSize = 9.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                Surface(
                                    color = CategoryAcademic.copy(alpha = 0.1f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = null,
                                            tint = CategoryAcademic,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = Color(0xFF0F172A),
                                thickness = 1.5.dp
                            )

                            // Document Title & Reference
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Ref: SC/ACA/2026/DOC-${previewData.attachment.id.takeLast(4).uppercase()}",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF475569)
                                )
                                Text(
                                    text = "DATE: MARCH 2026",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF475569)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = previewData.attachment.originalFileName.replace("_", " ").replace(".pdf", ""),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Page Content Simulation
                            when (currentPage) {
                                1 -> {
                                    Text(
                                        text = "OFFICIAL INSTITUTIONAL CIRCULAR & SCHEDULE",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoNavyDark
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "This document contains authorized schedules, instructions, and procedural rules released by the institutional authority. All targeted cohorts are required to adhere strictly to the timelines outlined herein.",
                                        fontSize = 11.sp,
                                        color = Color(0xFF334155),
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Schedule Table Simulation
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
                                    ) {
                                        Surface(color = Color(0xFFF1F5F9)) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Component", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                Text("Timeline / Venue", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                Text("Status", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        listOf(
                                            Triple("Registration & Verification", "March 20 • Portal", "Mandatory"),
                                            Triple("Slot Allocation & Assessment", "March 24 • Exam Hall 3", "Scheduled"),
                                            Triple("Evaluation & Result Publication", "April 02 • Dashboard", "Pending")
                                        ).forEach { (a, b, c) ->
                                            HorizontalDivider(color = Color(0xFFE2E8F0))
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(a, fontSize = 10.sp, color = Color(0xFF1E293B))
                                                Text(b, fontSize = 10.sp, color = Color(0xFF475569))
                                                Text(c, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BentoBluePrimary)
                                            }
                                        }
                                    }
                                }
                                2 -> {
                                    Text(
                                        text = "RULES, GUIDELINES & ACADEMIC INTEGRITY",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoNavyDark
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    listOf(
                                        "1. Candidates must report 15 minutes prior to commencement with smart institutional identity badges.",
                                        "2. Digital smart devices and unauthorized communication accessories are strictly prohibited.",
                                        "3. Late entry beyond 30 minutes of scheduled commencement will not be permitted under any condition.",
                                        "4. Any grievances or clash in academic schedules must be submitted to the HOD within 48 hours."
                                    ).forEach { rule ->
                                        Text(
                                            text = rule,
                                            fontSize = 11.sp,
                                            color = Color(0xFF334155),
                                            lineHeight = 16.sp,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    }
                                }
                                3 -> {
                                    Text(
                                        text = "AUTHORIZATION & CRYPTOGRAPHIC VERIFICATION",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoNavyDark
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "This document has been digitally verified and sealed by the Controller of Examinations. Unauthorized duplication or tampering is a punishable institutional violation.",
                                        fontSize = 11.sp,
                                        color = Color(0xFF334155),
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Signature block
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Column {
                                            Surface(
                                                color = Color(0xFFF8FAFC),
                                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Column(modifier = Modifier.padding(8.dp)) {
                                                    Text("SHA-256 Document Hash:", fontSize = 8.sp, color = Color(0xFF64748B))
                                                    Text("8f9c2d1e4b7a6590c...", fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }

                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "[ Digitally Signed ]",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CategoryAcademic
                                            )
                                            Text(
                                                text = "Dean / Head of Institution",
                                                fontSize = 10.sp,
                                                color = Color(0xFF475569)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(30.dp))

                            // Footer
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Smart Campus Cloud Object Storage Vault",
                                    fontSize = 9.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = "Page $currentPage of $totalPages",
                                    fontSize = 9.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dialog to select sample / custom files for attaching to an announcement
 */
@Composable
fun FilePickerModal(
    onFileSelected: (fileName: String, bytes: ByteArray) -> Unit,
    onBrowseDeviceFiles: () -> Unit,
    onDismiss: () -> Unit
) {
    var customFileName by remember { mutableStateOf("") }
    var customExtension by remember { mutableStateOf("pdf") }
    var customSizeMb by remember { mutableIntStateOf(2) }

    val samplePresets = listOf(
        Triple("Internal_Exam_Timetable.pdf", "pdf", 1887436),
        Triple("Lab_Manual_Operating_Systems.docx", "docx", 870400),
        Triple("Campus_Placement_Roadmap.pptx", "pptx", 4404019),
        Triple("Annual_Budget_Allocation.xlsx", "xlsx", 1258291),
        Triple("Campus_Orientation_Map.png", "png", 2306867),
        Triple("Student_Code_of_Conduct.pdf", "pdf", 943718),
        Triple("Hostel_Rules_and_Fee_Structure.csv", "csv", 154200)
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Attach Documents",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoNavyDark
                        )
                        Text(
                            text = "Browse device storage or choose template",
                            fontSize = 11.sp,
                            color = BentoTextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Primary Action: Device Storage Picker
                Button(
                    onClick = {
                        onDismiss()
                        onBrowseDeviceFiles()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("modal_browse_device_files_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoBluePrimary)
                ) {
                    Icon(imageVector = Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Browse Local Device Storage", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BentoBorderLight)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "CAMPUS PRESET TEMPLATES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextSecondary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                samplePresets.forEach { (name, ext, bytesCount) ->
                    val (icon, color) = getAttachmentVisuals(ext)
                    Surface(
                        color = BentoNeutralCard,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BentoBorderLight),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable {
                                val sampleBytes = ByteArray(bytesCount) { (it % 128).toByte() }
                                onFileSelected(name, sampleBytes)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(color.copy(alpha = 0.12f), RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BentoNavyDark)
                                    Text("${ext.uppercase()} • ${(bytesCount / 1024 / 1024.0).let { "%.1f".format(it) }} MB", fontSize = 10.sp, color = BentoTextSecondary)
                                }
                            }
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = BentoBluePrimary, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BentoBorderLight)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "CUSTOM DOCUMENT CREATOR",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextSecondary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = customFileName,
                    onValueChange = { customFileName = it },
                    placeholder = { Text("e.g. Workshop_Manual.pdf", fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_filename_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val name = if (customFileName.isNotBlank()) customFileName.trim() else "Institutional_Notice_${System.currentTimeMillis().toString().takeLast(4)}.$customExtension"
                        val size = customSizeMb * 1024 * 1024
                        val sampleBytes = ByteArray(size) { (it % 128).toByte() }
                        onFileSelected(name, sampleBytes)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("confirm_custom_file_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoNavyDark)
                ) {
                    Text("Attach Named Document", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
