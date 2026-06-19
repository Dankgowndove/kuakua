package com.calldad.boast

import android.app.Application
import android.content.ComponentCallbacks2
import android.util.Log

class BoastApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        Log.d("BoastApplication", "应用启动")
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
            }
            ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> {
                Log.d("BoastApplication", "UI 隐藏，可以释放一些资源")
            }
        }
    }
}