# 系统架构

## 技术栈

- **开发语言**：Kotlin
- **UI框架**：Jetpack Compose
- **设计规范**：Material Design 3
- **最低版本**：Android 5.0 (API 21)
- **目标版本**：Android 14 (API 36)

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
   - 负责业务逻辑和状态管理

3. **Model 层**
   - 数据实体（Entity）
   - 数据访问对象（DAO）
   - 数据仓库（Repository）
   - 负责数据存储和获取

## 核心模块

### 1. 数据持久化

- **DataStore**：存储用户设置
- **Room**：存储夸赞语句数据
- **SharedPreferences**：兼容旧版数据存储

### 2. 音乐播放

- **ExoPlayer**：播放背景音乐
- **MusicPlayer**：封装播放器逻辑
- **MusicRepository**：管理音乐资源

### 3. 导航系统

- **Navigation Compose**：页面导航
- **NavGraph**：定义导航路由
- **Screen**：路由定义

### 4. 文档系统

- **Markdown**：文档格式
- **Assets**：存储文档资源
- **DocumentViewModel**：文档加载逻辑

## 数据流向

```
用户操作 -> View -> ViewModel -> Model
                ↓
           UI 更新
```

## 组件关系

```
MainActivity
    ├─ NavGraph
    │   ├─ MainScreen
    │   │   └─ ComplimentViewModel
    │   ├─ SettingsScreen
    │   │   └─ SettingsViewModel
    │   └─ DocumentScreen
    │        └─ DocumentViewModel
    ├─ MusicPlayer
    └─ AppPreferences
```

## 设计原则

1. **单一职责**：每个类和模块只负责一个功能
2. **依赖注入**：使用依赖注入管理组件依赖
3. **响应式编程**：使用 Flow 和 StateFlow 实现响应式数据流
4. **生命周期感知**：使用 ViewModel 管理生命周期相关状态