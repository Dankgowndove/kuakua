package com.calldad.boast.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.calldad.boast.viewmodel.SettingsViewModel

@Composable
fun MusicSetting(viewModel: SettingsViewModel) {
    val musicEnabled by viewModel.musicEnabled.collectAsState()
    val musicVolume by viewModel.musicVolume.collectAsState()
    val currentMusicIndex by viewModel.currentMusicIndex.collectAsState()
    val musicList = viewModel.getMusicList()
    
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "背景音乐",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = musicEnabled,
                    onCheckedChange = { viewModel.toggleMusic() }
                )
            }
            
            if (musicEnabled) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "当前音乐",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = viewModel.getCurrentMusicName(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        musicList.forEachIndexed { index, music ->
                            FilterChip(
                                selected = currentMusicIndex == index,
                                onClick = { viewModel.setCurrentMusicIndex(index) },
                                label = { Text(music.name) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "音量",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${(musicVolume * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Slider(
                            value = musicVolume,
                            onValueChange = { viewModel.setMusicVolume(it) },
                            valueRange = 0f..1f,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}