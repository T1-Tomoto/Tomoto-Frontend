package com.example.tomoto.structure.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel

class TomotoViewModel:ViewModel() {
    private val _TestingTitle = mutableStateOf("TestTitle")
    val testList: MutableState<String>
        get() = _TestingTitle

    private var _TestingChange = mutableStateListOf("TestTitle", "TestTitle2")
    val testchange:SnapshotStateList<String>
        get() = _TestingChange

    fun changingTest(string: String){
        _TestingChange.forEachIndexed() { index, item ->
            _TestingChange[index] = "$string $index"
        }
    }
    
    val UserName = "UserName"
    val UserEmail = "tomoto@gmail.com"
    val musicList = mutableStateListOf("url1", "url2", "url3")
    val challengeList = mutableStateListOf("challenge1", "challenge2", "challenge3")


}