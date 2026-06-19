package com.calldad.boast.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.calldad.boast.data.database.ComplimentDatabase
import com.calldad.boast.data.database.ComplimentEntity
import com.calldad.boast.data.preferences.AppPreferences
import com.calldad.boast.music.MusicPlayer
import com.calldad.boast.music.MusicRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val appPreferences = AppPreferences(application)
    private val musicPlayer = MusicPlayer(application)
    private val musicRepository = MusicRepository()
    private val database = ComplimentDatabase.getDatabase(application)
    
    private val _musicEnabled = MutableStateFlow(true)
    val musicEnabled: StateFlow<Boolean> = _musicEnabled.asStateFlow()
    
    private val _musicVolume = MutableStateFlow(0.5f)
    val musicVolume: StateFlow<Float> = _musicVolume.asStateFlow()
    
    private val _currentMusicIndex = MutableStateFlow(0)
    val currentMusicIndex: StateFlow<Int> = _currentMusicIndex.asStateFlow()
    
    private val _backgroundType = MutableStateFlow("solid")
    val backgroundType: StateFlow<String> = _backgroundType.asStateFlow()
    
    private val _backgroundColor = MutableStateFlow("#6200EE")
    val backgroundColor: StateFlow<String> = _backgroundColor.asStateFlow()
    
    private val _backgroundGradient = MutableStateFlow("linear|#6200EE|#3700B3")
    val backgroundGradient: StateFlow<String> = _backgroundGradient.asStateFlow()
    
    private val _backgroundImagePath = MutableStateFlow<String?>(null)
    val backgroundImagePath: StateFlow<String?> = _backgroundImagePath.asStateFlow()
    
    private val _themeMode = MutableStateFlow("default")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()
    
    private val _themeColor = MutableStateFlow("default")
    val themeColor: StateFlow<String> = _themeColor.asStateFlow()
    
    val builtinCount: StateFlow<Int> = database.complimentDao().getBuiltinCount()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)
    
    val customCount: StateFlow<Int> = database.complimentDao().getCustomCount()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)
    
    val totalCount: StateFlow<Int> = database.complimentDao().getTotalCount()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)
    
    val customCompliments: StateFlow<List<ComplimentEntity>> = database.complimentDao().getCustomCompliments()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    init {
        loadSettings()
        musicPlayer.initialize()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            // 分别收集各个设置，避免过度重组
            launch {
                appPreferences.musicEnabled.collectLatest { enabled ->
                    _musicEnabled.value = enabled
                    if (enabled) {
                        musicPlayer.setVolume(_musicVolume.value)
                        musicPlayer.playMusic(_currentMusicIndex.value)
                    } else {
                        musicPlayer.pauseMusic()
                    }
                }
            }
            
            launch {
                appPreferences.musicVolume.collectLatest { volume ->
                    _musicVolume.value = volume
                    if (_musicEnabled.value) {
                        musicPlayer.setVolume(volume)
                    }
                }
            }
            
            launch {
                appPreferences.currentMusicIndex.collectLatest { index ->
                    _currentMusicIndex.value = index
                    if (_musicEnabled.value) {
                        musicPlayer.playMusic(index)
                    }
                }
            }
            
            launch {
                appPreferences.backgroundType.collectLatest { bgType ->
                    _backgroundType.value = bgType
                }
            }
            
            launch {
                appPreferences.backgroundColor.collectLatest { bgColor ->
                    _backgroundColor.value = bgColor
                }
            }
            
            launch {
                appPreferences.backgroundGradient.collectLatest { bgGradient ->
                    _backgroundGradient.value = bgGradient
                }
            }
            
            launch {
                appPreferences.backgroundImagePath.collectLatest { bgImage ->
                    _backgroundImagePath.value = bgImage
                }
            }
            
            launch {
                appPreferences.themeMode.collectLatest { mode ->
                    _themeMode.value = mode
                }
            }
            
            launch {
                appPreferences.themeColor.collectLatest { color ->
                    _themeColor.value = color
                }
            }
        }
    }
    
    fun toggleMusic() {
        viewModelScope.launch {
            val newValue = !_musicEnabled.value
            _musicEnabled.value = newValue
            appPreferences.setMusicEnabled(newValue)
            if (newValue) {
                musicPlayer.playMusic(_currentMusicIndex.value)
            } else {
                musicPlayer.pauseMusic()
            }
        }
    }
    
    fun setMusicVolume(volume: Float) {
        viewModelScope.launch {
            _musicVolume.value = volume
            appPreferences.setMusicVolume(volume)
            musicPlayer.setVolume(volume)
        }
    }
    
    fun setCurrentMusicIndex(index: Int) {
        viewModelScope.launch {
            _currentMusicIndex.value = index
            appPreferences.setCurrentMusicIndex(index)
            if (_musicEnabled.value) {
                musicPlayer.playMusic(index)
            }
        }
    }
    
    fun setBackgroundType(type: String) {
        viewModelScope.launch {
            _backgroundType.value = type
            appPreferences.setBackgroundType(type)
        }
    }
    
    fun setBackgroundColor(color: String) {
        viewModelScope.launch {
            _backgroundColor.value = color
            appPreferences.setBackgroundColor(color)
        }
    }
    
    fun setBackgroundGradient(gradient: String) {
        viewModelScope.launch {
            _backgroundGradient.value = gradient
            appPreferences.setBackgroundGradient(gradient)
        }
    }
    
    fun setBackgroundImagePath(path: String?) {
        viewModelScope.launch {
            _backgroundImagePath.value = path
            appPreferences.setBackgroundImagePath(path)
        }
    }
    
    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            _themeMode.value = mode
            appPreferences.setThemeMode(mode)
        }
    }
    
    fun setThemeColor(color: String) {
        viewModelScope.launch {
            _themeColor.value = color
            appPreferences.setThemeColor(color)
        }
    }
    
    fun addCustomCompliment(text: String, category: String) {
        viewModelScope.launch {
            try {
                val compliment = ComplimentEntity(
                    text = text,
                    category = category,
                    isCustom = true
                )
                database.complimentDao().insertCompliment(compliment)
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "添加自定义夸赞失败", e)
            }
        }
    }
    
    fun updateCustomCompliment(compliment: ComplimentEntity) {
        viewModelScope.launch {
            try {
                database.complimentDao().updateCompliment(compliment)
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "更新自定义夸赞失败", e)
            }
        }
    }
    
    fun deleteCustomCompliment(id: Long) {
        viewModelScope.launch {
            try {
                database.complimentDao().deleteComplimentById(id)
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "删除自定义夸赞失败", e)
            }
        }
    }
    
    fun getMusicList() = musicRepository.getMusicList()
    
    fun getCurrentMusicName() = musicPlayer.getCurrentMusicName()
    
    suspend fun exportCompliments(): String {
        return try {
            val allCompliments = database.complimentDao().getAllCompliments()
            val gson = com.google.gson.GsonBuilder().setPrettyPrinting().create()
            gson.toJson(allCompliments)
        } catch (e: Exception) {
            Log.e("SettingsViewModel", "导出夸赞失败", e)
            "[]"
        }
    }
    
    suspend fun importCompliments(json: String): Int {
        return try {
            val gson = com.google.gson.Gson()
            val compliments = gson.fromJson(json, Array<ComplimentEntity>::class.java).toList()
            var importedCount = 0
            compliments.forEach { compliment ->
                try {
                    database.complimentDao().insertCompliment(compliment)
                    importedCount++
                } catch (e: Exception) {
                    Log.e("SettingsViewModel", "导入单条夸赞失败: ${compliment.text}", e)
                }
            }
            importedCount
        } catch (e: Exception) {
            Log.e("SettingsViewModel", "导入夸赞失败", e)
            0
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        musicPlayer.release()
    }
}