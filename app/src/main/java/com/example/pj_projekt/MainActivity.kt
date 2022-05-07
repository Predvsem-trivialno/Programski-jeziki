package com.example.pj_projekt

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.pj_projekt.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnScanQR.setOnClickListener{
            IntentIntegrator(this).initiateScan()
        }
    }

    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
        if(result!=null){
            if(result.contents==null){
                Toast.makeText(applicationContext,"Cancelled", Toast.LENGTH_LONG).show()
            } else {
                try{
                    val json = result.contents
                    val gson = Gson()
                    val id = gson.fromJson(json, String::class.java)
                    Toast.makeText(applicationContext,"Scanned postbox with id: $id", Toast.LENGTH_SHORT).show()
                } catch (e: Exception){
                    binding.root.performHapticFeedback(HapticFeedbackConstants.REJECT)
                    Toast.makeText(applicationContext,"Invalid QR code", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}