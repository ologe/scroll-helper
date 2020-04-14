package dev.olog.scrollhelper.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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


        bottomNavigation.setOnNavigationItemReselectedListener {
            // TODO has to click 2 times to change item, why??
            when (it.itemId) {
                R.id.item1 -> toFirstItem()
                R.id.item2 -> toSecondItem()
            }
        }
    }


    private fun toFirstItem() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ViewPagerFragment(), ViewPagerFragment.TAG)
            .commit()
    }

    private fun toSecondItem() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, SecondItemFragment.newInstance(), SecondItemFragment.TAG)
            .commit()
    }

}