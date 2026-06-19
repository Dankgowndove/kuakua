package com.calldad.boast.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.calldad.boast.ui.MainScreen
import com.calldad.boast.ui.SettingsScreen
import com.calldad.boast.ui.DocumentScreen
import com.calldad.boast.ui.NoteEditorScreen
import com.calldad.boast.viewmodel.ComplimentViewModel
import com.calldad.boast.viewmodel.SettingsViewModel
import com.calldad.boast.viewmodel.DocumentViewModel

/**
 * 导航图
 * 定义应用内所有页面的导航路由
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    complimentViewModel: ComplimentViewModel,
    settingsViewModel: SettingsViewModel,
    documentViewModel: DocumentViewModel
) {
    var currentDocumentId by androidx.compose.runtime.remember { mutableStateOf<String?>(null) }
    
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                viewModel = complimentViewModel,
                settingsViewModel = settingsViewModel,
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        
        composable(Screen.NoteEditor.route) {
            NoteEditorScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { 
                    navController.popBackStack()
                    currentDocumentId = null
                },
                onNavigateToDocument = { documentId ->
                    currentDocumentId = documentId
                    navController.navigate(Screen.Document.createRoute(documentId))
                },
                onNavigateToEditor = {
                    navController.navigate(Screen.NoteEditor.route)
                },
                currentDocumentId = currentDocumentId
            )
        }
        
        composable(
            route = Screen.Document.route,
            arguments = listOf(
                navArgument("documentId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId") ?: "README"
            DocumentScreen(
                viewModel = documentViewModel,
                documentId = documentId,
                onNavigateBack = { 
                    navController.popBackStack()
                    // 返回设置页面时不清除currentDocumentId，以便高亮显示
                }
            )
        }
    }
}