package com.solarcalculator

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SolarCalculatorApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // 应用初始化
    }
}
