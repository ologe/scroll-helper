package dev.olog.scrollhelper.layoutmanagers

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

open class OverScrollStaggeredLayoutManager(
    private val recyclerView: RecyclerView,
    spanCount: Int,
    orientation: Int
) : StaggeredGridLayoutManager(spanCount, orientation),
    OverScrollDelegate {

    private val overScrollListeners = mutableListOf<OnOverScrollListener>()

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        val scrollRange = super.scrollVerticallyBy(dy, recycler, state)
        val overscroll = dy - scrollRange
        if (overscroll > 0) {
            overScrollListeners.forEach { it.onRecyclerViewOverScroll(recyclerView, overscroll) }
        } else if (overscroll < 0) {
            overScrollListeners.forEach { it.onRecyclerViewOverScroll(recyclerView, overscroll) }
        }
        return scrollRange
    }

    override fun addOnOverScrollListener(listener: OnOverScrollListener) {
        overScrollListeners.add(listener)
    }

    override fun removeOnOverScrollListener(listener: OnOverScrollListener) {
        overScrollListeners.remove(listener)
    }

}