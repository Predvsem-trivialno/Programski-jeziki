package com.example.pj_projekt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.pj_projekt.databinding.ActivityRegisterBinding
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.concurrent.thread

class RegisterActivity : BaseActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener{
            registerRequest()
        }
    }

    data class registrationInfo(val username: String, val email: String, val password: String, val repeatpassword: String)

    fun registerRequest() {
        thread(start = true) {
            val client = OkHttpClient()
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val jsonParams = Gson().toJson(registrationInfo(binding.usernameRegister.text.toString(), binding.emailRegister.text.toString(), binding.passwordRegister.text.toString(), binding.confirmPasswordRegister.text.toString()))
            val formBody = jsonParams.toRequestBody(mediaType)

            val request = Request.Builder()
                .url("https://pametni-paketnik.herokuapp.com/user/")
                .post(formBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val errorCode = response.code.toString()
                Log.i("Response code: ", errorCode)
            }
            showLogin()
        }
    }
}