package dev.olog.scrollhelper.state

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import dev.olog.scrollhelper.BottomSheetData

// TODO savedStateRegistry don't work as expected
private val stateBundle = mutableMapOf<String, Bundle>()

internal class StateResetter(
    private val restoreState: Boolean
) {

    fun restoreActivity(view: View?, key: String) {
        if (restoreState && view != null) {
            view.translationY = stateBundle[ACTIVITY_STATE]?.getFloat(key) ?: view.translationY
        }

    }

    fun restoreFab(
        bottomSheetData: BottomSheetData?,
        fab: View
    ) {
        if (restoreState && bottomSheetData != null) {
            fab.translationY = bottomSheetData.view.translationY
        }
    }

    fun restore(fragment: Fragment, view: View, key: String) {
        val bundle = stateBundle[fragment.tag]
        view.translationY = bundle?.getFloat(key) ?: view.translationY
    }

    fun save(
        fragment: Fragment,
        toolbar: View?,
        tablayout: View?
    ) {
        if (restoreState) {
            val bundle = bundleOf(
                TOOLBAR_STATE to toolbar?.translationY,
                TAB_LAYOUT_STATE to tablayout?.translationY
            )
            // save for back stack changes
            stateBundle[fragment.tag!!] = bundle
        }
    }

    fun save(bottomNavigation: View?, bottomSheet: View?) {
        if (restoreState) {
            val bundle = bundleOf(
                BOTTOM_NAVIGATION_STATE to bottomNavigation?.translationY,
                BOTTOM_SHEET_STATE to bottomSheet?.translationY
            )
            stateBundle[ACTIVITY_STATE] = bundle
        }
    }

    companion object {
        private const val PREFIX = "scroll.helper"
        private const val ACTIVITY_STATE = "$PREFIX.state"

        const val TOOLBAR_STATE = "$PREFIX.toolbar"
        const val TAB_LAYOUT_STATE = "$PREFIX.tabLayout"
        const val BOTTOM_SHEET_STATE = "$PREFIX.bottom.sheet"
        const val BOTTOM_NAVIGATION_STATE = "$PREFIX.bottom.navigation"
    }

}