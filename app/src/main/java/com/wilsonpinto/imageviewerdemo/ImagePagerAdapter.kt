package com.wilsonpinto.imageviewerdemo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import java.util.*

class ImagePagerAdapter(
    private val _imageList: MutableList<ImageViewer>,
    private val _context: Context
) :
    PagerAdapter() {

    override fun getCount(): Int {
        return _imageList.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ConstraintLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val view: View = LayoutInflater.from(container.context).inflate(
            R.layout.pager_image_viewer,
            container,
            false
        )

        val ivImage: PinchToZoomImageView = view.findViewById(R.id.ivImage)

        with(_imageList[position]) {
            GlideUtil.loadImage(
                getFileUrl(),
                ivImage,
                    _context
            )
        }

        Objects.requireNonNull(container).addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }

}