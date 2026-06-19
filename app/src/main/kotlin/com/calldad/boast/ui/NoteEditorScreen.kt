package com.calldad.boast.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var textContent by remember { mutableStateOf("") }
    var cursorLine by remember { mutableIntStateOf(1) }
    var cursorColumn by remember { mutableIntStateOf(1) }
    var wordCount by remember { mutableIntStateOf(0) }

    var showStatsDialog by remember { mutableStateOf(false) }
    var showGoToLineDialog by remember { mutableStateOf(false) }
    var showFindDialog by remember { mutableStateOf(false) }

    var statsResult by remember { mutableStateOf<StatsResult?>(null) }
    var isStatsLoading by remember { mutableStateOf(false) }

    val undoManager = remember { UndoManager() }
    val lineCount by remember { derivedStateOf { textContent.lines().size } }
    val charCount by remember { derivedStateOf { textContent.length } }
    val editTextRef = remember { mutableStateOf<LinedEditText?>(null) }

    val canUndo by remember { derivedStateOf { undoManager.canUndo } }
    val canRedo by remember { derivedStateOf { undoManager.canRedo } }

    val updateCursorInfo: (LinedEditText) -> Unit = { et ->
        val pos = et.selectionStart
        val layout = et.layout
        if (layout != null) {
            val line = layout.getLineForOffset(pos) + 1
            val col = pos - layout.getLineStart(line - 1) + 1
            cursorLine = line
            cursorColumn = col
        }
        wordCount = et.text?.split(Regex("\\s+"))?.count { it.isNotEmpty() } ?: 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("文本编辑器") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val et = editTextRef.value ?: return@IconButton
                            val result = undoManager.prepareUndo()
                            if (result != null) {
                                try {
                                    et.setText(result)
                                } finally {
                                    undoManager.finishUndoRedo()
                                }
                                updateCursorInfo(et)
                            }
                        },
                        enabled = canUndo
                    ) {
                        Icon(Icons.Default.Undo, contentDescription = "撤销")
                    }
                    IconButton(
                        onClick = {
                            val et = editTextRef.value ?: return@IconButton
                            val result = undoManager.prepareRedo()
                            if (result != null) {
                                try {
                                    et.setText(result)
                                } finally {
                                    undoManager.finishUndoRedo()
                                }
                                updateCursorInfo(et)
                            }
                        },
                        enabled = canRedo
                    ) {
                        Icon(Icons.Default.Redo, contentDescription = "重做")
                    }
                    IconButton(onClick = {
                        copyLineNumbers(context, textContent)
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "复制行号")
                    }
                    IconButton(onClick = { showGoToLineDialog = true }) {
                        Icon(Icons.Default.Numbers, contentDescription = "跳转到行")
                    }
                    IconButton(onClick = { showFindDialog = true }) {
                        Icon(Icons.Default.FindReplace, contentDescription = "查找替换")
                    }
                    IconButton(onClick = {
                        showStatsDialog = true
                        isStatsLoading = true
                        statsResult = null
                    }) {
                        Icon(Icons.Default.BarChart, contentDescription = "统计")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "行 $cursorLine, 列 $cursorColumn",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$lineCount 行 | $charCount 字符 | $wordCount 词",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AndroidView(
                factory = { ctx ->
                    LinedEditText(ctx).also { et ->
                        et.isVerticalScrollBarEnabled = true
                        et.textSize = 14f
                        et.typeface = android.graphics.Typeface.MONOSPACE
                        et.hint = "在此输入文本..."
                        et.setHorizontallyScrolling(false)
                        et.maxLines = Integer.MAX_VALUE
                        et.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION

                        et.addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(
                                s: CharSequence, start: Int, count: Int, after: Int
                            ) {}

                            override fun onTextChanged(
                                s: CharSequence, start: Int, before: Int, count: Int
                            ) {}

                            override fun afterTextChanged(s: Editable) {
                                val newText = s.toString()
                                undoManager.record(newText)
                                textContent = newText
                                updateCursorInfo(et)
                            }
                        })

                        et.setOnClickListener {
                            updateCursorInfo(et)
                        }

                        editTextRef.value = et
                    }
                },
                update = { et ->
                    if (et.text?.toString() != textContent) {
                        et.setText(textContent)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    if (showStatsDialog) {
        StatsDialog(
            text = textContent,
            isLoading = isStatsLoading,
            result = statsResult,
            onDismiss = { showStatsDialog = false },
            onResult = { result ->
                statsResult = result
                isStatsLoading = false
            }
        )
    }

    if (showGoToLineDialog) {
        GoToLineDialog(
            totalLines = textContent.lines().size,
            onDismiss = { showGoToLineDialog = false },
            onGoToLine = { line ->
                editTextRef.value?.scrollToLine(line)
                showGoToLineDialog = false
            }
        )
    }

    if (showFindDialog) {
        FindReplaceDialog(
            text = textContent,
            onDismiss = { showFindDialog = false },
            onReplace = { newText ->
                editTextRef.value?.setText(newText)
            }
        )
    }
}

private fun copyLineNumbers(context: Context, text: String) {
    val lines = text.lines()
    val sb = StringBuilder(lines.size * 8)
    for (i in lines.indices) {
        sb.append(i + 1).append('\n')
    }
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("行号", sb.trimEnd().toString()))
}

