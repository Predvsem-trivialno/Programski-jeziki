package com.example.pj_projekt

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.pj_projekt.databinding.ActivityOpenBinding
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.concurrent.thread

class OpenActivity : BaseActivity() {
    private lateinit var binding: ActivityOpenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(app.boxId.isEmpty()){
            binding.btnOpen.isEnabled=false
            binding.btnLogs.isEnabled=false
        }
        binding.txtWelcome.text = getString(R.string.text_welcome,app.username)
        binding.btnScanQR.setOnClickListener{
            IntentIntegrator(this).initiateScan()
        }
        binding.btnOpen.setOnClickListener{
            makeOpenRequest()
        }
        binding.btnLogs.setOnClickListener{
            showLogs()
        }
        binding.btnLogout.setOnClickListener{
            app.sharedPrefSet("username","")
            app.sharedPrefSet("email","")
            app.sharedPrefSet("userId","")
            app.username = ""
            app.email = ""
            app.userId = ""
            showLogin()
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
                    binding.btnOpen.isEnabled=true
                    binding.btnLogs.isEnabled=true
                } catch (e: Exception){
                    binding.root.performHapticFeedback(HapticFeedbackConstants.REJECT)
                    Toast.makeText(applicationContext,"Invalid QR code", Toast.LENGTH_SHORT).show()
                    binding.txtSelected.text = getString(R.string.text_selected)
                }
            }
        }
    }

    //Implementation of sound & API calls

    data class OpenRequest(val postboxId: String, val openedBy: String, val success: Boolean)
    data class SoundRequest(val boxId: String, val tokenFormat: Int)

    private lateinit var data: JSONObject
    private lateinit var fileName: String

    private fun generateZip() {
        val decodedBytes = Base64.decode(data.getString("data"), Base64.NO_WRAP)
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
        runOnUiThread{Toast.makeText(applicationContext,"Playing opening sound for box ${app.boxId}...",Toast.LENGTH_SHORT).show()}
        mp.start()
    }

    private fun makeOpenRequest() {
        thread(start = true) {
            val client = OkHttpClient()
            val jsonParams = Gson().toJson(OpenRequest(app.boxId, app.userId, true))
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val formBody = jsonParams.toRequestBody(mediaType)

            val request = Request.Builder()
                .url("https://pametni-paketnik.herokuapp.com/postbox/mobileOpen")
                .post(formBody)
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                when (response.code) {
                    403 -> runOnUiThread{Toast.makeText(applicationContext,"You don't have access to this postbox!",Toast.LENGTH_SHORT).show()}
                    404 -> runOnUiThread{Toast.makeText(applicationContext,"This postbox is not registered!",Toast.LENGTH_SHORT).show()}
                    500 -> runOnUiThread{Toast.makeText(applicationContext,response.message,Toast.LENGTH_SHORT).show()}
                }
            } else {
                makeSoundRequest()
            }
        }
    }

    private fun makeSoundRequest() {
        thread(start = true) {
            val client = OkHttpClient()
            val jsonParams = Gson().toJson(SoundRequest(app.boxId, 2))
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
                Thread.sleep(4000)
                runOnUiThread{
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Open info")
                    builder.setMessage("Did you open the box successfully?")
                    builder.setPositiveButton("Yes") { dialog, _ ->
                        makeAccessLogs(true)
                        dialog.cancel()
                    }
                    builder.setNegativeButton("No") { dialog, _ ->
                        makeAccessLogs(false)
                        dialog.cancel()
                    }
                    builder.show()
                }
            }
        }
    }

    private fun makeAccessLogs(success: Boolean) {
        thread(start = true) {
            val client = OkHttpClient()
            val jsonParams = Gson().toJson(OpenRequest(app.boxId, app.userId, success))
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val formBody = jsonParams.toRequestBody(mediaType)

            val request = Request.Builder()
                .url("https://pametni-paketnik.herokuapp.com/accesslog")
                .post(formBody)
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                when (response.code) {
                    403 -> runOnUiThread{Toast.makeText(applicationContext,"You don't have access to this postbox!",Toast.LENGTH_SHORT).show()}
                    404 -> runOnUiThread{Toast.makeText(applicationContext,"This postbox is not registered!",Toast.LENGTH_SHORT).show()}
                    500 -> runOnUiThread{Toast.makeText(applicationContext,response.message,Toast.LENGTH_SHORT).show()}
                }
            } else {
                val successText: String = if(success){
                    "successful"
                } else {
                    "unsuccessful"
                }
                runOnUiThread{Toast.makeText(applicationContext,"Access logged as $successText!",Toast.LENGTH_SHORT).show()}
            }
        }
    }

}