package com.example.pj_projekt

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.pj_projekt.databinding.ActivityUploadImagesBinding

class UploadImagesActivity : BaseActivity() {
    private lateinit var binding: ActivityUploadImagesBinding
    val REQUEST_CODE = 200
    var imageList = ArrayList<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chooseImagesButton.setOnClickListener {
            openGallery()
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
                    //var imageUri: Uri = data.clipData!!.getItemAt(i).uri
                    imageList.add(data.clipData!!.getItemAt(i).uri)
                    Log.i("Image data: ", imageList[i].toString())
                }
            }
        }
    }
}