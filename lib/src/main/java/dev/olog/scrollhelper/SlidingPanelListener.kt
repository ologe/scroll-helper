package dev.olog.scrollhelper

import android.view.View
import androidx.core.math.MathUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 * Adjust bottom navigation translation when sliding panel is dragged.
 * 1) When is dragged up, moves down bottom navigation
 * 2) When is dragged down, restores bottom navigation to its initial position
 */
internal class SlidingPanelListener(private val bottomNavigation: View) : BottomSheetBehavior.BottomSheetCallback() {

    private var lastState = BottomSheetBehavior.STATE_COLLAPSED
    private var lastCollapsedTranslationY = bottomNavigation.translationY

    override fun onSlide(bottomSheet: View, slideOffset: Float) {
        val translationY = MathUtils.clamp(
            bottomNavigation.height * MathUtils.clamp(slideOffset, 0f, 1f),
            lastCollapsedTranslationY,
            bottomNavigation.height.toFloat()
        )
        bottomNavigation.translationY = translationY
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        if (lastState == BottomSheetBehavior.STATE_COLLAPSED && newState == BottomSheetBehavior.STATE_DRAGGING) {
            lastCollapsedTranslationY = bottomNavigation.translationY
        }
        lastState = newState
    }
}