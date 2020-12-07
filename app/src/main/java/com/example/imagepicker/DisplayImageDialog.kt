package com.example.imagepicker

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.util.*

class DisplayImageDialog(private val _imageList: ArrayList<String>) : DialogFragment(),
    ImageViewerThumbnailAdapter.ImageThumbnailListener {

    private val TAG = "WILLS"
    private var _currentPosition: Int = 0

    private lateinit var _viewPagerAdapter: ImagePager
    private lateinit var _vpImages: ViewPager
    private lateinit var _imageViewerThumbnailAdapter: ImageViewerThumbnailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.dialog_theme)
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_image_viewer, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _vpImages = view.findViewById<View>(R.id.pager) as ViewPager

        _viewPagerAdapter = ImagePager(_imageList, view.context)
        _imageViewerThumbnailAdapter = ImageViewerThumbnailAdapter(this)

        val btnCropImage: ImageButton = view.findViewById(R.id.btnCropImage)
        val btnBack: ImageButton = view.findViewById(R.id.btnBack)
        val itemCount: TextView = view.findViewById(R.id.itemCount)
        val rvImages: RecyclerView = view.findViewById(R.id.rvImages)
        val ibAddMore: ImageButton = view.findViewById(R.id.ibAddMore)

        _vpImages.adapter = _viewPagerAdapter

        itemCount.text = "${_vpImages.currentItem + 1}/${_imageList.size}"

        _vpImages.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                _currentPosition = position
                itemCount.text = "${_vpImages.currentItem + 1}/${_imageList.size}"
                _imageViewerThumbnailAdapter.changeSelectedItem(position)
                rvImages.scrollToPosition(_currentPosition)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        rvImages.apply {
            adapter = _imageViewerThumbnailAdapter
        }

        _imageViewerThumbnailAdapter.addList(_imageList)

        btnCropImage.setOnClickListener {
            _currentPosition = _vpImages.currentItem
            CropImage.activity(("file://" + _imageList[_currentPosition]).toUri())
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAllowFlipping(false)
                .start(context!!, this)
        }

        btnBack.setOnClickListener {
            dismiss()
        }

        ibAddMore.setOnClickListener {
            val options = Options.init()
                .setPreSelectedUrls(_imageList)
                .setRequestCode(100)
                .setCount(15)
                .setSpanCount(4)
                .setExcludeVideos(true)
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
                .setPath("/pix/images")

            Pix.start(this, options)
        }
    }

    fun setCroppedImage(url: Uri) {
        _imageList[_currentPosition] = url.toString()
        _viewPagerAdapter.notifyDataSetChanged()
        _vpImages.currentItem = _currentPosition
        _imageViewerThumbnailAdapter.notifyItemChanged(_currentPosition)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === AppCompatActivity.RESULT_OK) {
                setCroppedImage(result.uri)

            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        _imageList.clear()
        super.onDismiss(dialog)
    }

    override fun onItemSelected(position: Int) {
        _currentPosition = position
        _vpImages.currentItem = position
    }
}