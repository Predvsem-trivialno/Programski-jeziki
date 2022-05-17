package com.example.pj_projekt

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pj_projekt.databinding.ActivityPlaySoundBinding
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.concurrent.thread


class PlaySoundActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityPlaySoundBinding
    private lateinit var data: JSONObject

    private fun generateZip() {
        val decodedBytes = Base64.decode(data.getString("data"),Base64.NO_WRAP)
        val fos = FileOutputStream(filesDir.absolutePath+"/token.zip")
        fos.write(decodedBytes)
        fos.flush()
        fos.close()
    }

    private fun extractZip() {
        val fin = FileInputStream(filesDir.absolutePath+"/token.zip")
        val zin = ZipInputStream(fin)
        var ze: ZipEntry?
        while (zin.nextEntry.also { ze = it } != null) {
            val fos = FileOutputStream(filesDir.absolutePath+"/"+ze?.name)
            var c: Int = zin.read()
            while (c != -1) {
                fos.write(c)
                c = zin.read()
            }
            zin.closeEntry()
            fos.close()
        }
        zin.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaySoundBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mp = MediaPlayer.create(this, R.raw.testeffect)
        binding.openButton.setOnClickListener {
            mp.start()
        }

        binding.requestButton.setOnClickListener{
            makeHttpRequest()
        }
    }

    data class parameters(val boxId: String, val tokenFormat: Int)

    private fun makeHttpRequest() {
        thread(start = true) {
            val client = OkHttpClient()
            val jsonParams = Gson().toJson(parameters("352", 2))
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val formBody = jsonParams.toRequestBody(mediaType)

            val token = "9ea96945-3a37-4638-a5d4-22e89fbc998f"

            val request = Request.Builder()
                .url("https://api-ms-stage.direct4.me/sandbox/v1/Access/openbox")
                .addHeader("Authorization", "Bearer " + token)
                .post(formBody)
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                Log.i("Response code: ", response.code.toString())
            }
            else {
                Log.i("Response code: ", response.code.toString())
                data = JSONObject(response.body?.string()!!)
            }
        }
    }
}