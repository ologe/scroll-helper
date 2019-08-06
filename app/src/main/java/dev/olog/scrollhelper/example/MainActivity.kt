package dev.olog.scrollhelper.example

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dev.olog.scrollhelper.ScrollHelper
import dev.olog.scrollhelper.ScrollType
import dev.olog.scrollhelper.example.listener.MyScrollHelper
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Change me to test other behavior
 */
var type = MainActivity.Type.FULL

class MainActivity : AppCompatActivity() {

    private lateinit var onScrollBehavior: ScrollHelper

    enum class Type {
        FULL,
        ONLY_SLIDING_PANEL,
        ONLY_BOTTOM_NAVIGATION,
        NONE
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        when (type) {
            Type.ONLY_SLIDING_PANEL -> {
                root.removeView(bottomNavigation)
            }
            Type.ONLY_BOTTOM_NAVIGATION -> {
                root.removeView(slidingPanel)
            }
            Type.NONE -> {
                root.removeView(bottomNavigation)
                root.removeView(slidingPanel)
            }
            Type.FULL -> { /*keep all*/
            }
        }

        setupScrollBehavior()
        setupBottomNavigation()

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, FragmentWithViewPager(), FragmentWithViewPager.TAG)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        onScrollBehavior.onAttach()
    }

    override fun onPause() {
        super.onPause()
        onScrollBehavior.onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()
        onScrollBehavior.dispose()
    }

    private fun setupScrollBehavior() {
        val slidingPanel = findViewById<View>(R.id.slidingPanel)

        val bottomNavigation = findViewById<View>(R.id.bottomNavigation)

        val input: ScrollType = when (type) {
            Type.FULL -> {
                ScrollType.Full(
                    slidingPanel = slidingPanel,
                    bottomNavigation = bottomNavigation,
                    toolbarHeight = dimen(R.dimen.toolbar),
                    tabLayoutHeight = dimen(R.dimen.tabLayout),
                    realSlidingPanelPeek = dimen(R.dimen.sliding_panel)
                )
            }
            Type.ONLY_SLIDING_PANEL -> {
                ScrollType.OnlySlidingPanel(
                    slidingPanel,
                    toolbarHeight = dimen(R.dimen.toolbar),
                    tabLayoutHeight = dimen(R.dimen.tabLayout),
                    scrollableSlidingPanel = true
                )
            }
            Type.ONLY_BOTTOM_NAVIGATION -> {
                ScrollType.OnlyBottomNavigation(
                    bottomNavigation,
                    toolbarHeight = dimen(R.dimen.toolbar),
                    tabLayoutHeight = dimen(R.dimen.tabLayout)
                )
            }
            Type.NONE -> {
                ScrollType.None(
                    toolbarHeight = dimen(R.dimen.toolbar),
                    tabLayoutHeight = dimen(R.dimen.tabLayout)
                )
            }
        }

        onScrollBehavior = MyScrollHelper(this, input, true)
    }

    private fun setupBottomNavigation() {
        if (findViewById<View>(R.id.bottomNavigation) == null) {
            return
        }
        bottomNavigation.setOnNavigationItemSelectedListener {
            val fragment: Pair<Fragment, String> = when (it.itemId) {
                R.id.item1 -> FragmentWithViewPager() to FragmentWithViewPager.TAG
                R.id.item2 -> SecondFragment() to SecondFragment.TAG
                else -> throw IllegalArgumentException("invalid item id ${it.itemId}")
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment.first, fragment.second)
                .commit()
            true
        }
    }

}