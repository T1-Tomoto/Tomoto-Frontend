package com.example.tomoto.structure.datastructures

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomoto.structure.data.dto.response.DailyPomoCountDto
import com.example.tomoto.structure.data.service.ServicePool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class PomoViewModel: ViewModel() {

    private val _pomoHistory = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())
    val pomoHistory: StateFlow<Map<LocalDate, Int>> = _pomoHistory

    fun fetchPomoHistory() {
        Log.i("1","11s")
        viewModelScope.launch {
            try {
                val response: List<DailyPomoCountDto> = ServicePool.pomoService.getAllPomoHistory()
                val mapped = response.groupBy { it.createdAt.toLocalDate() }
                    .mapValues { (_, list) -> list.sumOf { it.pomoNum } }
                _pomoHistory.value = mapped
                Log.d("여기","악")
            } catch (e: Exception) {
                Log.e("PomoHistory", "실패: ${e.message}")
            }
        }
    }
}