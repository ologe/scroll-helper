package dev.olog.scrollhelper.bottom.sheet

import android.view.View
import androidx.core.math.MathUtils.clamp
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_DRAGGING
import java.lang.ref.WeakReference

internal class BottomSheetListener(
    bottomNavigation: View,
    bottomSheet: View
) : BottomSheetBehavior.BottomSheetCallback() {

    private val view = WeakReference(bottomNavigation)
    private var lastState = STATE_COLLAPSED
    private var lastBottomNavigationTranslation = bottomNavigation.translationY
    private var lastBottomSheetTranslation = bottomSheet.translationY

    override fun onSlide(bottomSheet: View, slideOffset: Float) {
        val view = view.get() ?: return
        val height = view.height.toFloat()

        val diff = height - lastBottomNavigationTranslation

        val translation = clamp(
            lastBottomNavigationTranslation + diff * clamp(slideOffset, 0f, 1f),
            lastBottomNavigationTranslation,
            height
        )
        view.translationY = translation
        bottomSheet.translationY = (1f - slideOffset) * lastBottomSheetTranslation
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        val view = view.get() ?: return
        if (lastState == STATE_COLLAPSED && newState == STATE_DRAGGING) {
            // save state before sliding up
            lastBottomNavigationTranslation = view.translationY
            lastBottomSheetTranslation = bottomSheet.translationY
        }
        lastState = newState
    }

}