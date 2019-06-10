package dev.olog.scrollhelper

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import dev.olog.scrollhelper.impl.*

abstract class OnScrollSlidingBehavior(
    private val activity: FragmentActivity,
    input: Input
) {


    private val impl: AbsScroll = when (input) {
        is Input.Full -> ScrollWithSlidingPanelAndBottomNavigation(input)
        is Input.OnlyBottomNavigation -> ScrollWithBottomNavigation(input)
        is Input.OnlySlidingPanel -> ScrollWithSlidingPanel(input)
        is Input.None -> ScrollWithOnlyToolbarAndTabLayout(input)

    }

    /**
     * Attach listeners
     */
    open fun onAttach() {
        impl.onAttach(activity)
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
    }

    /**
     * Detach listeners
     */
    open fun onDetach() {
        impl.onDetach(activity)
        activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
    }

    /**
     * Clear resources
     */
    open fun dispose() {
        impl.dispose()
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            onRecyclerViewScrolled(recyclerView, dx, dy)
        }
    }

    private val fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentResumed(fm: FragmentManager, fragment: Fragment) {
            if (fragment.view == null || skipFragment(fragment)) {
                return
            }

            searchForViewPager(fragment)?.let { viewPager ->
                val listener = ViewPagerListener(fragment.childFragmentManager, ::onPageChanged)
                impl.viewPagerListenerMap.append(viewPager.hashCode(), listener)
                viewPager.addOnPageChangeListener(listener)
            }

            searchForRecyclerView(fragment)?.let { recyclerView ->
                // recycler view found, add scroll listener
                recyclerView.addOnScrollListener(onScrollListener)
                // map recycler view to toolbar
                searchForToolbar(fragment)?.let {
                    impl.toolbarMap.append(recyclerView.hashCode(), it)
                }
                // map recycler view to tabLayout
                searchForTabLayout(fragment)?.let {
                    impl.tabLayoutMap.append(recyclerView.hashCode(), it)
                }
                // map recycler view to fab
                searchForFab(fragment)?.let { fab ->
                    impl.fabMap.append(recyclerView.hashCode(), fab)
                    applyMarginToFab(fab)
                }

                applyInsetsToList(
                    recyclerView,
                    impl.toolbarMap.get(recyclerView.hashCode()),
                    impl.tabLayoutMap.get(recyclerView.hashCode())
                )
            }
        }

        override fun onFragmentPaused(fm: FragmentManager, fragment: Fragment) {
            if (skipFragment(fragment)) {
                return
            }

            searchForViewPager(fragment)?.let { viewPager ->
                val listener = impl.viewPagerListenerMap.get(viewPager.hashCode())
                viewPager.removeOnPageChangeListener(listener)
                impl.viewPagerListenerMap.remove(viewPager.hashCode())
            }

            searchForRecyclerView(fragment)?.let { recyclerView ->
                // recycler view found, detach listener and clean
                recyclerView.removeOnScrollListener(onScrollListener)
                impl.fabMap.remove(recyclerView.hashCode())
                impl.toolbarMap.remove(recyclerView.hashCode())
                impl.tabLayoutMap.remove(recyclerView.hashCode())
            }
        }

    }

    /**
     * When scrolling up, scrolls up toolbar and tablayout, and scrolls down bottom navigation and sliding panel
     * When scrolling down, restores all the view to their initial position
     */
    protected open fun onRecyclerViewScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        impl.onRecyclerViewScrolled(recyclerView, dx, dy)
    }

    /**
     * TODO need to fix animation
     */
    protected open fun restoreInitialPosition(recyclerView: RecyclerView) {
        impl.restoreInitialPosition(recyclerView)
    }

    // assumes that uses view pager default tag -> [android:switcher:containerId:pagePosition]
    protected open fun onPageChanged(fm: FragmentManager, position: PagePosition) {
        val fragment = fm.fragments.find { it.tag?.last().toString() == position.toString() } ?: return

        val recyclerView = searchForRecyclerView(fragment) ?: return
        if (!recyclerView.canScrollVertically(-1)) {
            // there are no items offscreen, restore views to their initial position
            restoreInitialPosition(recyclerView)
        }
    }

    /**
     * Don't any operation on the fragments that returns true.
     * @param fragment the fragment where to search
     * Skips all 'searchFor*' calls.
     */
    protected abstract fun skipFragment(fragment: Fragment): Boolean

    /**
     * Search for recycler view in current view
     * @param fragment the fragment where to search
     * @return the recycler view of current fragment (if any)
     */
    protected abstract fun searchForRecyclerView(fragment: Fragment): RecyclerView?

    /**
     * Search for view pager in current view
     * @param fragment the fragment where to search
     * @return the view pager of current fragment (if any)
     */
    protected abstract fun searchForViewPager(fragment: Fragment): ViewPager?

    /**
     * Search for toolbar in current view
     * @param fragment the fragment where to search
     * @return the toolbar of current fragment (if any)
     */
    protected abstract fun searchForToolbar(fragment: Fragment): View?

    /**
     * Search for tab layout in current view
     * @param fragment the fragment where to search
     * @return the tab layout of current fragment (if any)
     */
    protected abstract fun searchForTabLayout(fragment: Fragment): View?

    /**
     * Search for floating action button in current view
     * @param fragment the fragment where to search
     * @return the floating action button of current fragment (if any)
     */
    protected abstract fun searchForFab(fragment: Fragment): View?

    /**
     * Automatically adds padding to list, override to change the behavior
     * @param list list to apply padding
     * @param toolbar associated toolbar
     * @param tabLayout associated tabLayout
     */
    protected open fun applyInsetsToList(list: RecyclerView, toolbar: View?, tabLayout: View?) {
        impl.applyInsetsToList(list, toolbar, tabLayout)
    }

    /**
     * Automatically adds margin to fab, override to change the behavior
     * @param fab view to apply margin
     */
    protected open fun applyMarginToFab(fab: View) {
        impl.applyMarginToFab(fab)
    }

}