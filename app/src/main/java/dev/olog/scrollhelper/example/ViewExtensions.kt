package dev.olog.scrollhelper.example

import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach

@Suppress("UNCHECKED_CAST")
fun <T : View> View.findViewByIdNotRecursive(id: Int): T? {
    if (this is ViewGroup) {
        forEach { child ->
            if (child.id == id) {
                return child as T
            }
        }
    }
    return null
}