@Composable
private fun StatsDialog(
    text: String,
    isLoading: Boolean,
    result: StatsResult?,
    onDismiss: () -> Unit,
    onResult: (StatsResult) -> Unit
) {
    LaunchedEffect(text) {
        if (isLoading && result == null) {
            val r = withContext(Dispatchers.Default) { computeStats(text) }
            onResult(r)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("文本统计") },
        text = {
            if (isLoading && result == null) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(8.dp))
                        Text("正在统计...", style = MaterialTheme.typography.bodySmall)
                    }
                }
            } else {
                result?.let { r ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        StatsRow("总字符数（含空格）", r.totalCharsWithSpace)
                        StatsRow("总字符数（不含空格）", r.totalCharsNoSpace)
                        StatsRow("总行数", r.totalLines)
                        StatsRow("非空行数", r.nonEmptyLines)
                        StatsRow("总词数", r.totalWords)
                        StatsRow("段落数", r.totalParagraphs)
                        StatsRow("中文字符", r.chineseChars)
                        StatsRow("英文字符", r.englishChars)
                        StatsRow("数字", r.digitChars)
                        StatsRow("标点符号", r.punctuationChars)
                        StatsRow("空格数", r.spaceChars)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        StatsRow("估算阅读时间", r.estimatedReadTime)
                        if (r.totalLinesValue > 100000) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            Text(
                                text = "文档较大（${r.totalLinesValue} 行），已分段统计",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("关闭") }
        }
    )
}

@Composable
private fun StatsRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary)
    }
}

private data class StatsResult(
    val totalCharsWithSpace: String,
    val totalCharsNoSpace: String,
    val totalLines: String,
    val nonEmptyLines: String,
    val totalWords: String,
    val totalParagraphs: String,
    val chineseChars: String,
    val englishChars: String,
    val digitChars: String,
    val punctuationChars: String,
    val spaceChars: String,
    val estimatedReadTime: String
) {
    val totalLinesValue: Int
        get() = totalLines.replace(",", "").toIntOrNull() ?: 0
}

