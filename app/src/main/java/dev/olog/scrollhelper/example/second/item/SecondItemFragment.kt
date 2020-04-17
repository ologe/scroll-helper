package dev.olog.scrollhelper.example.second.item

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dev.olog.scrollhelper.example.R
import dev.olog.scrollhelper.example.detail.DetailFragment
import dev.olog.scrollhelper.example.model.Model
import dev.olog.scrollhelper.example.model.times
import dev.olog.scrollhelper.example.tab.TabFragmentAdapter
import kotlinx.android.synthetic.main.fragment_second_item.*

class SecondItemFragment : Fragment(R.layout.fragment_second_item) {

    companion object {

        const val TAG = "dev.olog.second.fragment"

        private const val POSITION = "dev.olog.position"

        fun newInstance(): SecondItemFragment {
            return SecondItemFragment().apply {
                arguments = bundleOf(POSITION to 5)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val position = requireArguments().getInt(POSITION) + 2

        val adapter = TabFragmentAdapter(this::onClick)
        list.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            adapter.submitList(Model.IMAGES * position)
        }

    }

    private fun onClick(model: Model) {
        val fragment = DetailFragment.newInstance(model.image)

        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment, DetailFragment.TAG)
            .addToBackStack(DetailFragment.TAG)
            .commit()
    }

}