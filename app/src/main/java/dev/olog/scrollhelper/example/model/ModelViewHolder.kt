package dev.olog.scrollhelper.example.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dev.olog.scrollhelper.example.R
import kotlinx.android.synthetic.main.list_item.view.*

class ModelViewHolder(
    parent: ViewGroup
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
) {

    fun bind(model: Model) {
        Glide.with(itemView)
            .load(model.image)
            .placeholder(R.drawable.placeholder)
            .transition(DrawableTransitionOptions.withCrossFade())
            .override(200)
            .centerCrop()
            .into(itemView.image)
    }

}