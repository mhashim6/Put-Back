package mhashim6.android.putback.ui.preferences


import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import mhashim6.android.putback.R
import mhashim6.android.putback.ui.BaseFragment

class SettingsFragment : BaseFragment() {

    @LayoutRes
    override val layoutRes = R.layout.fragment_settings
    override var menuRes = R.menu.settings

    override val toolbarId: Int = R.id.toolbarId

    override val navigationIconRes = R.drawable.ic_check_white_24dp

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction().replace(R.id.preferences_screen, PreferencesScreen()).commit()
    }

    override fun onNavigationIconClickListener(view: View) {
        navigateUp()
    }
}
