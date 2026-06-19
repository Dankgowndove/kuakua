package com.calldad.boast.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.calldad.boast.viewmodel.DocumentViewModel
import io.noties.markwon.Markwon
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.syntax.Prism4jThemeDarkula
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import io.noties.prism4j.Prism4j

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentScreen(
    viewModel: DocumentViewModel,
    documentId: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val documentContent by viewModel.documentContent.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorState by viewModel.error.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val canGoBack by remember { derivedStateOf { viewModel.canGoBack() } }
    
    var showSearchBar by remember { mutableStateOf(false) }
    
    val markwon = remember { createMarkwon(context) }
    
    LaunchedEffect(documentId) {
        viewModel.loadDocument(documentId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    if (showSearchBar) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.searchDocument(it) },
                            placeholder = { Text("搜索文档内容") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "搜索")
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.searchDocument("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "清除")
                                    }
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(getDocumentTitle(documentId))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { 
                            if (canGoBack) {
                                viewModel.getPreviousDocument()?.let { prevId ->
                                    viewModel.loadDocument(prevId)
                                }
                            }
                        },
                        enabled = canGoBack
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "上一页")
                    }
                    IconButton(onClick = { showSearchBar = !showSearchBar }) {
                        Icon(Icons.Default.Search, contentDescription = "搜索")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            errorState != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        // 错误图标
                        Text(
                            text = "⚠️",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                        
                        // 友好的错误标题
                        Text(
                            text = "文档加载失败",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                        
                        // 错误描述
                        Text(
                            text = getFriendlyErrorMessage(errorState!!),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))
                        
                        // 返回按钮
                        Button(
                            onClick = onNavigateBack,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("返回")
                        }
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // 搜索结果
                    if (searchResults.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "搜索结果 (${searchResults.size})",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyColumn(
                                    modifier = Modifier.heightIn(max = 200.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(searchResults) { result ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surface
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(12.dp)
                                            ) {
                                                Text(
                                                    text = result.content,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = result.context,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // 文档版本信息
                    DocumentVersionInfo()
                    
                    // 文档内容
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        AndroidView(
                            factory = { context ->
                                android.widget.TextView(context).apply {
                                    setText(markwon.toMarkdown(documentContent))
                                    setMovementMethod(android.text.method.LinkMovementMethod.getInstance())
                                    setTextAppearance(android.R.style.TextAppearance_Medium)
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

private fun createMarkwon(context: Context): Markwon {
    val prism4j = Prism4j { null }
    return Markwon.builder(context)
        .usePlugin(CorePlugin.create())
        .usePlugin(SyntaxHighlightPlugin.create(prism4j, Prism4jThemeDarkula.create()))
        .build()
}

/**
 * 将技术错误转换为友好的用户提示
 */
private fun getFriendlyErrorMessage(error: String): String {
    val lower = error.lowercase()
    return when {
        lower.contains("filenotfound") || lower.contains("file not found") -> {
            "文档文件不存在，可能已被删除或移动"
        }
        lower.contains("ioexception") || lower.contains("io exception") || lower.contains("failed to open") -> {
            "读取文档时发生错误，请检查文件权限"
        }
        lower.contains("outofmemory") || lower.contains("out of memory") -> {
            "文档过大，无法加载"
        }
        lower.contains("nullpointer") || lower.contains("null pointer") -> {
            "文档数据异常，请尝试重新加载"
        }
        else -> {
            "加载文档时出现未知错误，请稍后重试"
        }
    }
}

fun getDocumentTitle(documentId: String): String {
    return when (documentId) {
        "user-guide" -> "使用说明"
        "architecture" -> "系统架构"
        "api-reference" -> "开发文档"
        "changelog" -> "更新日志"
        "faq" -> "常见问题"
        else -> "文档"
    }
}

@Composable
fun DocumentVersionInfo() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "文档版本：1.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "更新日期：2025-12-27",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}