package com.example.tomoto.structure.bottombarcontents.settings.uiconponents

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UserInfoTopAppBar(
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    titleText : String? = null
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp) // 💥 여기서 높이 조절 가능
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBackButton && onBackClick != null) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기"
                )

            }
            Spacer(modifier = Modifier.width(4.dp))
            if(titleText != null)
                Text(text = "$titleText", style = MaterialTheme.typography.titleLarge)
        }

    }

}
