package dev.olog.scrollhelper.example.detail

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import dev.olog.scrollhelper.example.model.Model
import dev.olog.scrollhelper.example.model.ModelDiffCallback
import dev.olog.scrollhelper.example.model.ModelViewHolder

class DetailFragmentAdapter(

) : ListAdapter<Model, ModelViewHolder>(ModelDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        return ModelViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}