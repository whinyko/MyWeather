package com.myweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class MyWeatherApplication: Application() {
    companion object{
        const val TOKEN = "V517jki5W6sPXHtd" //it's from https://platform.caiyunapp.com/login?redirect=/dashboard

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}