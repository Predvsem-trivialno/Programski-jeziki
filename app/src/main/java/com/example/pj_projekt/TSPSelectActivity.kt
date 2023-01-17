package com.example.pj_projekt

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pj_projekt.data.Location
import com.example.pj_projekt.databinding.ActivityTspselectBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class TSPSelectActivity : BaseActivity() {

    private lateinit var binding: ActivityTspselectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTspselectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartTsp.setOnClickListener{
            var counter = 0
            for(l: Location in app.locations){
                if(l.isSelected()) counter++;
            }
            if(counter>50){
                Toast.makeText(applicationContext,"You selected $counter / 50 locations. Please deselect some locations.", Toast.LENGTH_LONG).show()
            } else if(counter<2){
                Toast.makeText(applicationContext,"You selected only $counter location(s). The solution is trivial. Please select more locations.", Toast.LENGTH_LONG).show()
            } else {
                app.distanceType = binding.distanceTypeSpinner.selectedItem.toString()
                showTSP()
            }
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
        val file = resources.openRawResource(R.raw.locations)
        val lines: MutableList<String> = ArrayList()
        try {
            BufferedReader(InputStreamReader(file)).use { br ->
                var line = br.readLine()
                while (line != null) {
                    lines.add(line)
                    line = br.readLine()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        for ((num, line) in lines.withIndex()) {
            Log.i("line", line)
            val splitLine = line.split("~").toTypedArray()
            app.locations.add(Location(num,splitLine[0],splitLine[1],splitLine[2],splitLine[3].toInt(),java.lang.Double.valueOf(splitLine[4]),java.lang.Double.valueOf(splitLine[5])))
            app.locations[app.locations.size-1].select(false)
        }
    }
}