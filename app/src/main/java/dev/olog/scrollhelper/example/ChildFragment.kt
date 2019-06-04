package dev.olog.scrollhelper.example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_child.view.*
import kotlinx.android.synthetic.main.list_item.view.*

class ChildFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_child, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.list.adapter = Adapter(arguments!!.getInt("child_count"))
        view.list.layoutManager = LinearLayoutManager(view.context)
    }

}

class Holder(view: View): RecyclerView.ViewHolder(view)

class Adapter(private val childCount: Int) : RecyclerView.Adapter<Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false))
    }

    override fun getItemCount(): Int = childCount

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.itemView.text.text = position.toString()
    }
}