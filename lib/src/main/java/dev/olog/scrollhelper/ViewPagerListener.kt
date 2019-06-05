package dev.olog.scrollhelper

import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager

/**
 * Used to restore scrolling when the list contains too few items or the selected page never scrolled
 */
internal typealias PagePosition = Int

internal class ViewPagerListener(
    private val fm: FragmentManager,
    private val onPageChanged: (FragmentManager, PagePosition) -> Unit
) : ViewPager.SimpleOnPageChangeListener() {

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