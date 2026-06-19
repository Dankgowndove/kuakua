package com.calldad.boast.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

class AppPreferences(private val context: Context) {
    
    private object PreferencesKeys {
        val MUSIC_ENABLED = booleanPreferencesKey("music_enabled")
        val MUSIC_VOLUME = floatPreferencesKey("music_volume")
        val CURRENT_MUSIC_INDEX = intPreferencesKey("current_music_index")
        val BACKGROUND_TYPE = stringPreferencesKey("background_type")
        val BACKGROUND_COLOR = stringPreferencesKey("background_color")
        val BACKGROUND_GRADIENT = stringPreferencesKey("background_gradient")
        val BACKGROUND_IMAGE_PATH = stringPreferencesKey("background_image_path")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val THEME_COLOR = stringPreferencesKey("theme_color")
    }
    
    val musicEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.MUSIC_ENABLED] ?: true
        }
    
    val musicVolume: Flow<Float> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.MUSIC_VOLUME] ?: 0.5f
        }
    
    val currentMusicIndex: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.CURRENT_MUSIC_INDEX] ?: 0
        }
    
    val backgroundType: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.BACKGROUND_TYPE] ?: "solid"
        }
    
    val backgroundColor: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.BACKGROUND_COLOR] ?: "#6200EE"
        }
    
    val backgroundGradient: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.BACKGROUND_GRADIENT] ?: "linear|#6200EE|#3700B3"
        }
    
    val backgroundImagePath: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.BACKGROUND_IMAGE_PATH]
        }
    
    val themeMode: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.THEME_MODE] ?: "default"
        }
    
    val themeColor: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.THEME_COLOR] ?: "default"
        }
    
    suspend fun setMusicEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.MUSIC_ENABLED] = enabled
        }
    }
    
    suspend fun setMusicVolume(volume: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.MUSIC_VOLUME] = volume
        }
    }
    
    suspend fun setCurrentMusicIndex(index: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENT_MUSIC_INDEX] = index
        }
    }
    
    suspend fun setBackgroundType(type: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BACKGROUND_TYPE] = type
        }
    }
    
    suspend fun setBackgroundColor(color: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BACKGROUND_COLOR] = color
        }
    }
    
    suspend fun setBackgroundGradient(gradient: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BACKGROUND_GRADIENT] = gradient
        }
    }
    
    suspend fun setBackgroundImagePath(path: String?) {
        context.dataStore.edit { preferences ->
            if (path != null) {
                preferences[PreferencesKeys.BACKGROUND_IMAGE_PATH] = path
            } else {
                preferences.remove(PreferencesKeys.BACKGROUND_IMAGE_PATH)
            }
        }
    }
    
    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode
        }
    }
    
    suspend fun setThemeColor(color: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_COLOR] = color
        }
    }
}