package com.example.pj_projekt

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.example.pj_projekt.databinding.ActivityUploadImagesBinding
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlin.concurrent.thread

class UploadImagesActivity : BaseActivity() {
    private lateinit var binding: ActivityUploadImagesBinding
    val REQUEST_CODE = 200
    var imageList = ArrayList<Uri>()
    var imageFileList = ArrayList<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadImagesBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.chooseImagesButton.setOnClickListener {
            openGallery()
        }

        binding.uploadImagesButton.setOnClickListener {
            sendImages()
        }
    }

    fun sendImages() {
        thread(start = true) {
            val client = OkHttpClient()
            val MEDIA_TYPE_JPG: MediaType? = "image/*jpg".toMediaTypeOrNull()

            val requestBody = MEDIA_TYPE_JPG?.let {
                MultipartBody.Builder()
                    .setType(it)
                    .addFormDataPart("image1", app.username + "1.jpg", imageFileList[0].asRequestBody(MEDIA_TYPE_JPG))
                    .addFormDataPart("image2", app.username + "2.jpg", imageFileList[1].asRequestBody(MEDIA_TYPE_JPG))
                    .addFormDataPart("image3", app.username + "3.jpg", imageFileList[2].asRequestBody(MEDIA_TYPE_JPG))
                    .addFormDataPart("image4", app.username + "4.jpg", imageFileList[3].asRequestBody(MEDIA_TYPE_JPG))
                    .addFormDataPart("image5", app.username + "5.jpg", imageFileList[4].asRequestBody(MEDIA_TYPE_JPG))
                    .addFormDataPart("image6", app.username + "6.jpg", imageFileList[5].asRequestBody(MEDIA_TYPE_JPG))
                    .addFormDataPart("image7", app.username + "7.jpg", imageFileList[6].asRequestBody(MEDIA_TYPE_JPG))
                    .addFormDataPart("image8", app.username + "8.jpg", imageFileList[7].asRequestBody(MEDIA_TYPE_JPG))
                    .addFormDataPart("image9", app.username + "9.jpg", imageFileList[8].asRequestBody(MEDIA_TYPE_JPG))
                    .addFormDataPart("image10", app.username + "10.jpg", imageFileList[9].asRequestBody(MEDIA_TYPE_JPG))
                    .build()
            }
        }
    }

    fun openGallery() {
        var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                for (i in 0 until data.clipData!!.itemCount) {
                    imageList.add(data.clipData!!.getItemAt(i).uri)
                    imageFileList.add(File(imageList[i].path))
                }
            }
        }
    }
}

