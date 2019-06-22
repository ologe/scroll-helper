package dev.olog.scrollhelper.impl

import android.view.View
import androidx.fragment.app.FragmentActivity
import dev.olog.scrollhelper.Input

/**
 * Behavior when sliding panel and bottom navigation are missing
 */
internal class ScrollWithOnlyToolbarAndTabLayout(
    input: Input.None,
    enableClipRecursively: Boolean
) : AbsScroll(input, enableClipRecursively) {

    override fun onAttach(activity: FragmentActivity) {

    }

    override fun onDetach(activity: FragmentActivity) {

    }

    override fun applyMarginToFab(fab: View) {

    }
}