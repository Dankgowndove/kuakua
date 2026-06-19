package com.calldad.boast.navigation

/**
 * 导航路由定义
 * 定义应用中所有页面的路由路径
 */
sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object Settings : Screen("settings")
    data object NoteEditor : Screen("note_editor")
    data object Document : Screen("document/{documentId}") {
        fun createRoute(documentId: String) = "document/$documentId"
    }
}