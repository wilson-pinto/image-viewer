package com.example.imagepicker

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.util.ArrayList

class DisplayImageDialog(private val _imageList: ArrayList<String>) : DialogFragment() {

    private val TAG = "WILLS"
    private var _currentPosition: Int = 0

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

        val vpImages = view.findViewById<View>(R.id.pager) as ViewPager

        val mViewPagerAdapter = ImagePager(_imageList, view.context)
        val imageListAdapter = ImageListAdapter()

        val btnCropImage: ImageButton = view.findViewById(R.id.btnCropImage)
        val btnBack: ImageButton = view.findViewById(R.id.btnBack)
        val itemCount: TextView = view.findViewById(R.id.itemCount)
        val rvImages: RecyclerView = view.findViewById(R.id.rvImages)

        vpImages.adapter = mViewPagerAdapter

        itemCount.text = "${vpImages.currentItem + 1}/${_imageList.size}"

        vpImages.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                _currentPosition = position
                itemCount.text = "${vpImages.currentItem + 1}/${_imageList.size}"
                imageListAdapter.changeSelectedItem(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        rvImages.apply {
            adapter = imageListAdapter
        }

        imageListAdapter.addList(_imageList)

        btnCropImage.setOnClickListener {
            Log.i(TAG, "onViewCreated: ${vpImages.currentItem}")
            CropImage.activity(_imageList[_currentPosition].toUri())
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAllowFlipping(false)
                .start(this.requireActivity())
        }

        btnBack.setOnClickListener {
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        _imageList.clear()
        super.onDismiss(dialog)
    }
}