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
    private var lastCollapsedBottomNavigationTranslationY = bottomNavigation.translationY
    private var lastCollapsedSlidingPanelTranslationY = bottomNavigation.translationY

    override fun onSlide(bottomSheet: View, slideOffset: Float) {
        if (slideOffset > 0.1){
            val translationY = MathUtils.clamp(
                bottomNavigation.height * MathUtils.clamp(slideOffset, 0f, 1f),
                lastCollapsedBottomNavigationTranslationY,
                bottomNavigation.height.toFloat()
            )
            bottomNavigation.translationY = translationY
            bottomSheet.translationY = (1 - slideOffset) * lastCollapsedSlidingPanelTranslationY
        }
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        if (lastState == BottomSheetBehavior.STATE_COLLAPSED && newState == BottomSheetBehavior.STATE_DRAGGING) {
            // save state before starting sliding up
            lastCollapsedBottomNavigationTranslationY = bottomNavigation.translationY
            lastCollapsedSlidingPanelTranslationY = bottomSheet.translationY
        }
        lastState = newState
    }
}