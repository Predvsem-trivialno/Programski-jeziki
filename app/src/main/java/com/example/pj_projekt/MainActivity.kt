package com.example.pj_projekt

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.pj_projekt.databinding.ActivityMainBinding
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import kotlin.concurrent.thread

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener{
            if(binding.inputUsername.text.toString()!="" && binding.inputPassword.text.toString()!=""){
                makeLoginRequest()
            }
        }

        binding.btnFaceLogin.setOnClickListener{

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
                showOpen()
            }
        }
    }
}