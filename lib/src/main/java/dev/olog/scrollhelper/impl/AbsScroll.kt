package dev.olog.scrollhelper.impl

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.annotation.CallSuper
import androidx.core.math.MathUtils
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import dev.olog.scrollhelper.Input
import dev.olog.scrollhelper.ViewPagerListener

internal abstract class AbsScroll(private val input: Input) {

    val toolbarMap = SparseArray<View>()
    val tabLayoutMap = SparseArray<View>()
    val fabMap = SparseArray<View>()
    val viewPagerListenerMap = SparseArray<ViewPagerListener>()

    @CallSuper
    open fun onRecyclerViewScrolled(recyclerView: RecyclerView, dx: Int, dy: Int){
        var clampedTabLayoutTranslation = 0f
        toolbarMap.get(recyclerView.hashCode())?.let { toolbar ->
            clampedTabLayoutTranslation = MathUtils.clamp(toolbar.translationY - dy, -toolbar.height.toFloat(), 0f)
            // moves the toolbar just a little more than its height, to handle cases when status bar is transparent
            val clampedToolbarTranslation =
                MathUtils.clamp(toolbar.translationY - dy, -toolbar.height.toFloat() * 1.2f, 0f)
            toolbar.translationY = clampedToolbarTranslation
        }
        tabLayoutMap.get(recyclerView.hashCode())?.let { tabLayout ->
            tabLayout.translationY = clampedTabLayoutTranslation
        }
    }

    abstract fun onAttach(activity: FragmentActivity)
    abstract fun onDetach(activity: FragmentActivity)

    @CallSuper
    open fun applyInsetsToList(list: RecyclerView, toolbar: View?, tabLayout: View?){
        list.clipChildren = false
        list.clipToPadding = false
        disableClipRecursively(list)

        val minimumTopInset = when {
            toolbar != null && tabLayout != null -> input.toolbarHeight.value + (input.tabLayoutHeight?.value ?: 0)
            toolbar != null -> input.toolbarHeight.value
            tabLayout != null -> input.tabLayoutHeight?.value ?: 0
            else -> 0
        }
        val updatePadding = list.paddingTop - minimumTopInset < 0
        if (updatePadding){
            list.updatePadding(top = minimumTopInset)
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
    open fun restoreInitialPosition(recyclerView: RecyclerView){
        tabLayoutMap.get(recyclerView.hashCode())?.animate()?.translationY(0f)
        toolbarMap.get(recyclerView.hashCode())?.animate()?.translationY(0f)
        fabMap.get(recyclerView.hashCode())?.animate()?.translationY(0f)
    }

    @CallSuper
    open fun dispose() {
        toolbarMap.clear()
        tabLayoutMap.clear()
        fabMap.clear()
        viewPagerListenerMap.clear()
    }

}