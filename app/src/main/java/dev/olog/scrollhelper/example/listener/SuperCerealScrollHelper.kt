package dev.olog.scrollhelper.example.listener

import android.content.Context
import android.view.View
import androidx.annotation.DimenRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import dev.olog.scrollhelper.ScrollHelper
import dev.olog.scrollhelper.example.R
import dev.olog.scrollhelper.example.findViewByIdNotRecursive

private fun Context.dimen(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)

class SuperCerealScrollHelper(
    private val activity: FragmentActivity,
    fullScrollTop: Boolean,
    fullScrollBottom: Boolean
) : ScrollHelper(
    activity = activity,
    fullScrollTop = fullScrollTop,
    fullScrollBottom = fullScrollBottom,
    toolbarHeight = activity.dimen(R.dimen.toolbar),
    tabLayoutHeight = activity.dimen(R.dimen.tab_layout),
    bottomSheetHeight = activity.dimen(R.dimen.bottom_sheet), // just it's height, not counting the offset of bottom navigation
    bottomNavigationHeight = activity.dimen(R.dimen.bottom_navigation),
    restoreState = true // if true, you'll need to implement your recycler view scroll restore
) {

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
        val view = if (isViewPagerFragment(fragment.tag ?: "")) {
            fragment.requireParentFragment().requireView()
        } else {
            fragment.requireView()
        }
        return view.findViewByIdNotRecursive(R.id.tabLayout)
    }

    override fun findToolbar(fragment: Fragment): View? {
        val view = if (isViewPagerFragment(fragment.tag ?: "")) {
            fragment.requireParentFragment().requireView()
        } else {
            fragment.requireView()
        }
        return view.findViewByIdNotRecursive(R.id.toolbar)
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