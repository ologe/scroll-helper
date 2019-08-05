package dev.olog.scrollhelper.impl

import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.annotation.CallSuper
import androidx.core.math.MathUtils.clamp
import androidx.core.util.forEach
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import dev.olog.scrollhelper.ScrollType
import dev.olog.scrollhelper.ViewPagerListener
import kotlin.math.abs

internal abstract class AbsScroll(
    private val input: ScrollType,
    private val enableClipRecursively: Boolean,
    private var debugScroll: Boolean
) {

    companion object {
        private const val TAG = "ScrollHelper"
        const val TOLERANCE = 0.01
    }

    val toolbarMap = SparseArray<View>()
    val tabLayoutMap = SparseArray<View>()
    val fabMap = SparseArray<View>()
    val viewPagerListenerMap = SparseArray<ViewPagerListener>()

    protected inline fun logVerbose(msg: () -> String) {
        if (debugScroll) {
            Log.v(TAG, msg())
        }
    }

    @CallSuper
    open fun onRecyclerViewScrolled(recyclerView: RecyclerView, dx: Int, dy: Int, forced: Boolean) {
        logVerbose { "onRecyclerViewScrolled $recyclerView\ndy=$dy" }

        var clampedTabLayoutTranslation = 0f

        toolbarMap.get(recyclerView.hashCode())?.let { toolbar ->

            val toolbarHeight = toolbar.height.toFloat()
            val currentToolbarTranslation = toolbar.translationY

            clampedTabLayoutTranslation =
                clamp(currentToolbarTranslation - dy, -toolbarHeight, 0f)

            // moves the toolbar just a little more than its height, to handle cases when status bar is transparent
            val clampedToolbarTranslation =
                clamp(currentToolbarTranslation - dy, -toolbarHeight, 0f)

            logVerbose {
                "onRecyclerViewScrolled: translating toolbar from=${toolbar.translationY} to $clampedToolbarTranslation min=${-toolbarHeight}, max=${0}"
            }

            updateOnlyIfNeeded(toolbar, clampedToolbarTranslation)
        }

        tabLayoutMap.get(recyclerView.hashCode())?.let { tabLayout ->
            logVerbose {
                "onRecyclerViewScrolled: translating tab layout from=${tabLayout.translationY} to $clampedTabLayoutTranslation"
            }
            updateOnlyIfNeeded(tabLayout, clampedTabLayoutTranslation)
        }
    }

    abstract fun onAttach(activity: FragmentActivity)
    abstract fun onDetach(activity: FragmentActivity)

    @CallSuper
    open fun applyInsetsToList(list: RecyclerView, toolbar: View?, tabLayout: View?) {
        if (enableClipRecursively) {
            list.clipChildren = false
            list.clipToPadding = false
            disableClipRecursively(list)
        }


        val minimumTopInset = when {
            toolbar != null && tabLayout != null -> input.toolbarHeight + (input.tabLayoutHeight
                ?: 0)
            toolbar != null -> input.toolbarHeight
            tabLayout != null -> input.tabLayoutHeight ?: 0
            else -> 0
        }
        val updatePadding = list.paddingTop - minimumTopInset < 0
        if (updatePadding) {
            // padding was never applied, apply now
            list.updatePadding(top = list.paddingTop + minimumTopInset)
        }
    }

    abstract fun applyMarginToFab(fab: View)

    private fun disableClipRecursively(view: ViewParent?) {
        if (view == null) {
            return
        }
        if (view is ViewGroup) {
            view.clipChildren = false
            view.clipToPadding = false
        }
    }

    @CallSuper
    open fun restoreInitialPosition(recyclerView: RecyclerView) {
        tabLayoutMap.get(recyclerView.hashCode())?.animate()?.translationY(0f)
        toolbarMap.get(recyclerView.hashCode())?.animate()?.translationY(0f)
        fabMap.forEach { _, value ->
            value.animate().translationY(0f)
        }
    }

    @CallSuper
    open fun dispose() {
        toolbarMap.clear()
        tabLayoutMap.clear()
        fabMap.clear()
        viewPagerListenerMap.clear()
    }

    protected open fun updateOnlyIfNeeded(view: View, translationY: Float){
        if (abs(view.translationY - translationY) > TOLERANCE){
            view.translationY = translationY
        }
    }

}