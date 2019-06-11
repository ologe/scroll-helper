package dev.olog.scrollhelper.example

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_item.view.*

class Holder(view: View): RecyclerView.ViewHolder(view)

class Adapter(private val childCount: Int) : RecyclerView.Adapter<Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false))
    }

    override fun getItemCount(): Int = childCount

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val imageView = holder.itemView.image

        val image = IMAGES[position % IMAGES.size]

        Glide.with(imageView.context)
                .load(image)
                .centerCrop()
                .override(200, 200)
                .into(imageView)
    }
}