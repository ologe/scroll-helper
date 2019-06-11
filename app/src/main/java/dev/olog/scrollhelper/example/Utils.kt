package dev.olog.scrollhelper.example

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes
import androidx.core.view.forEach

inline fun Context.dimen(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)
inline fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()

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

val IMAGES = listOf(
        "https://homepages.cae.wisc.edu/~ece533/images/airplane.png",
        "https://homepages.cae.wisc.edu/~ece533/images/arctichare.png",
        "https://homepages.cae.wisc.edu/~ece533/images/boat.png",
        "https://homepages.cae.wisc.edu/~ece533/images/cat.png",
        "https://homepages.cae.wisc.edu/~ece533/images/fruits.png"
)