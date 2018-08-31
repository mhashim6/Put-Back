package mhashim.android.putback.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.disposables.CompositeDisposable
import mhashim.android.putback.R


/**
 * Created by mhashim6 on 29/08/2018.
 */
abstract class BaseFragment : Fragment(), ToolbarOwner {

	protected lateinit var subscriptions: CompositeDisposable

	abstract val layoutRes: Int

	override val toolbarTitleColor = Color.WHITE

	override val toolbarSubtitleColor: Int = Color.WHITE

	@MenuRes
	override var menuRes = 0

	@DrawableRes
	override val navigationIconRes = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		retainInstance = true
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(layoutRes, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setUpViews(view)
		setUpToolbar()
	}

	protected open fun setUpViews(view: View) {}

	protected open fun setUpToolbar() {
		if (menuRes != 0)
			toolbar.inflateMenu(menuRes)
		if (navigationIconRes != 0)
			toolbar.setNavigationIcon(navigationIconRes)

		toolbar.setTitleTextColor(toolbarTitleColor)
		toolbar.setSubtitleTextColor(toolbarSubtitleColor)
		toolbar.setOnMenuItemClickListener(::onMenuItemClickedListener)
		toolbar.setNavigationOnClickListener(::onNavigationIconClickListener)
	}

	override fun onNavigationIconClickListener(view: View) = showBottomSheetDialog()

	private fun showBottomSheetDialog() {
		val dialog = BottomSheetDialog(activity!!)

		val bottomSheetView = layoutInflater.inflate(R.layout.navigation_bottom_sheet, null)

		val notionsDestination = bottomSheetView.findViewById<AppCompatTextView>(R.id.notionsItem)
		notionsDestination.setOnClickListener {
			dialog.cancel()
			onNavigationItemClick(it)
		}

		val idleNotionsDestination = bottomSheetView.findViewById<AppCompatTextView>(R.id.archiveItem)
		idleNotionsDestination.setOnClickListener {
			dialog.cancel()
			onNavigationItemClick(it)
		}

		dialog.setContentView(bottomSheetView)
		dialog.show()
	}

	protected open fun onNavigationItemClick(view: View) {}

	protected fun navigateTo(@IdRes destination: Int) {
		findNavController(this).navigate(destination)
	}


	override fun onAttach(context: Context?) {
		super.onAttach(context)
		subscriptions = CompositeDisposable()
	}

	override fun onDetach() {
		super.onDetach()
		subscriptions.clear()
	}
}

interface ToolbarOwner {
	val toolbar: Toolbar

	val toolbarTitleColor: Int

	val toolbarSubtitleColor: Int

	val menuRes: Int

	val navigationIconRes: Int

	fun onMenuItemClickedListener(menuItem: MenuItem): Boolean = true

	fun onNavigationIconClickListener(view: View) {}
}