package mhashim.android.putback.ui.notionsFragment

import android.view.View
import mhashim.android.putback.R

/**
 * Created by mhashim6 on 31/08/2018.
 */
class IdleNotionsFragment : NotionsFragment() {
	override val isIdle: Boolean = true

	override val navigationIconRes = R.drawable.ic_archive_white_24dp

	override fun setUpToolbar() {
		super.setUpToolbar()
		toolbar.subtitle = "Idle Notions"
	}

	override fun setUpViews(view: View) {
		super.setUpViews(view)
	}


	override fun onNavigationItemClick(view: View) {
		when {
			view.id == R.id.notionsItem -> navigateUp()
		}
	}

}