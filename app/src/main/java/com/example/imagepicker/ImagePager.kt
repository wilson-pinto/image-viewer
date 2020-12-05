package com.example.imagepicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.viewpager.widget.PagerAdapter
import java.util.*


class ImagePager(private val _views: MutableList<String>, val _context: Context) : PagerAdapter() {

    override fun getCount(): Int {
        return _views.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ConstraintLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val view: View = LayoutInflater.from(container.context).inflate(
            R.layout.item,
            container,
            false
        )

        val tvTest: TextView = view.findViewById(R.id.tvTest)
        val ivImage: ImageView = view.findViewById(R.id.ivImage)

        tvTest.text = _views[position]
        ivImage.setImageURI(_views[position].toUri())

        Objects.requireNonNull(container).addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }


}