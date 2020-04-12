package dev.olog.scrollhelper.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.olog.scrollhelper.ScrollHelper
import dev.olog.scrollhelper.example.listener.SuperCerealScrollHelper
import dev.olog.scrollhelper.example.pager.ViewPagerFragment

class MainActivity : AppCompatActivity() {

    private lateinit var scrollHelper: ScrollHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scrollHelper = SuperCerealScrollHelper(this, false, false)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ViewPagerFragment(), ViewPagerFragment.TAG)
                .commit()
        }
    }

}