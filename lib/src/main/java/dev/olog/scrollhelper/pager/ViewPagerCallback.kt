package dev.olog.scrollhelper.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2

internal class ViewPagerCallback(
    private val fragmentManager: FragmentManager,
    private val onPageChanged: (Fragment) -> Unit
) : ViewPager2.OnPageChangeCallback() {

    override fun onPageSelected(position: Int) {
        val tag = "f$position"

        val fragment = fragmentManager.findFragmentByTag(tag) ?: return
        onPageChanged(fragment)
    }

}