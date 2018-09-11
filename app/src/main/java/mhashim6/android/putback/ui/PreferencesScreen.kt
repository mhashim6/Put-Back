package mhashim6.android.putback.ui

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import mhashim6.android.putback.R
import mhashim6.android.putback.data.PreferencesRepository
import mhashim6.android.putback.data.PreferencesRepository.KEY_SOUND_PREFERENCE
import mhashim6.android.putback.data.PreferencesRepository.KEY_THEME_PREFERENCE
import mhashim6.android.putback.debug


class PreferencesScreen : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        preferenceManager.findPreference(KEY_THEME_PREFERENCE).onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
            activity?.recreate()
            true
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            KEY_SOUND_PREFERENCE -> {
                launchSoundSelector()
                return true
            }
        }

        return super.onPreferenceTreeClick(preference)
    }

    companion object {
        const val REQUEST_CODE_ALERT_RINGTONE = 0
    }

    private fun launchSoundSelector() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Notification Sound")
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_NOTIFICATION_URI)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, PreferencesRepository.soundUri)
        startActivityForResult(intent, REQUEST_CODE_ALERT_RINGTONE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        debug(resultCode)
        when (requestCode) {
            REQUEST_CODE_ALERT_RINGTONE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.let {
                        debug("why")
                        PreferencesRepository.soundUri = it.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
