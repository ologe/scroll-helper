package dev.olog.scrollhelper

import android.view.View
import android.view.ViewGroup

fun View.setMargin(leftPx: Int = -1, topPx: Int = -1, rightPx: Int = -1, bottomPx: Int = -1){

    val params = this.layoutParams
    if (params is ViewGroup.MarginLayoutParams){
        params.leftMargin = if (leftPx == -1) params.leftMargin else leftPx
        params.rightMargin = if (rightPx == -1) params.rightMargin else rightPx
        params.topMargin = if (top == -1) params.topMargin else topPx
        params.bottomMargin = if (bottomPx == -1) params.bottomMargin else bottomPx
    }
    layoutParams = params
}