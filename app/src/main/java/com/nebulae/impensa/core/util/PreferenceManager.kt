package com.nebulae.impensa.core.util

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class PreferencesManager(private val context: Context) {

    companion object {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val CATEGORY_MAP = stringPreferencesKey("category_map")
        val STATS_SCREEN_STATE = stringPreferencesKey("stats_screen_state")
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_THEME] = enabled
        }
    }

    val darkThemeFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs ->
            prefs[DARK_THEME] ?: false
        }

    suspend fun saveCategoryMap(categoryMap: Map<String, Color>) {
        val json = JSONObject()
        categoryMap.forEach { (category, color) ->
            json.put(category, color.toArgb())
        }
        context.dataStore.edit { prefs ->
            prefs[CATEGORY_MAP] = json.toString()
        }
    }

    suspend fun initializeDefaultCategoriesIfNeeded(defaultMap: Map<String, Color>) {
        val prefs = context.dataStore.data.first()
        if (!prefs.contains(CATEGORY_MAP)) {
            saveCategoryMap(defaultMap)
        }
    }

    val savedCategoryFlow: Flow<Map<String, Color>> = context.dataStore.data
        .map { prefs ->
            val map = mutableMapOf<String, Color>()
            prefs[CATEGORY_MAP]?.let { jsonString ->
                val json = JSONObject(jsonString)
                for (key in json.keys()) {
                    map[key] = Color(json.getInt(key))
                }
            }
            map
        }

    suspend fun setStatsScreenState(state: Int) {
        context.dataStore.edit { prefs ->
            prefs[STATS_SCREEN_STATE] = state.toString()
        }
    }

    val savedStatsScreenStateFlow: Flow<Int> = context.dataStore.data
        .map {
            it[STATS_SCREEN_STATE]?.toIntOrNull() ?: 0
        }

}