package mhashim6.android.putback.data

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.core.content.edit
import mhashim6.android.putback.R
import org.jetbrains.anko.defaultSharedPreferences

object PreferencesRepository {

    private lateinit var preferences: SharedPreferences


    var firstLaunch: Boolean
        get() = preferences.getBoolean(KEY_FIRST_LAUNCH_PREFERENCE, true)
        set(value) {
            preferences.edit {
                putBoolean(KEY_FIRST_LAUNCH_PREFERENCE, value)
            }
        }

    var tutorialShown: Boolean
        get() = preferences.getBoolean(KEY_TUTORIAL_SHOWN_PREFERENCE, false)
        set(value) {
            preferences.edit {
                putBoolean(KEY_TUTORIAL_SHOWN_PREFERENCE, value)
            }
        }

    var soundUri: Uri?
        get() {
            val current = sound
            return if (current == SILENT_SOUND_PREFERENCE) null else Uri.parse(current)
        }
        set(value) {
            sound = value?.toString() ?: SILENT_SOUND_PREFERENCE
        }

    private var sound: String
        get() = preferences.getString(KEY_SOUND_PREFERENCE, Settings.System.DEFAULT_NOTIFICATION_URI.toString())!!
        set(value) = preferences.edit {
            putString(KEY_SOUND_PREFERENCE, value)
        }

    private val themes = mapOf(
            "red_dust" to R.drawable.window_background_red_dust,
            "vivid" to R.drawable.window_background_vivid,
            "copper" to R.drawable.window_background_copper,
            "predawn" to R.drawable.window_background_predawn,
            "dusk" to R.drawable.window_background_dusk,
            "bronze_atmosphere" to R.drawable.window_background_bronze_atmosphere,
            "pink_horizon" to R.drawable.window_background_pink_horizon)

    val theme: Int
        @DrawableRes get() = themes.getValue(preferences.getString(KEY_THEME_PREFERENCE, "red_dust")
                ?: "red_dust")

    fun init(context: Context) {
        preferences = context.defaultSharedPreferences
    }

    private const val KEY_FIRST_LAUNCH_PREFERENCE = "first_launch_preference"
    private const val KEY_TUTORIAL_SHOWN_PREFERENCE = "tutorial_shown_preference"
    const val KEY_SOUND_PREFERENCE = "sound_preference"
    private const val SILENT_SOUND_PREFERENCE = "SILENT"
    const val KEY_THEME_PREFERENCE = "theme_preference"
    const val KEY_DONATE_PREFERENCE = "donate_preference"
    const val KEY_OPEN_SOURCE_PREFERENCE = "open_source_preference"
    const val KEY_DEVELOPER_PREFERENCE = "developer_preference"
    const val KEY_FEEDBACK_PREFERENCE = "feedback_preference"

}