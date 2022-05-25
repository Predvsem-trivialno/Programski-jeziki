package com.example.pj_projekt

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pj_projekt.databinding.ActivityPlaySoundBinding
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.concurrent.thread


class PlaySoundActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityPlaySoundBinding
    private lateinit var data: JSONObject
    private lateinit var fileName: String
    private lateinit var boxId: String

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
            fileName = filesDir.absolutePath+"/"+ze?.name
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

    private fun playSound() {
        val mp = MediaPlayer.create(this, Uri.parse(fileName))
        runOnUiThread{Toast.makeText(applicationContext,"Playing opening sound for box $boxId...",Toast.LENGTH_SHORT).show()}
        mp.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        boxId = "530"
        binding = ActivityPlaySoundBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.openButton.setOnClickListener {
            //makeHttpRequest()
            makeOpenRequest()
        }
    }

    data class openRequest(val postboxId: String, val userId: String, val success: Boolean)

    private fun makeOpenRequest() {
        Log.i("User ID:", app.userId)
        thread(start = true) {
            val client = OkHttpClient()
            val jsonParams = Gson().toJson(openRequest(boxId, app.userId, true))
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val formBody = jsonParams.toRequestBody(mediaType)

            val request = Request.Builder()
                .url("https://pametni-paketnik.herokuapp.com/postbox/mobileOpen")
                .post(formBody)
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                Log.i("Response code: ", response.code.toString())
            }
                makeHttpRequest()
        }
    }

    data class parameters(val boxId: String, val tokenFormat: Int)

    private fun makeHttpRequest() {
        thread(start = true) {
            val client = OkHttpClient()
            val jsonParams = Gson().toJson(parameters(boxId, 2))
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val formBody = jsonParams.toRequestBody(mediaType)

            val token = "9ea96945-3a37-4638-a5d4-22e89fbc998f"

            val request = Request.Builder()
                .url("https://api-ms-stage.direct4.me/sandbox/v1/Access/openbox")
                .addHeader("Authorization", "Bearer $token")
                .post(formBody)
                .build()

            runOnUiThread{Toast.makeText(applicationContext,"Sending request to the server...",Toast.LENGTH_SHORT).show()}

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                Log.i("Response code: ", response.code.toString())
                runOnUiThread{Toast.makeText(applicationContext,"An error occurred during communication.",Toast.LENGTH_SHORT).show()}
            }
            else {
                Log.i("Response code: ", response.code.toString())
                data = JSONObject(response.body?.string()!!)
                generateZip()
                extractZip()
                playSound()
            }
        }
    }
}