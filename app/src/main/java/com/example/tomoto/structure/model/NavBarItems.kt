package com.example.tomoto.structure.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ThumbUp

object NavBarItems {
    val BarItems = listOf(
        BarItem(
            title = Routes.Timer.route,
            selectIcon = Icons.Default.CheckCircle,
            onSelectedIcon = Icons.Outlined.CheckCircle,
            route = Routes.Timer.route
        ),
        BarItem(
            title = Routes.TodoList.route,
            selectIcon = Icons.Default.DateRange,
            onSelectedIcon = Icons.Outlined.DateRange,
            route = Routes.TodoList.route
        ),
        BarItem(
            title = Routes.Rank.route,
            selectIcon = Icons.Default.ThumbUp,
            onSelectedIcon = Icons.Outlined.ThumbUp,
            route = Routes.Rank.route,
        ),
        BarItem(
            title = Routes.Settings.route,
            selectIcon = Icons.Default.Settings,
            onSelectedIcon = Icons.Outlined.Settings,
            route = Routes.Settings.route,
        )
    )
}