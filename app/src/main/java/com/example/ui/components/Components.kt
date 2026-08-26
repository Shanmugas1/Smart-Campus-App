package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Notice
import com.example.model.NoticeCategory
import com.example.model.NoticePriority
import com.example.model.NoticeWithState
import com.example.service.AudienceEngine
import com.example.ui.theme.BentoBackgroundLight
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.theme.BentoBluePrimary
import com.example.ui.theme.BentoBorderBlue
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
import com.example.ui.theme.BentoSkyOnContainer
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.BentoTextTertiary
import com.example.ui.theme.CategoryAcademic
import com.example.ui.theme.CategoryEmergency
import com.example.ui.theme.CategoryEvent
import com.example.ui.theme.CategoryExamination
import com.example.ui.theme.CategoryGeneral
import com.example.ui.theme.CategoryPlacement
import com.example.ui.theme.PriorityImportant
import com.example.ui.theme.PriorityNormal
import com.example.ui.theme.PriorityUrgent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun getCategoryColor(category: NoticeCategory): Color {
    return when (category) {
        NoticeCategory.ACADEMIC -> CategoryAcademic
        NoticeCategory.EXAMINATION -> CategoryExamination
        NoticeCategory.PLACEMENT -> CategoryPlacement
        NoticeCategory.EVENT -> CategoryEvent
        NoticeCategory.EMERGENCY -> CategoryEmergency
        NoticeCategory.GENERAL -> CategoryGeneral
    }
}

@Composable
fun getCategoryIcon(category: NoticeCategory): ImageVector {
    return when (category) {
        NoticeCategory.ACADEMIC -> Icons.Default.School
        NoticeCategory.EXAMINATION -> Icons.Default.Assignment
        NoticeCategory.PLACEMENT -> Icons.Default.Work
        NoticeCategory.EVENT -> Icons.Default.Event
        NoticeCategory.EMERGENCY -> Icons.Default.Warning
        NoticeCategory.GENERAL -> Icons.Default.Campaign
    }
}

/**
 * Bento Header matching the Bento Grid theme specifications
 */
