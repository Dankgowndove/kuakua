package com.calldad.boast.ui.components.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.CachePolicy
import com.calldad.boast.viewmodel.SettingsViewModel

@Composable
fun BackgroundSetting(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val backgroundType by viewModel.backgroundType.collectAsState()
    val backgroundColor by viewModel.backgroundColor.collectAsState()
    val backgroundGradient by viewModel.backgroundGradient.collectAsState()
    val backgroundImagePath by viewModel.backgroundImagePath.collectAsState()
    
    // 配置优化的ImageLoader
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .memoryCache {
                coil.memory.MemoryCache.Builder(context)
                    .maxSizePercent(0.25) // 使用25%的可用内存
                    .build()
            }
            .diskCache {
                coil.disk.DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(10 * 1024 * 1024) // 10MB磁盘缓存
                    .build()
            }
            .respectCacheHeaders(false)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    }
    
    // 运行时权限请求
    var showPermissionDeniedMessage by remember { mutableStateOf(false) }
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val path = it.toString()
            viewModel.setBackgroundImagePath(path)
        }
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // 权限已授予，启动图片选择器
            imagePickerLauncher.launch("image/*")
        } else {
            // 权限被拒绝，显示提示
            showPermissionDeniedMessage = true
        }
    }
    
    // 检查权限并启动图片选择器
    fun launchImagePickerWithPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

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
                text = "背景类型",
                style = MaterialTheme.typography.bodyLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = backgroundType == "solid",
                    onClick = { viewModel.setBackgroundType("solid") },
                    label = { Text("纯色") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = backgroundType == "gradient",
                    onClick = { viewModel.setBackgroundType("gradient") },
                    label = { Text("渐变") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = backgroundType == "image",
                    onClick = { viewModel.setBackgroundType("image") },
                    label = { Text("图片") },
                    modifier = Modifier.weight(1f)
                )
            }

            if (backgroundType == "solid") {
                Text(
                    text = "纯色背景",
                    style = MaterialTheme.typography.bodyMedium
                )
                val solidColors = listOf(
                    "#6200EE", "#03DAC6", "#FF5722",
                    "#4CAF50", "#2196F3", "#E91E63"
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    solidColors.forEach { color ->
                        val isSelected = backgroundColor == color
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clickable { viewModel.setBackgroundColor(color) },
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = RoundedCornerShape(16.dp),
                                color = Color(android.graphics.Color.parseColor(color)),
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
            } else if (backgroundType == "gradient") {
                Text(
                    text = "渐变背景",
                    style = MaterialTheme.typography.bodyMedium
                )
                val gradients = listOf(
                    "linear|#6200EE|#3700B3",
                    "linear|#03DAC6|#018786",
                    "linear|#FF5722|#E64A19",
                    "linear|#4CAF50|#388E3C"
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    gradients.chunked(2).forEach { rowGradients ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowGradients.forEach { gradient ->
                                val colors = gradient.split("|").drop(1)
                                val isSelected = backgroundGradient == gradient
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                        .clickable { viewModel.setBackgroundGradient(gradient) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Surface(
                                        modifier = Modifier.fillMaxSize(),
                                        shape = RoundedCornerShape(16.dp),
                                        color = Color.Transparent,
                                        border = if (isSelected) {
                                            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface)
                                        } else null
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    brush = Brush.linearGradient(
                                                        colors = colors.map {
                                                            Color(android.graphics.Color.parseColor(it))
                                                        }
                                                    ),
                                                    shape = RoundedCornerShape(16.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSelected) {
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
                            if (rowGradients.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            } else if (backgroundType == "image") {
                Text(
                    text = "自定义背景图片",
                    style = MaterialTheme.typography.bodyMedium
                )

                if (backgroundImagePath != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clickable { launchImagePickerWithPermission() }
                    ) {
                        AsyncImage(
                            model = coil.request.ImageRequest.Builder(LocalContext.current)
                                .data(backgroundImagePath)
                                .crossfade(true)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .build(),
                            imageLoader = imageLoader,
                            contentDescription = "背景图片",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp),
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                        ) {
                            Text(
                                text = "点击更换",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { launchImagePickerWithPermission() },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "添加图片",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "点击选择图片",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                if (backgroundImagePath != null) {
                    OutlinedButton(
                        onClick = { viewModel.setBackgroundImagePath(null) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("清除背景图片")
                    }
                }
            }
        }
    }

    if (showPermissionDeniedMessage) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.inverseSurface,
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(start = 16.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "需要存储权限才能选择图片",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.inverseOnSurface
                    )
                    TextButton(onClick = {
                        val intent = android.content.Intent(
                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            android.net.Uri.fromParts("package", context.packageName, null)
                        )
                        context.startActivity(intent)
                        showPermissionDeniedMessage = false
                    }) {
                        Text("去设置")
                    }
                }
            }
        }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(4000)
            showPermissionDeniedMessage = false
        }
    }
}