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

    fun showPlaySound() {
        val intent = Intent(this, PlaySoundActivity::class.java)
        startActivity(intent)
    }
}