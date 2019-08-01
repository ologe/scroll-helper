package dev.olog.scrollhelper.impl

import android.view.View
import android.view.ViewGroup
import androidx.core.math.MathUtils.clamp
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.scrollhelper.MultiListenerBottomSheetBehavior
import dev.olog.scrollhelper.ScrollType
import kotlin.math.abs

internal class ScrollWithSlidingPanel(
    input: ScrollType.OnlySlidingPanel,
    enableClipRecursively: Boolean,
    debugScroll: Boolean
) : AbsScroll(input, enableClipRecursively, debugScroll) {

    companion object {
        private const val TOLERANCE = 0.01
    }

    private val scrollSlidingPanel: Boolean = input.scrollableSlidingPanel

    private val slidingPanel: View = input.slidingPanel
    private val slidingPanelBehavior =
        BottomSheetBehavior.from(slidingPanel) as MultiListenerBottomSheetBehavior<*>

    override fun onAttach(activity: FragmentActivity) {
    }

    override fun onDetach(activity: FragmentActivity) {
    }

    override fun restoreInitialPosition(recyclerView: RecyclerView) {
        super.restoreInitialPosition(recyclerView)
        if (!scrollSlidingPanel) {
            return
        }
        slidingPanel.animate()?.translationY(0f)
    }

    override fun onRecyclerViewScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onRecyclerViewScrolled(recyclerView, dx, dy)
        if (!scrollSlidingPanel) {
            return
        }
        val newPeekHeight = clamp(
            slidingPanel.translationY + dy,
            0f,
            slidingPanelBehavior.peekHeight.toFloat()
        )

        logVerbose {
            """
             onRecyclerViewScrolled: translating sliding panel
                from=${slidingPanel.translationY} to $newPeekHeight
        """.trimIndent()
        }

        if (abs(slidingPanel.translationY - newPeekHeight) > TOLERANCE &&
            slidingPanelBehavior.state == BottomSheetBehavior.STATE_COLLAPSED
        ) {
            slidingPanel.translationY = newPeekHeight

            fabMap.get(recyclerView.hashCode())?.let {
                it.translationY = slidingPanel.translationY
            }
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
}