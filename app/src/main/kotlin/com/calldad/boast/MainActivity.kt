package com.calldad.boast

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.calldad.boast.navigation.AppNavGraph
import com.calldad.boast.ui.theme.ComposeEmptyActivityTheme
import com.calldad.boast.viewmodel.ComplimentViewModel
import com.calldad.boast.viewmodel.SettingsViewModel
import com.calldad.boast.viewmodel.DocumentViewModel

class MainActivity : ComponentActivity() {
    
    private val complimentViewModel: ComplimentViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val documentViewModel: DocumentViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setupImmersiveFullscreen()
        
        setContent {
            ComposeEmptyActivityTheme(settingsViewModel) {
                val navController = rememberNavController()
                
                AppNavGraph(
                    navController = navController,
                    complimentViewModel = complimentViewModel,
                    settingsViewModel = settingsViewModel,
                    documentViewModel = documentViewModel
                )
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 清理数据库实例以防止内存泄漏
        com.calldad.boast.data.database.ComplimentDatabase.destroyInstance()
    }
    
    private fun setupImmersiveFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }
}