package mhashim6.android.putback.ui.notionsFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * Create a @BaseAdapter
 *
 *  ```
 * recyclerView.adapter = makeAdapter<ItemView, DataClass>(R.layout.item_view, items) {
 *     onBindViewHolder { view, appSites ->
 *         ...
 *     }
 *     onItemClickListener { view, appSites ->
 *         ...
 *     }
 * }
 * ```
 *
 * To avoid use View.findViewById() in all `onBindViewHolder` call, it is better to use a custom view class ItemView in `R.layout.item_view` :
 * ```
 *  <com.tgirard12.krecyclerdsl.ItemView >
 *      ...
 *  </com.tgirard12.krecyclerdsl.ItemView >
 * ```h
 */
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

	override fun getItemCount(): Int = items.size

	fun replaceAll(all: List<T>) {
		items = all
	}

	fun addAll(all: Collection<T>) {
//		items.addAll(all)
	}

	fun addItem(item: T) {
//		items.add(item)
	}

	fun removeItem(item: T) {
//		items.remove(item)
	}

	class DataClassViewHolder<T : Any>(itemView: View) : RecyclerView.ViewHolder(itemView) {
		lateinit var item: T
	}
}