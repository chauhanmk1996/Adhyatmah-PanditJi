package com.app.panditji.custom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SimpleAdapter<T, V : RecyclerView.ViewHolder>(
    var list: ArrayList<T>,
    private val getLayoutId: (Int) -> Int,
    private var vh: (View) -> V,
    private var viewBinder: (V, T) -> Unit
) : RecyclerView.Adapter<V>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): V {

        return vh(LayoutInflater.from(parent.context).inflate(getLayoutId(viewType), parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: V, position: Int) {
        viewBinder(holder, list[position])
    }

    fun updateList(list: ArrayList<T>,pos: Int=-1) {
        this.list = list
       if (pos==-1) notifyDataSetChanged() else notifyItemChanged(pos)
    }



    fun addItem(item: T, pos: Int = -1, notify: Boolean = true) {
        if (pos > -1) list.add(pos, item) else list.add(item)
        if (notify) notifyItemInserted(if (pos > -1) pos else list.size - 1)
    }

    fun addItems(items: ArrayList<T>, notify: Boolean = true, animate: Boolean = true) {
        println("addItems() called with: items = [$items], notify = [$notify]")
        val start = itemCount
        this.list.addAll(items)
        if (notify)
            if (animate) notifyItemRangeInserted(start, items.size)
            else notifyDataSetChanged()
    }


    fun addItemsAt(items: ArrayList<T>, pos: Int, notify: Boolean = true, animate: Boolean = true) {
        this.list.addAll(pos, items)
        if (notify)
            if (animate) notifyItemRangeInserted(pos, items.size)
            else notifyDataSetChanged()
    }

    fun removeItem(pos: Int) {
        println("removeItem: item removed: ${list.removeAt(pos)}")
        notifyItemRemoved(pos)
    }

    fun removeItem(pos: T) {
        val position = list.indexOf(pos)

        println("removeItem: item removed: ${list.remove(pos)}")
        notifyItemRemoved(position)
    }


    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

}