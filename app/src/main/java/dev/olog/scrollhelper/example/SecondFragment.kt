package dev.olog.scrollhelper.example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.olog.scrollhelper.layoutmanagers.OverScrollGridLayoutManager
import kotlinx.android.synthetic.main.fragment_second.*

class SecondFragment : Fragment(){

    companion object {
        val TAG = SecondFragment::class.java.canonicalName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.adapter = Adapter(100)
        list.layoutManager = OverScrollGridLayoutManager(list, 2)
    }

}