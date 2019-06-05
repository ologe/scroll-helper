package dev.olog.scrollhelper

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.core.math.MathUtils.clamp
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

@Suppress("MemberVisibilityCanBePrivate")
abstract class OnScrollSlidingBehavior(
    protected val slidingPanel: MultiListenerBottomSheetBehavior<*>?,
    protected val bottomNavigation: View?,
    protected val initialHeights: ViewHeights
) {

    private var toolbarMap = SparseArray<View>()
    private var tabLayoutMap = SparseArray<View>()
    private var fabMap = SparseArray<View>()
    private var viewPagerListenerMap = SparseArray<ViewPagerListener>()

    private val slidingPanelListener by lazy(LazyThreadSafetyMode.NONE) { SlidingPanelListener(bottomNavigation!!) }

    private val slidingPanelHeight = initialHeights.slidingPanel
    private val slidingPanelPlusNavigationHeight by lazy(LazyThreadSafetyMode.NONE) {
        slidingPanelHeight + initialHeights.bottomNavigation
    }

    init {
        // TODO move to a sealed class?
        if (slidingPanel != null && initialHeights.slidingPanel == 0) {
            throw IllegalStateException("sliding panel height can not be zero")
        }
        if (bottomNavigation != null && initialHeights.bottomNavigation == 0) {
            throw IllegalStateException("bottom navigation height can not be zero")
        }
        if (slidingPanel == null && initialHeights.slidingPanel > 0){
            throw IllegalStateException("a null sliding panel can not have positive height")
        }
        if (bottomNavigation == null && initialHeights.bottomNavigation > 0){
            throw IllegalStateException("a null bottom navigation can not have positive height")
        }
    }

    /**
     * Attach listeners
     */
    open fun onAttach(activity: FragmentActivity) {
        if (slidingPanel != null && bottomNavigation != null) {
            slidingPanel.addPanelSlideListener(slidingPanelListener)
        }
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
    }

    /**
     * Detach listeners
     */
    open fun onDetach(activity: FragmentActivity) {
        if (slidingPanel != null && bottomNavigation != null) {
            slidingPanel.removePanelSlideListener(slidingPanelListener)
        }
        activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
    }

    open fun dispose() {
        toolbarMap.clear()
        tabLayoutMap.clear()
        fabMap.clear()
        viewPagerListenerMap.clear()
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            onRecyclerViewScrolled(recyclerView, dx, dy)
        }
    }

    // used to restore scrolling when the list contains too few items
    private inner class ViewPagerListener(private val fm: FragmentManager) : ViewPager.SimpleOnPageChangeListener() {

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

    private val fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentResumed(fm: FragmentManager, fragment: Fragment) {
            if (skipFragment(fragment)) {
                return
            }

            searchForViewPager(fragment)?.let { viewPager ->
                val listener = ViewPagerListener(fragment.childFragmentManager)
                viewPagerListenerMap.append(viewPager.hashCode(), listener)
                viewPager.addOnPageChangeListener(listener)
            }

            val recyclerView = searchForRecyclerView(fragment)
            if (recyclerView != null) {
                addOnScrollListener(fragment, recyclerView)
                searchForToolbar(fragment)?.let { toolbarMap.append(recyclerView.hashCode(), it) }
                searchForTabLayout(fragment)?.let { tabLayoutMap.append(recyclerView.hashCode(), it) }
                applyInsetsToList(
                    recyclerView,
                    toolbarMap.get(recyclerView.hashCode()),
                    tabLayoutMap.get(recyclerView.hashCode())
                )
            }
        }

        override fun onFragmentPaused(fm: FragmentManager, fragment: Fragment) {
            if (skipFragment(fragment)) {
                return
            }

            searchForViewPager(fragment)?.let { viewPager ->
                val listener = viewPagerListenerMap.get(viewPager.hashCode())
                viewPager.removeOnPageChangeListener(listener)
                viewPagerListenerMap.remove(viewPager.hashCode())
            }

            val recyclerView = searchForRecyclerView(fragment)
            if (recyclerView != null) {
                recyclerView.removeOnScrollListener(onScrollListener)
                fabMap.remove(recyclerView.hashCode())

                searchForToolbar(fragment)?.let { toolbarMap.remove(recyclerView.hashCode()) }
                searchForTabLayout(fragment)?.let { tabLayoutMap.remove(recyclerView.hashCode()) }
            }
        }

        private fun addOnScrollListener(f: Fragment, recyclerView: RecyclerView) {
            recyclerView.addOnScrollListener(onScrollListener)

            searchForFab(f)?.let { fab ->
                fabMap.append(recyclerView.hashCode(), fab)
                applyMarginToFab(fab)
            }
        }

    }

    /**
     * When scrolling up, scrolls up toolbar and tablayout, and scrolls down bottom navigation and sliding panel
     * When scrolling down, restores all the view to their initial position
     */
    protected open fun onRecyclerViewScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

        var clampedTabLayoutTranslation = 0f
        toolbarMap.get(recyclerView.hashCode())?.let { toolbar ->
            clampedTabLayoutTranslation = clamp(toolbar.translationY - dy, -toolbar.height.toFloat(), 0f)
            // moves the toolbar just a little more than its height, to handle cases when status bar is transparent
            val clampedToolbarTranslation = clamp(toolbar.translationY - dy, -toolbar.height.toFloat() * 1.2f, 0f)
            toolbar.translationY = clampedToolbarTranslation
        }
        tabLayoutMap.get(recyclerView.hashCode())?.let { tabLayout ->
            tabLayout.translationY = clampedTabLayoutTranslation
        }

        if (bottomNavigation == null || slidingPanel == null) {
            return
        }

        val clampedNavigationTranslation =
            clamp(bottomNavigation.translationY + dy, 0f, bottomNavigation.height.toFloat())
        val clampedSlidingPanelTranslationY = clamp(
            slidingPanelPlusNavigationHeight - clampedNavigationTranslation.toInt(),
            slidingPanelHeight,
            slidingPanelPlusNavigationHeight
        )
        slidingPanel.peekHeight = clampedSlidingPanelTranslationY
        bottomNavigation.translationY = clampedNavigationTranslation

        fabMap.get(recyclerView.hashCode())?.let { it.translationY = clampedNavigationTranslation }
    }

    /**
     * TODO need to fix animation
     */
    protected open fun restoreInitialPosition(recyclerView: RecyclerView) {
        tabLayoutMap.get(recyclerView.hashCode())?.animate()?.translationY(0f)
        toolbarMap.get(recyclerView.hashCode())?.animate()?.translationY(0f)
        bottomNavigation?.animate()?.translationY(0f)
        slidingPanel?.peekHeight = slidingPanelPlusNavigationHeight
        fabMap.get(recyclerView.hashCode())?.animate()?.translationY(0f)
    }

    private fun onPageChanged(fm: FragmentManager, position: Int) {
        val fragment = fm.fragments.find { it.tag?.last().toString() == position.toString() } ?: return
        val recyclerView = searchForRecyclerView(fragment) ?: return
        if (!recyclerView.canScrollVertically(-1)) {
            // there are no items offscreen
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
     * Automatically applies padding
     */
    protected open fun applyInsetsToList(list: RecyclerView, toolbar: View?, tabLayout: View?) {
        val minimumTopInset =
            (if (toolbar != null) initialHeights.toolbar else 0) + (if (tabLayout != null) initialHeights.tabLayout else 0)
        val minimumBottomInset = slidingPanelHeight
        var updatePadding = list.paddingTop - minimumTopInset < 0
        updatePadding = updatePadding || list.paddingBottom < minimumBottomInset
        if (updatePadding) {
            list.updatePadding(
                top = list.paddingTop + minimumTopInset,
                bottom = list.paddingBottom + minimumBottomInset
            )
        }
        list.clipChildren = false
        list.clipToPadding = false
        disableClipRecursively(list.parent)
    }

    protected open fun applyMarginToFab(fab: View) {
        val params = fab.layoutParams
        val marginsToApply = when {
            bottomNavigation != null && slidingPanel != null -> slidingPanelPlusNavigationHeight
            bottomNavigation == null && slidingPanel == null -> 0
            bottomNavigation == null -> initialHeights.slidingPanel
            slidingPanel == null -> initialHeights.bottomNavigation
            else -> throw IllegalStateException("invalid state")
        }

        if (params is ViewGroup.MarginLayoutParams && params.bottomMargin < marginsToApply) {
            params.bottomMargin += marginsToApply
            fab.layoutParams = params
        }
    }

    private fun disableClipRecursively(view: ViewParent?) {
        if (view == null) {
            return
        }
        if (view is ViewGroup) {
            view.clipChildren = false
            view.clipToPadding = false
        }
    }

    // TODO check after migrating to viewpager 2
    protected fun isViewPagerChildTag(tag: String?) = tag?.startsWith("android:switcher:") == true

}