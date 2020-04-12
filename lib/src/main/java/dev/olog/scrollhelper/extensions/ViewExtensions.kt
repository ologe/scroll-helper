package dev.olog.scrollhelper.extensions

import android.view.View
import android.view.ViewGroup
import androidx.annotation.Px
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

internal fun View.updateMargin(
    @Px left: Int = marginLeft,
    @Px top: Int = marginTop,
    @Px right: Int = marginRight,
    @Px bottom: Int = marginBottom
) {
    val params = layoutParams as? ViewGroup.MarginLayoutParams ?: return
    params.leftMargin = left
    params.topMargin = top
    params.rightMargin = right
    params.bottomMargin = bottom
    layoutParams = params
}