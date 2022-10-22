package mhashim6.android.putback

import android.content.Context
import androidx.multidex.MultiDexApplication
import io.realm.Realm
import io.realm.RealmConfiguration
import mhashim6.android.putback.data.PreferencesRepository
import mhashim6.android.putback.reminder.NotionsReminder
import mhashim6.lib.ratemonitor.RateConditionsMonitor


/**
 * Created by mhashim6 on 24/08/2018.
 */

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        context = this
        initRealm()
        PreferencesRepository.init(applicationContext)
        NotionsReminder.start(applicationContext)
        initRate()
    }

    private fun initRealm() {
        Realm.init(applicationContext)
        Realm.setDefaultConfiguration(RealmConfiguration.Builder()
                .build())
    }

    private fun initRate() {
        RateConditionsMonitor.init(applicationContext) {
            applyConditions(launchTimes = 3, remindTimes = 5, debug = false)
        }
    }

    companion object {
        lateinit var context: Context
    }

}
