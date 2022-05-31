package com.example.pj_projekt

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.pj_projekt.databinding.ActivityOpenBinding
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator

class OpenActivity : BaseActivity() {
    private lateinit var binding: ActivityOpenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(app.boxId.isEmpty()){
            binding.btnPlaySound.isEnabled=false
            binding.btnLogs.isEnabled=false
        }
        binding.txtWelcome.text = getString(R.string.text_welcome,app.username)
        binding.btnScanQR.setOnClickListener{
            IntentIntegrator(this).initiateScan()
        }
        binding.btnPlaySound.setOnClickListener{
            showPlaySound()
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
                    app.boxId = id
                    Toast.makeText(applicationContext,"Scanned box with id: $id", Toast.LENGTH_SHORT).show()
                    binding.txtSelected.text = getString(R.string.text_selected_2,app.boxId)
                    binding.btnPlaySound.isEnabled=true
                    binding.btnLogs.isEnabled=true
                } catch (e: Exception){
                    binding.root.performHapticFeedback(HapticFeedbackConstants.REJECT)
                    Toast.makeText(applicationContext,"Invalid QR code", Toast.LENGTH_SHORT).show()
                    binding.txtSelected.text = getString(R.string.text_selected)
                }
            }
        }
    }

    fun showPlaySoundActivity(view: View) {
        val intent = Intent(this, PlaySoundActivity::class.java)
        startActivity(intent)
    }
}