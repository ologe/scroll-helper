package dev.olog.scrollhelper

import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2

internal class ViewPager2Listener(
    private val fm: FragmentManager,
    private val onPageChanged: (FragmentManager, PagePosition) -> Unit
) : ViewPager2.OnPageChangeCallback() {

    private var lastPage = -1

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (lastPage != position) {
            lastPage = position
            onPageChanged(fm, position)
        }
    }

    override fun onPageSelected(position: Int) {
        if (lastPage != position) {
            lastPage = position
            onPageChanged(fm, position)
        }
    }

}