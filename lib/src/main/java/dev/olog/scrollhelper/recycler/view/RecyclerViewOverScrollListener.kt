package dev.olog.scrollhelper.recycler.view

import androidx.recyclerview.widget.RecyclerView
import dev.olog.scrollhelper.layoutmanagers.OnOverScrollListener

internal class RecyclerViewOverScrollListener(
    private val recyclerViewListener: RecyclerViewListener
) : OnOverScrollListener {

    override fun onRecyclerViewOverScroll(recyclerView: RecyclerView, dy: Int) {
        recyclerViewListener.onScrolledInternal(recyclerView, dy, true)
    }
}