package dev.olog.scrollhelper.example.model

import androidx.recyclerview.widget.DiffUtil

object ModelDiffCallback : DiffUtil.ItemCallback<Model>() {

    override fun areItemsTheSame(oldItem: Model, newItem: Model): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: Model, newItem: Model): Boolean {
        return false
    }
}