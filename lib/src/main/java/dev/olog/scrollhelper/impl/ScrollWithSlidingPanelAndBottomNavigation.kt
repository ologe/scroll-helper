package dev.olog.scrollhelper.impl

import android.view.View
import android.view.ViewGroup
import androidx.core.math.MathUtils.clamp
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import dev.olog.scrollhelper.InitialHeight
import dev.olog.scrollhelper.Input
import dev.olog.scrollhelper.SlidingPanelListener

internal class ScrollWithSlidingPanelAndBottomNavigation(
    input: Input.Full,
    enableClipRecursively: Boolean,
    debugScroll: Boolean
) : AbsScroll(input, enableClipRecursively, debugScroll) {

    private val slidingPanelHeight: InitialHeight = input.slidingPanel.second
    private val slidingPanelPlusNavigationHeight: InitialHeight = slidingPanelHeight + input.bottomNavigation.second

    private val slidingPanel = input.slidingPanel.first
    private val bottomNavigation = input.bottomNavigation.first

    private val slidingPanelListener by lazy(LazyThreadSafetyMode.NONE) { SlidingPanelListener(bottomNavigation) }

    override fun onAttach(activity: FragmentActivity) {
        slidingPanel.addPanelSlideListener(slidingPanelListener)
    }

    override fun onDetach(activity: FragmentActivity) {
        slidingPanel.removePanelSlideListener(slidingPanelListener)
    }

    override fun onRecyclerViewScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onRecyclerViewScrolled(recyclerView, dx, dy)

        val clampedNavigationTranslation =
            clamp(bottomNavigation.translationY + dy, 0f, bottomNavigation.height.toFloat())

        val clampedSlidingPanelTranslationY = clamp(
            slidingPanelPlusNavigationHeight - clampedNavigationTranslation.toInt(),
            slidingPanelHeight,
            slidingPanelPlusNavigationHeight
        )

        logVerbose {
            """
                onRecyclerViewScrolled: 
                    - translating sliding panel from ${slidingPanel.peekHeight} to $clampedSlidingPanelTranslationY
                    - translating bottom navigation from ${bottomNavigation.translationY} to $clampedNavigationTranslation
            """.trimIndent()
        }

        slidingPanel.peekHeight = clampedSlidingPanelTranslationY
        bottomNavigation.translationY = clampedNavigationTranslation

        fabMap.get(recyclerView.hashCode())?.let { it.translationY = clampedNavigationTranslation }
    }

    override fun restoreInitialPosition(recyclerView: RecyclerView) {
        super.restoreInitialPosition(recyclerView)
        bottomNavigation.animate()?.translationY(0f)
        slidingPanel.peekHeight = slidingPanelPlusNavigationHeight
    }

    override fun applyInsetsToList(list: RecyclerView, toolbar: View?, tabLayout: View?) {
        super.applyInsetsToList(list, toolbar, tabLayout)

        val minimumBottomInset = slidingPanelHeight

        val updatePadding = list.paddingBottom < minimumBottomInset

        if (updatePadding) {
            list.updatePadding(bottom = list.paddingBottom + minimumBottomInset)
        }
    }

    override fun applyMarginToFab(fab: View) {
        val params = fab.layoutParams
        val marginsToApply = slidingPanelPlusNavigationHeight
        if (params is ViewGroup.MarginLayoutParams && params.bottomMargin < marginsToApply) {
            params.bottomMargin += marginsToApply
            fab.layoutParams = params
        }
    }
}
