package com.myweather.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "Jacky-start")
        setContentView(R.layout.activity_main)
        Log.d("MainActivity", "Jacky-end")
    }
}
