package com.example.pj_projekt

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.pj_projekt.databinding.ActivityPlaySoundBinding
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import kotlin.concurrent.thread

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

        binding.requestButton.setOnClickListener{
            makeHttpRequest()
        }
    }

    data class parameters(val boxId: String, val tokenFormat: Int)

    fun makeHttpRequest() {
        thread(start = true) {
            val client = OkHttpClient()
            val jsonParams = Gson().toJson(parameters("358", 2))
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val formBody = jsonParams.toRequestBody(mediaType)

            val token = "9ea96945-3a37-4638-a5d4-22e89fbc998f"

            val request = Request.Builder()
                .url("https://api-ms-stage.direct4.me/sandbox/v1/Access/openbox")
                .addHeader("Authorization", "Bearer " + token)
                .post(formBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body
            if (!response.isSuccessful) {
                Log.i("Response code: ", response.code.toString())
            }
            else {
                Log.i("Response code: ", response.code.toString())
            }
        }
    }
}