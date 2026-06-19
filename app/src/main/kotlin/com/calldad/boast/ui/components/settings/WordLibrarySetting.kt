package com.calldad.boast.ui.components.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.calldad.boast.data.database.ComplimentEntity
import com.calldad.boast.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun WordLibrarySetting(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val builtinCount by viewModel.builtinCount.collectAsState()
    val customCount by viewModel.customCount.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val customCompliments by viewModel.customCompliments.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCompliment by remember { mutableStateOf<ComplimentEntity?>(null) }
    var exportMessage by remember { mutableStateOf<String?>(null) }
    var importMessage by remember { mutableStateOf<String?>(null) }
    var isExporting by remember { mutableStateOf(false) }
    var isImporting by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            scope.launch {
                isExporting = true
                try {
                    val json = viewModel.exportCompliments()
                    context.contentResolver.openOutputStream(uri)?.use { output ->
                        output.write(json.toByteArray())
                    }
                    exportMessage = "导出成功"
                } catch (e: Exception) {
                    exportMessage = "导出失败: ${e.message}"
                } finally {
                    isExporting = false
                }
            }
        }
    }
    
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch {
                isImporting = true
                try {
                    val json = context.contentResolver.openInputStream(uri)?.bufferedReader()?.readText() ?: ""
                    val count = viewModel.importCompliments(json)
                    importMessage = "成功导入 $count 条夸赞"
                } catch (e: Exception) {
                    importMessage = "导入失败: ${e.message}"
                } finally {
                    isImporting = false
                }
            }
        }
    }
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "词库统计",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatItem("内置", builtinCount.toString(), Modifier.weight(1f))
                StatItem("自定义", customCount.toString(), Modifier.weight(1f))
                StatItem("总计", totalCount.toString(), Modifier.weight(1f))
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { exportLauncher.launch("compliments_export.json") },
                    modifier = Modifier.weight(1f),
                    enabled = !isExporting && !isImporting
                ) {
                    if (isExporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("导出中...")
                    } else {
                        Text("导出")
                    }
                }
                OutlinedButton(
                    onClick = { importLauncher.launch(arrayOf("application/json", "text/json")) },
                    modifier = Modifier.weight(1f),
                    enabled = !isExporting && !isImporting
                ) {
                    if (isImporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("导入中...")
                    } else {
                        Text("导入")
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "自定义夸赞",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(
                    onClick = { showAddDialog = true },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "添加", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("添加")
                }
            }
            
            if (customCompliments.isEmpty()) {
                Text(
                    text = "暂无自定义夸赞",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    items(customCompliments) { compliment ->
                        ComplimentItem(
                            compliment = compliment,
                            onEdit = { editingCompliment = compliment },
                            onDelete = { viewModel.deleteCustomCompliment(compliment.id) }
                        )
                    }
                }
            }
        }
    }
    
    if (showAddDialog) {
        AddComplimentDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { text, category ->
                viewModel.addCustomCompliment(text, category)
                showAddDialog = false
            }
        )
    }
    
    if (editingCompliment != null) {
        EditComplimentDialog(
            compliment = editingCompliment!!,
            onDismiss = { editingCompliment = null },
            onUpdate = { updated ->
                viewModel.updateCustomCompliment(updated)
                editingCompliment = null
            }
        )
    }
    
    if (exportMessage != null) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = { exportMessage = null }) {
                    Text("关闭")
                }
            }
        ) {
            Text(exportMessage!!)
        }
    }
    
    if (importMessage != null) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = { importMessage = null }) {
                    Text("关闭")
                }
            }
        ) {
            Text(importMessage!!)
        }
    }
}

@Composable
fun StatItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ComplimentItem(
    compliment: ComplimentEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = compliment.text,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = compliment.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "编辑")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "删除")
                }
            }
        }
    }
}

@Composable
fun AddComplimentDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("其他") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加自定义夸赞") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("夸赞内容") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("类别") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(text, category) },
                enabled = text.isNotBlank()
            ) {
                Text("添加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun EditComplimentDialog(
    compliment: ComplimentEntity,
    onDismiss: () -> Unit,
    onUpdate: (ComplimentEntity) -> Unit
) {
    var text by remember { mutableStateOf(compliment.text) }
    var category by remember { mutableStateOf(compliment.category) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑夸赞") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("夸赞内容") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("类别") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onUpdate(compliment.copy(text = text, category = category)) },
                enabled = text.isNotBlank()
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}