package dev.olog.scrollhelper

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior

typealias InitialHeight = Int

sealed class ScrollType(
    val toolbarHeight: InitialHeight,
    val tabLayoutHeight: InitialHeight? = null
) {

    init {
        require(toolbarHeight >= 0)
        tabLayoutHeight?.let { require(tabLayoutHeight > 0) }
    }

    /**
     * Handles both sliding panel and bottom sheet
     */
    class Full(
        val slidingPanel: View,
        val bottomNavigation: View,
        val realSlidingPanelPeek: InitialHeight,
        toolbarHeight: InitialHeight,
        tabLayoutHeight: InitialHeight? = null
    ) : ScrollType(toolbarHeight, tabLayoutHeight) {

        init {
            require(BottomSheetBehavior.from(slidingPanel) is MultiListenerBottomSheetBehavior<*>)
            require(realSlidingPanelPeek > 0)
        }

    }

    /**
     * Handles only sliding panel
     */
    class OnlySlidingPanel(
        val slidingPanel: View,
        toolbarHeight: InitialHeight,
        tabLayoutHeight: InitialHeight? = null,
        val scrollableSlidingPanel: Boolean
    ) : ScrollType(toolbarHeight, tabLayoutHeight) {
        init {
            require(BottomSheetBehavior.from(slidingPanel) is MultiListenerBottomSheetBehavior<*>)
        }
    }

    /**
     * Handles only bottom navigation
     */
    class OnlyBottomNavigation(
        val bottomNavigation: View,
        toolbarHeight: InitialHeight,
        tabLayoutHeight: InitialHeight? = null
    ) : ScrollType(toolbarHeight, tabLayoutHeight)

    /**
     * No sliding panel, neither bottom navigation
     */
    class None(
        toolbarHeight: InitialHeight,
        tabLayoutHeight: InitialHeight? = null
    ) : ScrollType(toolbarHeight, tabLayoutHeight)
}