package dev.olog.scrollhelper.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.transition.MaterialFadeThrough
import dev.olog.scrollhelper.ScrollHelper
import dev.olog.scrollhelper.example.listener.SuperCerealScrollHelper
import dev.olog.scrollhelper.example.pager.ViewPagerFragment
import dev.olog.scrollhelper.example.second.item.SecondItemFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var scrollHelper: ScrollHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scrollHelper = SuperCerealScrollHelper(this, false, false)

        if (savedInstanceState == null) {
            toFirstItem()
        }


        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item1 -> toFirstItem()
                R.id.item2 -> toSecondItem()
            }
            true
        }
    }


    private fun toFirstItem() {
        val current = supportFragmentManager.findFragmentByTag(ViewPagerFragment.TAG)
            ?: supportFragmentManager.findFragmentByTag(SecondItemFragment.TAG)
        current?.exitTransition = MaterialFadeThrough.create()

        val newFragment = ViewPagerFragment().apply {
            enterTransition = MaterialFadeThrough.create()
        }

        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragmentContainer, newFragment, ViewPagerFragment.TAG)
            .commit()
    }

    private fun toSecondItem() {
        val current = supportFragmentManager.findFragmentByTag(ViewPagerFragment.TAG)
            ?: supportFragmentManager.findFragmentByTag(SecondItemFragment.TAG)
        current?.exitTransition = MaterialFadeThrough.create()

        val newFragment = SecondItemFragment.newInstance().apply {
            enterTransition = MaterialFadeThrough.create()
        }

        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(
                R.id.fragmentContainer,
                newFragment,
                SecondItemFragment.TAG
            )
            .commit()
    }

}