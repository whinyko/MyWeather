package com.myweather.android.ui.place

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myweather.android.MainActivity
import com.myweather.android.R

//import com.myweather.android.ui.weather.WeatherActivity
//import kotlinx.android.synthetic.main.fragment_place.*

class PlaceFragment : Fragment() {

//    val viewModel by lazy { ViewModelProviders.of(this).get(PlaceViewModel::class.java) }
    val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter
    

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("PlaceFragment", "onCreateView-start")
        return inflater.inflate(R.layout.fragment_place, container, false)
        Log.d("PlaceFragment", "onCreateView-end")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("PlaceFragment", "onViewCreated-start")
        //Get views
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val searchPlaceEdit = view.findViewById<EditText>(R.id.searchPlaceEdit)
        val bgImageView = view.findViewById<ImageView>(R.id.bgImageView)

        Log.d("PlaceFragment", "new RecyclerView.PlaceAdapter-start")
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)
        recyclerView.adapter = adapter
        Log.d("PlaceFragment", "new RecyclerView.PlaceAdapter-end")

        searchPlaceEdit.addTextChangedListener { editable -> val content = editable.toString()
            Log.d("PlaceFragment", "content value: " + content)
            if (content.isNotEmpty()) {
                Log.d("PlaceFragment", "search content: " + content)
                viewModel.searchPlaces(content)
            } else {
                recyclerView.visibility = View.GONE
                //val bgImageView = view.findViewById<ImageView>(R.id.bgImageView)
                bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer { result ->
            Log.d("PlaceFragment.observe", "observe start.")
            val places = result.getOrNull()
            Log.d("PlaceFragment.observe", "places value:" + places.toString())
            if (places != null) {
                //val recyclerView2 = view.findViewById<RecyclerView>(R.id.recyclerView)
                //val bgImageView2 = view.findViewById<ImageView>(R.id.bgImageView)
                recyclerView.visibility = View.VISIBLE
                Log.d("PlaceFragment.observe","set recycleView visibility - visible")
                bgImageView.visibility = View.GONE
                Log.d("PlaceFragment.observe","set bgImageView visibility - gone")
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                Log.d("PlaceFragment.observe","add places to viewModel.placeList")
                adapter.notifyDataSetChanged()
                Log.d("PlaceFragment.observe","adapter.notifyDataSetChanged() completed")
            } else {
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            Log.d("PlaceFragment.observe", "observe end.")
        })
        Log.d("PlaceFragment", "onViewCreated-end")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onAttach(context:Context) {
        super.onAttach(context)

//        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer { result ->
//            Log.d("PlaceFragment.observe", "observe start.")
//            val places = result.getOrNull()
//            Log.d("PlaceFragment.observe", "places value:" + places.toString())
//            if (places != null) {
//                val recyclerView = activity.findViewById<RecyclerView>(R.id.recyclerView)
//                val bgImageView = activity.findViewById<ImageView>(R.id.bgImageView)
//
//                recyclerView.visibility = View.VISIBLE
//                Log.d("PlaceFragment.observe","set recycleView visibility - visible")
//                bgImageView.visibility = View.GONE
//                Log.d("PlaceFragment.observe","set bgImageView visibility - gone")
//                viewModel.placeList.clear()
//                viewModel.placeList.addAll(places)
//                Log.d("PlaceFragment.observe","add places to viewModel.placeList")
//                adapter.notifyDataSetChanged()
//                Log.d("PlaceFragment.observe","adapter.notifyDataSetChanged() completed")
//            } else {
//                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
//                result.exceptionOrNull()?.printStackTrace()
//            }
//            Log.d("PlaceFragment.observe", "observe end.")
//        })
    }

    /*
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (activity is MainActivity && viewModel.isPlaceSaved()) {
            val place = viewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)
        recyclerView.adapter = adapter
        searchPlaceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                recyclerView.visibility = View.GONE
                bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.placeLiveData.observe(this, Observer { result ->
            val places = result.getOrNull()
            if (places != null) {
                recyclerView.visibility = View.VISIBLE
                bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }
    */
}