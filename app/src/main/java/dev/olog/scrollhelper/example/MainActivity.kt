package dev.olog.scrollhelper.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.scrollhelper.OnScrollSlidingBehavior
import dev.olog.scrollhelper.SuperCerealBottomSheetBehavior
import dev.olog.scrollhelper.ViewHeights
import dev.olog.scrollhelper.example.listener.MyOnScrollSlidingBehavior
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var onScrollBehavior: OnScrollSlidingBehavior

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val slidingPanelBehavior = BottomSheetBehavior.from(slidingPanel) as SuperCerealBottomSheetBehavior<*>
        onScrollBehavior = MyOnScrollSlidingBehavior(
            this, slidingPanelBehavior, bottomNavigation,
            ViewHeights(
                dimen(R.dimen.sliding_panel),
                dimen(R.dimen.bottomNavigation),
                dimen(R.dimen.toolbar),
                dimen(R.dimen.tabLayout)
            )
        )

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, PagerFragment(), PagerFragment.TAG)
                .commit()
        }

        bottomNavigation.setOnNavigationItemSelectedListener {
            val fragment: Pair<Fragment, String> = when (it.itemId) {
                R.id.item1 -> PagerFragment() to PagerFragment.TAG
                R.id.item2 -> ChildFragment() to "item2tag"
                else -> throw IllegalArgumentException("invalid item id ${it.itemId}")
            }
            // TODO
            true
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

}
