package dev.olog.scrollhelper.impl

import android.view.View
import android.view.ViewGroup
import androidx.core.math.MathUtils.clamp
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import dev.olog.scrollhelper.InitialHeight
import dev.olog.scrollhelper.Input

internal class ScrollWithSlidingPanel(
    input: Input.OnlySlidingPanel,
    enableClipRecursively: Boolean,
    debugScroll: Boolean
) : AbsScroll(input, enableClipRecursively, debugScroll) {

    private val scrollSlidingPanel = input.scrollableSlidingPanel

    private val slidingPanel = input.slidingPanel.first
    private val slidingPanelHeight: InitialHeight = input.slidingPanel.second

    override fun onAttach(activity: FragmentActivity) {
    }

    override fun onDetach(activity: FragmentActivity) {
    }

    override fun restoreInitialPosition(recyclerView: RecyclerView) {
        super.restoreInitialPosition(recyclerView)
        if (!scrollSlidingPanel) {
            return
        }
        slidingPanel.peekHeight = slidingPanelHeight

    }

    override fun onRecyclerViewScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onRecyclerViewScrolled(recyclerView, dx, dy)
        if (!scrollSlidingPanel) {
            return
        }
        val newPeekHeight = clamp(
            slidingPanel.peekHeight - dy,
            0,
            slidingPanelHeight
        )

        logVerbose { """
             onRecyclerViewScrolled: translating sliding panel
                from=${slidingPanel.peekHeight} to $newPeekHeight
        """.trimIndent() }

        slidingPanel.peekHeight = newPeekHeight
        fabMap.get(recyclerView.hashCode())?.let {
            it.translationY = (slidingPanelHeight - newPeekHeight).toFloat()
        }
    }

    override fun applyMarginToFab(fab: View) {
        val params = fab.layoutParams
        val marginsToApply = slidingPanelHeight

        if (params is ViewGroup.MarginLayoutParams && params.bottomMargin < marginsToApply) {
            params.bottomMargin += marginsToApply
            fab.layoutParams = params
        }
    }
}