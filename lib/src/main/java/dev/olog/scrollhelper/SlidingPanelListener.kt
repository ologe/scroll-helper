package dev.olog.scrollhelper

import android.view.View
import androidx.core.math.MathUtils
import com.sothree.slidinguppanel.SlidingUpPanelLayout

/**
 * Adjust bottom navigation translation when sliding panel is dragged.
 * 1) When is dragged up, moves down bottom navigation
 * 2) When is dragged down, restores bottom navigation to its initial position
 */
internal class SlidingPanelListener(
    private val bottomNavigation: View
) : SlidingUpPanelLayout.PanelSlideListener {

    private var lastState = SlidingUpPanelLayout.PanelState.COLLAPSED
    private var lastCollapsedTranslationY = bottomNavigation.translationY

    override fun onPanelSlide(panel: View, slideOffset: Float) {
        val translationY = MathUtils.clamp(
            bottomNavigation.height * MathUtils.clamp(slideOffset, 0f, 1f),
            lastCollapsedTranslationY,
            bottomNavigation.height.toFloat()
        )
        bottomNavigation.translationY = translationY
    }

    override fun onPanelStateChanged(
        panel: View,
        previousState: SlidingUpPanelLayout.PanelState,
        newState: SlidingUpPanelLayout.PanelState
    ) {
        if (lastState == SlidingUpPanelLayout.PanelState.COLLAPSED && newState == SlidingUpPanelLayout.PanelState.DRAGGING) {
            lastCollapsedTranslationY = bottomNavigation.translationY
        }
        lastState = newState
    }

}