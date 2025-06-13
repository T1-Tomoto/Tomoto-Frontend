package com.example.tomoto.structure.datastructures

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomoto.structure.data.dto.request.AddFriendReq
import com.example.tomoto.structure.data.dto.response.AllUserRankRes
import com.example.tomoto.structure.data.dto.response.FriendsRankRes
import com.example.tomoto.structure.data.service.ServicePool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FriendsViewModel : ViewModel() {

    private val _userRanking = MutableStateFlow<List<AllUserRankRes>>(emptyList())
    val userRanking: StateFlow<List<AllUserRankRes>> = _userRanking

    private val _friendsRanking = MutableStateFlow<List<FriendsRankRes>>(emptyList())
    val friendsRanking: StateFlow<List<FriendsRankRes>> = _friendsRanking

    fun fetchAllUserRanking() {
        viewModelScope.launch {
            try {
                //TODO: 받아온 전체 랭킹 정보 처리
                val ranking = ServicePool.rankService.getAllUserRanking()
                Log.i("전체 랭킹",ranking.toString())
                _userRanking.value = ranking
            } catch (e: Exception) {
                Log.e("Ranking", "에러: ${e.message}")
            }
        }
    }

    fun fetchFriendsRanking() {
        viewModelScope.launch {
            try {
                //TODO: 받아온 친구 랭킹 정보 처리
                val friendsRanking = ServicePool.rankService.getFriendsRanking()
                Log.i("친구 랭킹",friendsRanking.toString())
                _friendsRanking.value = friendsRanking
            } catch (e: Exception) {
                Log.e("friendsRanking", "에러: ${e.message}")
            }
        }
    }

    fun fetchAddFriend( addFriendReq: AddFriendReq) {
        viewModelScope.launch {
            try {
                ServicePool.rankService.addFriend(addFriendReq)
                Log.i("유저 {}가 {}를 친구로 추가했습니다.", addFriendReq.friendName);
                Log.i("친구 추가", "${addFriendReq.friendName} 친구 추가 성공")
                fetchFriendsRanking()

            } catch (e: Exception) {
                Log.e("친구 추가 에러", e.message ?: "예외 발생")
            }
        }
    }

    fun fetchDeleteFriend(nickname: String){
        //TODO: 백엔드 로직. 안 건들이셔도 돼요
    }
}