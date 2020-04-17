package dev.olog.scrollhelper.example.tab

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import dev.olog.scrollhelper.example.model.Model
import dev.olog.scrollhelper.example.model.ModelDiffCallback
import dev.olog.scrollhelper.example.model.ModelViewHolder

class TabFragmentAdapter(
    private val onClick: (Model) -> Unit
) : ListAdapter<Model, ModelViewHolder>(ModelDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        return ModelViewHolder(parent).apply {
            itemView.setOnClickListener {
                onClick(getItem(adapterPosition))
            }
        }
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item)
    }
}