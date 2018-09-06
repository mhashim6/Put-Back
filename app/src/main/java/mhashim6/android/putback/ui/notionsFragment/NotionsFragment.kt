package mhashim6.android.putback.ui.notionsFragment


import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_notions.*
import mhashim6.android.putback.R
import mhashim6.android.putback.ui.*


open class NotionsFragment : BaseFragment() {
	protected open val isIdle = false

	private var subscriptions: CompositeDisposable = CompositeDisposable()

	@LayoutRes
	override val layoutRes = R.layout.fragment_notions

	private lateinit var toolbar: Toolbar
	private lateinit var fillerView: AppCompatImageView
	private lateinit var notionsRecycler: RecyclerView
	private lateinit var fab: FloatingActionButton

	private val idleStates: PublishSubject<Pair<NotionCompactViewModel, Boolean>> = PublishSubject.create()

	private val notionsAdapter by lazy {
		makeAdapter<NotionCompactView, NotionCompactViewModel>(R.layout.notion_compact, listOf()) {
			onBindViewHolder { notionView, notion ->
				notionView.render(notion)
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setUpViews(view)

/* TODO
		val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select ringtone for notifications:")
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
		this.startActivityForResult(intent, 999)*/
	}

	override fun onResume() {
		super.onResume()
		showData()
	}

	override fun onPause() {
		subscriptions.clear()
		SnackbarQueue.clear()
		super.onPause()
	}

	private fun setUpViews(view: View) {
		toolbar = view.findViewById(R.id.toolbarId)
		setUpToolbar(toolbar)
		fillerView = view.findViewById(R.id.emptyViewId)

		notionsRecycler = view.findViewById(R.id.notionsRecyclerId)
		initRecyclerView()

		fab = view.findViewById(R.id.fabId)
		fab.visibility = isIdle.not().visibility
	}

	private fun initRecyclerView() {
		notionsRecycler.layoutManager = StaggeredGridLayoutManager(resources.getInteger(R.integer.span_count), StaggeredGridLayoutManager.VERTICAL)
		val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {

//			override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder) = .7f

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
				archive(notion, viewHolder.adapterPosition)
			}
		})
		itemTouchHelper.attachToRecyclerView(notionsRecycler)
		notionsRecycler.adapter = notionsAdapter
	}

	private fun showData() {
		val viewModel = present(idleStates, resources, isIdle)
		with(viewModel) {
			subscriptions.addAll(
					notionsChanges.subscribe(notionsAdapter::handleChanges),
					emptyNotionsVisibility.subscribe(::updateEmptyFillerView),
					archives
			)
		}
	}

	private fun archive(notion: NotionCompactViewModel, pos: Int) {
		idleStates.onNext(notion to isIdle.not())
		Snackbar.make(root, if (isIdle) getString(R.string.un_archived_message) else getString(R.string.archived_message), LENGTH_SHORT)
				.setAction(getString(R.string.undo)) {
					idleStates.onNext(notion to isIdle)
				}.enqueue()
	}

	private fun updateEmptyFillerView(visibility: Int) {
		fillerView.visibility = visibility
	}

	override fun onNavigationItemClick(view: View) {
		when {
			view.id == R.id.archiveItem -> navigateTo(R.id.action_notionsFragment_to_idleNotionsFragment)
			view.id == R.id.settingsItem -> navigateTo(R.id.action_notionsFragment_to_preferncesFragment)
		}
	}
}