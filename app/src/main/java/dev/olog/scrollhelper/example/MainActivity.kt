package dev.olog.scrollhelper.example

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.scrollhelper.MultiListenerBottomSheetBehavior
import dev.olog.scrollhelper.OnScrollSlidingBehavior
import dev.olog.scrollhelper.ViewHeights
import dev.olog.scrollhelper.example.listener.MyOnScrollSlidingBehavior
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var onScrollBehavior: OnScrollSlidingBehavior

    enum class Type {
        FULL,
        ONLY_SLIDING_PANEL,
        ONLY_BOTTOM_NAVIGATION,
        NONE
    }

    /**
     * Change me to test other behavior
     */
    private val type = Type.FULL

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
        val slidingPanelBehavior = BottomSheetBehavior.from(slidingPanel) as MultiListenerBottomSheetBehavior<*>?
        val slidingPanel = findViewById<View>(R.id.slidingPanel)
        val bottomNavigation = findViewById<View>(R.id.bottomNavigation)
        onScrollBehavior = MyOnScrollSlidingBehavior(
            slidingPanelBehavior, bottomNavigation,
            ViewHeights(
                if (slidingPanel != null) dimen(R.dimen.sliding_panel) else 0,
                if (bottomNavigation != null) dimen(R.dimen.bottomNavigation) else 0,
                dimen(R.dimen.toolbar),
                dimen(R.dimen.tabLayout)
            )
        )
    }

    private fun setupBottomNavigation() {
        if (slidingPanel.findViewById<View>(R.id.bottomNavigation) == null) {
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
