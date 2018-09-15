package mhashim6.android.putback.ui.notionsFragment


import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.PopupMenu
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import mhashim6.android.putback.APP_URL
import mhashim6.android.putback.R
import mhashim6.android.putback.data.PreferencesRepository
import mhashim6.android.putback.ui.BaseFragment
import mhashim6.android.putback.ui.SnackbarQueue
import mhashim6.android.putback.ui.enqueue
import mhashim6.android.putback.ui.launchUrl
import mhashim6.android.putback.ui.notionsDetailFragment.NotionDetailFragment
import mhashim6.android.putback.ui.notionsDetailFragment.NotionDetailFragment.Companion.NOTION_DETAIL_ACTION_CREATE
import mhashim6.android.putback.ui.notionsDetailFragment.NotionDetailFragment.Companion.NOTION_DETAIL_ACTION_DISPLAY
import mhashim6.android.putback.ui.notionsDetailFragment.NotionDetailFragment.Companion.NOTION_DETAIL_ACTION_TYPE
import mhashim6.android.putback.ui.notionsDetailFragment.NotionDetailFragment.Companion.NOTION_DETAIL_NOTION_ID
import mhashim6.lib.ratemonitor.RateConditionsMonitor


open class NotionsFragment : BaseFragment() {
    protected open val isIdle = false

    private var subscriptions: CompositeDisposable = CompositeDisposable()

    @LayoutRes
    override val layoutRes = R.layout.fragment_notions

    override val toolbarId: Int = R.id.toolbarId

    private lateinit var root: CoordinatorLayout
    private lateinit var fillerView: AppCompatImageView
    private lateinit var notionsRecycler: RecyclerView
    private lateinit var fab: FloatingActionButton

    private val idleStates: PublishSubject<Pair<NotionCompactViewModel, Boolean>> = PublishSubject.create()
    private val deletes: PublishSubject<Pair<NotionCompactViewModel, Boolean>> = PublishSubject.create()

    private val notionsAdapter by lazy {
        makeAdapter<NotionCompactView, NotionCompactViewModel>(R.layout.notion_compact, listOf()) {
            onBindViewHolder { notionView, notion ->
                notionView.render(notion)
            }
            onItemClickListener { _, model ->
                showNotionDetail(NOTION_DETAIL_ACTION_DISPLAY, model.notionId)
            }
            onItemLongClickListener(::showControlsMenu)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews(view)
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
        root = view.findViewById(R.id.root)

        fillerView = view.findViewById(R.id.emptyViewId)

        notionsRecycler = view.findViewById(R.id.notionsRecyclerId)
        initRecyclerView()

        fab = view.findViewById(R.id.fabId)
        if (isIdle)
            fab.hide()
        fab.setOnClickListener {
            showNotionDetail(NOTION_DETAIL_ACTION_CREATE, null)
        }
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

            @Suppress("UNCHECKED_CAST")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val notion = (viewHolder as BaseAdapter.DataClassViewHolder<NotionCompactViewModel>).item
                archive(notion)
            }
        })
        itemTouchHelper.attachToRecyclerView(notionsRecycler)
        notionsRecycler.adapter = notionsAdapter
    }

    private fun showData() {
        val viewModel = present(idleStates, deletes, resources, isIdle)
        with(viewModel) {
            subscriptions.addAll(
                    notionsChanges.subscribe(notionsAdapter::handleChanges),
                    emptyNotionsVisibility.subscribe(::updateEmptyFillerView),
                    archives,
                    deletes
            )
        }
        showTutorial()
        showRateDialog()
    }

    private fun showControlsMenu(view: View, notion: NotionCompactViewModel): Boolean {
        val menu = PopupMenu(activity!!, view)
        menu.inflate(if (isIdle) R.menu.notion_controls_idle else R.menu.notion_controls)
        menu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.deleteItem -> delete(notion)
                R.id.archiveItem -> archive(notion)
            }
            true
        }
        menu.show()
        return true
    }

    private fun archive(notion: NotionCompactViewModel) {
        idleStates.onNext(notion to isIdle.not())
        Snackbar.make(root, if (isIdle) R.string.un_archived_message else R.string.archived_message, LENGTH_SHORT)
                .setAction(R.string.undo) {
                    idleStates.onNext(notion to isIdle)
                }.enqueue()
    }

    private fun delete(notion: NotionCompactViewModel) {
        deletes.onNext(notion to false)
        Snackbar.make(root, R.string.notion_is_deleted, LENGTH_SHORT)
                .setAction(R.string.undo) {
                    deletes.onNext(notion to true)
                }.enqueue()
    }

    private fun updateEmptyFillerView(visibility: Int) {
        fillerView.visibility = visibility
    }

    private fun showNotionDetail(actionType: Int, notionId: String?) {
        NotionDetailFragment.create(
                bundleOf(NOTION_DETAIL_ACTION_TYPE to actionType,
                        NOTION_DETAIL_NOTION_ID to notionId))
                .show(fragmentManager, NotionDetailFragment::class.java.simpleName)
    }

    private fun showTutorial() {
        if (PreferencesRepository.tutorialShown)
            return

        root.postDelayed({
            Snackbar.make(root, R.string.welcome_message, LENGTH_LONG).enqueue()
            Snackbar.make(root, R.string.introduction_message, LENGTH_LONG).enqueue()
            Snackbar.make(root, R.string.pencil_message, LENGTH_LONG).enqueue()
            Snackbar.make(root, R.string.archive_tutorial, LENGTH_LONG).enqueue()
            Snackbar.make(root, R.string.long_press_tutorial, LENGTH_LONG).enqueue()
            Snackbar.make(root, R.string.preferences_tutorial, LENGTH_LONG).enqueue()
            Snackbar.make(root, R.string.goodbye_tutorial, LENGTH_LONG).enqueue()
            PreferencesRepository.tutorialShown = true
        }, 2000)
    }

    private fun showRateDialog() {
        if (RateConditionsMonitor.isConditionsMet)
            AlertDialog.Builder(activity!!).apply {
                setTitle(R.string.rate_app)
                setMessage(R.string.rate_message)
                setPositiveButton(R.string.rate_now) { _, _ ->
                    RateConditionsMonitor.rated()
                    launchUrl(APP_URL)
                }
                setNegativeButton(R.string.later) { _, _ -> RateConditionsMonitor.later() }
                        .setOnDismissListener { RateConditionsMonitor.later() }
                setCancelable(true)
            }.create().show()

    }

    override fun onNavigationItemClick(view: View) {
        when {
            view.id == R.id.archiveOption -> navigateTo(R.id.action_notionsFragment_to_idleNotionsFragment)
            view.id == R.id.settingsOption -> navigateTo(R.id.action_notionsFragment_to_preferencesFragment)
        }
    }
}