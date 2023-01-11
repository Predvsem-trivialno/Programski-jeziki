package com.example.pj_projekt

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pj_projekt.data.Location
import com.example.pj_projekt.databinding.ActivityTspselectBinding

class TSPSelectActivity : BaseActivity() {

    private lateinit var binding: ActivityTspselectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTspselectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartTsp.setOnClickListener{
            showTSP()
        }

        fillLocations()

        binding.locationRecycler.layoutManager = LinearLayoutManager(applicationContext)
        val adapter = LocationAdapter(app.locations, object: LocationAdapter.MyOnClick{
            @SuppressLint("NotifyDataSetChanged")
            override fun onClick(p0: View?, position: Int) {
                app.locations[position].select(!app.locations[position].isSelected())
                binding.locationRecycler.adapter?.notifyDataSetChanged()
            }
        })
        binding.locationRecycler.adapter = adapter
    }

    private fun fillLocations(){
        app.locations.add(Location("Koroski most",46.563158, 15.627748))
        app.locations.add(Location("FERI",46.558952, 15.638226))
        app.locations.add(Location("Glavni trg",46.557674, 15.645588))
        app.locations.add(Location("AP MARIBOR",46.559386, 15.655580))
        app.locations.add(Location("Europark",46.554076, 15.652252))
    }
}