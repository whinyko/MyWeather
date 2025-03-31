package com.myweather.android.ui.place

import android.util.Log
import androidx.lifecycle.*
import com.myweather.android.logic.Repository
import com.myweather.android.logic.model.Place
import retrofit2.http.Query


class PlaceViewModel : ViewModel() {

    private val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<Place>()

    /*
    val placeLiveData = Transformations.switchMap(searchLiveData) { query ->
        Repository.searchPlaces(query)
    }
    */

    val placeLiveData = searchLiveData.switchMap { query ->
        Repository. searchPlaces(query)
    }

    fun searchPlaces(query: String) {
        Log.d("PlaceViewModel.searchPlaces", "search - start.")
        Log.d("PlaceViewModel.searchPlaces", "query value:" + query)
        searchLiveData.value = query
        Log.d("PlaceViewModel.searchPlaces", "search - end.")
    }

    fun savePlace(place: Place) = Repository.savePlace(place)

    fun getSavedPlace() = Repository.getSavedPlace()

    fun isPlaceSaved() = Repository.isPlaceSaved()
}