package com.wilsonpinto.imageviewerdemo

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

object GlideUtil {
    fun loadImage(url: String?,  view: ImageView?, context : Context) {
        Glide.with(context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view!!)
    }
}