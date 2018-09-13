package mhashim6.android.putback.ui.preferences

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.franmontiel.attributionpresenter.AttributionPresenter
import com.franmontiel.attributionpresenter.entities.Attribution
import com.franmontiel.attributionpresenter.entities.Library.*
import com.franmontiel.attributionpresenter.entities.License
import mhashim6.android.putback.APP_URL
import mhashim6.android.putback.GITHUB_URL
import mhashim6.android.putback.R
import mhashim6.android.putback.data.PreferencesRepository
import mhashim6.android.putback.data.PreferencesRepository.KEY_DEVELOPER_PREFERENCE
import mhashim6.android.putback.data.PreferencesRepository.KEY_FEEDBACK_PREFERENCE
import mhashim6.android.putback.data.PreferencesRepository.KEY_OPEN_SOURCE_PREFERENCE
import mhashim6.android.putback.data.PreferencesRepository.KEY_SOUND_PREFERENCE
import mhashim6.android.putback.data.PreferencesRepository.KEY_THEME_PREFERENCE
import mhashim6.android.putback.debug
import mhashim6.android.putback.ui.launchUrl


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
            KEY_SOUND_PREFERENCE -> launchSoundSelector()

/*            KEY_DONATE_PREFERENCE -> {
                launchDonationsDialog()
                return true
            }
            */

            KEY_OPEN_SOURCE_PREFERENCE -> launchCreditsDialog()

            KEY_DEVELOPER_PREFERENCE -> launchUrl(GITHUB_URL)

            KEY_FEEDBACK_PREFERENCE -> launchUrl(APP_URL)
        }

        return true
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

    /*
    private fun launchDonationsDialog() {
        if (DonationsRepository.billingReady && DonationsRepository.productsReady) {

        }
    }
*/
    private fun launchCreditsDialog() {
        AttributionPresenter.Builder(context)
                .addAttributions(REALM, RX_JAVA, RX_ANDROID)
                .addAttributions(
                        Attribution.Builder("Androidx Appcompat")
                                .addLicense(License.APACHE)
                                .addCopyrightNotice("Google")
                                .build(),
                        Attribution.Builder("Androidx Architecture Components")
                                .addCopyrightNotice("Google")
                                .addLicense(License.APACHE)
                                .build(),
                        Attribution.Builder("Android KTX")
                                .addCopyrightNotice("Google")
                                .addLicense(License.APACHE)
                                .setWebsite("https://github.com/android/android-ktx")
                                .build(),
                        Attribution.Builder("RateConditionsMonitor")
                                .addLicense(License.MIT)
                                .addCopyrightNotice("Copyright 2018 Muhammad Hashim(mhashim6)")
                                .setWebsite("https://github.com/mhashim6/RateConditionsMonitor")
                                .build(),
                        Attribution.Builder("Spider Icon Vector")
                                .addCopyrightNotice("Freepik")
                                .setWebsite("https://www.flaticon.com/free-icon/spider_93292")
                                .build(),
                        Attribution.Builder("AttributionPresenter")
                                .addCopyrightNotice("Copyright 2017 Francisco José Montiel Navarro")
                                .addLicense(License.APACHE)
                                .setWebsite("https://github.com/franmontiel/AttributionPresenter")
                                .build())
                .build()
                .showDialog(null)
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