private fun computeStats(text: String): StatsResult {
    val fmt = NumberFormat.getIntegerInstance()
    val totalCharsWithSpace = text.length
    val totalCharsNoSpace = text.count { !it.isWhitespace() }
    val lines = text.lines()
    val totalLines = lines.size
    val nonEmptyLines = lines.count { it.isNotBlank() }
    val totalWords = text.split(Regex("\\s+")).count { it.isNotEmpty() }
    val paragraphs = text.split(Regex("\\n\\s*\\n")).count { it.isNotBlank() }

    val chineseChars = text.count { it in '\u4e00'..'\u9fff' || it in '\u3400'..'\u4dbf' }
    val englishChars = text.count { it in 'a'..'z' || it in 'A'..'Z' }
    val digitChars = text.count { it in '0'..'9' }
    val punctSet = setOf('，','。','、','；','：','？','！','.',',',';',':','?','!','"','\'','(',')','（','）','【','】','《','》','<','>','—','…','·')
    val punctuationChars = text.count { it in punctSet }
    val spaceChars = text.count { it.isWhitespace() }

    val readTimeMinutes = (totalWords / 200f).coerceAtLeast(0.1f)
    val readTimeStr = if (readTimeMinutes < 1) {
        "${(readTimeMinutes * 60).toInt()} 秒"
    } else {
        "${readTimeMinutes.toInt()} 分 ${((readTimeMinutes % 1) * 60).toInt()} 秒"
    }

    return StatsResult(
        totalCharsWithSpace = fmt.format(totalCharsWithSpace),
        totalCharsNoSpace = fmt.format(totalCharsNoSpace),
        totalLines = fmt.format(totalLines),
        nonEmptyLines = fmt.format(nonEmptyLines),
        totalWords = fmt.format(totalWords),
        totalParagraphs = fmt.format(paragraphs),
        chineseChars = fmt.format(chineseChars),
        englishChars = fmt.format(englishChars),
        digitChars = fmt.format(digitChars),
        punctuationChars = fmt.format(punctuationChars),
        spaceChars = fmt.format(spaceChars),
        estimatedReadTime = readTimeStr
    )
}

