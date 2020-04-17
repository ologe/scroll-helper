package dev.olog.scrollhelper.state

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

// TODO savedStateRegistry don't work as expected
private val stateBundle = mutableMapOf<String, Bundle>()

internal class StateResetter {

    fun isRestored(fragment: Fragment): Boolean {
        return stateBundle[fragment.tag] != null
    }

    fun restoreActivity(view: View?, key: String) {
        view ?: return
        view.translationY = stateBundle[ACTIVITY_STATE]?.getFloat(key) ?: view.translationY
    }

    fun restore(fragment: Fragment, view: View, key: String) {
        val bundle = stateBundle[fragment.tag]
        view.translationY = bundle?.getFloat(key) ?: view.translationY
    }

    fun restore(
        fragment: Fragment,
        recyclerView: RecyclerView
    ) {
        val scroll = stateBundle[fragment.tag]?.getInt(
            RECYCLER_VIEW_STATE
        ) ?: return
        if (scroll == 0) {
            recyclerView.scrollToPosition(0)
        } else {
            recyclerView.scrollBy(0, scroll)
        }
    }

    fun save(
        fragment: Fragment,
        toolbar: View?,
        tablayout: View?,
        fab: View?,
        recyclerView: RecyclerView
    ) {
        val bundle = bundleOf(
            TOOLBAR_STATE to toolbar?.translationY,
            TAB_LAYOUT_STATE to tablayout?.translationY,
            FAB_STATE to fab?.translationY,
            RECYCLER_VIEW_STATE to recyclerView.computeVerticalScrollOffset() - recyclerView.paddingTop
        )
        // save for back stack changes
        stateBundle[fragment.tag!!] = bundle
    }

    fun save(bottomNavigation: View?, bottomSheet: View?) {
        val bundle = bundleOf(
            BOTTOM_NAVIGATION_STATE to bottomNavigation?.translationY,
            BOTTOM_SHEET_STATE to bottomSheet?.translationY
        )
        stateBundle[ACTIVITY_STATE] = bundle
    }

    fun dispose() {

    }

    companion object {
        private const val PREFIX = "scroll.helper"
        private const val ACTIVITY_STATE = "$PREFIX.state"

        const val TOOLBAR_STATE = "$PREFIX.toolbar"
        const val TAB_LAYOUT_STATE = "$PREFIX.tabLayout"
        const val FAB_STATE = "$PREFIX.fab"
        const val BOTTOM_SHEET_STATE = "$PREFIX.bottom.sheet"
        const val BOTTOM_NAVIGATION_STATE = "$PREFIX.bottom.navigation"
        const val RECYCLER_VIEW_STATE = "$PREFIX.recycler.view"
    }

}