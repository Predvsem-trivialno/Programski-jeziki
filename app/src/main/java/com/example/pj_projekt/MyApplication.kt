package com.example.pj_projekt

import android.app.Application
import android.content.Context
import com.example.pj_projekt.data.Location

const val SP_FILE = "sharedpref.data"

class MyApplication: Application() {
    val locations: ArrayList<Location> = arrayListOf()
    var distanceType: String = ""
    var username: String = ""
    var email: String = ""
    var userId: String = ""
    var boxId: String = ""

    override fun onCreate(){
        super.onCreate()
        username = sharedPrefGet("username","")!!
        email = sharedPrefGet("email","")!!
        userId = sharedPrefGet("userId","")!!
    }

    fun sharedPrefSet(pref: String, value: String) {
        val sharedPref = getSharedPreferences(SP_FILE, Context.MODE_PRIVATE)
        with(sharedPref.edit()){
            putString(pref,value)
            apply()
            commit()
        }
    }

    fun sharedPrefGet(pref: String, defaultVal: String): String? {
        val sharedPref = getSharedPreferences(SP_FILE, Context.MODE_PRIVATE)
        return sharedPref.getString(pref, defaultVal)
    }

}