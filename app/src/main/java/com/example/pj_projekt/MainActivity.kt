package com.example.pj_projekt

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.example.pj_projekt.databinding.ActivityMainBinding
import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import kotlin.concurrent.thread

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(app.userId!="" && app.username!="" && app.email!=""){
            Toast.makeText(applicationContext,"Logged in as ${app.username}.",Toast.LENGTH_SHORT).show()
            showOpen()
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener{
            if(binding.inputUsername.text.toString()!="" && binding.inputPassword.text.toString()!=""){
                makeLoginRequest()
            }
        }

        binding.btnFaceLogin.setOnClickListener{
            dispatchTakePictureIntent()
        }

        binding.registerRedirectButton.setOnClickListener{
            showRegister()
        }
    }

    data class LoginInfo(val username: String, val password: String)

    private fun makeLoginRequest() {
        thread(start = true) {
            val client = OkHttpClient()
            val jsonParams = Gson().toJson(LoginInfo(binding.inputUsername.text.toString(),binding.inputPassword.text.toString()))
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val formBody = jsonParams.toRequestBody(mediaType)

            val request = Request.Builder()
                .url("https://pametni-paketnik.herokuapp.com/user/mobileLogin")
                .post(formBody)
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                val errorCode = response.code.toString()
                Log.i("Response code: ", errorCode)
                val errorMessage: String = if(errorCode=="404"){
                    "Incorrect username or password."
                } else {
                    "An error occurred during communication."
                }
                runOnUiThread{
                    Toast.makeText(applicationContext,errorMessage,
                        Toast.LENGTH_SHORT).show()}
            } else {
                Log.i("Response code: ", response.code.toString())
                val data = JSONObject(response.body?.string()!!)
                app.username = data.getString("username")
                app.email = data.getString("email")
                app.userId = data.getString("_id")
                app.sharedPrefSet("username",app.username)
                app.sharedPrefSet("email",app.email)
                app.sharedPrefSet("userId",app.userId)
                showOpen()
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, 1)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(applicationContext,"An error occurred.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun makeFaceLoginRequest(file: String) {
        Log.i("Encoded",file)
        thread(start = true) {
            val client = OkHttpClient()
            val formBody = FormBody.Builder()
                .add("faceImage", file)
                .build()

            val request = Request.Builder()
                .url("https://pametni-paketnik.herokuapp.com/user/mobileLoginFace")
                .post(formBody)
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                val errorMessage: String = if(response.code.toString()=="404"){
                    "User not found."
                } else {
                    response.message
                }
                runOnUiThread{
                    Toast.makeText(applicationContext,errorMessage,
                        Toast.LENGTH_SHORT).show()}
            } else {
                Log.i("Response code: ", response.code.toString())
                val data = JSONObject(response.body?.string()!!)
                app.username = data.getString("username")
                app.email = data.getString("email")
                app.userId = data.getString("_id")
                app.sharedPrefSet("username",app.username)
                app.sharedPrefSet("email",app.email)
                app.sharedPrefSet("userId",app.userId)
                showOpen()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.imageView.setImageBitmap(imageBitmap)
            val outputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            makeFaceLoginRequest(Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP))
        }
    }
}