package com.calldad.boast.ui.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DocumentSetting(
    onNavigateToDocument: (String) -> Unit,
    currentDocumentId: String? = null
) {
    val documents = listOf(
        DocumentItem("user-guide", "使用说明", "详细介绍软件功能和使用方法"),
        DocumentItem("architecture", "系统架构", "介绍软件的技术架构和设计理念"),
        DocumentItem("api-reference", "开发文档", "面向开发者的技术文档"),
        DocumentItem("changelog", "更新日志", "记录软件版本更新历史"),
        DocumentItem("faq", "常见问题", "解答用户常见疑问")
    )
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            items(documents) { document ->
                DocumentListItem(
                    document = document,
                    onClick = { onNavigateToDocument(document.id) },
                    isSelected = document.id == currentDocumentId
                )
            }
        }
    }
}

@Composable
fun DocumentListItem(
    document: DocumentItem,
    onClick: () -> Unit,
    isSelected: Boolean = false
) {
    ListItem(
        headlineContent = { 
            Text(
                text = document.title,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        },
        supportingContent = { 
            Text(
                text = document.description,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        },
        trailingContent = {
            if (isSelected) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "打开文档",
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "打开文档"
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    )
    Divider()
}

data class DocumentItem(
    val id: String,
    val title: String,
    val description: String
)