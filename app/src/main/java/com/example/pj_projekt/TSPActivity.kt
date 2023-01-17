package com.example.pj_projekt

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import com.example.pj_projekt.data.TSP
import com.example.pj_projekt.databinding.ActivityTspBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places


class TSPActivity : BaseActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityTspBinding
    private var map: GoogleMap? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null
    private var markers: ArrayList<LatLng> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTspBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TSP()

        Places.initialize(this, BuildConfig.MAPS_API_KEY)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun TSP(){
        val tsp = TSP(app.locations, app.distanceType, 100000)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        map?.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        map.clear()
        markers.clear()
        generateMarkers()
        generatePaths()
    }

    private fun generateMarkers(){
        var first = true
        for(location: com.example.pj_projekt.data.Location in app.locations){
            if(location.isSelected()){
                val position = LatLng(location.getCoordLat(), location.getCoordLong())
                if(first){
                    map?.animateCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM))
                    first = false
                }
                val markerOptions = MarkerOptions()
                markerOptions.position(position)
                markerOptions.title(location.getIndex().toString() + " " + location.getNumOfBoxes())
                map?.addMarker(markerOptions)
                markers.add(position)
            }
        }
    }

    private fun generatePaths(){
        map?.addPolyline(PolylineOptions()
            .addAll(markers)
            .color(Color.BLACK)
            .width(10.0F)
        )
    }


    companion object {
        private const val DEFAULT_ZOOM = 13F
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
    }
}