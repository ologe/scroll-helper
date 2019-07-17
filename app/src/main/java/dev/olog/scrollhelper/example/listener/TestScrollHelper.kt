package dev.olog.scrollhelper.example.listener

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import dev.olog.scrollhelper.Input
import dev.olog.scrollhelper.ScrollHelper

class TestScrollHelper(
    activity: FragmentActivity,
    input: Input
) : ScrollHelper(activity, input, true, false, false) {

    override fun skipFragment(fragment: Fragment): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun searchForRecyclerView(fragment: Fragment): RecyclerView? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun searchForViewPager(fragment: Fragment): ViewPager? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun searchForToolbar(fragment: Fragment): View? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun searchForTabLayout(fragment: Fragment): View? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun searchForFab(fragment: Fragment): View? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}