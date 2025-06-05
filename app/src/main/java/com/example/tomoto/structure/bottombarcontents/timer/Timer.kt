package com.example.tomoto.structure.bottombarcontents.timer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tomoto.structure.viewmodel.TomotoViewModel

@Composable
fun Timer(modifier: Modifier = Modifier, tomotoViewModel: TomotoViewModel) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
           // tomotoViewModel.testchange.forEach{item ->
             //   Text(item)
            }
        }

    }
