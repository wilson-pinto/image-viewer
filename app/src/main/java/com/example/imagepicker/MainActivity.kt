package com.example.imagepicker

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
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
import com.theartofdev.edmodo.cropper.CropImageView
import java.util.*


//source : https://github.com/ArthurHub/Android-Image-Cropper
//source : https://github.com/akshay2211/PixImagePicker


class MainActivity : AppCompatActivity() {

    private var returnValue: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val btnLaunchCropper: Button = findViewById(R.id.btnLaunchCropper)
        val btnLaunchPageViewer: Button = findViewById(R.id.btnLaunchPageViewer)
        val btnLaunchPicker: Button = findViewById(R.id.btnLaunchPicker)

        btnLaunchPicker.setOnClickListener {

            val options = Options.init()
                .setPreSelectedUrls(returnValue)
                .setRequestCode(100)
                .setCount(15)
                .setSpanCount(4)
                .setExcludeVideos(true)
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
                .setPath("/pix/images")

            Pix.start(this@MainActivity, options)

        }

        btnLaunchCropper.setOnClickListener {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAllowFlipping(false)
                .start(this)
        }

        btnLaunchPageViewer.setOnClickListener {
            DisplayImageDialog(returnValue).show(supportFragmentManager, "Submit")
        }

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

        if (requestCode == 100) {
            returnValue = data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)!!

            Log.i("WILLS", "onActivityResult: $returnValue")
            DisplayImageDialog(returnValue).show(supportFragmentManager, "Submit")

        }
        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === RESULT_OK) {
                val resultUri = result.uri

                Snackbar.make(
                    findViewById(android.R.id.content),
                    resultUri.toString(),
                    Snackbar.LENGTH_LONG
                ).show()

            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

}