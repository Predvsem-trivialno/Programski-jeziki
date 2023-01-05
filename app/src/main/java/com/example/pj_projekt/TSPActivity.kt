package com.example.pj_projekt

import android.os.Bundle
import com.example.pj_projekt.databinding.ActivityTspBinding

class TSPActivity : BaseActivity() {
    private lateinit var binding: ActivityTspBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTspBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}