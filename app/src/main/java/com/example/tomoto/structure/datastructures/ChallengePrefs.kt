package com.example.tomoto.structure.datastructures

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// 확장 함수 포함
val Context.dataStore by preferencesDataStore(name = "tomoto_prefs")

object ChallengePrefs {
    private val RESET_DATE_KEY = stringPreferencesKey("daily_challenge_reset_date")
    private val DAILY_STATES_KEY = stringPreferencesKey("daily_challenge_states")
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    suspend fun shouldResetDaily(context: Context): Boolean {
        val prefs = context.dataStore.data.first()
        val last = prefs[RESET_DATE_KEY]
        val today = LocalDate.now().format(formatter)
        return last != today
    }

    suspend fun updateResetDate(context: Context) {
        context.dataStore.edit { prefs ->
            prefs[RESET_DATE_KEY] = LocalDate.now().format(formatter)
        }
    }

    suspend fun saveDailyStates(context: Context, states: List<Boolean>) {
        val value = states.joinToString(",") { it.toString() }
        context.dataStore.edit { prefs ->
            prefs[DAILY_STATES_KEY] = value
        }
    }

    suspend fun loadDailyStates(context: Context): List<Boolean> {
        val prefs = context.dataStore.data.first()
        val value = prefs[DAILY_STATES_KEY] ?: return emptyList()
        return value.split(",").map { it.toBooleanStrictOrNull() ?: false }
    }
}

