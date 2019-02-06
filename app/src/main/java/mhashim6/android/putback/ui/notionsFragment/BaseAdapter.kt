package mhashim6.android.putback.ui.notionsFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView


inline fun <reified V : View, reified T : Any> makeAdapter(
        @LayoutRes resId: Int, items: List<T>, f: BaseAdapter<V, T>.() -> Unit): BaseAdapter<V, T> {
    return BaseAdapter<V, T>(resId, items).apply { f() }
}

/**
 * RecyclerView.Adapter for data class, use fun [makeAdapter] to create it
 */
class BaseAdapter<out V : View, T : Any>(@LayoutRes private val resId: Int, private var items: List<T>)
    : RecyclerView.Adapter<BaseAdapter.DataClassViewHolder<T>>() {
    private var _onBindViewHolder: (view: V, item: T) -> Unit = { _, _ -> }
    private var _onItemClickListener: (view: V, item: T) -> Unit = { _, _ -> }
    private var _onItemLongClickListener: (view: V, item: T) -> Boolean = { _, _ -> false }

    fun onBindViewHolder(f: (V, T) -> Unit) {
        _onBindViewHolder = f
    }

    fun onItemClickListener(f: (V, T) -> Unit) {
        _onItemClickListener = f
    }

    fun onItemLongClickListener(f: (V, T) -> Boolean) {
        _onItemLongClickListener = f
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataClassViewHolder<T> {
        val itemView = LayoutInflater.from(parent.context).inflate(resId, parent, false)
        val viewHolder = DataClassViewHolder<T>(itemView)
        itemView.setOnClickListener { _onItemClickListener(itemView as V, viewHolder.item) }
        itemView.setOnLongClickListener { _onItemLongClickListener(itemView as V, viewHolder.item) }
        return viewHolder
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: DataClassViewHolder<T>, position: Int) {
        val item = items[position]
        holder.item = item
        _onBindViewHolder(holder.itemView as V, item)
    }

    override fun getItemCount(): Int = items.size

    fun replaceAll(all: List<T>) {
        items = all
    }

    class DataClassViewHolder<T : Any>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var item: T
    }
}