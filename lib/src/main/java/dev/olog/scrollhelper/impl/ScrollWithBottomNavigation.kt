package dev.olog.scrollhelper.impl

import android.view.View
import android.view.ViewGroup
import androidx.core.math.MathUtils.clamp
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import dev.olog.scrollhelper.ScrollType

internal class ScrollWithBottomNavigation(
    input: ScrollType.OnlyBottomNavigation,
    enableClipRecursively: Boolean,
    debugScroll: Boolean
) : AbsScroll(input, enableClipRecursively, debugScroll) {

    private val bottomNavigation = input.bottomNavigation

    override fun onAttach(activity: FragmentActivity) {
    }

    override fun onDetach(activity: FragmentActivity) {
    }

    override fun restoreInitialPosition(recyclerView: RecyclerView) {
        super.restoreInitialPosition(recyclerView)
        bottomNavigation.animate()?.translationY(0f)
    }

    override fun onRecyclerViewScrolled(recyclerView: RecyclerView, dx: Int, dy: Int, overScroll: Boolean) {
        if (!overScroll) {
            super.onRecyclerViewScrolled(recyclerView, dx, dy, overScroll)
        }

        val clampedNavigationTranslation =
            clamp(bottomNavigation.translationY + dy, 0f, bottomNavigation.height.toFloat())

        logVerbose {
            """
                onRecyclerViewScrolled: translating bottom navigation
                    from=${bottomNavigation.translationY} to=$clampedNavigationTranslation
            """.trimIndent()
        }

        updateOnlyIfNeeded(bottomNavigation, clampedNavigationTranslation)

        fabMap.get(recyclerView.hashCode())?.let {
            updateOnlyIfNeeded(it, clampedNavigationTranslation)
        }
    }

    override fun applyMarginToFab(fab: View) {
        fab.doOnPreDraw {
            val params = fab.layoutParams as? ViewGroup.MarginLayoutParams ?: return@doOnPreDraw

            val neverApplied = params.bottomMargin < bottomNavigation.height
            if (neverApplied){
                params.bottomMargin = params.bottomMargin + bottomNavigation.height
                fab.layoutParams = params
            }
        }
    }
}