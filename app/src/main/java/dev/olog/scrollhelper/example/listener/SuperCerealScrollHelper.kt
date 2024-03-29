package dev.olog.scrollhelper.example.listener

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import dev.olog.scrollhelper.ScrollHelper
import dev.olog.scrollhelper.ScrollType
import dev.olog.scrollhelper.example.BuildConfig
import dev.olog.scrollhelper.example.FragmentWithViewPager
import dev.olog.scrollhelper.example.R
import dev.olog.scrollhelper.example.findViewByIdNotRecursive

class SuperCerealScrollHelper(
    activity: FragmentActivity,
    input: ScrollType,
    enableClipRecursively: Boolean = true
) : ScrollHelper(activity, input, enableClipRecursively, true, true) {

    /**
     * Override this to restore your custom views to their start position
     */
    override fun restoreInitialPosition(recyclerView: RecyclerView) {
        super.restoreInitialPosition(recyclerView)
    }

    /**
     * Override this to translate your custom views
     */
    override fun onRecyclerViewScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onRecyclerViewScrolled(recyclerView, dx, dy)
    }

    override fun skipFragment(fragment: Fragment): Boolean {
        return !hasFragmentOwnership(fragment.tag) && !isViewPagerFragment(fragment.tag)
    }

    override fun searchForRecyclerView(fragment: Fragment): RecyclerView? {
        return fragment.view?.findViewByIdNotRecursive(R.id.list)
    }

    /**
     * Only [FragmentWithViewPager] can have viewpager
     */
    override fun searchForViewPager(fragment: Fragment): ViewPager? {
        if (fragment.tag == FragmentWithViewPager.TAG) {
            return fragment.view?.findViewByIdNotRecursive(R.id.viewPager)
        }
        return null
    }

    override fun searchForToolbar(fragment: Fragment): View? {
        val view: View? = when {
            isViewPagerChildTag(fragment.tag) -> {
                // search toolbar and tab layout in parent fragment
                fragment.parentFragment?.view
            }
            couldHaveToolbar(fragment) -> fragment.view
            else -> null
        }
        return view?.findViewByIdNotRecursive(R.id.toolbar)
    }

    override fun searchForTabLayout(fragment: Fragment): View? {
        val view: View? = when {
            isViewPagerChildTag(fragment.tag) -> {
                // search toolbar and tab layout in parent fragment
                fragment.parentFragment?.view
            }
            couldHaveToolbar(fragment) -> fragment.view
            else -> null
        }

        return view?.findViewByIdNotRecursive(R.id.tabLayout)
    }

    override fun searchForFab(fragment: Fragment): View? {
        return fragment.view?.findViewById(R.id.fab)
    }

    /**
     * Search only in my fragments, skip traversing the hierarchy view of non application fragments
     */
    private fun hasFragmentOwnership(tag: String?) = tag?.startsWith(BuildConfig.APPLICATION_ID) == true

    private fun isViewPagerFragment(tag: String?) = tag?.startsWith("android:switcher") == true

    private fun couldHaveToolbar(f: Fragment): Boolean {
        return hasFragmentOwnership(f.tag)
    }

    // TODO check after migrating to viewpager 2
    private fun isViewPagerChildTag(tag: String?) = tag?.startsWith("android:switcher:") == true

}