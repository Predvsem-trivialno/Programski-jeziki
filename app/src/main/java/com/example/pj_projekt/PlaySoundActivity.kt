package com.example.pj_projekt

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pj_projekt.databinding.ActivityPlaySoundBinding
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.RequestBody

class PlaySoundActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityPlaySoundBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaySoundBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var mp = MediaPlayer.create(this, R.raw.testeffect)
        binding.openButton.setOnClickListener {
            mp.start()
        }
    }

    fun makeHttpRequest() {
        val formBody = FormBody.Builder()
            .add("boxId", "358")
            .add("tokenFormat", "2")
            .build()
    }
}