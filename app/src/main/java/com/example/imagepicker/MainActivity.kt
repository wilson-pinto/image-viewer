package com.example.imagepicker

import android.R.attr
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList


//source : https://github.com/ArthurHub/Android-Image-Cropper
https://github.com/ArthurHub/Android-Image-Cropper


class MainActivity : AppCompatActivity() {

    private lateinit var tvSelectedUri: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bm = BitmapFactory.decodeResource(resources, R.drawable.testimage)

        val extStorageDirectory: String = Environment.getExternalStorageDirectory().toString()
        val file = File(extStorageDirectory, "MyIMG.png")
        var outStream: FileOutputStream? = null
        try {
            outStream = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            outStream.flush()
            outStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val imgUri = Uri.fromFile(file)

        val btnLaunchCropper: Button = findViewById(R.id.btnLaunchCropper)
        tvSelectedUri = findViewById(R.id.tvSelectedUri)

        btnLaunchCropper.setOnClickListener {

            Pix.start(this, Options.init().setRequestCode(100))

//            CropImage.activity("file:///data/user/0/com.example.imagepicker/cache/cropped8266080196312026720.jpg".toUri())
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .setFixAspectRatio(true)
//                .start(this)
        }


//        CropImage.activity()
//            .setGuidelines(CropImageView.Guidelines.ON)
//            .start(this)
//
//        CropImage.activity(imgUri)
//            .start(this)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                    Pix.start(this@MainActivity, Options.init().setRequestCode(100))
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Approve permissions to open Pix ImagePicker",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === RESULT_OK) {
                val resultUri = result.uri

                Snackbar.make(
                    findViewById(android.R.id.content),
                    resultUri.toString(),
                    Snackbar.LENGTH_LONG
                ).show()

                if(resultCode == 100){
                    val returnValue: ArrayList<String>? =
                       data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)

                    Log.i("WILLS", "onActivityResult: $returnValue")
                }


            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

}