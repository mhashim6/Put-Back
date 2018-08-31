package mhashim.android.putback.ui


import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_notions.*
import mhashim.android.putback.R
import mhashim.android.putback.data.Notion
import mhashim.android.putback.data.NotionsRealm.changeIdleStatus
import mhashim.android.putback.data.NotionsRealm.closeRealm
import mhashim.android.putback.data.NotionsRealm.findAllWithIdleStatus
import mhashim.android.putback.debug
import mhashim.android.putback.ui.NotionsPresenter.NotionCompactViewModel
import mhashim.android.putback.ui.NotionsPresenter.present
import java.util.concurrent.TimeUnit

open class NotionsFragment : BaseFragment() {
	protected open val isIdle = false

	override val layoutRes = R.layout.fragment_notions

	override lateinit var toolbar: Toolbar
	private lateinit var fillerView: AppCompatImageView
	private lateinit var notionsRecycler: RecyclerView
	private lateinit var fab: FloatingActionButton

	private val snacks = SnackbarQueue()

	private val archives: PublishSubject<NotionCompactViewModel> = PublishSubject.create()

	private val notionsAdapter by lazy {
		makeAdapter<NotionCompactView, NotionCompactViewModel>(R.layout.notion_compact, notionsSortedList()) {
			onBindViewHolder { notionView, notion ->
				notionView.apply {
					content.text = notion.content
					setCardBackgroundColor(notion.color)
				}
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		initRecyclerView()
		showData()
	}

	override fun setUpViews(view: View) {
		toolbar = view.findViewById(R.id.toolbarId)
		fillerView = view.findViewById(R.id.emptyViewId)
		notionsRecycler = view.findViewById(R.id.notionsRecyclerId)
		fab = view.findViewById(R.id.fabId)
	}

	private fun initRecyclerView() {
		notionsRecycler.layoutManager = StaggeredGridLayoutManager(resources.getInteger(R.integer.span_count), StaggeredGridLayoutManager.VERTICAL)
		val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {

			override fun isLongPressDragEnabled() = false

			override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
				val dragFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
				val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
				return makeMovementFlags(dragFlags, swipeFlags)
			}

			override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
				return false
			}

			override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
				val notion = (viewHolder as BaseAdapter.DataClassViewHolder<NotionCompactViewModel>).item
				attemptToArchive(notion, viewHolder.adapterPosition)
			}
		})
		itemTouchHelper.attachToRecyclerView(notionsRecycler)
		notionsRecycler.adapter = notionsAdapter
	}

	private fun attemptToArchive(notion: NotionsPresenter.NotionCompactViewModel, pos: Int) {
		notionsAdapter.removeItem(notion)
		snacks.enqueue(Snackbar
				.make(root, if (isIdle) getString(R.string.un_archived_message) else getString(R.string.archived_message), LENGTH_SHORT)
				.setAction(getString(R.string.undo)) {
					notionsAdapter.addItem(notion)

				}.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
					override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
						if (event == DISMISS_EVENT_TIMEOUT)
							archives.onNext(notion)
					}
				})
		)
	}

	private fun showData() {
		val viewModel = present(archives.buffer(10, TimeUnit.SECONDS), resources, isIdle)
		with(viewModel) {
			subscriptions.addAll(
					notions.subscribe(::updateRecyclerView),
					successfulArchives
			)
		}
	}

	private fun updateRecyclerView(notions: List<NotionCompactViewModel>) {
		debug("items updated, size: ${notions.size}")
		fillerView.visibility = if (notions.isEmpty()) VISIBLE else GONE
		notionsAdapter.replaceAll(notions)
		notionsAdapter.notifyDataSetChanged()
	}

	private fun notionsSortedList(): SortedList<NotionsPresenter.NotionCompactViewModel> {
		return SortedList<NotionCompactViewModel>(NotionCompactViewModel::class.java, (object : SortedList.Callback<NotionCompactViewModel>() {
			override fun areItemsTheSame(item1: NotionCompactViewModel, item2: NotionCompactViewModel): Boolean {
				return item1.model.id == item2.model.id
			}

			override fun onMoved(fromPosition: Int, toPosition: Int) {
				notionsAdapter.notifyItemMoved(fromPosition, toPosition)
			}

			override fun onChanged(position: Int, count: Int) {
				notionsAdapter.notifyItemChanged(position)
			}

			override fun onInserted(position: Int, count: Int) {
				notionsAdapter.notifyItemInserted(position)
			}

			override fun onRemoved(position: Int, count: Int) {
				notionsAdapter.notifyItemRemoved(position)
			}

			override fun compare(o1: NotionCompactViewModel, o2: NotionCompactViewModel): Int {
				return o1.model.createdAt.compareTo(o2.model.createdAt)
			}

			override fun areContentsTheSame(oldItem: NotionCompactViewModel, newItem: NotionCompactViewModel): Boolean {
				return oldItem == newItem
			}
		}))
	}

	override fun onNavigationItemClick(view: View) {
		when {
			view.id == R.id.archiveItem -> navigateTo(R.id.action_notionsFragment_to_idleNotionsFragment)
		}
	}
}

private object NotionsPresenter {

	class ViewModel(val notions: Flowable<List<NotionCompactViewModel>>, val successfulArchives: Disposable)

	class NotionCompactViewModel(
			val model: Notion,
			resources: Resources,
			val content: String = model.content,
			val interval: Int = model.interval, //TODO
			val createdAt: Long = model.createdAt,
			val modifiedAt: Long = createdAt,
			val lastRunAt: Long = createdAt,
			val isArchived: Boolean = false,
			val color: Int = colorSelector(model, resources)
	)

	fun present(archiveAttempts: Observable<MutableList<NotionCompactViewModel>>, resources: Resources, isIdle: Boolean): ViewModel {
		val realm = Realm.getDefaultInstance()

		val notions = findAllWithIdleStatus(isIdle)
				.map { realm.copyFromRealm(it).map { notion -> NotionCompactViewModel(notion, resources) } }
//				.map { emptyList<NotionCompactViewModel>() } //for debugging empty results.

//		successful archives
		val successfulArchives = archiveAttempts
				.map {
					val models = it.map { it.model }
					models.map {
						it.apply { isArchived = isIdle.not() }
					}
				}
				.filter { it.isNotEmpty() }
				.subscribeOn(Schedulers.computation())
				.observeOn(AndroidSchedulers.mainThread())
				.doFinally { closeRealm(realm) }
				.subscribe { list ->
					debug("10 seconds have passed!")
					changeIdleStatus(list, isIdle.not())
				}

		return ViewModel(notions, successfulArchives)
	}

}