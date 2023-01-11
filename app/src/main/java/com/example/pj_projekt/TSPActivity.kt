package com.example.pj_projekt

import android.location.Location
import android.os.Bundle
import com.example.pj_projekt.databinding.ActivityTspBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

class TSPActivity : BaseActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityTspBinding
    private var selectedLocation: Marker? = null;
    private var map: GoogleMap? = null
    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTspBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TSP()

        Places.initialize(this, BuildConfig.MAPS_API_KEY)
        placesClient = Places.createClient(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun TSP(){
        // execute TSP here
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
        map.setOnMapClickListener { latLng -> // Creating a marker
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.title(latLng.latitude.toString() + " : " + latLng.longitude)
            map.clear()
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            selectedLocation = map.addMarker(markerOptions)
        }
    }

    companion object {
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
    }
}