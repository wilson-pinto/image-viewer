package com.wilsonpinto.imageviewerdemo

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage


//source : https://github.com/ArthurHub/Android-Image-Cropper
//source : https://github.com/akshay2211/PixImagePicker


class MainActivity : AppCompatActivity() {

    private lateinit var _btnLaunchPicker: Button
    private lateinit var _tvSelectedItems: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        _btnLaunchPicker = findViewById(R.id.btnLaunchPicker)
        _tvSelectedItems = findViewById(R.id.tvSelectedItems)

        _btnLaunchPicker.setOnClickListener {

            val options = Options.init()
                .setRequestCode(100)
                .setCount(15)
                .setSpanCount(4)
                .setExcludeVideos(true)
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)

            Pix.start(this@MainActivity, options)

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
            launchImageViewer(data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)!!)
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

    private fun launchImageViewer(uriList: MutableList<String>) {
        val demoMediaList: MutableList<DemoMedia> = mutableListOf()
        uriList.forEach {
            demoMediaList.add(
                DemoMedia(
                    "This is Test field",
                    it,
                    getMimeType(it)!!,
                )
            )
        }
        DisplayImageDialog(
            _imageList = demoMediaList,
            _cropEnabled = true,
            _onSubmit = object : DisplayImageDialog.OnSubmit {
                @SuppressLint("SetTextI18n")
                override fun onSubmit() {
                    demoMediaList.forEachIndexed { index, testPojo ->
                        Log.i(
                            "IMG_VIEWER",
                            "onSubmit: $index ${testPojo.croppedFilePath}, ${testPojo.filePath}"
                        )
                    }
                    _tvSelectedItems.text = "${demoMediaList.size} Items selected"
                }
            }
        ).show(supportFragmentManager, "Submit")
    }

    private fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }
}