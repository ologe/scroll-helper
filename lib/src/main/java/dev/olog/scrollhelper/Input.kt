package dev.olog.scrollhelper

import android.view.View

inline class InitialHeight(val value: Int)

sealed class Input(
    val toolbarHeight: InitialHeight,
    val tabLayoutHeight: InitialHeight? = null
) {

    init {
        require(toolbarHeight.value >= 0)
        tabLayoutHeight?.let { require(tabLayoutHeight.value > 0) }
    }

    /**
     * Handles both sliding panel and bottom sheet
     */
    class Full(
        val slidingPanel: Pair<MultiListenerBottomSheetBehavior<*>, InitialHeight>,
        val bottomNavigation: Pair<View, InitialHeight>,
        toolbarHeight: InitialHeight,
        tabLayoutHeight: InitialHeight? = null
    ) : Input(toolbarHeight, tabLayoutHeight) {

        init {
            require(slidingPanel.second.value > 0)
            require(bottomNavigation.second.value > 0)
        }

    }

    /**
     * Handles only sliding panel
     */
    class OnlySlidingPanel(
        val slidingPanel: Pair<MultiListenerBottomSheetBehavior<*>, InitialHeight>,
        toolbarHeight: InitialHeight,
        tabLayoutHeight: InitialHeight? = null,
        val scrollableSlidingPanel: Boolean
    ) : Input(toolbarHeight, tabLayoutHeight) {
        init {
            require(slidingPanel.second.value > 0)
        }
    }

    /**
     * Handles only bottom navigation
     */
    class OnlyBottomNavigation(
        val bottomNavigation: Pair<View, InitialHeight>,
        toolbarHeight: InitialHeight,
        tabLayoutHeight: InitialHeight? = null
    ) : Input(toolbarHeight, tabLayoutHeight) {
        init {
            require(bottomNavigation.second.value > 0)
        }
    }

    /**
     * No sliding panel, neither bottom navigation
     */
    class None(
        toolbarHeight: InitialHeight,
        tabLayoutHeight: InitialHeight? = null
    ) : Input(toolbarHeight, tabLayoutHeight)
}