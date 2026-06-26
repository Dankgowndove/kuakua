package com.calldad.boast.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calldad.boast.data.PopupTextData

/**
 * 弹窗文字列表组件文件
 * 功能：显示和管理所有夸赞语句弹窗的列表容器
 * 说明：使用LazyColumn实现高效滚动列表，支持自动滚动到底部，空状态提示
 */

/**
 * 弹窗文字列表Composable函数
 *
 * @param texts 弹窗文字数据列表，用于显示所有夸赞语句
 * @param modifier 修饰符，用于调整布局
 * @param onExpired 当某个弹窗动画完成时的回调，用于从列表中移除该项
 *
 * 功能：显示夸赞语句弹窗列表，包含以下特性：
 * 1. 使用LazyColumn实现高效列表渲染
 * 2. 自动滚动到底部（当新项目添加时）
 * 3. 空状态提示（当没有夸赞语句时）
 * 4. 反向排列（最新的在最上面）
 * 5. 平滑的滚动动画
 * 6. 自动移除已完成的弹窗项，防止内存泄漏
 * 
 * 性能优化：
 * - 使用LazyColumn只渲染可见项目
 * - 使用rememberLazyListState记住滚动状态
 * - 使用LaunchedEffect监听列表变化并自动滚动
 * - 动画完成后自动移除不可见项，防止内存泄漏
 */
@Composable
fun PopupTextsList(
    texts: List<PopupTextData>,
    modifier: Modifier = Modifier,
    onExpired: (String) -> Unit = {}
) {
    // 获取LazyColumn的滚动状态
    val listState = rememberLazyListState()
    
    // 当列表变化时，滚动到最新项（reverseLayout 下 index=0 在底部）
    LaunchedEffect(texts.size) {
        if (texts.isNotEmpty()) {
            listState.animateScrollToItem(index = 0)
        }
    }
    
    // 列表容器
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        if (texts.isEmpty()) {
            // 空状态提示：当没有夸赞语句时显示
            EmptyState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            )
        } else {
            // 使用LazyColumn显示夸赞语句列表
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                reverseLayout = true,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 底部间距率先声明，reverseLayout 会将其置于底部
                item {
                    Box(modifier = Modifier.padding(bottom = 100.dp))
                }

                itemsIndexed(
                    items = texts,
                    key = { index, item -> item.id }
                ) { index, popupText ->
                    PopupTextItem(
                        popupText = popupText,
                        modifier = Modifier.padding(vertical = 4.dp),
                        onExpired = onExpired
                    )
                }
            }
        }
    }
}

/**
 * 空状态提示组件
 *
 * @param modifier 修饰符，用于调整布局
 *
 * 功能：当没有夸赞语句时显示的提示界面
 * 设计：使用Material 3的排版和颜色，提供友好的用户引导
 */
@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 提示图标（使用文字表情符号代替图标资源）
        Text(
            text = "💬",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 64.sp
            ),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
        
        // 提示标题
        Text(
            text = "等待夸赞",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            textAlign = TextAlign.Center
        )
        
        // 提示描述
        Text(
            text = "点击下方按钮获取随机夸赞\n每一条夸赞都会以弹窗形式显示",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp
            ),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
        
        // 装饰性分隔线
        Box(
            modifier = Modifier
                .padding(vertical = 24.dp)
                .widthIn(max = 200.dp)
                .height(1.dp)
                .background(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(1.dp)
                )
        )
        
        // 操作提示
        Text(
            text = "试试点击 \"夸我一下\" 按钮",
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}



/**
 * 预览函数（用于Android Studio的预览面板）
 */
@Preview(showBackground = true)
@Composable
fun PopupTextsListPreview() {
    // 应用主题
    com.calldad.boast.ui.theme.ComposeEmptyActivityTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            // 预览空状态
            PopupTextsList(
                texts = emptyList(),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * 预览函数：带数据的列表预览
 */
@Preview(showBackground = true)
@Composable
fun PopupTextsListWithDataPreview() {
    // 创建测试数据
    val testTexts = listOf(
        PopupTextData(id = "1", text = "你今天看起来真帅！"),
        PopupTextData(id = "2", text = "你的笑容真迷人！"),
        PopupTextData(id = "3", text = "你真是聪明又机智！"),
        PopupTextData(id = "4", text = "你的气质与众不同！"),
        PopupTextData(id = "5", text = "你总是这么努力！")
    )
    
    // 应用主题
    com.calldad.boast.ui.theme.ComposeEmptyActivityTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            // 预览带数据的列表
            PopupTextsList(
                texts = testTexts,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * 实现说明：
 * 1. LazyColumn：Compose的高性能滚动列表，只渲染可见项目
 * 2. reverseLayout：设置为true，使最新的夸赞语句显示在列表顶部
 * 3. itemsIndexed：使用索引和唯一ID作为key，优化列表项的重组
 * 4. LaunchedEffect：监听列表大小变化，自动滚动到底部显示最新项
 * 5. EmptyState：友好的空状态提示，引导用户操作
 * 
 * 自动滚动逻辑：
 * 1. 当texts.size变化时（添加新项），LaunchedEffect触发
 * 2. 如果列表非空，调用listState.animateScrollToItem滚动到最后一项
 * 3. 使用animateScrollToItem实现平滑滚动动画
 * 
 * 性能注意事项：
 * 1. 为每个PopupTextData使用唯一ID作为key，避免不必要的重组
 * 2. LazyColumn的reverseLayout可能会影响性能，但考虑到夸赞语句数量有限，影响可忽略
 * 3. 添加底部内边距避免内容被按钮遮挡，同时不会影响滚动性能
 */