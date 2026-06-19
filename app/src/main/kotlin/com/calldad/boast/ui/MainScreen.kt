package com.calldad.boast.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.calldad.boast.viewmodel.ComplimentViewModel
import com.calldad.boast.viewmodel.SettingsViewModel
import com.calldad.boast.ui.components.PopupTextsList

/**
 * 主界面组件文件
 * 功能：应用的主UI界面，包含夸赞按钮和弹窗文字显示区域
 * 说明：使用Scaffold布局，Material Design 3组件，与ViewModel进行数据交互
 */

/**
 * 主界面Composable函数
 *
 * @param viewModel 夸赞语句的ViewModel，管理状态和数据
 * @param settingsViewModel 设置ViewModel，管理背景等设置
 * @param onNavigateToSettings 导航到设置页面的回调
 * @param modifier 修饰符，用于调整布局
 *
 * 功能：构建应用的主界面，包含：
 * 1. Scaffold布局容器
 * 2. 中央的夸赞按钮
 * 3. 显示弹窗文字的列表区域
 * 4. 与ViewModel交互，处理按钮点击事件
 * 5. 应用背景设置（纯色、渐变、图片）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: ComplimentViewModel = viewModel(),
    settingsViewModel: SettingsViewModel? = null,
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // 收集StateFlow的状态
    val currentTexts by viewModel.currentTexts.collectAsState()
    
    // 收集背景设置
    val backgroundType by settingsViewModel?.backgroundType?.collectAsState(initial = "solid") ?: remember { mutableStateOf("solid") }
    val backgroundColor by settingsViewModel?.backgroundColor?.collectAsState(initial = "#FFFFFF") ?: remember { mutableStateOf("#FFFFFF") }
    val backgroundGradient by settingsViewModel?.backgroundGradient?.collectAsState(initial = "#FF6B6B|#4ECDC4") ?: remember { mutableStateOf("#FF6B6B|#4ECDC4") }
    val backgroundImagePath by settingsViewModel?.backgroundImagePath?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }
    
    // 初始化加载状态
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // 模拟初始化加载
        kotlinx.coroutines.delay(500)
        isInitialized = true
    }
    
    // 创建背景修饰符 - 使用 remember 缓存计算结果
    val backgroundModifier = remember(backgroundType, backgroundColor, backgroundGradient, backgroundImagePath) {
        when (backgroundType) {
            "solid" -> {
                try {
                    Modifier.background(Color(android.graphics.Color.parseColor(backgroundColor)))
                } catch (e: Exception) {
                    Modifier.background(Color.White)
                }
            }
            "gradient" -> {
                try {
                    val colors = backgroundGradient.split("|").map { colorStr ->
                        Color(android.graphics.Color.parseColor(colorStr.trim()))
                    }
                    Modifier.background(
                        Brush.linearGradient(colors)
                    )
                } catch (e: Exception) {
                    Modifier.background(
                        Brush.linearGradient(
                            listOf(Color(0xFFFF6B6B), Color(0xFF4ECDC4))
                        )
                    )
                }
            }
            "image" -> {
                if (backgroundImagePath != null) {
                    Modifier
                } else {
                    Modifier.background(Color.White)
                }
            }
            else -> Modifier.background(Color.White)
        }
    }
    
    // 使用Scaffold作为根布局，提供Material Design的标准结构
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "设置",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        },
        content = { innerPadding ->
            // 主内容区域
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(backgroundModifier)
                    .padding(innerPadding)
            ) {
                // 如果是图片背景，显示图片
                if (backgroundType == "image" && backgroundImagePath != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(backgroundImagePath)
                            .crossfade(true)
                            .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
                            .diskCachePolicy(coil.request.CachePolicy.ENABLED)
                            .size(800, 1200)  // 限制图片尺寸以减少内存占用
                            .build(),
                        contentDescription = "背景图片",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
                
                // 初始化加载状态
                if (!isInitialized) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            Text(
                                text = "正在加载...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                } else {
                    // 垂直布局：顶部显示弹窗文字列表，底部中央显示按钮
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 弹窗文字列表组件 - 显示所有夸赞语句
                        PopupTextsList(
                            texts = currentTexts,
                            modifier = Modifier.weight(1f), // 占用剩余空间
                            onExpired = { id ->
                                viewModel.removeExpiredCompliment(id)
                            }
                        )
                        
                        // 夸赞按钮 - 点击生成随机夸赞语句
                        var isPressed by remember { mutableStateOf(false) }
                        val scale by animateFloatAsState(
                            targetValue = if (isPressed) 0.95f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "buttonScale"
                        )

                        val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }

                        // 监听按钮按下和释放状态
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect { interaction ->
                                when (interaction) {
                                    is androidx.compose.foundation.interaction.PressInteraction.Press -> {
                                        isPressed = true
                                    }
                                    is androidx.compose.foundation.interaction.PressInteraction.Release,
                                    is androidx.compose.foundation.interaction.PressInteraction.Cancel -> {
                                        isPressed = false
                                    }
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .padding(bottom = 32.dp)
                                .scale(scale)
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .combinedClickable(
                                    onClick = {
                                        viewModel.generateRandomCompliment()
                                    }
                                )
                                .padding(horizontal = 32.dp, vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "夸我一下",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    )
}



/**
 * 界面预览函数（用于Android Studio的预览面板）
 */
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    // 应用主题
    com.calldad.boast.ui.theme.ComposeEmptyActivityTheme {
        // 预览主界面
        MainScreen()
    }
}

/**
 * 实现说明：
 * 1. Scaffold布局：提供标准的Material Design布局结构，自动处理系统栏和安全区域
 * 2. Box布局：作为容器，便于子组件的定位
 * 3. Column布局：垂直排列弹窗文字列表和按钮
 * 4. PopupTextsList组件：负责显示所有夸赞语句，支持滚动和动画
 * 5. Button组件：Material 3风格按钮，点击触发ViewModel的方法
 * 
 * 状态管理：
 * - viewModel.currentTexts：观察当前显示的夸赞语句列表
 * - viewModel.generateRandomCompliment()：调用ViewModel生成新的夸赞语句
 * 
 * 交互流程：
 * 1. 用户点击"夸我一下"按钮
 * 2. 调用viewModel.generateRandomCompliment()
 * 3. ViewModel从数据源随机选择一条夸赞语句
 * 4. ViewModel将新语句添加到currentTexts列表
 * 5. PopupTextsList观察到列表变化，显示新的弹窗文字
 * 6. PopupTextItem组件执行进入动画
 */