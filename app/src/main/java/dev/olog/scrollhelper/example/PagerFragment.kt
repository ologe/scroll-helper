package dev.olog.scrollhelper.example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.fragment_view_pager.view.*

class PagerFragment : Fragment(){

    companion object {
        val TAG = PagerFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.viewPager.adapter = ViewPagerAdapter(childFragmentManager)
        view.tabLayout.setupWithViewPager(view.viewPager)
    }

}

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm){

    private val childCount = listOf(50, 50, 2, 50)

    override fun getItem(position: Int): Fragment {
        val fragment = ChildFragment()
        val bundle = bundleOf("child_count" to childCount[position])
        fragment.arguments = bundle
        return fragment
    }

    override fun getCount(): Int = 4

    override fun getPageTitle(position: Int): CharSequence? {
        return "${childCount[position]} items"
    }
}