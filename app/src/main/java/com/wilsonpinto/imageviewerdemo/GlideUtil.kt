package com.wilsonpinto.imageviewerdemo

import android.app.Application
import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

object GlideUtil {

    var instance: Application? = null

    private fun getContext(): Context {
        return instance!!.applicationContext
    }

    fun loadImage(url: String?,  view: ImageView?, context : Context) {
        Glide.with(context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view!!)
    }

    fun loadImage(url: String?, diskCacheRequired: Boolean, view: ImageView?) {
        val diskCacheStrategy = if (diskCacheRequired) {
            DiskCacheStrategy.ALL
        } else {
            DiskCacheStrategy.NONE
        }
        Glide.with(getContext())
            .load(url)
            .diskCacheStrategy(diskCacheStrategy)
            .into(view!!)
    }

    fun loadImage(url: String?, view: ImageView?) {
        // TODO: set disk caching strategy
        Glide.with(getContext()).load(url).into(view!!)
    }

}