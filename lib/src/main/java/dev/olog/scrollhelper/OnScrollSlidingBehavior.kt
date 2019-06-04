package dev.olog.scrollhelper

import android.annotation.SuppressLint
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.math.MathUtils.clamp
import androidx.core.util.forEach
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 * Adjust toolbar, tab layout(if present), bottom navigation and sliding panel sliding when a list
 * is scrollied
 *
 * This class assumes each fragment has a unique tag
 */

class ViewHeights(
    val slidingPanel: Int,
    val bottomNavigation: Int,
    val toolbar: Int,
    val tablayout: Int
)

abstract class OnScrollSlidingBehavior(
    protected val activity: AppCompatActivity,
    protected val slidingPanel: SuperCerealBottomSheetBehavior<*>,
    protected val bottomNavigation: View,
    protected val initialHeights: ViewHeights
) {

    private var toolbarMap = SparseArray<View>()
    private var tabLayoutMap = SparseArray<View>()
    private var fabMap = SparseArray<View>()
    private var viewPagerListenerMap = SparseArray<ViewPagerListener>()

    private val slidingPanelListener by lazy { SlidingPanelListener() }

    private val slidingPanelHeight = initialHeights.slidingPanel
    private val slidingPanelPlusNavigationHeight by lazy { slidingPanelHeight + initialHeights.bottomNavigation }

    open fun onAttach() {
        slidingPanel.addPanelSlideListener(slidingPanelListener)
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(callbacks, true)
    }

    open fun onDetach() {
        slidingPanel.removePanelSlideListener(slidingPanelListener)
        activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(callbacks)
    }

    open fun dispose() {
        toolbarMap.clear()
        tabLayoutMap.clear()
        fabMap.clear()
        viewPagerListenerMap.clear()
    }

    protected open fun onRecyclerViewScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        var clampedTabLayoutTranslation = 0f
        toolbarMap.get(recyclerView.hashCode())?.let { toolbar ->
            clampedTabLayoutTranslation = clamp(toolbar.translationY - dy, -toolbar.height.toFloat(), 0f)
            // moves the toolbar just a little more than its height
            val clampedToolbartTranslation = clamp(toolbar.translationY - dy, -toolbar.height.toFloat() * 1.2f, 0f)
            toolbar.translationY = clampedToolbartTranslation
        }
        tabLayoutMap.get(recyclerView.hashCode())?.let { tabLayout ->
            tabLayout.translationY = clampedTabLayoutTranslation
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

        fabMap.forEach { _, value -> value.translationY = clampedNavigationTranslation }
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            onRecyclerViewScrolled(recyclerView, dx, dy)
        }
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
     * TODO
     */
    @CallSuper
    protected open fun restoreInitialPosition(recyclerView: RecyclerView) {
        tabLayoutMap.get(recyclerView.hashCode())?.animate()?.translationY(0f)
        toolbarMap.get(recyclerView.hashCode())?.animate()?.translationY(0f)
        bottomNavigation.animate().translationY(0f)
        slidingPanel.peekHeight = slidingPanelPlusNavigationHeight
        fabMap.forEach { key, value ->
            value.animate().translationY(0f)
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

    private inner class SlidingPanelListener : BottomSheetBehavior.BottomSheetCallback() {

        private var lastState = BottomSheetBehavior.STATE_COLLAPSED
        private var lastCollapsedTranslationY = bottomNavigation.translationY

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            val translationY = clamp(
                bottomNavigation.height * clamp(slideOffset, 0f, 1f),
                lastCollapsedTranslationY,
                bottomNavigation.height.toFloat()
            )
            bottomNavigation.translationY = translationY
        }

        @SuppressLint("SwitchIntDef")
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (lastState == BottomSheetBehavior.STATE_COLLAPSED && newState == BottomSheetBehavior.STATE_DRAGGING) {
                lastCollapsedTranslationY = bottomNavigation.translationY
            }
            lastState = newState
        }
    }

    protected abstract fun couldHaveToolbar(f: Fragment): Boolean

    protected abstract fun skipFragment(f: Fragment): Boolean

    protected abstract fun searchForRecyclerView(f: Fragment): RecyclerView?

    protected abstract fun searchForViewPager(f: Fragment): ViewPager?

    protected abstract fun searchForToolbar(f: Fragment): View?

    protected abstract fun searchForTabLayout(f: Fragment): View?

    protected abstract fun searchForFab(f: Fragment): View?

    protected open fun applyInsetsToList(list: RecyclerView, toolbar: View?, tabLayout: View?) {
        val minimumTopInset =
            (if (toolbar != null) initialHeights.toolbar else 0) + (if (tabLayout != null) initialHeights.tablayout else 0)
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
        if (params is ViewGroup.MarginLayoutParams && params.bottomMargin < slidingPanelPlusNavigationHeight) {

            params.bottomMargin += slidingPanelPlusNavigationHeight
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

    private val callbacks = object : FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentResumed(fm: FragmentManager, fragment: Fragment) {
            println("on fragment resumed ${fragment.tag}")
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
            println("on fragment paused ${fragment.tag}")
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
            println("adding scroll listener to ${f.tag}")
        }

    }

}