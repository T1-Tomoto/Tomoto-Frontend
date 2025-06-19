package com.example.tomoto.structure.datastructures

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomoto.structure.data.dto.request.UserLoginReq
import com.example.tomoto.structure.data.dto.request.UserRegisterReq
import com.example.tomoto.structure.data.service.ServicePool
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AuthViewModel : ViewModel() {
    var errorMessage by mutableStateOf("")
        private set
    fun clearErrorMessage() {
        errorMessage = ""
    }

    fun signup(id: String, password: String, nickname: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val req = UserRegisterReq(id, password, nickname)
                val response = ServicePool.userService.signup(req)
//                errorMessage = "" // 성공 시 에러 메시지 초기화
                onSuccess(response.accessToken)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string() ?: ""
                errorMessage = parseErrorMessage(errorBody) ?: "회원가입에 실패했습니다."
                Log.e("AuthViewModel", "회원가입 실패: $errorBody")
            } catch (e: Exception) {
                errorMessage = "네트워크 오류가 발생했습니다."
                Log.e("AuthViewModel", "회원가입 네트워크 오류: ${e.message}")
            }
        }
    }

    // 로그인 함수
    fun login(id: String, password: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val req = UserLoginReq(id, password)
                val response = ServicePool.userService.login(req)
                errorMessage = "" // 성공 시 에러 메시지 초기화
                onSuccess(response.accessToken)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string() ?: ""
                errorMessage = parseErrorMessage(errorBody) ?: "로그인에 실패했습니다."
                Log.e("AuthViewModel", "로그인 실패: $errorBody")
            } catch (e: Exception) {
                errorMessage = "네트워크 오류가 발생했습니다."
                Log.e("AuthViewModel", "로그인 네트워크 오류: ${e.message}")
            }
        }
    }

    private fun parseErrorMessage(errorBody: String): String? {
        return errorBody.takeIf { it.isNotBlank() }?.let {
            if (it.contains("이미 존재하는 아이디입니다.")) "이미 존재하는 아이디입니다."
            else if (it.contains("존재하지 않는 회원입니다.")) "존재하지 않는 회원입니다."
            else if (it.contains("비밀번호가 일치하지 않습니다.")) "비밀번호가 일치하지 않습니다."
            else null
        }
    }
}