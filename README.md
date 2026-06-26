### 📱 夸夸 (Kuakua) 项目 README

这是一个基于 Android 平台的“夸夸”应用，旨在通过随机生成的赞美语句为用户带来积极的情绪价值。项目采用现代化的 Android 技术栈构建，注重简洁的用户体验和高效的开发流程。

#### 📖 项目简介

夸夸应用的核心功能是向用户展示温暖、积极的赞美。应用内置了丰富的赞美语料库，用户可以随机获取一句赞美，并支持查看历史记录、自定义设置以及阅读相关文档。

#### ✨ 主要功能

*   **随机赞美**: 点击主界面按钮，即可随机获取一条精心准备的赞美语句。
*   **历史记录**: 应用会保存用户获取过的赞美，方便随时回顾。
*   **个性化设置**:
    *   **主题切换**: 支持 Material Design 3 动态配色及多种预设主题。
    *   **背景音乐**: 内置多首舒缓的背景音乐，可自由切换。
    *   **词库管理**: 支持对赞美词库进行增删改查。
*   **文档与编辑**:
    *   **Markdown 阅读**: 内置 Markdown 渲染器，用于查看帮助文档等。
    *   **文本编辑器**: 提供简易的文本编辑功能，支持行号、撤销/重做等。

#### 🛠️ 技术栈

本项目采用 MVVM 架构和单 Activity 模式，主要技术选型如下：

| 类别 | 技术 |
| :--- | :--- |
| **语言** | Kotlin 2.4.0 |
| **UI 框架** | Jetpack Compose + Material Design 3 |
| **架构** | MVVM |
| **构建系统** | Gradle 9.6.0 (Kotlin DSL), AGP 9.2.1 |
| **依赖注入** | KSP (Kotlin Symbol Processing) |
| **导航** | Navigation Compose 2.9.8 |
| **数据持久化** | Room 2.8.4 (数据库), DataStore Preferences (键值对) |
| **网络/媒体** | Media3 ExoPlayer 1.10.1 (音乐播放), Coil 2.7.0 (图片加载) |
| **其他** | Markwon 4.6.2 (Markdown 渲染), Gson 2.14.0 (JSON 解析) |

#### 🚀 构建与运行

本项目**不推荐在本地进行构建**，而是通过 GitHub Actions 进行云端持续集成。

1.  将代码推送到 `master` 或 `main` 分支。
2.  GitHub Actions 会自动触发构建流程（`.github/workflows/android-build.yml`）。
3.  构建流程会依次执行 `assembleDebug` 和 `assembleRelease`，并将生成的 APK 文件作为 Artifact 上传。

**构建环境要求**
*   **JDK**: 21 (Temurin)
*   **Gradle**: 9.6.0 (Wrapper)
*   **Kotlin**: 2.4.0
*   **AGP**: 9.2.1

#### ⚙️ 关键配置

*   **Room 数据库**: 为适配特定开发环境，已配置 `room.skipVerification = true` 以跳过 schema 校验。
*   **资源压缩**: Release 构建时，仅保留中文 (`zh`, `zh-rCN`) 语言资源以减小 APK 体积。
*   **代码混淆**: Release 构建启用了 R8 代码混淆和资源压缩，规则文件位于 `app/proguard-rules.pro`。
*   **内存优化**: Coil 图片加载库的内存缓存上限设置为可用内存的 25%。
*   **音乐资源**: 项目内置了 4 首背景音乐轨道，由 `MusicRepository.kt` 统一管理。
*   **无测试**: 当前项目未配置任何单元测试或 UI 测试。
