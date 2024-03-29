package dev.olog.scrollhelper.impl

import android.view.View
import android.view.ViewGroup
import androidx.core.math.MathUtils.clamp
import androidx.core.view.doOnPreDraw
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.from
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
    private val slidingPanelRealPeek = input.realSlidingPanelPeek
    private val slidingPanelBehavior = from(slidingPanel) as BottomSheetBehavior<*>
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
        }
    }

    override fun onAttach(activity: FragmentActivity) {
        slidingPanelBehavior.addBottomSheetCallback(slidingPanelListener)
    }

    override fun onDetach(activity: FragmentActivity) {
        slidingPanelBehavior.removeBottomSheetCallback(slidingPanelListener)
    }

    override fun onRecyclerViewScrolled(recyclerView: RecyclerView, dx: Int, dy: Int, overScroll: Boolean) {
        if (!overScroll) {
            super.onRecyclerViewScrolled(recyclerView, dx, dy, overScroll)
        }

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

        val updatePadding = list.paddingBottom < slidingPanelRealPeek

        if (updatePadding) {
            list.updatePadding(bottom = list.paddingBottom + slidingPanelRealPeek)
        }
    }

    override fun applyMarginToFab(fab: View) {
        fab.doOnPreDraw {
            val params = fab.layoutParams as? ViewGroup.MarginLayoutParams ?: return@doOnPreDraw

            val neverApplied = params.bottomMargin < slidingPanelBehavior.peekHeight
            if (neverApplied){
                params.bottomMargin = params.bottomMargin + slidingPanelBehavior.peekHeight + slidingPanel.translationY.toInt()
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
