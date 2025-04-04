package com.myweather.android.ui.weather

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.myweather.android.R
import com.myweather.android.databinding.ActivityWeatherBinding
import com.myweather.android.logic.Repository.refreshWeather
import com.myweather.android.logic.model.Weather
import com.myweather.android.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy {ViewModelProvider(this).get(WeatherViewModel::class.java)}

    //private lateinit var binding: ActivityWeatherBinding
    lateinit var binding: ActivityWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val decorView = window.decorView
//        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//        window.statusBarColor = Color.TRANSPARENT

        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val placeNameView = findViewById<TextView>(R.id.placeName)
        placeNameView.setOnApplyWindowInsetsListener{ v, insets ->
            val insetsCompat = insets.getInsets(WindowInsets.Type.systemBars())
            val topInset = insetsCompat.top
            placeNameView.setPadding(placeNameView.paddingLeft, topInset,
                placeNameView.paddingRight, placeNameView.paddingBottom)
            insets
        }

        val navBtnView = findViewById<Button>(R.id.navBtn)
        navBtnView.setOnApplyWindowInsetsListener{ v, insets ->
            val insetsCompat = insets.getInsets(WindowInsets.Type.systemBars())
            val topInset = insetsCompat.top
            navBtnView.setPadding(navBtnView.paddingLeft, topInset,
                navBtnView.paddingRight, navBtnView.paddingBottom)
            insets
        }

        if(viewModel.locationLng.isEmpty()){
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if(viewModel.locationLat.isEmpty()){
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if(viewModel.placeName.isEmpty()){
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }

        Log.d("WeatherActivity.kt - onCreate - location_lng", viewModel.locationLng)
        Log.d("WeatherActivity.kt - onCreate - location_lat", viewModel.locationLat)
        Log.d("WeatherActivity.kt - onCreate - place_name", viewModel.placeName)

        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather =  result.getOrNull()
            if(weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this,"無法成功獲取天氣信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }

            binding.swipeRefresh.isRefreshing = false
        })

        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        refreshWeather()
        binding.swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }

        binding.layoutNow.navBtn.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.drawerLayout.addDrawerListener(object:DrawerLayout.DrawerListener{
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })
        //viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
    }

    //private fun refreshWeather() {
    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        binding.swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {

        Log.d("WeatherActivity.kt - showWeatherInfo - placeName.text(Before)", binding.layoutNow.placeName.text.toString())
        binding.layoutNow.placeName.text = viewModel.placeName
        Log.d("WeatherActivity.kt - showWeatherInfo - placeName.text(After)", binding.layoutNow.placeName.text.toString())

        val realtime = weather.realtime
        val daily = weather.daily
        Log.d("WeatherActivity.kt - showWeatherInfo - daily", daily.toString())

        //Fill layout data for now.xml
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        binding.layoutNow.currentTemp.text = currentTempText
        binding.layoutNow.currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空氣指數 ${realtime.ariQuality.aqi.chn.toInt()}"
        binding.layoutNow.currentAQI.text = currentPM25Text
        val bgName = getSky(realtime.skycon).bg.toString()
        Log.d("WeatherActivity.kt - showWeatherInfo - bgName", bgName)
        binding.layoutNow.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        //Fill layout data for forecast.xml
        binding.layoutForecast.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for(i in 0 until days) {
            val skycon = daily.skycon[i]
            Log.d("WeatherActivity.kt - showWeatherInfo", skycon.toString())
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item, binding.layoutForecast.forecastLayout, false)
            val dateInfo = view.findViewById<TextView>(R.id.dateInfo)
            val skyIcon = view.findViewById<ImageView>(R.id.skyIcon)
            val skyInfo = view.findViewById<TextView>(R.id.skyInfo)
            val temperatureInfo = view.findViewById<TextView>(R.id.temperatureInfo)
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            Log.d("WeatherActivity.kt - showWeatherInfo", skycon.value?:"")
            val sky = getSky(skycon.value?:"")
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            binding.layoutForecast.forecastLayout.addView(view)
        }

        //Fill layout data for life_index.xml
        val lifeIndex = daily.lifeIndex
        binding.layoutLifeIndex.coldRiskText.text = lifeIndex.coldRisk[0].desc
        binding.layoutLifeIndex.dressingText.text = lifeIndex.dressing[0].desc
        binding.layoutLifeIndex.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        binding.layoutLifeIndex.carWashingText.text = lifeIndex.ultraviolet[0].desc

        Log.d("activityWeatherBinding.weatherLayout.visibility(before)", binding.weatherLayout.visibility.toString())
        binding.weatherLayout.visibility = View.VISIBLE
        Log.d("activityWeatherBinding.weatherLayout.visibility(after)", binding.weatherLayout.visibility.toString())






    }
}