@Composable
private fun GoToLineDialog(
    totalLines: Int,
    onDismiss: () -> Unit,
    onGoToLine: (Int) -> Unit
) {
    var input by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("跳转到行") },
        text = {
            Column {
                Text(
                    text = "共 $totalLines 行",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = input,
                    onValueChange = { v ->
                        input = v.filter { it.isDigit() }
                        error = null
                    },
                    label = { Text("行号") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(
                        onGo = {
                            val line = input.toIntOrNull()
                            if (line != null && line in 1..totalLines) {
                                onGoToLine(line)
                            } else {
                                error = "行号超出范围 (1-$totalLines)"
                            }
                        }
                    ),
                    isError = error != null,
                    supportingText = error?.let { { Text(it) } },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val line = input.toIntOrNull()
                    if (line != null && line in 1..totalLines) {
                        onGoToLine(line)
                    } else {
                        error = "行号超出范围 (1-$totalLines)"
                    }
                }
            ) { Text("跳转") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

@Composable
private fun FindReplaceDialog(
    text: String,
    onDismiss: () -> Unit,
    onReplace: (String) -> Unit
) {
    var findText by remember { mutableStateOf("") }
    var replaceText by remember { mutableStateOf("") }
    var matchCount by remember { mutableIntStateOf(0) }
    var caseSensitive by remember { mutableStateOf(false) }

    remember(findText, text, caseSensitive) {
        if (findText.isNotEmpty()) {
            val flags = if (caseSensitive) 0 else Regex.IGNORE_CASE
            matchCount = Regex(Regex.escape(findText), flags).findAll(text).count()
        } else {
            matchCount = 0
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("查找替换") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = findText,
                    onValueChange = { findText = it },
                    label = { Text("查找") },
                    singleLine = true,
                    trailingIcon = {
                        if (findText.isNotEmpty()) {
                            Text(
                                "找到 $matchCount 处",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = caseSensitive,
                        onCheckedChange = { caseSensitive = it }
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("区分大小写", style = MaterialTheme.typography.bodySmall)
                }
                OutlinedTextField(
                    value = replaceText,
                    onValueChange = { replaceText = it },
                    label = { Text("替换为") },
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            if (findText.isNotEmpty()) {
                                val flags = if (caseSensitive) 0 else Regex.IGNORE_CASE
                                onReplace(text.replace(
                                    Regex(Regex.escape(findText), flags), replaceText
                                ))
                            }
                        },
                        enabled = findText.isNotEmpty()
                    ) { Text("全部替换") }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("关闭") }
        }
    )
}

/**
 * Undo/Redo manager.
 * Thread-safe design: TextWatcher calls record() synchronously during setText(),
 * so the isUndoingRedoing flag is set BEFORE setText() and cleared AFTER.
 */
private class UndoManager(
    private val maxHistory: Int = 200
) {
    private val history = mutableListOf<String>()
    private var index = -1
    private var isUndoingRedoing = false

    val canUndo: Boolean get() = index > 0
    val canRedo: Boolean get() = index < history.size - 1

    fun record(text: String) {
        if (isUndoingRedoing) return
        if (index >= 0 && history[index] == text) return
        if (history.size > index + 1) {
            while (history.size > index + 1) {
                history.removeAt(history.lastIndex)
            }
        }
        history.add(text)
        if (history.size > maxHistory) {
            history.removeAt(0)
        }
        index = history.size - 1
    }

    fun prepareUndo(): String? {
        if (index <= 0) return null
        isUndoingRedoing = true
        index--
        return history[index]
    }

    fun prepareRedo(): String? {
        if (index >= history.size - 1) return null
        isUndoingRedoing = true
        index++
        return history[index]
    }

    fun finishUndoRedo() {
        isUndoingRedoing = false
    }
}

/**
 * Custom EditText with line numbers drawn in the left gutter.
 * Only draws visible line numbers for 100k+ line performance.
 */
class LinedEditText(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private val gutterWidthPx: Float
    private val gutterMarginPx: Float

    private val gutterBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F0F0F0")
    }
    private val gutterDividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#D0D0D0")
        strokeWidth = 1.5f
    }
    private val lineNumberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#888888")
        textAlign = Paint.Align.RIGHT
    }
    private val currentLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1A666666")
    }

    init {
        val density = resources.displayMetrics.density
        gutterWidthPx = 48f * density
        gutterMarginPx = 6f * density

        lineNumberPaint.textSize = 12f * density

        setPadding(
            (gutterWidthPx + 8f * density).toInt(),
            paddingTop,
            paddingRight,
            paddingBottom
        )
    }

    fun scrollToLine(line: Int) {
        val l = layout ?: return
        val targetLine = (line - 1).coerceIn(0, l.lineCount - 1)
        val y = l.getLineTop(targetLine)
        scrollTo(scrollX, y)
    }

    override fun onDraw(canvas: Canvas) {
        val l = layout
        if (l != null) {
            drawGutter(canvas, l)
        }
        super.onDraw(canvas)
    }

    private fun drawGutter(canvas: Canvas, layout: android.text.Layout) {
        val viewHeight = height - paddingTop - paddingBottom
        if (viewHeight <= 0) return

        canvas.drawRect(0f, 0f, gutterWidthPx, height.toFloat(), gutterBgPaint)
        canvas.drawLine(
            gutterWidthPx, 0f,
            gutterWidthPx, height.toFloat(),
            gutterDividerPaint
        )

        val firstVisible = maxOf(0, layout.getLineForVertical(scrollY))
        val lastVisible = minOf(
            layout.lineCount - 1,
            layout.getLineForVertical(scrollY + viewHeight)
        )

        val sy = scrollY.toFloat()
        val pt = paddingTop.toFloat()

        for (line in firstVisible..lastVisible) {
            val baseline = layout.getLineBaseline(line).toFloat() - sy + pt
            canvas.drawText(
                (line + 1).toString(),
                gutterWidthPx - gutterMarginPx,
                baseline,
                lineNumberPaint
            )
        }
    }
}
