package dev.olog.scrollhelper.example.listener

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.math.MathUtils.clamp
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import dev.olog.scrollhelper.OnScrollSlidingBehavior
import dev.olog.scrollhelper.SuperCerealBottomSheetBehavior
import dev.olog.scrollhelper.ViewHeights
import dev.olog.scrollhelper.example.PagerFragment
import dev.olog.scrollhelper.example.R
import dev.olog.scrollhelper.example.findViewByIdNotRecursive

class MyOnScrollSlidingBehavior(
    activity: AppCompatActivity,
    slidingPanel: SuperCerealBottomSheetBehavior<*>,
    bottomNavigation: View,
    initialHeights: ViewHeights
//        private val blurView: View
) : OnScrollSlidingBehavior(activity, slidingPanel, bottomNavigation, initialHeights) {

    override fun restoreInitialPosition(recyclerView: RecyclerView) {
        super.restoreInitialPosition(recyclerView)
//        blurView.animate()?.translationY(0f)
    }

    override fun onRecyclerViewScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onRecyclerViewScrolled(recyclerView, dx, dy)
        val clampedNavigationTranslation =
            clamp(bottomNavigation.translationY + dy, 0f, bottomNavigation.height.toFloat())
//        blurView.translationY = clampedNavigationTranslation
    }

    private fun hasFragmentOwnership(tag: String?) = tag?.startsWith("dev.olog.msc") == true
    private fun isPlayerTag(tag: String?) = tag?.contains("Player") == true

    override fun couldHaveToolbar(f: Fragment): Boolean {
        return hasFragmentOwnership(f.tag) && !isPlayerTag(f.tag)
    }

    override fun skipFragment(f: Fragment): Boolean {
        return isPlayerTag(f.tag)
    }

    /**
     * All main recycler view in the app have [android:id] = [R.id.list] and are
     *  placed as direct child of root.
     * If fails to find R.id.list, try recursiveley to find [R.id.recycler_view]
     *  in the hierarchy (settings fragment)
     */
    override fun searchForRecyclerView(f: Fragment): RecyclerView? {
        var recyclerView = f.view?.findViewByIdNotRecursive<RecyclerView>(R.id.list)
//        if (recyclerView == null && f.tag == Fragments.SETTINGS) {
//            recyclerView = f.view?.findViewById(R.id.list)
//        }
        return recyclerView
    }

    override fun searchForViewPager(f: Fragment): ViewPager? {
        if (f.tag == PagerFragment.TAG) {
            return f.view?.findViewByIdNotRecursive(R.id.viewPager)
        }
        return null
    }

    override fun searchForToolbar(f: Fragment): View? {
        val view: View? = when {
            isViewPagerChildTag(f.tag) -> {
                // search toolbar and tab layout in parent fragment
                f.parentFragment?.view
            }
            couldHaveToolbar(f) -> f.view
            else -> null
        }
        return view?.findViewByIdNotRecursive(R.id.toolbar)
    }

    override fun searchForTabLayout(f: Fragment): View? {
        val view: View? = when {
            isViewPagerChildTag(f.tag) -> {
                // search toolbar and tab layout in parent fragment
                f.parentFragment?.view
            }
            couldHaveToolbar(f) -> f.view
            else -> null
        }

        return view?.findViewByIdNotRecursive(R.id.tabLayout)
    }

    override fun searchForFab(f: Fragment): View? {
        return f.view?.findViewById<View>(R.id.fab)
    }

}