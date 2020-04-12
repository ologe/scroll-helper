package dev.olog.scrollhelper.example.tab

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import dev.olog.scrollhelper.example.model.Model
import dev.olog.scrollhelper.example.model.ModelDiffCallback
import dev.olog.scrollhelper.example.model.ModelViewHolder

class TabFragmentAdapter(
    private val tabPosition: Int,
    private val onClick: (View, Model) -> Unit
) : ListAdapter<Model, ModelViewHolder>(ModelDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        return ModelViewHolder(parent).apply {
            itemView.setOnClickListener {
                onClick(it, getItem(adapterPosition))
            }
        }
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.transitionName = "$holder $tabPosition ${item.image}"

        holder.bind(item)
    }
}