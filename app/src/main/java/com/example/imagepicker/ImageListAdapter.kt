package com.example.imagepicker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView

class ImageListAdapter : RecyclerView.Adapter<ImageListAdapter.ImageListHolder>() {

    private var _imageList: MutableList<String> = mutableListOf()
    private var _selectedPosition: Int = 0

    fun addList(imageList: MutableList<String>) {
        _imageList = imageList
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
        holder.ivImage.setImageURI(_imageList[holder.adapterPosition].toUri())

        if (holder.adapterPosition == _selectedPosition) {
            holder.ivImage.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.image_border)
        }else{
            holder.ivImage.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.image_no_border)
        }
    }

    override fun getItemCount(): Int {
        return _imageList.size
    }

    inner class ImageListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.ivImage)
    }
}