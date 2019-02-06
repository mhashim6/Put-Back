package mhashim6.android.putback.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList

@Deprecated("no longer used", ReplaceWith("BaseAdapter"))
inline fun <reified V : View, reified T : Any> makeAdapter(
        @LayoutRes resId: Int, items: SortedList<T>, f: OrderedListAdapter<V, T>.() -> Unit): OrderedListAdapter<V, T> {
    return OrderedListAdapter<V, T>(resId, items).apply { f() }
}

class OrderedListAdapter<out V : View, T : Any>(@LayoutRes private val resId: Int, var items: SortedList<T>)
    : RecyclerView.Adapter<OrderedListAdapter.DataClassViewHolder<T>>() {
    private var _onBindViewHolder: (view: V, item: T) -> Unit = { _, _ -> }
    private var _onItemClickListener: (view: V, item: T) -> Unit = { _, _ -> }

    fun onBindViewHolder(f: (V, T) -> Unit) {
        _onBindViewHolder = f
    }

    fun onItemClickListener(f: (V, T) -> Unit) {
        _onItemClickListener = f
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataClassViewHolder<T> {
        val itemView = LayoutInflater.from(parent.context).inflate(resId, parent, false)
        val viewHolder = DataClassViewHolder<T>(itemView)
        itemView.setOnClickListener { _onItemClickListener(itemView as V, viewHolder.item) }
        return viewHolder
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: DataClassViewHolder<T>, position: Int) {
        val item = items[position]
        holder.item = item
        _onBindViewHolder(holder.itemView as V, item)
    }

    override fun getItemCount(): Int = items.size()

    fun replaceAll(all: Collection<T>) {
        items.replaceAll(all)
    }

    fun addAll(all: Collection<T>) {
        items.addAll(all)
    }

    fun addItem(item: T) {
        items.add(item)
    }

    fun removeItem(item: T) {
        items.remove(item)
    }

    class DataClassViewHolder<T : Any>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var item: T
    }
}