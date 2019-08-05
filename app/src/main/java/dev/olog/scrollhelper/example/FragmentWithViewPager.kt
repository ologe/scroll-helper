package dev.olog.scrollhelper.example

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.fragment_view_pager.view.*

class FragmentWithViewPager : Fragment(){

    companion object {
        val TAG = FragmentWithViewPager::class.java.canonicalName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.viewPager.adapter = ViewPagerAdapter(childFragmentManager)
        view.tabLayout.setupWithViewPager(view.viewPager)

        view.menu.setOnClickListener {
            // utility for changing input type at runtime
            val popup = PopupMenu(view.context, view.menu, Gravity.BOTTOM)
            popup.inflate(R.menu.menu)
            popup.setOnMenuItemClickListener {
                type = when (it.itemId){
                    R.id.full -> MainActivity.Type.FULL
                    R.id.only_navigation -> MainActivity.Type.ONLY_BOTTOM_NAVIGATION
                    R.id.only_sliding_panel -> MainActivity.Type.ONLY_SLIDING_PANEL
                    R.id.none -> MainActivity.Type.NONE
                    else -> throw IllegalStateException()
                }
                requireActivity().recreate()
                true
            }
            popup.show()
        }
    }

}

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm){

    private val childCount = listOf(6, 50, 2, 50)

    override fun getItem(position: Int): Fragment {
        val fragment = ViewpagerChildFragment()
        val bundle = bundleOf("child_count" to childCount[position])
        fragment.arguments = bundle
        return fragment
    }

    override fun getCount(): Int = 4

    override fun getPageTitle(position: Int): CharSequence? {
        return "${childCount[position]} items"
    }
}