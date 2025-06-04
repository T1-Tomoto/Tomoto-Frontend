package com.example.tomoto.structure.bottombarcontents.settings.uiconponents

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun MusicCard(
    url: String,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val videoId = remember(url) { extractYoutubeId(url) }
    val thumbnailUrl = "https://img.youtube.com/vi/$videoId/0.jpg"

    // 메뉴 상태
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 썸네일
            if (videoId.isNotBlank()) {
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = "YouTube Thumbnail",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text("썸네일 없음", color = Color.Gray)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // URL 텍스트
            Text(
                text = url,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )

            // 점 세 개 메뉴 버튼
            Box {
                IconButton(
                    onClick = { expanded = true },
                    modifier = Modifier.size(32.dp) // 조금 작게
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "메뉴"
                    )
                }

                // 메뉴 UI
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("수정") },
                        onClick = {
                            expanded = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("삭제") },
                        onClick = {
                            expanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}


fun extractYoutubeId(url: String): String {
    return try {
        val uri = Uri.parse(url)
        when {
            uri.host == "youtu.be" -> uri.lastPathSegment ?: ""
            uri.host?.contains("youtube.com") == true -> uri.getQueryParameter("v") ?: ""
            else -> ""
        }
    } catch (e: Exception) {
        ""
    }
}

