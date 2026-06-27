# 系统架构

## 技术栈

- **开发语言**：Kotlin
- **UI框架**：Jetpack Compose + Material Design 3
- **最低版本**：Android 6.0 (API 23)
- **目标版本**：Android 16 (API 37)

## 架构模式

应用采用 MVVM（Model-View-ViewModel）架构模式。

### MVVM 分层

1. **View 层**
   - MainActivity：应用入口
   - Composable 组件：UI 界面
   - 负责用户交互和界面显示

2. **ViewModel 层**
   - ComplimentViewModel：管理夸赞语句状态
   - SettingsViewModel：管理设置状态
   - DocumentViewModel：管理文档状态

3. **Model 层**
   - 数据实体（Entity）
   - 数据访问对象（DAO）
   - 数据仓库（Repository）

## 核心模块

### 1. 数据持久化

- **DataStore**：存储用户设置
- **Room**：存储夸赞语句数据

### 2. 音乐播放

- **ExoPlayer**：播放背景音乐
- 支持多首音乐切换和音量控制

### 3. 导航系统

- **Navigation Compose**：页面导航
- 支持文档历史记录浏览

### 4. 文档系统

- **Markwon**：Markdown 渲染
- **Assets**：存储文档资源
- 支持文档内搜索

### 5. 主题系统

- 支持动态取色（Android 12+）
- 多种预设颜色方案
- 暗色/亮色模式切换

## 数据流向

```
用户操作 -> View -> ViewModel -> Model
                ↓
           UI 更新
```

## 设计原则

1. **单一职责**：每个类和模块只负责一个功能
2. **响应式编程**：使用 Flow 和 StateFlow 实现响应式数据流
3. **生命周期感知**：使用 ViewModel 管理生命周期相关状态
