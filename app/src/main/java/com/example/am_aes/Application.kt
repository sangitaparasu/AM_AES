package com.example.am_aes

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application(){
    companion object {
        private lateinit var instance: com.example.am_aes.Application

        fun getInstance(): com.example.am_aes.Application {
            return instance
        }

    }
    override fun onCreate() {
        super.onCreate()
        instance = this

    }
}