package dev.olog.scrollhelper.example.pager

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.Hold
import dev.olog.scrollhelper.example.R
import kotlinx.android.synthetic.main.fragment_view_pager.*

class ViewPagerFragment : Fragment(R.layout.fragment_view_pager){

    companion object {
        val TAG = "dev.olog.pager"
    }

    private lateinit var mediator: TabLayoutMediator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        exitTransition = Hold()
        postponeEnterTransition()
        viewPager.doOnPreDraw {
            startPostponedEnterTransition()
        }

        val strategy = TabLayoutMediator.TabConfigurationStrategy { tab, position ->
            tab.text = "Item $position"
        }

        mediator = TabLayoutMediator(tabLayout, viewPager, strategy)
        mediator.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediator.detach()
    }

}

