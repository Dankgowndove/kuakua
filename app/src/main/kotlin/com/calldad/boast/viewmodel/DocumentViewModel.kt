package com.calldad.boast.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class DocumentViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _documentContent = MutableStateFlow("")
    val documentContent: StateFlow<String> = _documentContent.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults.asStateFlow()
    
    private val documentCache = mutableMapOf<String, String>()
    private val documentHistory = mutableListOf<String>()
    private var currentDocumentId: String? = null
    
    private val documentList = listOf(
        DocumentInfo("user-guide", "使用说明"),
        DocumentInfo("architecture", "系统架构"),
        DocumentInfo("api-reference", "开发文档"),
        DocumentInfo("changelog", "更新日志"),
        DocumentInfo("faq", "常见问题")
    )
    
    fun loadDocument(documentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val fileName = when (documentId) {
                    "user-guide" -> "documents/user_guide.md"
                    "architecture" -> "documents/architecture.md"
                    "api-reference" -> "documents/api_reference.md"
                    "changelog" -> "documents/changelog.md"
                    "faq" -> "documents/faq.md"
                    else -> "documents/README.md"
                }
                
                // 使用缓存机制
                val content = documentCache[documentId] ?: loadMarkdownFromAssets(fileName)
                documentCache[documentId] = content
                
                // 添加到历史记录
                currentDocumentId?.let { 
                    if (documentHistory.isEmpty() || documentHistory.last() != it) {
                        documentHistory.add(it)
                    }
                }
                currentDocumentId = documentId
                
                _documentContent.value = content
                _searchQuery.value = ""
                _searchResults.value = emptyList()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun searchDocument(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        val content = _documentContent.value
        val results = mutableListOf<SearchResult>()
        val lines = content.split("\n")
        
        lines.forEachIndexed { index, line ->
            if (line.contains(query, ignoreCase = true)) {
                results.add(
                    SearchResult(
                        lineIndex = index,
                        content = line.trim(),
                        context = getSearchContext(lines, index, query)
                    )
                )
            }
        }
        
        _searchResults.value = results
    }
    
    private fun getSearchContext(lines: List<String>, index: Int, query: String): String {
        val start = maxOf(0, index - 1)
        val end = minOf(lines.size, index + 2)
        return lines.subList(start, end).joinToString("\n")
    }
    
    fun getPreviousDocument(): String? {
        return if (documentHistory.isNotEmpty()) {
            documentHistory.removeLast()
        } else {
            null
        }
    }
    
    fun canGoBack(): Boolean = documentHistory.isNotEmpty()
    
    fun clearCache() {
        documentCache.clear()
    }
    
    private fun loadMarkdownFromAssets(fileName: String): String {
        return getApplication<Application>().assets.open(fileName).bufferedReader().use { it.readText() }
    }
    
    fun getDocumentList(): List<DocumentInfo> = documentList
}

data class DocumentInfo(
    val id: String,
    val title: String
)

data class SearchResult(
    val lineIndex: Int,
    val content: String,
    val context: String
)