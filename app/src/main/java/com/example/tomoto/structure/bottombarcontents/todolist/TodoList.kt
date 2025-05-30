package com.example.tomoto.structure.bottombarcontents.todolist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tomoto.structure.viewmodel.TomotoViewModel

@Composable
fun TodoList(modifier: Modifier = Modifier, tomotoViewModel:TomotoViewModel) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Text(text = tomotoViewModel.testList.value)
            tomotoViewModel.testchange.forEach { item ->
                Text(text = item)
            }
            Button(onClick = {
                tomotoViewModel.changingTest("clickChangeTest")
            }) {
                Text("클릭하기")
            }
        }

    }
}