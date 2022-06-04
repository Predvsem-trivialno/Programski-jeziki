package com.example.pj_projekt

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pj_projekt.databinding.ActivityLogsBinding
import com.google.gson.Gson
import com.google.gson.JsonArray
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import kotlin.concurrent.thread

class LogsActivity : BaseActivity() {
    private lateinit var binding: ActivityLogsBinding
    private lateinit var jsonData: JSONArray
    var data = ArrayList<logStructure>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        logListRequest()
        Thread.sleep(1_000)
        val adapter = logAdapter(data)
        binding.recyclerView.adapter = adapter
        for(i in 0 until data.size) {
            Log.i("CREATED: ", data[i].toString())
        }
    }

    data class sendID(val boxId: String)
    fun logListRequest() {
        thread(start = true) {
            val client = OkHttpClient()
            val jsonParams = Gson().toJson(sendID(app.boxId))
            Log.i("JSON: ", jsonParams)
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val formBody = jsonParams.toRequestBody(mediaType)

            val request = Request.Builder()
                .url("https://pametni-paketnik.herokuapp.com/accesslog/mobile")
                .post(formBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.i("Error code: ", response.code.toString())
            }
            else {
                jsonData = JSONArray(response.body?.string()!!)
                //data = Gson().fromJson(jsonData.toString(),)
                Log.i("Json: ", jsonData.toString())
                for(i in 0 until jsonData.length()) {
                    val temp = jsonData.getJSONObject(i)
                    val user = temp.getJSONObject("openedBy").getString("username")
                    Log.i("INPUT: ", user)
                    data.add(Gson().fromJson(jsonData.getJSONObject(i).toString(), logStructure::class.java))
                    data[i].username = user
                }
            }
            for(i in 0 until data.size) {
                Log.i("INPUT INSIDE: ", data[i].toString())
            }
        }

    }
}