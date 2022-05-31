package com.example.pj_projekt

import android.os.Bundle
import com.example.pj_projekt.databinding.ActivityLogsBinding

class LogsActivity : BaseActivity() {
    private lateinit var binding: ActivityLogsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}