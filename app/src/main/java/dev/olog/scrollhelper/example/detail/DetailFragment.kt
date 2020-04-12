package dev.olog.scrollhelper.example.detail

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dev.olog.scrollhelper.example.model.Model
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : Fragment(){

    companion object {

        const val TAG = "dev.olog.detail"

        private const val IMAGE = "image"
        private const val TRANSITION = "transition"

        fun newInstance(image: String, transition: String): DetailFragment {
            return DetailFragment().apply {
                arguments = bundleOf(
                    IMAGE to image,
                    TRANSITION to transition
                )
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val image = requireArguments().getString(IMAGE)!!
        view.transitionName = requireArguments().getString(TRANSITION)!!

        postponeEnterTransition()

        view.doOnPreDraw {
            startPostponedEnterTransition()
        }

        val adapter = DetailFragmentAdapter()
        list.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            val model = Model(image)
            adapter.submitList(listOf(model))
        }
    }

}