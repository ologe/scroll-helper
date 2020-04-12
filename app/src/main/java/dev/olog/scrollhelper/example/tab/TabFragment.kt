package dev.olog.scrollhelper.example.tab

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialContainerTransform
import dev.olog.scrollhelper.example.R
import dev.olog.scrollhelper.example.detail.DetailFragment
import dev.olog.scrollhelper.example.model.Model
import dev.olog.scrollhelper.example.model.times
import kotlinx.android.synthetic.main.fragment_tab.*

class TabFragment : Fragment(R.layout.fragment_tab) {

    companion object {

        private const val POSITION = "dev.olog.position"

        fun newInstance(position: Int): TabFragment {
            return TabFragment().apply {
                arguments = bundleOf(POSITION to position)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val position = requireArguments().getInt(POSITION) + 2

        exitTransition = Hold()

        val adapter = TabFragmentAdapter(
            position,
            this::onClick
        )
        list.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            adapter.submitList(Model.IMAGES * position)
        }

    }

    private fun onClick(view: View, model: Model) {
        val fragment = DetailFragment.newInstance(model.image, view.transitionName)
        fragment.sharedElementEnterTransition = MaterialContainerTransform(requireContext()).apply {
            drawingViewId = R.id.fragmentContainer
            containerColor = Color.WHITE
        }

        requireActivity().supportFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragmentContainer, fragment, DetailFragment.TAG)
            .addToBackStack(DetailFragment.TAG)
            .addSharedElement(view, view.transitionName)
            .commit()
    }

}