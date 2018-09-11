package mhashim6.android.putback.ui


import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import mhashim6.android.putback.R

class SettingsFragment : BaseFragment() {


    @LayoutRes
    override val layoutRes = R.layout.fragment_settings

    private lateinit var toolbar: Toolbar

    override val navigationIconRes = R.drawable.ic_settings_white_24dp

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.toolbarId)
        setUpToolbar(toolbar)

        childFragmentManager.beginTransaction().replace(R.id.preferences_screen, PreferencesScreen()).commit()
    }

    override fun onNavigationItemClick(view: View) {
        navigateUp()
    }
}
