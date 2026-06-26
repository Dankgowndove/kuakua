package com.calldad.boast.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.calldad.boast.viewmodel.SettingsViewModel
import com.calldad.boast.ui.components.settings.MusicSetting
import com.calldad.boast.ui.components.settings.BackgroundSetting
import com.calldad.boast.ui.components.settings.ThemeSetting
import com.calldad.boast.ui.components.settings.WordLibrarySetting
import com.calldad.boast.ui.components.settings.DocumentSetting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDocument: (String) -> Unit,
    onNavigateToEditor: () -> Unit = {},
    currentDocumentId: String? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "背景音乐",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                MusicSetting(viewModel = viewModel)
            }
            
            item {
                Text(
                    text = "背景设置",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                BackgroundSetting(viewModel = viewModel)
            }
            
            item {
                Text(
                    text = "主题设置",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                ThemeSetting(viewModel = viewModel)
            }
            
            item {
                Text(
                    text = "词库管理",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                WordLibrarySetting(viewModel = viewModel)
            }
            
            item {
                Text(
                    text = "文档",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                DocumentSetting(
                    onNavigateToDocument = onNavigateToDocument,
                    currentDocumentId = currentDocumentId
                )
            }
            
            item {
                Text(
                    text = "工具箱",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNavigateToEditor
                ) {
                    ListItem(
                        headlineContent = { Text("文本编辑器") },
                        supportingContent = { Text("带行号、统计、撤销的纯文本编辑工具") },
                        trailingContent = {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "打开文本编辑器"
                            )
                        }
                    )
                }
            }
        }
    }
}