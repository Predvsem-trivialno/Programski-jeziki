package com.example.pj_projekt

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

lateinit var app: MyApplication

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        app = applicationContext as MyApplication
        super.onCreate(savedInstanceState)
    }
    fun showOpen() {
        val intent = Intent(this, OpenActivity::class.java)
        startActivity(intent)
    }

    fun showRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    fun showLogs() {
        val intent = Intent(this, LogsActivity::class.java)
        startActivity(intent)
    }

    fun showLogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun showTSP() {
        val intent = Intent(this, TSPActivity::class.java)
        startActivity(intent)
    }

    fun showImageUpload() {
        val intent = Intent(this, UploadImagesActivity::class.java)
        startActivity(intent)
    }
}