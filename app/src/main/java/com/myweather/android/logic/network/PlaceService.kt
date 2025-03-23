package com.myweather.android.logic.network

import com.myweather.android.MyWeatherApplication
import com.myweather.android.logic.model.PlaceResponse
import retrofit2.http.Query
import retrofit2.Call
import retrofit2.http.GET

interface PlaceService {
    @GET("v2/place?token=${MyWeatherApplication.TOKEN}&lang=zh_CN")
    fun searchPlaces(@Query("query") query: String): Call<PlaceResponse>
}