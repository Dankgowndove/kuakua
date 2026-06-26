package com.calldad.boast.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.calldad.boast.data.PopupTextData
import com.calldad.boast.data.database.ComplimentDatabase
import com.calldad.boast.data.repository.ComplimentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class ComplimentViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = ComplimentRepository(
        ComplimentDatabase.getDatabase(application).complimentDao()
    )
    
    private val _currentTexts = MutableStateFlow<List<PopupTextData>>(emptyList())
    val currentTexts: StateFlow<List<PopupTextData>> = _currentTexts.asStateFlow()
    
    init {
        initializeDatabase()
    }
    
    private fun initializeDatabase() {
        viewModelScope.launch {
            try {
                repository.initializeDatabase()
            } catch (e: Exception) {
                Log.e("ComplimentViewModel", "初始化数据库失败", e)
            }
        }
    }
    
    fun generateRandomCompliment() {
        viewModelScope.launch {
            val compliment = repository.getRandomCompliment()
            compliment?.let {
                val newPopupText = PopupTextData(
                    id = UUID.randomUUID().toString(),
                    text = it.text
                )
                _currentTexts.update { currentList ->
                    val updatedList = if (currentList.size >= 20) {
                        currentList.dropLast(1)
                    } else {
                        currentList
                    }
                    listOf(newPopupText) + updatedList
                }
            }
        }
    }
    
    fun clearAllCompliments() {
        viewModelScope.launch {
            _currentTexts.update { emptyList() }
        }
    }
    
    fun removeCompliment(id: String) {
        viewModelScope.launch {
            _currentTexts.update { currentList ->
                currentList.filter { it.id != id }
            }
        }
    }
    
    fun removeExpiredCompliment(id: String) = removeCompliment(id)
    
    suspend fun getRandomComplimentText(): String {
        val compliment = repository.getRandomCompliment()
        return compliment?.text ?: "你真棒！"
    }
}