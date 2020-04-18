package dev.olog.scrollhelper.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2

internal class ViewPager2Callback(
    private val fragmentManager: FragmentManager,
    private val onPageChanged: (Fragment) -> Unit
) : ViewPager2.OnPageChangeCallback() {

    private var first = true

    override fun onPageSelected(position: Int) {
        if (first) {
            // consumes on adapter attached event
            first = false
            return
        }

        val tag = "f$position"
        val fragment = fragmentManager.findFragmentByTag(tag) ?: return
        onPageChanged(fragment)
    }

}