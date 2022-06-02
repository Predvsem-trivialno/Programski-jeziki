package com.example.pj_projekt

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pj_projekt.databinding.ActivityLogsBinding
import org.json.JSONObject

class LogsActivity : BaseActivity() {
    private lateinit var binding: ActivityLogsBinding
    var data = ArrayList<logStructure>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ls = logStructure("ls", "test", true)
        val ls2 = logStructure("ls2", "test2", false)
        data.add(ls)
        data.add(ls2)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = logAdapter(data)
        binding.recyclerView.adapter = adapter
    }
}