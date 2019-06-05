package dev.olog.scrollhelper.example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_child.view.*

class SecondFragment : Fragment(){

    companion object {
        val TAG = SecondFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.list.adapter = Adapter(100)
        view.list.layoutManager = GridLayoutManager(view.context, 2)
    }

}