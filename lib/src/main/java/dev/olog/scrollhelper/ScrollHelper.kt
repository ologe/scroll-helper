package dev.olog.scrollhelper

import android.view.View
import androidx.core.view.doOnLayout
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.scrollhelper.bottom.sheet.BottomSheetListener
import dev.olog.scrollhelper.extensions.overScrollDelegate
import dev.olog.scrollhelper.extensions.updateMargin
import dev.olog.scrollhelper.state.StateResetter.Companion.BOTTOM_NAVIGATION_STATE
import dev.olog.scrollhelper.state.StateResetter.Companion.BOTTOM_SHEET_STATE
import dev.olog.scrollhelper.state.StateResetter.Companion.FAB_STATE
import dev.olog.scrollhelper.state.StateResetter.Companion.TAB_LAYOUT_STATE
import dev.olog.scrollhelper.state.StateResetter.Companion.TOOLBAR_STATE
import dev.olog.scrollhelper.pager.ViewPagerCallback
import dev.olog.scrollhelper.recycler.view.RecyclerViewListener
import dev.olog.scrollhelper.recycler.view.RecyclerViewOverScrollListener
import dev.olog.scrollhelper.state.StateResetter
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.math.max
import kotlin.math.min

abstract class ScrollHelper(
    private val activity: FragmentActivity,
    internal val fullScrollTop: Boolean,
    internal val fullScrollBottom: Boolean
) : LifecycleCallback {

    private val callback = FragmentLifecycleMonitor(this)

    internal val fabMap = mutableMapOf<Hash, View>()
    internal val tabLayoutMap = mutableMapOf<Hash, View>()
    internal val toolbarMap = mutableMapOf<Hash, View>()
    private val viewPagerCallbackMap = mutableMapOf<Hash, ViewPagerCallback>()

    private val stateResetter = StateResetter(activity)

    internal val bottomNavigation by lazy(NONE) {
        findBottomNavigation().apply {
            stateResetter.restoreActivity(this, BOTTOM_NAVIGATION_STATE)
        }
    }
    internal val bottomSheet: BottomSheetData? by lazy(NONE) {
        val view = findBottomSheet() ?: return@lazy null
        val behavior = BottomSheetBehavior.from(view)
        BottomSheetData(view, behavior, behavior.peekHeight).apply {
            stateResetter.restoreActivity(view, BOTTOM_SHEET_STATE)
        }
    }

    private val scrollListener = RecyclerViewListener(this)
    private val oversScrollListener = RecyclerViewOverScrollListener(scrollListener)
    private val bottomSheetListener: BottomSheetListener? by lazy(NONE) {
        val bottomSheet = bottomSheet ?: return@lazy null
        val bottomNavigation = bottomNavigation ?: return@lazy null
        BottomSheetListener(bottomNavigation, bottomSheet.view)
    }

    private val lifecycleListener = object : DefaultLifecycleObserver {

        override fun onStart(owner: LifecycleOwner) {
            bottomSheetListener?.let {
                bottomSheet?.behavior?.addBottomSheetCallback(it)
            }
        }

        override fun onStop(owner: LifecycleOwner) {
            bottomSheetListener?.let {
                bottomSheet?.behavior?.removeBottomSheetCallback(it)
            }
            stateResetter.save(bottomNavigation, bottomSheet?.view)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(callback)
            fabMap.clear()
            tabLayoutMap.clear()
            toolbarMap.clear()
            viewPagerCallbackMap.clear()
            stateResetter.dispose()
        }
    }

    init {
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(callback, true)
        activity.lifecycle.addObserver(lifecycleListener)
        activity.findViewById<View>(android.R.id.content).doOnLayout {
            val bottomSheet = this.bottomSheet ?: return@doOnLayout
            val bottomNavigation = this.bottomNavigation ?: return@doOnLayout
            bottomSheet.behavior.peekHeight = bottomSheet.defaultPeek + bottomNavigation.height
        }
    }

    override fun onFragmentViewCreated(fragment: Fragment) {
        val recyclerView = findRecyclerView(fragment)
        val viewPager= findViewPager(fragment)

        val fab = findFab(fragment)
        val toolbar = findToolbar(fragment)
        val tabLayout = findTabLayout(fragment)

        val secondCap = findSecondCap(tabLayout, toolbar)
        val firstBaseline = findFirstBaseline()
        val secondBaseline = findSecondBaseline()

        if (recyclerView != null) {
            recyclerView.updatePadding(
                top = recyclerView.paddingTop + secondCap,
                bottom = recyclerView.paddingBottom + firstBaseline
            )

            val hashCode: Hash = recyclerView.hashCode()

            if (!stateResetter.isRestored(fragment)) {
                recyclerView.scrollToPosition(0)
            } else {
                stateResetter.restore(fragment, recyclerView)
            }

            if (fab != null) {
                fab.updateMargin(bottom = fab.marginBottom + secondBaseline)
                stateResetter.restore(fragment, fab, FAB_STATE)
                fabMap[hashCode] = fab
            }

            if (tabLayout != null) {
                stateResetter.restore(fragment, tabLayout, TAB_LAYOUT_STATE)
                tabLayoutMap[hashCode] = tabLayout
            }
            if (toolbar != null) {
                stateResetter.restore(fragment, toolbar, TOOLBAR_STATE)
                toolbarMap[hashCode] = toolbar
            }
        }

        if (recyclerView != null) {
            recyclerView.addOnScrollListener(scrollListener)
            recyclerView.overScrollDelegate.addOnOverScrollListener(oversScrollListener)
        }
        if (viewPager != null) {
            val callback = ViewPagerCallback(fragment.childFragmentManager, ::onPageChanged)
            viewPagerCallbackMap[viewPager.hashCode()] = callback
            viewPager.registerOnPageChangeCallback(callback)
        }
    }

    override fun onFragmentStopped(fragment: Fragment) {
        val recyclerView = findRecyclerView(fragment)
        if (recyclerView != null) {
            val hash = recyclerView.hashCode()
            stateResetter.save(
                fragment,
                toolbarMap[hash],
                tabLayoutMap[hash],
                fabMap[hash],
                recyclerView
            )
        }
    }

    override fun onFragmentViewDestroyed(fragment: Fragment) {
        val recyclerView = findRecyclerView(fragment)
        val viewPager = findViewPager(fragment)

        if (recyclerView != null) {
            val hash: Hash = recyclerView.hashCode()
            recyclerView.removeOnScrollListener(scrollListener)
            recyclerView.overScrollDelegate.removeOnOverScrollListener(oversScrollListener)

            fabMap.remove(hash)
            toolbarMap.remove(hash)
            tabLayoutMap.remove(hash)
        }

        if (viewPager != null) {
            val listener = viewPagerCallbackMap.remove(viewPager.hashCode())
            if (listener != null) {
                viewPager.unregisterOnPageChangeCallback(listener)
            }
        }
    }

    private fun onPageChanged(fragment: Fragment) {
        val recyclerView = findRecyclerView(fragment) ?: return
        val hash: Hash = recyclerView.hashCode()
        restoreInitialPosition(
            findTabLayout(fragment),
            findToolbar(fragment),
            fabMap[hash],
            bottomSheet?.view,
            bottomNavigation
        )
    }

    protected open fun restoreInitialPosition(
        tabLayout: View?,
        toolbar: View?,
        fab: View?,
        bottomSheet: View?,
        bottomNavigation: View?
    ) {
        tabLayout?.animate()?.translationY(0f)
        toolbar?.animate()?.translationY(0f)
        fab?.animate()?.translationY(0f)
        bottomSheet?.animate()?.translationY(0f)
        bottomNavigation?.animate()?.translationY(0f)
    }

    abstract fun findTabLayout(fragment: Fragment): View?
    abstract fun findToolbar(fragment: Fragment): View?

    abstract fun findBottomSheet(): View?
    abstract fun findBottomNavigation(): View?

    abstract fun findRecyclerView(fragment: Fragment): RecyclerView?
    abstract fun findFab(fragment: Fragment): View?

    abstract fun findViewPager(fragment: Fragment): ViewPager2?

    internal fun findFirstCap(tabLayout: View?, toolbar: View?): Int {
        val tabLayoutHeight = tabLayout?.height ?: 0
        val toolbarHeight = toolbar?.height ?: 0
        return min(tabLayoutHeight, toolbarHeight)
    }

    internal fun findSecondCap(tabLayout: View?, toolbar: View?): Int {
        val tabLayoutHeight = tabLayout?.height ?: 0
        val toolbarHeight = toolbar?.height ?: 0
        return tabLayoutHeight + toolbarHeight
    }

    // the lowest view height
    internal fun findFirstBaseline(): Int {
        if (fullScrollBottom) {
            return 0
        }
        val bottomSheet = this.bottomSheet?.behavior?.peekHeight ?: 0
        val bottomNavigation = this.bottomNavigation?.height ?: 0
        return min(bottomNavigation, bottomSheet)
    }

    internal fun findSecondBaseline(): Int {
        val bottomSheet = this.bottomSheet?.behavior?.peekHeight ?: 0
        val bottomNavigation = this.bottomNavigation?.height ?: 0
        return max(bottomNavigation, bottomSheet)
    }

}

