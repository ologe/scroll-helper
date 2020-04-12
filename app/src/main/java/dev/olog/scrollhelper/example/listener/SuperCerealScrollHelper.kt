package dev.olog.scrollhelper.example.listener

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import dev.olog.scrollhelper.ScrollHelper
import dev.olog.scrollhelper.example.R
import dev.olog.scrollhelper.example.findViewByIdNotRecursive

class SuperCerealScrollHelper(
    private val activity: FragmentActivity,
    fullScrollTop: Boolean,
    fullScrollBottom: Boolean
) : ScrollHelper(activity,fullScrollTop, fullScrollBottom) {

    private val viewPagerRegex = "^f\\d+\$".toRegex()

    override fun shouldSkipFragment(fragment: Fragment): Boolean {
        val tag = fragment.tag ?: return true

        if (tag.startsWith("dev.olog")) {
            // my fragments
            return false
        }
        if (isViewPagerFragment(tag)) {
            // view pager fragment
            return false
        }
        return true
    }

    override fun findTabLayout(fragment: Fragment): View? {
        return fragment.requireActivity().findViewById(R.id.tabLayout)
    }

    override fun findToolbar(fragment: Fragment): View? {
        return fragment.requireActivity().findViewById(R.id.toolbar)
    }

    private fun isViewPagerFragment(tag: String): Boolean {
        return viewPagerRegex.matches(tag)
    }

    override fun findBottomSheet(): View? {
        return activity.findViewById(R.id.slidingPanel)
    }

    override fun findBottomNavigation(): View? {
        return activity.findViewById(R.id.bottomNavigation)
    }

    override fun findRecyclerView(fragment: Fragment): RecyclerView? {
        return fragment.requireView().findViewByIdNotRecursive(R.id.list)
    }

    override fun findFab(fragment: Fragment): View? {
        return fragment.requireView().findViewByIdNotRecursive(R.id.fab)
    }

    override fun findViewPager(fragment: Fragment): ViewPager2? {
        return fragment.requireView().findViewByIdNotRecursive(R.id.viewPager)
    }

}