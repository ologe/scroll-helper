package dev.olog.scrollhelper.layoutmanagers

import androidx.recyclerview.widget.RecyclerView

interface OnOverScrollListener {
    fun onRecyclerViewOverScroll(recyclerView: RecyclerView, dy: Int)
}

interface OverScrollDelegate {
    fun addOnOverScrollListener(listener: OnOverScrollListener)
    fun removeOnOverScrollListener(listener: OnOverScrollListener)

}