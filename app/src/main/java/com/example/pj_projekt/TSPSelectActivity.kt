package com.example.pj_projekt

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pj_projekt.data.Location
import com.example.pj_projekt.databinding.ActivityTspselectBinding

class TSPSelectActivity : BaseActivity() {

    private lateinit var binding: ActivityTspselectBinding
    private val locations: ArrayList<Location> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTspselectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartTsp.setOnClickListener{
            showTSP()
        }

        locations.add(Location("Maribor",10.0,10.0))
        binding.locationRecycler.layoutManager = LinearLayoutManager(applicationContext)
        val adapter = LocationAdapter(locations, object: LocationAdapter.MyOnClick{
            @SuppressLint("NotifyDataSetChanged")
            override fun onClick(p0: View?, position: Int) {
                locations[position].select(!locations[position].isSelected())
                binding.locationRecycler.adapter?.notifyDataSetChanged()
            }
        })
        binding.locationRecycler.adapter = adapter
    }
}