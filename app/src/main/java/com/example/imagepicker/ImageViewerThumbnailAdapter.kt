package com.example.imagepicker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView

class ImageViewerThumbnailAdapter(private val _callback: ImageThumbnailListener) :
    RecyclerView.Adapter<ImageViewerThumbnailAdapter.ImageListHolder>() {

    private var _thumbnailList: MutableList<ImageViewer> = mutableListOf()
    private var _selectedPosition: Int = 0

    fun addList(imageList: MutableList<ImageViewer>) {
        _thumbnailList = imageList
        notifyDataSetChanged()
    }

    fun changeSelectedItem(position: Int) {
        val oldPos = _selectedPosition
        _selectedPosition = position
        notifyItemChanged(_selectedPosition)
        if (position != oldPos) {
            notifyItemChanged(oldPos)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.col_image_viewer, parent, false)
        return ImageListHolder(view)
    }

    override fun onBindViewHolder(holder: ImageListHolder, position: Int) {

        with(_thumbnailList[holder.adapterPosition]) {
            GlideUtil.loadImage(
                if (croppedFilePath.isEmpty()) filePath else croppedFilePath,
                holder.ivImage,
                holder.itemView.context
            )
        }

        if (holder.adapterPosition == _selectedPosition) {
            holder.flWrapper.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.image_border)
        } else {
            holder.flWrapper.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.image_no_border)
        }

        holder.ivImage.setOnClickListener {
            if (_selectedPosition == holder.adapterPosition)
                return@setOnClickListener
            _callback.onItemSelected(holder.adapterPosition)
            changeSelectedItem(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return _thumbnailList.size
    }

    inner class ImageListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        val flWrapper: FrameLayout = itemView.findViewById(R.id.flWrapper)
    }

    interface ImageThumbnailListener {
        fun onItemSelected(position: Int)
    }
}