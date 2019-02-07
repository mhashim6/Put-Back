package mhashim6.android.putback.ui.preferences

import android.Manifest
import android.content.Context
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import mhashim6.android.putback.APP_URL
import mhashim6.android.putback.GITHUB_URL
import mhashim6.android.putback.R
import mhashim6.android.putback.data.PreferencesRepository.KEY_BACKUP_PREFERENCE
import mhashim6.android.putback.data.PreferencesRepository.KEY_DEVELOPER_PREFERENCE
import mhashim6.android.putback.data.PreferencesRepository.KEY_FEEDBACK_PREFERENCE
import mhashim6.android.putback.data.PreferencesRepository.KEY_OPEN_SOURCE_PREFERENCE
import mhashim6.android.putback.data.PreferencesRepository.KEY_RESTORE_PREFERENCE
import mhashim6.android.putback.data.PreferencesRepository.KEY_SOUND_PREFERENCE
import mhashim6.android.putback.data.backup
import mhashim6.android.putback.data.restore


class PreferencesViewModel(
        val soundSelectorRequests: Observable<Unit>,
        val snackbars: Observable<Int>,
        val creditsRequests: Observable<Unit>,
        val preferencesDisposable: Disposable,
        val urls: Observable<String>)

fun present(preferences: PublishSubject<Pair<Context, String>>): PreferencesViewModel {

    val soundSelectorRequests = PublishSubject.create<Unit>()
    val snackbars = PublishSubject.create<Int>()
    val creditsRequests = PublishSubject.create<Unit>()
    val urls = PublishSubject.create<String>()

    val preferencesDisposable = preferences.subscribe { (context, key) ->
        when (key) {
            KEY_BACKUP_PREFERENCE -> withStoragePermissions(context,
                    onDenied = {
                        snackbars.onNext(R.string.storage_permissions_denied)
                    }, onGranted = {
                if (backup()) //TODO coroutines
                    snackbars.onNext(R.string.backup_successful)
                else
                    snackbars.onNext(R.string.backup_error)
            }, snackbars = snackbars)
            KEY_RESTORE_PREFERENCE -> withStoragePermissions(context,
                    onDenied = {
                        snackbars.onNext(R.string.storage_permissions_denied)
                    }
                    , onGranted = {
                if (restore()) //TODO coroutines
                    snackbars.onNext(R.string.restore_successful)
                else
                    snackbars.onNext(R.string.restore_error)
            }, snackbars = snackbars)

            KEY_SOUND_PREFERENCE -> soundSelectorRequests.onNext(Unit)
            KEY_OPEN_SOURCE_PREFERENCE -> creditsRequests.onNext(Unit)
            KEY_DEVELOPER_PREFERENCE -> urls.onNext(GITHUB_URL)
            KEY_FEEDBACK_PREFERENCE -> urls.onNext(APP_URL)
        }
    }

    return PreferencesViewModel(soundSelectorRequests, snackbars, creditsRequests, preferencesDisposable, urls)
}

private fun withStoragePermissions(context: Context, onDenied: () -> Unit, onGranted: () -> Unit, snackbars: PublishSubject<Int>) {
    Permissions.check(context, Manifest.permission.WRITE_EXTERNAL_STORAGE, "do it stopid",
            object : PermissionHandler() {
                override fun onGranted() = onGranted()
                override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
                    snackbars.onNext(R.string.storage_permissions_denied)

                }
            })
}