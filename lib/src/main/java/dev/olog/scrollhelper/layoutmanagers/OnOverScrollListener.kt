package dev.olog.scrollhelper.layoutmanagers

import androidx.recyclerview.widget.RecyclerView

interface OnOverScrollListener {
    fun onRecyclerViewOverScroll(recyclerView: RecyclerView, dy: Int)
}

