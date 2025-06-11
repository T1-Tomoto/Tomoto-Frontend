package com.example.tomoto.structure.datastructures

import androidx.compose.runtime.snapshots.SnapshotStateList

object MusicManager {
    fun addMusicUrl(list: SnapshotStateList<String>, url: String) {
        if (url.isNotBlank()) {
            list.add(url.trim())
        }
    }

    fun removeMusicUrl(list: SnapshotStateList<String>, url: String) {
        list.remove(url)
    }

    fun editMusicUrl(list: SnapshotStateList<String>, oldUrl: String, newUrl: String) {
        val index = list.indexOf(oldUrl)
        if (index != -1 && newUrl.isNotBlank()) {
            list[index] = newUrl.trim()
        }
    }
}
