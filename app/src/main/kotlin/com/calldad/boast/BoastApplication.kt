package com.calldad.boast

import android.app.Application
import android.content.ComponentCallbacks2
import android.util.Log
import com.calldad.boast.data.database.ComplimentDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class BoastApplication : Application() {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var database: ComplimentDatabase? = null
    
    override fun onCreate() {
        super.onCreate()
        Log.d("BoastApplication", "应用启动")
        
        // 延迟初始化数据库，避免启动卡顿
        applicationScope.launch {
            try {
                database = ComplimentDatabase.getDatabase(this@BoastApplication)
                Log.d("BoastApplication", "数据库初始化完成")
            } catch (e: Exception) {
                Log.e("BoastApplication", "数据库初始化失败", e)
            }
        }
    }
    
    /**
     * 低内存警告回调
     * 当系统内存不足时调用，应释放不必要的资源
     */
    override fun onLowMemory() {
        super.onLowMemory()
        Log.w("BoastApplication", "系统内存不足，开始清理资源")
        
        // 关闭数据库以释放内存
        database?.close()
        database = null
        
        // 建议系统进行垃圾回收
        System.gc()
    }
    
    /**
     * 内存修剪回调
     * level 表示内存紧张程度
     */
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        
        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE -> {
                Log.w("BoastApplication", "内存紧张，level=$level")
                // 可以在此清理缓存等非必要资源
            }
            ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> {
                Log.d("BoastApplication", "UI 隐藏，可以释放一些资源")
                // UI 隐藏时可以释放部分资源
            }
        }
    }
    
    override fun onTerminate() {
        super.onTerminate()
        Log.d("BoastApplication", "应用终止")
        
        // 清理数据库实例
        database?.close()
        database = null
        ComplimentDatabase.destroyInstance()
        
        // 取消所有协程
        applicationScope.cancel()
    }
}