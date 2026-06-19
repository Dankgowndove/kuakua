# 开发文档

## 项目结构

```
app/src/main/kotlin/com/calldad/boast/
├── MainActivity.kt
├── navigation/
│   ├── Screen.kt
│   └── NavGraph.kt
├── ui/
│   ├── MainScreen.kt
│   ├── SettingsScreen.kt
│   ├── DocumentScreen.kt
│   ├── components/
│   │   ├── PopupTextItem.kt
│   │   ├── PopupTextsList.kt
│   │   └── settings/
│   │       ├── MusicSetting.kt
│   │       ├── BackgroundSetting.kt
│   │       ├── ThemeSetting.kt
│   │       ├── WordLibrarySetting.kt
│   │       └── DocumentSetting.kt
│   └── theme/
│       ├── Theme.kt
│       ├── Color.kt
│       └── Type.kt
├── viewmodel/
│   ├── ComplimentViewModel.kt
│   ├── SettingsViewModel.kt
│   └── DocumentViewModel.kt
├── data/
│   ├── PopupTextData.kt
│   ├── database/
│   │   ├── ComplimentDatabase.kt
│   │   ├── ComplimentDao.kt
│   │   └── ComplimentEntity.kt
│   └── preferences/
│       └── AppPreferences.kt
└── music/
    ├── MusicPlayer.kt
    └── MusicRepository.kt
```

## 核心类说明

### ViewModel

#### ComplimentViewModel

管理夸赞语句的生成和显示。

```kotlin
class ComplimentViewModel : ViewModel() {
    val currentTexts: StateFlow<List<PopupTextData>>
    fun generateRandomCompliment()
    fun clearAllCompliments()
    fun removeCompliment(id: String)
}
```

#### SettingsViewModel

管理应用设置状态。

```kotlin
class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    val musicEnabled: StateFlow<Boolean>
    val musicVolume: StateFlow<Float>
    val backgroundType: StateFlow<String>
    val themeMode: StateFlow<String>
    fun toggleMusic()
    fun setMusicVolume(volume: Float)
    fun addCustomCompliment(text: String, category: String)
}
```

### 数据层

#### AppPreferences

使用 DataStore 存储用户设置。

```kotlin
class AppPreferences(private val context: Context) {
    val musicEnabled: Flow<Boolean>
    suspend fun setMusicEnabled(enabled: Boolean)
}
```

#### ComplimentDatabase

Room 数据库，存储夸赞语句。

```kotlin
@Database(entities = [ComplimentEntity::class], version = 1)
abstract class ComplimentDatabase : RoomDatabase() {
    abstract fun complimentDao(): ComplimentDao
}
```

### 音乐模块

#### MusicPlayer

封装 ExoPlayer 播放逻辑。

```kotlin
class MusicPlayer(private val context: Context) {
    val isPlaying: StateFlow<Boolean>
    fun playMusic(index: Int)
    fun pauseMusic()
    fun setVolume(volume: Float)
}
```

## 数据模型

### PopupTextData

夸赞语句数据类。

```kotlin
data class PopupTextData(
    val id: String,
    val text: String
)
```

### ComplimentEntity

数据库实体。

```kotlin
@Entity(tableName = "compliments")
data class ComplimentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val category: String,
    val isCustom: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
```

## 开发指南

### 添加新的设置项

1. 在 `AppPreferences` 中添加新的 Flow 和 setter
2. 在 `SettingsViewModel` 中添加对应的 StateFlow
3. 在设置组件中添加 UI
4. 测试设置项的持久化

### 添加新的文档

1. 在 `app/src/main/assets/documents/` 中添加新的 Markdown 文件
2. 在 `DocumentViewModel` 中添加文档信息
3. 在 `DocumentSetting` 中添加文档列表项

### 自定义主题

在 `ui/theme/Color.kt` 中添加新的颜色方案，然后在 `ThemeSetting` 中引用。

## 依赖说明

主要依赖库：

- Jetpack Compose：UI 框架
- Navigation Compose：导航组件
- DataStore：数据持久化
- ExoPlayer：音乐播放
- Room：本地数据库
- Coil：图片加载
- Markwon：Markdown 解析