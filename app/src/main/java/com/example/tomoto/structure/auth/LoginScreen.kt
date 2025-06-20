package com.example.tomoto.structure.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tomoto.structure.bottombarcontents.settings.uiconponents.ErrorDialog
import com.example.tomoto.structure.datastructures.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onSignupClick: () -> Unit = {},
    errorMessage: String
) {
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    // 토마토 색상 팔레트
    val tomatoRed = Color(0xFFFF6347)
    val tomatoOrange = Color(0xFFFF8C00)
    val creamWhite = Color(0xFFFFF8E7)
    val warmBrown = Color(0xFF8B4513)

    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotBlank()) {
            showDialog = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(creamWhite)
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "로그인 하기",
            style = MaterialTheme.typography.headlineMedium,
            color = tomatoRed,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("ID", color = warmBrown) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = tomatoRed,
                unfocusedBorderColor = tomatoOrange,
                focusedLabelColor = tomatoRed,
                cursorColor = tomatoRed
            )
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("PW", color = warmBrown) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = tomatoRed,
                unfocusedBorderColor = tomatoOrange,
                focusedLabelColor = tomatoRed,
                cursorColor = tomatoRed
            )
        )

        Text(
            text = "아직 회원이 아니신가요?",
            style = MaterialTheme.typography.bodyMedium,
            color = warmBrown,
            modifier = Modifier.padding(top = 8.dp)
        )

        TextButton(onClick = onSignupClick) {
            Text(
                text = "회원가입하러 가기",
                color = tomatoOrange,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { onLoginClick(id, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = tomatoRed,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "로그인",
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (showDialog) {
        ErrorDialog(
            errorMessage = "다시 입력해주세요",
            onDismiss = { showDialog = false }
        )
    }
}
