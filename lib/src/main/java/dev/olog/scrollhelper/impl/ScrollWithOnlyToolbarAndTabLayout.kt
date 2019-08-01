package dev.olog.scrollhelper.impl

import android.view.View
import androidx.fragment.app.FragmentActivity
import dev.olog.scrollhelper.ScrollType

/**
 * Behavior when sliding panel and bottom navigation are missing
 */
internal class ScrollWithOnlyToolbarAndTabLayout(
    input: ScrollType.None,
    enableClipRecursively: Boolean,
    debugScroll: Boolean
) : AbsScroll(input, enableClipRecursively, debugScroll) {

    override fun onAttach(activity: FragmentActivity) {

    }

    override fun onDetach(activity: FragmentActivity) {

    }

    override fun applyMarginToFab(fab: View) {

    }
}