@Composable
fun BentoHeader(
    subtitle: String = "SMART CAMPUS",
    title: String = "Noticeboard",
    userInitials: String = "AS",
    onAvatarClick: () -> Unit = {},
    showLogo: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (showLogo) {
                SmartCampusBrandLogo(
                    size = 42.dp,
                    showContainer = true,
                    modifier = Modifier.testTag("bento_header_logo")
                )
            }
            Column {
                Text(
                    text = subtitle.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextSecondary,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoNavyDark,
                    letterSpacing = (-0.5).sp
                )
            }
        }

        // Circular Avatar Badge with white border and soft shadow
        Surface(
            modifier = Modifier
                .size(44.dp)
                .clickable { onAvatarClick() }
                .testTag("bento_avatar_badge"),
            shape = CircleShape,
            color = BentoSkyContainer,
            border = BorderStroke(2.dp, Color.White),
            shadowElevation = 2.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = userInitials.take(2).uppercase(),
                    color = BentoNavyDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

/**
 * Bento Priority Badge
 */
@Composable
fun PriorityBadge(priority: NoticePriority) {
    val (bgColor, textColor, label) = when (priority) {
        NoticePriority.URGENT -> Triple(
            PriorityUrgent,
            Color.White,
            "URGENT"
        )
        NoticePriority.IMPORTANT -> Triple(
            BentoBluePrimary,
            Color.White,
            "IMPORTANT"
        )
        NoticePriority.NORMAL -> Triple(
            BentoSkyContainer,
            BentoNavyDark,
            "NORMAL"
        )
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(100.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

/**
 * Bento Category Chip
 */
@Composable
fun CategoryChip(
    category: NoticeCategory,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val catColor = getCategoryColor(category)
    val icon = getCategoryIcon(category)

    val surfaceColor = if (isSelected) catColor else BentoNeutralCard
    val contentColor = if (isSelected) Color.White else catColor

    Surface(
        color = surfaceColor,
        shape = RoundedCornerShape(20.dp),
        border = if (!isSelected) BorderStroke(1.dp, BentoBorderLight) else null,
        modifier = Modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .testTag("category_chip_${category.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = category.displayName,
                color = contentColor,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
            )
        }
    }
}

/**
 * Bento Audience Chip
 */
@Composable
fun AudienceChip(target: String) {
    val label = AudienceEngine.formatTargetLabel(target)
    Surface(
        color = BentoSkyContainer.copy(alpha = 0.7f),
        shape = RoundedCornerShape(100.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = null,
                tint = BentoNavyDark,
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = BentoNavyDark,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Bento Grid Tile Notice Card (28dp corners, pastel container, high-contrast typography)
 */
@Composable
fun NoticeCard(
    noticeWithState: NoticeWithState,
    onClick: () -> Unit,
    onBookmarkToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val notice = noticeWithState.notice
    val isRead = noticeWithState.isRead
    val isBookmarked = noticeWithState.isBookmarked
    val catColor = getCategoryColor(notice.category)
    val catIcon = getCategoryIcon(notice.category)

    val dateFormat = remember { SimpleDateFormat("MMM dd • hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(notice.createdAt) { dateFormat.format(Date(notice.createdAt)) }

    val targets = remember(notice.targetAudience) { AudienceEngine.parseTargets(notice.targetAudience) }
    val hasAttachments = notice.attachmentsJson != "[]" && notice.attachmentsJson.isNotEmpty()

    // Card background adapts smoothly to priority or status
    val cardBackground = when {
        notice.priority == NoticePriority.URGENT -> BentoSkyContainer.copy(alpha = 0.85f)
        notice.category == NoticeCategory.EXAMINATION -> BentoLavenderContainer.copy(alpha = 0.7f)
        notice.category == NoticeCategory.PLACEMENT -> BentoSageContainer.copy(alpha = 0.6f)
        notice.category == NoticeCategory.EVENT -> BentoPeachContainer.copy(alpha = 0.7f)
        isRead -> BentoNeutralCard
        else -> Color.White
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .clickable { onClick() }
            .testTag("notice_card_${notice.id}"),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(
            1.dp,
            if (notice.priority == NoticePriority.URGENT) BentoBluePrimary.copy(alpha = 0.4f)
            else if (notice.pinned) BentoBluePrimary.copy(alpha = 0.3f)
            else BentoBorderLight
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isRead) 0.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Header Row: Category Pill, Priority Pill, Pin & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Priority Pill
                    if (notice.priority != NoticePriority.NORMAL) {
                        PriorityBadge(priority = notice.priority)
                    }

                    // Poster / Department Tag
                    Text(
                        text = notice.postedBy,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (notice.priority == NoticePriority.URGENT) BentoBluePrimary else BentoTextSecondary
                    )

                    // Pinned Indicator
                    if (notice.pinned) {
                        Surface(
                            color = BentoBluePrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = "Pinned",
                                    tint = BentoBluePrimary,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "PIN",
                                    color = BentoBluePrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Bookmark & Unread State
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(BentoBluePrimary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("bookmark_button_${notice.id}")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (isBookmarked) "Remove bookmark" else "Bookmark notice",
                            tint = if (isBookmarked) BentoNavyDark else BentoTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Notice Title
            Text(
                text = notice.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = BentoNavyDark,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Content snippet
            Text(
                text = notice.content,
                fontSize = 13.sp,
                color = BentoTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom metadata row with Audience Chips, Date, and Action Chevron
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Audience chip & Attachments
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val primaryTarget = targets.firstOrNull() ?: "ALL"
                    AudienceChip(target = primaryTarget)
                    if (targets.size > 1) {
                        Surface(
                            color = BentoNavyDark.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text(
                                text = "+${targets.size - 1}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoNavyDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (hasAttachments) {
                        val attachmentBadgeText = remember(notice.attachmentsJson) {
                            try {
                                val arr = org.json.JSONArray(notice.attachmentsJson)
                                when (arr.length()) {
                                    1 -> {
                                        val firstObj = arr.getJSONObject(0)
                                        val type = firstObj.optString("fileType", "FILE")
                                        type
                                    }
                                    else -> "${arr.length()} Files"
                                }
                            } catch (e: Exception) {
                                "Files"
                            }
                        }

                        Surface(
                            color = BentoSkyContainer.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AttachFile,
                                    contentDescription = "Attachment",
                                    tint = BentoBluePrimary,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = attachmentBadgeText,
                                    fontSize = 9.sp,
                                    color = BentoNavyDark,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Date and Right Arrow
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = formattedDate,
                        fontSize = 11.sp,
                        color = BentoTextSecondary,
                        fontWeight = FontWeight.Medium
                    )

                    Surface(
                        modifier = Modifier.size(28.dp),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 1.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Open notice",
                                tint = BentoNavyDark,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Bento Urgent Alert Banner (28dp corners, high visibility)
 */
@Composable
fun UrgentAlertBanner(
    urgentNotice: NoticeWithState,
    onViewNotice: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .clickable { onViewNotice() }
            .testTag("urgent_alert_banner"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = BentoSkyContainer),
        border = BorderStroke(1.5.dp, BentoBluePrimary.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(BentoBluePrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationImportant,
                    contentDescription = "Urgent Announcement",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = BentoBluePrimary,
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Text(
                            text = "URGENT",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = urgentNotice.notice.postedBy,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoNavyDark
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = urgentNotice.notice.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoNavyDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Surface(
                modifier = Modifier.size(34.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open Alert",
                        tint = BentoNavyDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Bento Grid Stat Card (28dp corners)
 */
@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = BentoNeutralCard),
        border = BorderStroke(1.dp, BentoBorderLight)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextSecondary,
                    letterSpacing = 0.5.sp
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = BentoNavyDark
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = accentColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .background(BentoSkyContainer.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BentoNavyDark,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = BentoNavyDark
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = description,
            fontSize = 13.sp,
            color = BentoTextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 18.sp
        )

        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onActionClick,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, BentoBluePrimary)
            ) {
                Text(actionText, fontSize = 13.sp, color = BentoBluePrimary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/**
 * Expandable Interactive Audience Tree Selector with Bento styling
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AudienceTreeSelector(
    selectedTargets: Set<String>,
    onTargetsChanged: (Set<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val isEntireCollege = selectedTargets.contains("ALL")

    val expandedDepts = remember { mutableStateMapOf<String, Boolean>() }
    val expandedYears = remember { mutableStateMapOf<String, Boolean>() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(BentoNeutralCard)
            .padding(16.dp)
    ) {
        Text(
            text = "Target Audience Hierarchy",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = BentoNavyDark
        )
        Text(
            text = "Select scope: College, Departments, Academic Years, or Specific Sections",
            fontSize = 11.sp,
            color = BentoTextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Level 0: Entire College
        Surface(
            color = if (isEntireCollege) BentoSkyContainer else Color.White,
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(
                1.dp,
                if (isEntireCollege) BentoBluePrimary else BentoBorderLight
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isEntireCollege) {
                            onTargetsChanged(emptySet())
                        } else {
                            onTargetsChanged(setOf("ALL"))
                        }
                    }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isEntireCollege,
                    onCheckedChange = { checked ->
                        if (checked) onTargetsChanged(setOf("ALL")) else onTargetsChanged(emptySet())
                    },
                    colors = CheckboxDefaults.colors(checkedColor = BentoBluePrimary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Entire College (All Students & Departments)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoNavyDark
                    )
                    Text(
                        text = "Broadcasts to all ~2,880 enrolled students across all branches",
                        fontSize = 10.sp,
                        color = BentoTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // If not entire college, show Department Tree
        AnimatedVisibility(visible = !isEntireCollege) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AudienceEngine.INITIAL_DEPARTMENTS.forEach { (code, fullName) ->
                    val isDeptSelected = selectedTargets.contains(code)
                    val isExpanded = expandedDepts[code] == true

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(
                            1.dp,
                            if (isDeptSelected) BentoBluePrimary else BentoBorderLight
                        )
                    ) {
                        Column {
                            // Department Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isDeptSelected,
                                    onCheckedChange = { checked ->
                                        val updated = selectedTargets.toMutableSet()
                                        if (checked) {
                                            updated.removeAll { it.startsWith("$code|") }
                                            updated.add(code)
                                        } else {
                                            updated.remove(code)
                                        }
                                        onTargetsChanged(updated)
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = BentoBluePrimary)
                                )
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            expandedDepts[code] = !isExpanded
                                        }
                                ) {
                                    Text(
                                        text = "$code ($fullName)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoNavyDark
                                    )
                                }

                                IconButton(
                                    onClick = { expandedDepts[code] = !isExpanded },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = "Expand",
                                        tint = BentoTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Sub-tree: Years
                            if (isExpanded) {
                                Column(
                                    modifier = Modifier
                                        .padding(start = 32.dp, end = 12.dp, bottom = 8.dp)
                                        .animateContentSize()
                                ) {
                                    AudienceEngine.STANDARD_YEARS.forEach { yr ->
                                        val yearTargetKey = "$code|$yr"
                                        val isYearSelected = isDeptSelected || selectedTargets.contains(yearTargetKey)
                                        val isYearExpanded = expandedYears[yearTargetKey] == true

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = isYearSelected,
                                                enabled = !isDeptSelected,
                                                onCheckedChange = { checked ->
                                                    val updated = selectedTargets.toMutableSet()
                                                    if (checked) {
                                                        updated.removeAll { it.startsWith("$yearTargetKey|") }
                                                        updated.add(yearTargetKey)
                                                    } else {
                                                        updated.remove(yearTargetKey)
                                                    }
                                                    onTargetsChanged(updated)
                                                },
                                                colors = CheckboxDefaults.colors(checkedColor = BentoBluePrimary)
                                            )
                                            Text(
                                                text = yr,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = BentoNavyDark,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable {
                                                        expandedYears[yearTargetKey] = !isYearExpanded
                                                    }
                                            )
                                            IconButton(
                                                onClick = { expandedYears[yearTargetKey] = !isYearExpanded },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (isYearExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                    contentDescription = "Expand Year",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        // Sub-tree: Sections
                                        if (isYearExpanded) {
                                            Row(
                                                modifier = Modifier
                                                    .padding(start = 28.dp, bottom = 4.dp)
                                                    .fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                AudienceEngine.STANDARD_SECTIONS.forEach { sec ->
                                                    val secTargetKey = "$code|$yr|$sec"
                                                    val isSecSelected = isYearSelected || selectedTargets.contains(secTargetKey)

                                                    Surface(
                                                        color = if (isSecSelected) BentoSkyContainer else BentoNeutralCard,
                                                        shape = RoundedCornerShape(100.dp),
                                                        border = BorderStroke(1.dp, BentoBorderLight),
                                                        modifier = Modifier.clickable(enabled = !isYearSelected) {
                                                            val updated = selectedTargets.toMutableSet()
                                                            if (selectedTargets.contains(secTargetKey)) {
                                                                updated.remove(secTargetKey)
                                                            } else {
                                                                updated.add(secTargetKey)
                                                            }
                                                            onTargetsChanged(updated)
                                                        }
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            if (isSecSelected) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Check,
                                                                    contentDescription = null,
                                                                    tint = BentoNavyDark,
                                                                    modifier = Modifier.size(10.dp)
                                                                )
                                                                Spacer(modifier = Modifier.width(3.dp))
                                                            }
                                                            Text(
                                                                text = sec.replace("Section ", "Sec "),
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = if (isSecSelected) BentoNavyDark else BentoTextSecondary
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
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Compressed chips preview
        val compressed = AudienceEngine.compressTargets(selectedTargets)
        val estimatedCount = AudienceEngine.estimateRecipients(compressed)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Target Resolution (${compressed.size} Scope${if (compressed.size > 1) "s" else ""})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BentoTextSecondary
            )
            Text(
                text = "Est. Recipients: ~$estimatedCount students",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BentoBluePrimary
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            compressed.forEach { targetKey ->
                Surface(
                    color = BentoSkyContainer,
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AudienceEngine.formatTargetLabel(targetKey),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoNavyDark
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove",
                            modifier = Modifier
                                .size(12.dp)
                                .clickable {
                                    val updated = selectedTargets.toMutableSet()
                                    updated.remove(targetKey)
                                    onTargetsChanged(updated)
                                },
                            tint = BentoNavyDark
                        )
                    }
                }
            }
        }
    }
}

/**
 * Notice Read Analytics Dialog Modal
 */
@Composable
fun ReadAnalyticsDialog(
    analyticsData: com.example.viewmodel.NoticeAnalyticsData,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BentoBorderLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(BentoSkyContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = BentoNavyDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Notice Analytics",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoNavyDark
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = analyticsData.notice.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoNavyDark,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { (analyticsData.readRatePercent / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = BentoBluePrimary,
                    trackColor = BentoSkyContainer
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Read Rate",
                        fontSize = 12.sp,
                        color = BentoTextSecondary
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f%%", analyticsData.readRatePercent),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoBluePrimary
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Aggregated stats grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatPill(
                        label = "Targeted",
                        count = "${analyticsData.targetedCount}",
                        containerColor = BentoSkyContainer,
                        textColor = BentoNavyDark,
                        modifier = Modifier.weight(1f)
                    )
                    StatPill(
                        label = "Read",
                        count = "${analyticsData.readCount}",
                        containerColor = BentoSageContainer,
                        textColor = BentoSageOnContainer,
                        modifier = Modifier.weight(1f)
                    )
                    StatPill(
                        label = "Unread",
                        count = "${analyticsData.unreadCount}",
                        containerColor = BentoPeachContainer,
                        textColor = BentoPeachOnContainer,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Delivered according to institutional audience hierarchy. Only students matching ${analyticsData.notice.targetAudience} have access.",
                    fontSize = 11.sp,
                    color = BentoTextSecondary,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoNavyDark)
                ) {
                    Text("Close Analytics", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    count: String,
    containerColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textColor.copy(alpha = 0.8f)
            )
        }
    }
}

/**
 * Official Smart Campus Logo Emblem
 * Represents graduation cap, mobile notification portal, role badges, academic building,
 * database cloud storage, open study book, and the "CONNECT • LEARN • STAY INFORMED" crest.
 */
@Composable
fun SmartCampusBrandLogo(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 48.dp,
    showContainer: Boolean = false
) {
    if (showContainer) {
        Surface(
            modifier = modifier.size(size),
            shape = CircleShape,
            color = Color(0xFF0F172A),
            border = BorderStroke(1.5.dp, Color(0xFF38BDF8))
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(size * 0.08f)) {
                Image(
                    painter = painterResource(id = R.drawable.ic_smart_campus_logo),
                    contentDescription = "Smart Campus Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    } else {
        Image(
            painter = painterResource(id = R.drawable.ic_smart_campus_logo),
            contentDescription = "Smart Campus Logo",
            modifier = modifier.size(size)
        )
    }
}

/**
 * Full branded header with the official emblem, title, and tagline
 */
@Composable
fun SmartCampusEmblemHero(
    modifier: Modifier = Modifier,
    logoSize: androidx.compose.ui.unit.Dp = 100.dp,
    showTagline: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SmartCampusBrandLogo(
            size = logoSize,
            showContainer = true,
            modifier = Modifier.testTag("official_smart_campus_logo")
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SMART ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp
            )
            Text(
                text = "CAMPUS",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF38BDF8),
                letterSpacing = 1.sp
            )
        }

        if (showTagline) {
            Spacer(modifier = Modifier.height(3.dp))
            Surface(
                color = Color(0xFF0369A1).copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(0.5.dp, Color(0xFF38BDF8).copy(alpha = 0.4f))
            ) {
                Text(
                    text = "CONNECT • LEARN • STAY INFORMED",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFBAE6FD),
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
        }
    }
}

