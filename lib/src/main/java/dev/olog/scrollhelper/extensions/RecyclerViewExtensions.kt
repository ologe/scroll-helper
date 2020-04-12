package dev.olog.scrollhelper.extensions

import androidx.recyclerview.widget.RecyclerView
import dev.olog.scrollhelper.layoutmanagers.OverScrollDelegate

val RecyclerView.overScrollDelegate: OverScrollDelegate
    get() {
        require(layoutManager is OverScrollDelegate)
        return layoutManager as OverScrollDelegate
    }