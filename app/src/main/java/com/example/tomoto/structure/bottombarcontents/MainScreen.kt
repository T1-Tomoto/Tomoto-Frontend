package com.example.tomoto.structure.bottombarcontents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.tomoto.structure.navigation.BottomNavigationBar
import com.example.tomoto.structure.navigation.NavGraph
import com.example.tomoto.structure.viewmodel.TomotoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(tomotoViewModel: TomotoViewModel = viewModel()) {

    //LaunchedEffect(Unit) {
      //  tomotoViewModel.changingTest("changedText")
   // }
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Tomoto") }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }

    ) { contentPadding ->
        Column() {
            NavGraph(navController, tomotoViewModel, paddingValues = contentPadding)
        }
    }
}