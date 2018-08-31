package mhashim.android.putback.ui

import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import mhashim.android.putback.debug
import java.util.*

/**
 * Created by mhashim6 on 29/08/2018.
 */
class SnackbarQueue {
	private val snackbars: Queue<Snackbar> = LinkedList<Snackbar>()

	fun enqueue(snackbar: Snackbar) {
		snackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
			override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
				announceDismissal()
			}
		})

		if (snackbars.isEmpty()) //first snackbar
			snackbar.show()
		snackbars.offer(snackbar)
	}

	private fun announceDismissal() {
		debug("announcingDismissal")
		snackbars.poll()
		snackbars.peek()?.show()
	}

}