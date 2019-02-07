package mhashim6.android.putback.ui.preferences

import android.Manifest
import android.app.Activity
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
import pl.tajchert.nammu.Nammu
import pl.tajchert.nammu.PermissionCallback


class PreferencesViewModel(
        val soundSelectorRequests: Observable<Unit>,
        val snackbars: Observable<Int>,
        val creditsRequests: Observable<Unit>,
        val preferencesDisposable: Disposable,
        val urls: Observable<String>)

fun present(preferences: PublishSubject<Pair<Activity, String>>): PreferencesViewModel {

    val soundSelectorRequests = PublishSubject.create<Unit>()
    val snackbars = PublishSubject.create<Int>()
    val creditsRequests = PublishSubject.create<Unit>()
    val urls = PublishSubject.create<String>()


    val background = CoroutineScope(Dispatchers.IO)
    val main = CoroutineScope(Dispatchers.Main)

    val preferencesDisposable = preferences.subscribe { (context, key) ->
        when (key) {
            KEY_BACKUP_PREFERENCE -> withStoragePermissions(context,
                    onDenied = {
                        snackbars.onNext(R.string.storage_permissions_denied)
                    }, onGranted = {
                background.launch {
                    val successful = backup()
                    main.launch {
                        if (successful)
                            snackbars.onNext(R.string.backup_successful)
                        else
                            snackbars.onNext(R.string.backup_error)
                    }
                }
            })
            KEY_RESTORE_PREFERENCE -> withStoragePermissions(context,
                    onDenied = {
                        snackbars.onNext(R.string.storage_permissions_denied)
                    }
                    , onGranted = {
                background.launch {
                    val successful = restore()
                    main.launch {
                        if (successful)
                            snackbars.onNext(R.string.restore_successful)
                        else
                            snackbars.onNext(R.string.restore_error)
                    }
                }
            })

            KEY_SOUND_PREFERENCE -> soundSelectorRequests.onNext(Unit)
            KEY_OPEN_SOURCE_PREFERENCE -> creditsRequests.onNext(Unit)
            KEY_DEVELOPER_PREFERENCE -> urls.onNext(GITHUB_URL)
            KEY_FEEDBACK_PREFERENCE -> urls.onNext(APP_URL)
        }
    }
    return PreferencesViewModel(soundSelectorRequests, snackbars, creditsRequests, preferencesDisposable, urls)
}

private fun withStoragePermissions(activity: Activity, onDenied: () -> Unit, onGranted: () -> Unit) {
    Nammu.askForPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, object : PermissionCallback {
        //TODO for some reason these 2 don't work!
        override fun permissionGranted() = onGranted()

        override fun permissionRefused() = onDenied()
    })
}