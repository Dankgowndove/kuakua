package com.calldad.boast.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calldad.boast.data.PopupTextData
import kotlinx.coroutines.delay

/**
 * 弹窗文字动画组件文件
 * 功能：显示单个夸赞语句弹窗，带有进入和淡出动画效果
 * 说明：使用Compose动画API实现平滑的显示/隐藏动画，支持渐变色背景
 */

/**
 * 弹窗文字项Composable函数
 *
 * @param popupText 弹窗文字数据对象，包含文字内容和唯一ID
 * @param modifier 修饰符，用于调整布局
 * @param maxWidth 弹窗最大宽度，默认为屏幕宽度的80%
 * @param onExpired 动画完成后的回调，通知父组件移除该项
 *
 * 功能：显示单个夸赞语句弹窗，包含以下特性：
 * 1. 渐变色背景（Material You动态颜色）
 * 2. 圆角矩形形状
 * 3. 进入动画（从顶部滑入）
 * 4. 淡出动画（3秒后自动淡出）
 * 5. 阴影效果（深度感知）
 * 
 * 动画原理：
 * - 使用animateDpAsState实现位置动画
 * - 使用LaunchedEffect控制动画时序
 * - 使用透明度动画实现淡入淡出效果
 */
@Composable
fun PopupTextItem(
    popupText: PopupTextData,
    modifier: Modifier = Modifier,
    maxWidth: Dp = 280.dp,
    onExpired: (String) -> Unit = {}
) {
    // 动画状态控制
    var isVisible by remember { mutableStateOf(true) } // 控制组件是否显示
    var startAnimation by remember { mutableStateOf(false) } // 控制动画开始
    
    // 垂直位置动画：从顶部（-100.dp）滑动到目标位置（0.dp）
    val verticalOffset by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else (-100).dp,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "verticalOffsetAnimation"
    )
    
    // 透明度动画：淡入淡出效果
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "alphaAnimation"
    )
    
    // 使用LaunchedEffect控制动画时序
    LaunchedEffect(Unit) {
        // 延迟100ms后开始进入动画（让组件先挂载）
        delay(100)
        startAnimation = true
        
        // 3秒后开始淡出动画
        delay(3000)
        isVisible = false
        
        // 等待淡出动画完成（500ms）
        delay(500)
        // 通知父组件移除该项，防止内存泄漏
        onExpired(popupText.id)
    }
    
    // 如果完全不可见，则不渲染组件（优化性能）
    if (alpha <= 0f && !isVisible) {
        return
    }
    
    // 弹窗容器
    Box(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .widthIn(max = maxWidth)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            // 应用位置动画
            .graphicsLayer {
                translationY = verticalOffset.toPx()
                this.alpha = alpha
            }
    ) {
        // 渐变背景容器
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
                .padding(horizontal = 20.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            // 文字内容
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 夸赞文字
                Text(
                    text = popupText.text,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                // 分隔线（装饰性）
                Box(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .widthIn(max = 120.dp)
                        .height(2.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(1.dp)
                        )
                )
                
                // 表情符号（根据文字类型显示不同的emoji）
                Text(
                    text = getEmojiForText(popupText.text),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * 根据夸赞文字内容返回对应的表情符号
 * 
 * @param text 夸赞文字内容
 * @return 对应的表情符号字符串
 * 
 * 功能：增强用户体验，为不同类型的夸赞语句匹配相应的表情符号
 */
private fun getEmojiForText(text: String): String {
    return when {
        text.contains("美") || text.contains("漂亮") -> "💖"
        text.contains("帅") || text.contains("英俊") -> "✨"
        text.contains("聪明") || text.contains("智慧") -> "🧠"
        text.contains("可爱") || text.contains("萌") -> "🐰"
        text.contains("厉害") || text.contains("强大") -> "💪"
        text.contains("温柔") || text.contains("体贴") -> "🌸"
        text.contains("幽默") || text.contains("有趣") -> "😄"
        text.contains("努力") || text.contains("勤奋") -> "🏆"
        text.contains("善良") || text.contains("好心") -> "❤️"
        else -> "🌟" // 默认表情
    }
}



/**
 * 预览函数（用于Android Studio的预览面板）
 */
@Preview(showBackground = true)
@Composable
fun PopupTextItemPreview() {
    // 应用主题
    com.calldad.boast.ui.theme.ComposeEmptyActivityTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            // 预览弹窗文字项
            PopupTextItem(
                popupText = PopupTextData(
                    id = "preview-1",
                    text = "你今天看起来真帅！"
                )
            )
        }
    }
}

/**
 * 注意：需要添加以下导入（如果IDE没有自动导入）：
 * import androidx.compose.ui.layout.layout
 * import androidx.compose.ui.unit.roundToPx
 * import kotlinx.coroutines.delay
 * 
 * 动画参数说明：
 * 1. verticalOffset动画：从-100dp到0dp，持续时间600ms，使用FastOutSlowIn缓动函数
 * 2. alpha动画：从0到1（淡入），从1到0（淡出），持续时间500ms
 * 3. 时序控制：组件挂载后100ms开始进入动画，3秒后开始淡出动画
 * 
 * 性能优化：
 * 1. 当alpha <= 0f且isVisible为false时，直接返回不渲染组件
 * 2. 使用graphicsLayer进行硬件加速的动画
 * 3. 合理的动画时长，避免过度消耗资源
 */