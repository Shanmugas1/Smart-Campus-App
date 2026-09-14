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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.model.NoticeCategory
import com.example.model.NoticePriority
import com.example.model.NoticeWithState
import com.example.ui.components.BentoHeader
import com.example.ui.components.CategoryChip
import com.example.ui.components.EmptyState
import com.example.ui.components.NoticeCard
import com.example.ui.components.UrgentAlertBanner
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
import com.example.ui.theme.BentoSkyOnContainer
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.PriorityUrgent
import com.example.viewmodel.AppScreen
import com.example.viewmodel.InboxFilter
import com.example.viewmodel.SmartCampusViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeScreen(
    viewModel: SmartCampusViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val filteredNotices by viewModel.filteredNotices.collectAsStateWithLifecycle()
    val urgentNotices by viewModel.urgentNotices.collectAsStateWithLifecycle()
    val rawNotices by viewModel.rawPersonalizedNotices.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedPriority by viewModel.selectedPriority.collectAsStateWithLifecycle()
    val inboxFilter by viewModel.inboxFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val bookmarkedCount = remember(rawNotices) { rawNotices.count { it.isBookmarked } }
    val pinnedNotices = filteredNotices.filter { it.notice.pinned }
    val regularNotices = filteredNotices.filter { !it.notice.pinned }

    // Featured hero notice: Top urgent notice or latest pinned/regular notice
    val featuredNotice = urgentNotices.firstOrNull() ?: filteredNotices.firstOrNull()

    // Spotlight notice for the Bento Grid (e.g. Placement, Exam, or second notice)
    val spotlightNotice = filteredNotices.find { it.notice.category == NoticeCategory.PLACEMENT || it.notice.category == NoticeCategory.ACADEMIC }
        ?: filteredNotices.getOrNull(1)

    // Upcoming Event / Holiday notice for the Bento Peach tile
    val eventNotice = filteredNotices.find { it.notice.category == NoticeCategory.EVENT }

    val dateFormat = remember { SimpleDateFormat("MMM dd • hh:mm a", Locale.getDefault()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackgroundLight)
            .testTag("student_home_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Bento Header with Profile Avatar
        item {
            BentoHeader(
                subtitle = "SMART CAMPUS",
                title = "Noticeboard",
                userInitials = currentUser?.name?.take(2)?.uppercase() ?: "SC",
                onAvatarClick = { viewModel.navigateTo(AppScreen.PROFILE) }
            )
        }

        // 2. Bento Grid Hub (Shown when not actively searching)
        if (searchQuery.isEmpty() && inboxFilter == InboxFilter.ALL && selectedCategory == null) {
            // HERO BENTO CARD (2 Columns Wide, 28dp radius, BentoSkyContainer)
            if (featuredNotice != null) {
                item {
                    val notice = featuredNotice.notice
                    val isUrgent = notice.priority == NoticePriority.URGENT
                    val formattedDate = dateFormat.format(Date(notice.createdAt))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .clickable {
                                viewModel.markAsRead(notice.id)
                                viewModel.navigateTo(AppScreen.NOTICE_DETAILS, notice.id)
                            }
                            .testTag("bento_hero_card"),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoSkyContainer),
                        border = BorderStroke(1.dp, BentoBluePrimary.copy(alpha = 0.3f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            color = if (isUrgent) PriorityUrgent else BentoBluePrimary,
                                            shape = RoundedCornerShape(100.dp)
                                        ) {
                                            Text(
                                                text = if (isUrgent) "URGENT" else "FEATURED",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                letterSpacing = 0.5.sp
                                            )
                                        }
                                        Text(
                                            text = notice.postedBy,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = BentoBluePrimary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = notice.title,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoNavyDark,
                                    lineHeight = 24.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = notice.content,
                                    fontSize = 13.sp,
                                    color = BentoTextSecondary,
                                    lineHeight = 18.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = formattedDate,
                                    fontSize = 12.sp,
                                    color = BentoTextSecondary,
                                    fontWeight = FontWeight.Medium
                                )

                                Surface(
                                    modifier = Modifier.size(38.dp),
                                    shape = CircleShape,
                                    color = Color.White,
                                    shadowElevation = 2.dp
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "Read announcement",
                                            tint = BentoBluePrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // BENTO GRID ROW 1: Spotlight (Lavender) on Left, Unread (Sage) & Alert (Peach) on Right
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Left: Spotlight Bento Card (Lavender Container, 28dp radius)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(160.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .clickable {
                                if (spotlightNotice != null) {
                                    viewModel.markAsRead(spotlightNotice.notice.id)
                                    viewModel.navigateTo(AppScreen.NOTICE_DETAILS, spotlightNotice.notice.id)
                                } else {
                                    viewModel.setCategoryFilter(NoticeCategory.PLACEMENT)
                                }
                            }
                            .testTag("bento_spotlight_card"),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoLavenderContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(BentoLavenderOnContainer, RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (spotlightNotice?.notice?.category == NoticeCategory.PLACEMENT) Icons.Default.Work else Icons.Default.School,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = spotlightNotice?.notice?.title ?: "Placement Drive",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoLavenderOnContainer,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = spotlightNotice?.notice?.content ?: "Campus career & recruitment alerts",
                                    fontSize = 11.sp,
                                    color = BentoLavenderOnContainer.copy(alpha = 0.75f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Right Column: Two stacked Bento Cards (Unread Sage + Alert Peach)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(160.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 1. Sage Green Unread Bento Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(24.dp))
                                .clickable { viewModel.setInboxFilter(InboxFilter.UNREAD) }
                                .testTag("bento_unread_card"),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = BentoSageContainer),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "UNREAD",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoSageOnContainer,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = unreadCount.toString().padStart(2, '0'),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black,
                                        color = BentoSageOnContainer
                                    )
                                }

                                Surface(
                                    modifier = Modifier.size(28.dp),
                                    shape = CircleShape,
                                    color = Color.White.copy(alpha = 0.6f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(BentoSageOnContainer, CircleShape)
                                        )
                                    }
                                }
                            }
                        }

                        // 2. Peach Event / Alert Bento Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(24.dp))
                                .clickable {
                                    if (eventNotice != null) {
                                        viewModel.markAsRead(eventNotice.notice.id)
                                        viewModel.navigateTo(AppScreen.NOTICE_DETAILS, eventNotice.notice.id)
                                    } else {
                                        viewModel.setCategoryFilter(NoticeCategory.EVENT)
                                    }
                                }
                                .testTag("bento_event_card"),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = BentoPeachContainer),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(BentoPeachOnContainer, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Event,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = eventNotice?.notice?.title ?: "Campus Events",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoPeachOnContainer,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // BENTO GRID ROW 2: Bookmarks Bento Tile, Cloud Vault Tile & Cohort Identity Tile
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Bookmarks Bento Tile
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(96.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .clickable { viewModel.setInboxFilter(InboxFilter.SAVED) }
                            .testTag("bento_bookmarks_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoNeutralCard),
                        border = BorderStroke(1.dp, BentoBorderLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Bookmarks",
                                tint = BentoBluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Saved",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoNavyDark
                            )
                            Text(
                                text = "$bookmarkedCount Items",
                                fontSize = 9.sp,
                                color = BentoTextSecondary
                            )
                        }
                    }

                    // Cloud Vault Tile
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(96.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .clickable { viewModel.navigateTo(AppScreen.CLOUD_STORAGE) }
                            .testTag("bento_vault_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoSkyContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudQueue,
                                contentDescription = "Cloud Storage",
                                tint = BentoNavyDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Cloud Vault",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoNavyDark
                            )
                            Text(
                                text = "PDFs & Docs",
                                fontSize = 9.sp,
                                color = BentoNavyDark.copy(alpha = 0.75f)
                            )
                        }
                    }

                    // Cohort Tag Tile
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(96.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .clickable { viewModel.navigateTo(AppScreen.PROFILE) }
                            .testTag("bento_cohort_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoNeutralCard),
                        border = BorderStroke(1.dp, BentoBorderLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(BentoBluePrimary, CircleShape)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentUser?.department ?: "CAMPUS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoNavyDark
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = currentUser?.name?.split(" ")?.firstOrNull() ?: "Student",
                                fontSize = 11.sp,
                                color = BentoTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Profile",
                                fontSize = 9.sp,
                                color = BentoBluePrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // 3. Search Bar
        item {
            Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search notices, exams, events...", fontSize = 13.sp, color = BentoTextSecondary) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = BentoNavyDark,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = BentoTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_input"),
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
        }

        // 4. Category Filter Carousel
        item {
            Column(modifier = Modifier.padding(top = 8.dp, bottom = 6.dp)) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Surface(
                            color = if (selectedCategory == null) BentoNavyDark else BentoNeutralCard,
                            shape = RoundedCornerShape(20.dp),
                            border = if (selectedCategory != null) BorderStroke(1.dp, BentoBorderLight) else null,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { viewModel.setCategoryFilter(null) }
                        ) {
                            Text(
                                text = "All Categories",
                                fontSize = 12.sp,
                                fontWeight = if (selectedCategory == null) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedCategory == null) Color.White else BentoTextSecondary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }

                    items(NoticeCategory.values()) { cat ->
                        CategoryChip(
                            category = cat,
                            isSelected = selectedCategory == cat,
                            onClick = {
                                if (selectedCategory == cat) viewModel.setCategoryFilter(null)
                                else viewModel.setCategoryFilter(cat)
                            }
                        )
                    }
                }
            }
        }

        // 5. Segmented Status Filter Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    InboxFilter.ALL to "All",
                    InboxFilter.UNREAD to "Unread ($unreadCount)",
                    InboxFilter.IMPORTANT to "Important",
                    InboxFilter.SAVED to "Saved ($bookmarkedCount)"
                ).forEach { (filter, label) ->
                    val isSelected = inboxFilter == filter
                    Surface(
                        color = if (isSelected) BentoSkyContainer else BentoNeutralCard,
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) BentoBluePrimary else BentoBorderLight
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { viewModel.setInboxFilter(filter) }
                            .testTag("inbox_tab_${filter.name.lowercase()}")
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (isSelected) BentoNavyDark else BentoTextSecondary
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // 6. Pinned Announcements (if any)
        if (pinnedNotices.isNotEmpty() && inboxFilter != InboxFilter.UNREAD) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = null,
                        tint = BentoNavyDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PINNED ANNOUNCEMENTS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoNavyDark,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            items(pinnedNotices, key = { "pinned_" + it.notice.id }) { item ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                    NoticeCard(
                        noticeWithState = item,
                        onClick = {
                            viewModel.markAsRead(item.notice.id)
                            viewModel.navigateTo(AppScreen.NOTICE_DETAILS, item.notice.id)
                        },
                        onBookmarkToggle = {
                            viewModel.toggleBookmark(item.notice.id, item.isBookmarked)
                        }
                    )
                }
            }
        }

        // 7. Regular Announcements Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (inboxFilter == InboxFilter.UNREAD) "UNREAD NOTICES" else "LATEST ANNOUNCEMENTS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextSecondary,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${filteredNotices.size} Notice${if (filteredNotices.size != 1) "s" else ""}",
                    fontSize = 11.sp,
                    color = BentoTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 8. Notice Feed / Empty State
        if (filteredNotices.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Default.Campaign,
                    title = "No Announcements Found",
                    description = if (searchQuery.isNotEmpty()) "No notices matched your query '$searchQuery'. Try adjusting your keywords or category filters."
                    else if (inboxFilter == InboxFilter.UNREAD) "You are all caught up! There are no unread notices for your cohort."
                    else "No official announcements match the selected criteria.",
                    actionText = if (selectedCategory != null || searchQuery.isNotEmpty() || inboxFilter != InboxFilter.ALL) "Reset Filters" else null,
                    onActionClick = {
                        viewModel.setCategoryFilter(null)
                        viewModel.setSearchQuery("")
                        viewModel.setInboxFilter(InboxFilter.ALL)
                    }
                )
            }
        } else {
            items(regularNotices, key = { it.notice.id }) { item ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                    NoticeCard(
                        noticeWithState = item,
                        onClick = {
                            viewModel.markAsRead(item.notice.id)
                            viewModel.navigateTo(AppScreen.NOTICE_DETAILS, item.notice.id)
                        },
                        onBookmarkToggle = {
                            viewModel.toggleBookmark(item.notice.id, item.isBookmarked)
                        }
                    )
                }
            }
        }
    }
}
