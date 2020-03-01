package dev.olog.scrollhelper

import android.util.Log
import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import dev.olog.scrollhelper.impl.*
import dev.olog.scrollhelper.layoutmanagers.*
import java.lang.Exception

abstract class ScrollHelper(
    private val activity: FragmentActivity,
    input: ScrollType,
    enableClipRecursively: Boolean,
    private val debug: Boolean,
    debugScroll: Boolean
) : DefaultLifecycleObserver {

    companion object {
        private const val TAG = "ScrollHelper"
        private const val VIEW_PAGER_TAG_START = "android:switcher"
    }

    private val impl: AbsScroll = when (input) {
        is ScrollType.Full -> ScrollWithSlidingPanelAndBottomNavigation(
            input,
            enableClipRecursively,
            debugScroll
        )
        is ScrollType.OnlyBottomNavigation -> ScrollWithBottomNavigation(
            input,
            enableClipRecursively,
            debugScroll
        )
        is ScrollType.OnlySlidingPanel -> ScrollWithSlidingPanel(
            input,
            enableClipRecursively,
            debugScroll
        )
        is ScrollType.None -> ScrollWithOnlyToolbarAndTabLayout(
            input,
            enableClipRecursively,
            debugScroll
        )

    }

    private inline fun logVerbose(crossinline msg: () -> String) {
        if (debug) {
            Log.v(TAG, msg())
        }
    }

    /**
     * Attach listeners
     */
    @CallSuper
    override fun onStart(owner: LifecycleOwner) {
        logVerbose { "onAttach" }

        impl.onAttach(activity)
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
            fragmentLifecycleCallbacks,
            true
        )
    }

    /**
     * Detach listeners
     */
    @CallSuper
    override fun onStop(owner: LifecycleOwner) {
        logVerbose { "onDetach" }

        impl.onDetach(activity)
        activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(
            fragmentLifecycleCallbacks
        )
    }

    /**
     * Clear resources
     */
    @CallSuper
    override fun onDestroy(owner: LifecycleOwner) {
        logVerbose { "dispose" }

        impl.dispose()
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            onRecyclerViewScrolled(recyclerView, dx, dy)
        }
    }

    private val onOverScrollListener = object : OnOverScrollListener {
        override fun onRecyclerViewOverScroll(recyclerView: RecyclerView, dy: Int) {
            onRecyclerViewOverScrolled(recyclerView, dy)
        }
    }

    private val fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentResumed(fm: FragmentManager, fragment: Fragment) {
            logVerbose { "onFragmentResumed=${fragment.tag}" }
            if (fragment.view == null || skipFragment(fragment)) {
                logVerbose { "skipping" }
                return
            }

            logVerbose { "search view pager" }

            searchForViewPager(fragment)?.let { viewPager ->
                logVerbose { "view pager found" }
                val listener = ViewPagerListener(fragment.childFragmentManager, ::onPageChanged)
                impl.viewPagerListenerMap.append(viewPager.hashCode(), listener)
                viewPager.addOnPageChangeListener(listener)
            }

            logVerbose { "search recycler view" }
            searchForRecyclerView(fragment)?.let { recyclerView ->
                logVerbose { "recycler view found" }
                requireOverScrollLayoutManager(fragment.tag, recyclerView)
                // recycler view found, add scroll listener
                recyclerView.addOnScrollListener(onScrollListener)
                getOverScrollDelegate(recyclerView).addOnOverScrollListener(onOverScrollListener)

                logVerbose { "search toolbar" }
                // map recycler view to toolbar
                searchForToolbar(fragment)?.let {
                    logVerbose { "toolbar found" }
                    impl.toolbarMap.append(recyclerView.hashCode(), it)
                }
                logVerbose { "search tab layout" }
                // map recycler view to tabLayout
                searchForTabLayout(fragment)?.let {
                    logVerbose { "tab layout found" }
                    impl.tabLayoutMap.append(recyclerView.hashCode(), it)
                }
                logVerbose { "search fab" }
                // map recycler view to fab
                searchForFab(fragment)?.let { fab ->
                    logVerbose { "fab found" }
                    impl.fabMap.append(recyclerView.hashCode(), fab)
                    applyMarginToFab(fragment, fab)
                }

                applyInsetsToList(
                    fragment,
                    recyclerView,
                    impl.toolbarMap.get(recyclerView.hashCode()),
                    impl.tabLayoutMap.get(recyclerView.hashCode())
                )
            }
            logVerbose { "****" }
        }

        private fun requireOverScrollLayoutManager(tag: String?, recyclerView: RecyclerView) {
            val layoutManager = recyclerView.layoutManager
            require(
                layoutManager is OverScrollLinearLayoutManager ||
                        layoutManager is OverScrollGridLayoutManager ||
                        layoutManager is OverScrollStaggeredGridLayoutManager
            ) {
                "recycler view in fragment with tag=$tag must have one of the following layouts manager " +
                        "${OverScrollLinearLayoutManager::class.java.name}, " +
                        "${OverScrollGridLayoutManager::class.java.name}, " +
                        OverScrollStaggeredGridLayoutManager::class.java.name
            }
        }

        private fun getOverScrollDelegate(recyclerView: RecyclerView): OverScrollDelegate{
            return when (val layoutManager = recyclerView.layoutManager){
                is OverScrollLinearLayoutManager -> layoutManager
                is OverScrollGridLayoutManager -> layoutManager
                is OverScrollStaggeredGridLayoutManager -> layoutManager
                else -> throw Exception("shouldn't happen")
            }
        }

        override fun onFragmentPaused(fm: FragmentManager, fragment: Fragment) {
            logVerbose { "onFragmentPaused=${fragment.tag}" }
            if (skipFragment(fragment)) {
                logVerbose { "skipping" }
                return
            }

            logVerbose { "search view pager" }
            searchForViewPager(fragment)?.let { viewPager ->
                logVerbose { "view pager found" }
                val listener = impl.viewPagerListenerMap.get(viewPager.hashCode())
                viewPager.removeOnPageChangeListener(listener)
                impl.viewPagerListenerMap.remove(viewPager.hashCode())
            }

            logVerbose { "search recycler view" }
            searchForRecyclerView(fragment)?.let { recyclerView ->
                logVerbose { "recycler view found" }
                // recycler view found, detach listener and clean
                recyclerView.removeOnScrollListener(onScrollListener)
                getOverScrollDelegate(recyclerView).removeOnOverScrollListener(onOverScrollListener)
                impl.fabMap.remove(recyclerView.hashCode())
                impl.toolbarMap.remove(recyclerView.hashCode())
                impl.tabLayoutMap.remove(recyclerView.hashCode())
            }
            logVerbose { "****" }
        }

    }

    /**
     * When scrolling up, scrolls up toolbar and tablayout, and scrolls down bottom navigation and sliding panel
     * When scrolling down, restores all the view to their initial position
     */
    protected open fun onRecyclerViewScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        impl.onRecyclerViewScrolled(recyclerView, dx, dy, overScroll = false)
    }

    protected open fun onRecyclerViewOverScrolled(recyclerView: RecyclerView, dy: Int) {
        impl.onRecyclerViewScrolled(recyclerView, 0, dy, overScroll = true)
    }

    /**
     * TODO need to fix animation
     */
    protected open fun restoreInitialPosition(recyclerView: RecyclerView) {
        logVerbose { "restoreInitialPosition" }
        impl.restoreInitialPosition(recyclerView)
    }

    protected open fun onPageChanged(fm: FragmentManager, position: PagePosition) {
        logVerbose { "view pager page change $position" }
        val fragment = fm.fragments
            .find { filterViewPagerFragmentWithPosition(it, position) } ?: return

        val recyclerView = searchForRecyclerView(fragment) ?: return
        if (!recyclerView.canScrollVertically(-1)) {
            // there are no items offscreen, restore views to their initial position
            restoreInitialPosition(recyclerView)
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    // assumes that uses view pager default tag -> [android:switcher:containerId:pagePosition]
    private inline fun filterViewPagerFragmentWithPosition(
        fragment: Fragment,
        position: PagePosition
    ): Boolean {
        val tag = fragment.tag ?: ""
        return tag.startsWith(VIEW_PAGER_TAG_START) && tag.last().toString() == "$position"
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
    protected open fun applyInsetsToList(
        fragment: Fragment,
        list: RecyclerView,
        toolbar: View?,
        tabLayout: View?
    ) {
        logVerbose { "apply insets to list" }
        impl.applyInsetsToList(list, toolbar, tabLayout)
    }

    /**
     * Automatically adds margin to fab, override to change the behavior
     * @param fab view to apply margin
     */
    protected open fun applyMarginToFab(fragment: Fragment, fab: View) {
        logVerbose { "apply margin to fab" }
        impl.applyMarginToFab(fab)
    }

}