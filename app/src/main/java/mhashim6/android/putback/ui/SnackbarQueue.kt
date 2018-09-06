package mhashim6.android.putback.ui

import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.util.*

/**
 * Created by mhashim6 on 29/08/2018.
 */
object SnackbarQueue {
	private val snackbars: Queue<Snackbar> = LinkedList<Snackbar>()
	private val onDismissedListener = object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
		override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
			announceDismissal()
		}
	}

	fun enqueue(snackbar: Snackbar) {
		snackbar.addCallback(onDismissedListener)

		if (snackbars.isEmpty()) //first snackbar
			snackbar.show()
		snackbars.offer(snackbar)
	}

	private fun announceDismissal() {
		snackbars.poll()
		snackbars.peek()?.show()
	}

	fun clear() {
		snackbars.forEach { it.removeCallback(onDismissedListener) }
		snackbars.clear()
	}
}

fun Snackbar.enqueue() {
	SnackbarQueue.enqueue(this)
}