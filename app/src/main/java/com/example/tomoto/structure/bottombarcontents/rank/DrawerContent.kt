package com.example.tomoto.structure.bottombarcontents.rank

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun DrawerContent(
    navController: NavHostController,
    closeDrawer: () -> Unit
) {
    Column {
        Text(
            text = "친구 랭킹",
            modifier = Modifier.padding(16.dp).clickable {
                navController.navigate("friendRanking")
                closeDrawer()
            }
        )
        Text(
            text = "전체 랭킹",
            modifier = Modifier.padding(16.dp).clickable {
                navController.navigate("globalRanking")
                closeDrawer()
            }
        )
    }
}