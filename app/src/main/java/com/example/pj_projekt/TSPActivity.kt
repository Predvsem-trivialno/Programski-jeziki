package com.example.pj_projekt

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import com.example.pj_projekt.data.GA
import com.example.pj_projekt.data.TSP
import com.example.pj_projekt.databinding.ActivityTspBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import org.w3c.dom.Text


class TSPActivity : BaseActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityTspBinding
    private var map: GoogleMap? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null
    private var markers: ArrayList<LatLng> = arrayListOf()
    private var selectedLocations: ArrayList<com.example.pj_projekt.data.Location> = arrayListOf()
    private lateinit var bestTour: TSP.Tour

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTspBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareSelectedLocations()
        TSP()

        Places.initialize(this, BuildConfig.MAPS_API_KEY)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun TSP(){
        val tsp = TSP(selectedLocations, app.distanceType, 1000000)
        val ga = GA(100, 0.8, 0.1)
        bestTour = ga.execute(tsp)
    }

    private fun prepareSelectedLocations(){
        selectedLocations.clear()
        var i=0
        val locations: ArrayList<com.example.pj_projekt.data.Location> = app.locations.clone() as ArrayList<com.example.pj_projekt.data.Location>
        for(l: com.example.pj_projekt.data.Location in locations){
            if(l.isSelected()) {
                l.setIndex(i)
                selectedLocations.add(l)
                i++
            }
        }
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
        var counter = 1
        for(city: TSP.City in bestTour.path){
            val position = LatLng(selectedLocations[city.index].getCoordLat(), selectedLocations[city.index].getCoordLong())
            val markerOptions = MarkerOptions()
            if(counter == 1){
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM))
            } else if(counter == bestTour.path.size) {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            } else {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            }
            markerOptions.position(position)
            markerOptions.title(counter.toString() + ": " + selectedLocations[city.index].getAddress() + " (št. paketnikov: " + selectedLocations[city.index].getNumOfBoxes() + ")")
            map?.addMarker(markerOptions)
            markers.add(position)
            counter++
        }
    }

    private fun generatePaths(){
        map?.addPolyline(PolylineOptions()
            .addAll(markers)
            .color(Color.BLACK)
            .width(10.0F)
        )
        map?.addPolyline(PolylineOptions()
            .add(markers[0])
            .add(markers[markers.size-1])
            .color(Color.RED)
            .width(6.0F)
        )
        /*
        Ta koda bi morala izrisovati puščice v smeri črte, ampak zaradi nekega razloga ne naredi nič.
        vir: https://cloud.google.com/blog/products/maps-platform/announcing-advanced-polylines-maps-sdks-android
        val style: StampStyle = TextureStyle.newBuilder(BitmapDescriptorFactory.fromResource(R.drawable.arrow)).build()
        map?.addPolyline(PolylineOptions()
            .addSpan(StyleSpan(StrokeStyle.colorBuilder(Color.BLACK).stamp(style).build()))
            .addSpan(StyleSpan(StrokeStyle.colorBuilder(Color.BLUE).stamp(style).build()))
            .addAll(markers)
        )
         */
    }


    companion object {
        private const val DEFAULT_ZOOM = 13F
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
    }
}