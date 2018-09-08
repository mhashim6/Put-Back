package mhashim6.android.putback.ui

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
import mhashim6.android.putback.R


/**
 * Created by mhashim6 on 29/08/2018.
 */
abstract class BaseFragment : Fragment(), ToolbarOwner {

    abstract val layoutRes: Int

    override val toolbarTitleColor = Color.WHITE

    override val toolbarSubtitleColor: Int = Color.WHITE

    @MenuRes
    override var menuRes = 0

    @DrawableRes
    override val navigationIconRes = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutRes, container, false)
    }

    override fun setUpToolbar(toolbar: Toolbar) {
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

        val destinationClickListener = { view: View ->
            dialog.cancel()
            onNavigationItemClick(view)
        }

        val notionsDestination = bottomSheetView.findViewById<AppCompatTextView>(R.id.notionsOption)
        notionsDestination.setOnClickListener(destinationClickListener)

        val idleNotionsDestination = bottomSheetView.findViewById<AppCompatTextView>(R.id.archiveOption)
        idleNotionsDestination.setOnClickListener(destinationClickListener)

        val preferencesDestination = bottomSheetView.findViewById<AppCompatTextView>(R.id.settingsOption)
        preferencesDestination.setOnClickListener(destinationClickListener)

        dialog.setContentView(bottomSheetView)
        dialog.show()
    }

    protected open fun onNavigationItemClick(view: View) {}

    protected fun navigateTo(@IdRes destination: Int) {
        findNavController(this).navigate(destination)
    }

    protected fun navigateUp() {
        findNavController(this).navigateUp()
    }
}

interface ToolbarOwner {
    val toolbarTitleColor: Int

    val toolbarSubtitleColor: Int

    val menuRes: Int

    val navigationIconRes: Int

    fun setUpToolbar(toolbar: Toolbar)

    fun onMenuItemClickedListener(menuItem: MenuItem): Boolean = true

    fun onNavigationIconClickListener(view: View) {}
}