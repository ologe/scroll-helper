package dev.olog.scrollhelper.impl

import android.view.View
import android.view.ViewGroup
import androidx.core.math.MathUtils
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import dev.olog.scrollhelper.Input

internal class ScrollWithBottomNavigation(
    input: Input.OnlyBottomNavigation
): AbsScroll(input){

    private val bottomNavigation = input.bottomNavigation.first
    private val bottomNavigationHeight = input.bottomNavigation.second.value

    override fun onAttach(activity: FragmentActivity) {

    }

    override fun onDetach(activity: FragmentActivity) {

    }

    override fun restoreInitialPosition(recyclerView: RecyclerView) {
        super.restoreInitialPosition(recyclerView)
        bottomNavigation.animate()?.translationY(0f)
    }

    override fun onRecyclerViewScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onRecyclerViewScrolled(recyclerView, dx, dy)
        val clampedNavigationTranslation =
            MathUtils.clamp(bottomNavigation.translationY + dy, 0f, bottomNavigation.height.toFloat())
        bottomNavigation.translationY = clampedNavigationTranslation
        fabMap.get(recyclerView.hashCode())?.let { it.translationY = clampedNavigationTranslation }
    }

    override fun applyMarginToFab(fab: View) {
        val params = fab.layoutParams
        val marginsToApply = bottomNavigationHeight

        if (params is ViewGroup.MarginLayoutParams && params.bottomMargin < marginsToApply) {
            params.bottomMargin += marginsToApply
            fab.layoutParams = params
        }
    }
}