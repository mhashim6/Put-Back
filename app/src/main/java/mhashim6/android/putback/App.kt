package mhashim6.android.putback

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import io.realm.Realm
import io.realm.RealmConfiguration
import mhashim6.android.putback.data.PreferencesRepository
import mhashim6.android.putback.work.NotionsReminder
import mhashim6.android.putback.work.NotionsReminder.Factory.NOTIONS_REMINDER_TAG
import mhashim6.lib.ratemonitor.RateConditionsMonitor


/**
 * Created by mhashim6 on 24/08/2018.
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initRealm()
        PreferencesRepository.init(applicationContext)
//        DonationsRepository.init(applicationContext)
        NotionsReminder.createNotionsReminderNotificationChannel(applicationContext)
        submitWorks()
        initRate()
    }

    private fun initRealm() {
        Realm.init(applicationContext)
        Realm.setDefaultConfiguration(RealmConfiguration.Builder()
                .build())
    }

    private fun submitWorks() {

        val workRequest = NotionsReminder.createNotionsReminder()

        WorkManager.getInstance()
                .enqueueUniquePeriodicWork(NOTIONS_REMINDER_TAG,
                        ExistingPeriodicWorkPolicy.KEEP,
                        workRequest)
    }

    private fun initRate() {
        RateConditionsMonitor.apply {
            init(applicationContext)
            applyConditions(launchTimes = 3, remindTimes = 5, debug = false)
        }
    }

}
