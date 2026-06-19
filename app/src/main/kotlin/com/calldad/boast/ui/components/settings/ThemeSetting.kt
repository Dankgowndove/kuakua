package com.calldad.boast.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.calldad.boast.viewmodel.SettingsViewModel

@Composable
fun ThemeSetting(viewModel: SettingsViewModel) {
    val themeMode by viewModel.themeMode.collectAsState()
    val themeColor by viewModel.themeColor.collectAsState()
    
    Card(
        modifier = Modifier.fillMaxWidth()
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
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(100.dp)
            ) {
                items(themeColors.size) { index ->
                    val (colorName, color) = themeColors[index]
                    val isSelected = themeColor == colorName
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clickable { viewModel.setThemeColor(colorName) }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    color = color,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )
                        if (isSelected) {
                            Surface(
                                color = Color.White.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            ) {}
                        }
                    }
                }
            }
        }
    }
}