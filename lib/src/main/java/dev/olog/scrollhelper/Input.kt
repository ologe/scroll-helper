package dev.olog.scrollhelper

import android.view.View
import com.sothree.slidinguppanel.SlidingUpPanelLayout

typealias InitialHeight = Int

sealed class Input(
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
        val slidingPanel: Pair<SlidingUpPanelLayout, InitialHeight>,
        val bottomNavigation: Pair<View, InitialHeight>,
        toolbarHeight: InitialHeight,
        tabLayoutHeight: InitialHeight? = null
    ) : Input(toolbarHeight, tabLayoutHeight) {

        init {
            require(slidingPanel.second > 0)
            require(bottomNavigation.second > 0)
        }

    }

    /**
     * Handles only sliding panel
     */
    class OnlySlidingPanel(
        val slidingPanel: Pair<SlidingUpPanelLayout, InitialHeight>,
        toolbarHeight: InitialHeight,
        tabLayoutHeight: InitialHeight? = null,
        val scrollableSlidingPanel: Boolean
    ) : Input(toolbarHeight, tabLayoutHeight) {
        init {
            require(slidingPanel.second > 0)
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
            require(bottomNavigation.second > 0)
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