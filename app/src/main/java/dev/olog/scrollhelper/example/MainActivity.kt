package dev.olog.scrollhelper.example

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.scrollhelper.InitialHeight
import dev.olog.scrollhelper.Input
import dev.olog.scrollhelper.MultiListenerBottomSheetBehavior
import dev.olog.scrollhelper.OnScrollSlidingBehavior
import dev.olog.scrollhelper.example.listener.MyOnScrollSlidingBehavior
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Change me to test other behavior
 */
var type = MainActivity.Type.FULL

class MainActivity : AppCompatActivity() {

    private lateinit var onScrollBehavior: OnScrollSlidingBehavior

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
                    .add(R.id.fragmentContainer, PagerFragment(), PagerFragment.TAG)
                    .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        onScrollBehavior.onAttach(this)
    }

    override fun onPause() {
        super.onPause()
        onScrollBehavior.onDetach(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        onScrollBehavior.dispose()
    }

    private fun setupScrollBehavior() {
        val slidingPanel = findViewById<View>(R.id.slidingPanel)

        val slidingPanelBehavior = if (slidingPanel != null) BottomSheetBehavior.from(slidingPanel) as MultiListenerBottomSheetBehavior<*>?
        else null
        val bottomNavigation = findViewById<View>(R.id.bottomNavigation)

        val input : Input = when (type){
            Type.FULL -> {
                Input.Full(
                    slidingPanelBehavior!! to InitialHeight(dimen(R.dimen.sliding_panel)),
                    bottomNavigation to InitialHeight(dimen(R.dimen.bottomNavigation)),
                    toolbarHeight = InitialHeight(dimen(R.dimen.toolbar)),
                    tabLayoutHeight = InitialHeight(dimen(R.dimen.tabLayout))
                )
            }
            Type.ONLY_SLIDING_PANEL -> {
                Input.OnlySlidingPanel(
                    slidingPanelBehavior!! to InitialHeight(dimen(R.dimen.sliding_panel)),
                    toolbarHeight = InitialHeight(dimen(R.dimen.toolbar)),
                    tabLayoutHeight = InitialHeight(dimen(R.dimen.tabLayout)),
                    scrollableSlidingPanel = true
                )
            }
            Type.ONLY_BOTTOM_NAVIGATION -> {
                Input.OnlyBottomNavigation(
                    bottomNavigation to InitialHeight(dimen(R.dimen.bottomNavigation)),
                    toolbarHeight = InitialHeight(dimen(R.dimen.toolbar)),
                    tabLayoutHeight = InitialHeight(dimen(R.dimen.tabLayout))
                )
            }
            Type.NONE -> {
                Input.None(
                    toolbarHeight = InitialHeight(dimen(R.dimen.toolbar)),
                    tabLayoutHeight = InitialHeight(dimen(R.dimen.tabLayout))
                )
            }
        }

        onScrollBehavior = MyOnScrollSlidingBehavior(input)
    }

    private fun setupBottomNavigation() {
        if (findViewById<View>(R.id.bottomNavigation) == null) {
            return
        }
        bottomNavigation.setOnNavigationItemSelectedListener {
            val fragment: Pair<Fragment, String> = when (it.itemId) {
                R.id.item1 -> PagerFragment() to PagerFragment.TAG
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
