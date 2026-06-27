package com.calldad.boast.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.calldad.boast.viewmodel.SettingsViewModel

@Composable
fun ThemeSetting(viewModel: SettingsViewModel) {
    val themeMode by viewModel.themeMode.collectAsState()
    val themeColor by viewModel.themeColor.collectAsState()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "主题模式",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = themeMode == "default",
                    onClick = { viewModel.setThemeMode("default") },
                    label = { Text("默认") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = themeMode == "dark",
                    onClick = { viewModel.setThemeMode("dark") },
                    label = { Text("暗色") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = themeMode == "light",
                    onClick = { viewModel.setThemeMode("light") },
                    label = { Text("亮色") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Text(
                text = "主题颜色",
                style = MaterialTheme.typography.bodyMedium
            )
            
            val themeColors = listOf(
                "default" to Color.Gray,
                "purple" to Color(0xFF6200EE),
                "blue" to Color(0xFF2196F3),
                "green" to Color(0xFF4CAF50),
                "pink" to Color(0xFFE91E63),
                "orange" to Color(0xFFFF5722)
            )
            
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                themeColors.forEach { (colorName, color) ->
                    val isSelected = themeColor == colorName
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clickable { viewModel.setThemeColor(colorName) },
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(16.dp),
                            color = color,
                            tonalElevation = if (isSelected) 4.dp else 0.dp,
                            border = if (isSelected) {
                                androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface)
                            } else null
                        ) {
                            if (isSelected) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "已选择",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}