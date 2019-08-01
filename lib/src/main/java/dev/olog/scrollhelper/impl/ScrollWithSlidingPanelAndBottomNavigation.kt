package dev.olog.scrollhelper.impl

import android.view.View
import android.view.ViewGroup
import androidx.core.math.MathUtils.clamp
import androidx.core.view.doOnPreDraw
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.from
import dev.olog.scrollhelper.MultiListenerBottomSheetBehavior
import dev.olog.scrollhelper.ScrollType
import dev.olog.scrollhelper.SlidingPanelListener
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.math.abs

internal class ScrollWithSlidingPanelAndBottomNavigation(
    input: ScrollType.Full,
    enableClipRecursively: Boolean,
    debugScroll: Boolean
) : AbsScroll(input, enableClipRecursively, debugScroll) {

    private val slidingPanel = input.slidingPanel
    private val slidingPanelBehavior = from(slidingPanel) as MultiListenerBottomSheetBehavior<*>
    private val bottomNavigation = input.bottomNavigation

    private val slidingPanelListener by lazy(NONE) { SlidingPanelListener(bottomNavigation) }

    init {
        slidingPanel.doOnPreDraw {
            // for some reason start with a different translation, so force to 0
            it.translationY = 0f
        }
        bottomNavigation.doOnPreDraw {
            // to be consistent with sliding panel
            it.translationY = 0f
            slidingPanelBehavior.peekHeight = slidingPanelBehavior.peekHeight + it.height
        }
    }

    override fun onAttach(activity: FragmentActivity) {
        slidingPanelBehavior.addPanelSlideListener(slidingPanelListener)
    }

    override fun onDetach(activity: FragmentActivity) {
        slidingPanelBehavior.removePanelSlideListener(slidingPanelListener)
    }

    override fun onRecyclerViewScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onRecyclerViewScrolled(recyclerView, dx, dy)

        val clampedNavigationTranslation = clamp(
            bottomNavigation.translationY + dy,
            0f,
            bottomNavigation.height.toFloat()
        )

        logVerbose {
            """
                onRecyclerViewScrolled: 
                    - translating sliding panel from ${slidingPanel.translationY} to $clampedNavigationTranslation
                    - translating bottom navigation from ${bottomNavigation.translationY} to $clampedNavigationTranslation
            """.trimIndent()
        }

        updateOnlyIfNeeded(slidingPanel, clampedNavigationTranslation)

        updateOnlyIfNeeded(bottomNavigation, clampedNavigationTranslation)

        fabMap.get(recyclerView.hashCode())?.let { fab ->
            updateOnlyIfNeeded(fab, clampedNavigationTranslation)
        }
    }

    override fun restoreInitialPosition(recyclerView: RecyclerView) {
        super.restoreInitialPosition(recyclerView)
        bottomNavigation.animate()?.translationY(0f)
        slidingPanel.animate()?.translationY(0f)
    }

    override fun applyInsetsToList(list: RecyclerView, toolbar: View?, tabLayout: View?) {
        super.applyInsetsToList(list, toolbar, tabLayout)

        val minimumBottomInset = slidingPanelBehavior.peekHeight - bottomNavigation.height

        val updatePadding = list.paddingBottom < minimumBottomInset

        if (updatePadding) {
            list.updatePadding(bottom = list.paddingBottom + minimumBottomInset)
        }
    }

    override fun applyMarginToFab(fab: View) {
        fab.doOnPreDraw {
            val params = fab.layoutParams
            val marginsToApply = slidingPanelBehavior.peekHeight

            if (params is ViewGroup.MarginLayoutParams && params.bottomMargin < marginsToApply) {
                params.bottomMargin += marginsToApply
                fab.layoutParams = params
            }
        }
    }

    override fun updateOnlyIfNeeded(view: View, translationY: Float) {
        if (abs(view.translationY - translationY) > TOLERANCE &&
            slidingPanelBehavior.state == STATE_COLLAPSED
        ) {
            view.translationY = translationY
        }
    }
}
