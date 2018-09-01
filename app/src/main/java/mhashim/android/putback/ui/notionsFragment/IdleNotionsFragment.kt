package mhashim.android.putback.ui

import android.view.View
import mhashim.android.putback.R

/**
 * Created by mhashim6 on 31/08/2018.
 */
class IdleNotionsFragment : NotionsFragment() {
	override val isIdle: Boolean = true

	override val navigationIconRes = R.drawable.ic_archive_white_24dp

	/*
	override val layoutRes = R.layout.fragment_idle_notions

	override lateinit var toolbar: Toolbar
	override lateinit var fillerView: AppCompatImageView
	override lateinit var notionsRecycler: RecyclerView
	override lateinit var fab: FloatingActionButton

	override fun setUpViews(view: View) {
		toolbar =  view.findViewById(R.id.idleToolbarId)
		fillerView = view.findViewById(R.id.idleEmptyViewId)
		notionsRecycler = view.findViewById(R.id.idleNotionsRecyclerId)
		fab = view.findViewById(R.id.idleFabId)
	}
*/

	override fun setUpToolbar() {
		super.setUpToolbar()
		toolbar.subtitle = "Idle Notions"
	}

	override fun onNavigationItemClick(view: View) {
		when {
			view.id == R.id.notionsItem -> navigateUp()
		}
	}

}