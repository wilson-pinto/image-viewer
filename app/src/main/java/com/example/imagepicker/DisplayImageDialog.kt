package com.example.imagepicker

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.util.*

class DisplayImageDialog<T>(
    private val _imageList: MutableList<T> = mutableListOf(),
    private val _cropEnabled: Boolean = false,
    private val _onSubmit: OnSubmit? = null,
    private val _onDelete: OnDelete? = null
) : DialogFragment() {

    private val TAG = "IMG_VIEWER"
    private var _currentPosition: Int = 0

    private lateinit var _viewPagerAdapterAdapter: ImagePagerAdapter
    private lateinit var _vpImage: ViewPager
    private lateinit var _imageViewerThumbnailAdapter: ImageViewerThumbnailAdapter
    private lateinit var _itemCount: TextView
    private lateinit var _vBottom: View
    private lateinit var _rvThumbNail: RecyclerView

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

        _vpImage = view.findViewById<View>(R.id.vpImage) as ViewPager

        _viewPagerAdapterAdapter =
            ImagePagerAdapter(_imageList as MutableList<ImageViewer>, view.context)
        _imageViewerThumbnailAdapter = ImageViewerThumbnailAdapter(object :
            ImageViewerThumbnailAdapter.ImageThumbnailListener {
            override fun onItemSelected(position: Int) {
                _currentPosition = position
                _vpImage.currentItem = position
            }
        })

        val btnCropImage: ImageButton = view.findViewById(R.id.btnCropImage)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
        val btnBack: ImageButton = view.findViewById(R.id.btnBack)
        val tvNext: TextView = view.findViewById(R.id.tvNext)

        _itemCount = view.findViewById(R.id.itemCount)
        _rvThumbNail = view.findViewById(R.id.rvThumbNail)
        _vBottom = view.findViewById(R.id.vBottom)

        _vpImage.adapter = _viewPagerAdapterAdapter

        _rvThumbNail.apply {
            adapter = _imageViewerThumbnailAdapter
        }

        _imageViewerThumbnailAdapter.addList(_imageList)

        updateCount()
        updateThumbnailListVisibility()

        _vpImage.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                _currentPosition = position
                updateCount()
                _imageViewerThumbnailAdapter.changeSelectedItem(position)
                _rvThumbNail.scrollToPosition(_currentPosition)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        if (_cropEnabled) {
            btnCropImage.visibility = View.VISIBLE
            btnCropImage.setOnClickListener {
                _currentPosition = _vpImage.currentItem
                CropImage.activity(("file://" + _imageList[_currentPosition].filePath).toUri())
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAllowFlipping(false)
                    .start(context!!, this)
            }
        } else {
            btnCropImage.visibility = View.GONE
        }

        btnBack.setOnClickListener {
            dismiss()
        }

        btnDelete.setOnClickListener {
            if (_onDelete == null) {
                deleteImage(_currentPosition)
            } else {
                _onDelete.onDelete(_currentPosition)
            }
        }

        tvNext.text = if (_onSubmit == null) "Done" else "Upload"

        tvNext.setOnClickListener {
            if (_onSubmit != null) {
                _imageList.forEach { it.resetFilePath() }
                _onSubmit.onSubmit()
            }
            dismiss()
        }

    }

    fun deleteImage(position: Int) {
        _imageList.removeAt(position)
        _imageViewerThumbnailAdapter.notifyItemRemoved(position)
        _viewPagerAdapterAdapter.notifyDataSetChanged()
        _imageViewerThumbnailAdapter.changeSelectedItem(_vpImage.currentItem)
        updateCount()
        updateThumbnailListVisibility()
    }

    private fun updateThumbnailListVisibility() {
        if (_imageList.size <= 1) {
            _vBottom.visibility = View.GONE
            _rvThumbNail.visibility = View.GONE
        } else {
            _vBottom.visibility = View.VISIBLE
            _rvThumbNail.visibility = View.VISIBLE
        }

        if (_imageList.size == 0) {
            dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateCount() {
        _itemCount.text = "${_vpImage.currentItem + 1}/${_imageList.size}"
    }

    private fun setCroppedImage(url: Uri) {
        with(_imageList[_currentPosition] as ImageViewer) {
            if (!isCropped) {
                isCropped = true
            }
            croppedFilePath = url.toString()
        }
        _viewPagerAdapterAdapter.notifyDataSetChanged()
        _vpImage.currentItem = _currentPosition
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

    interface OnSubmit {
        fun onSubmit()
    }

    interface OnDelete {
        fun onDelete(position: Int)
    }
}