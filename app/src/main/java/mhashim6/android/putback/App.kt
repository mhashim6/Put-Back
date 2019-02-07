package mhashim6.android.putback

import android.app.Application
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import io.realm.Realm
import io.realm.RealmConfiguration
import mhashim6.android.putback.data.PreferencesRepository
import mhashim6.android.putback.reminder.NotionsReminder
import mhashim6.android.putback.reminder.NotionsReminder.Factory.NOTIONS_REMINDER_TAG
import mhashim6.lib.ratemonitor.RateConditionsMonitor


/**
 * Created by mhashim6 on 24/08/2018.
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
        initRealm()
        PreferencesRepository.init(applicationContext)
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
        val workManager = WorkManager.getInstance()
        val workRequest = NotionsReminder.createNotionsReminder()

        ifNewUpdate { workManager.cancelAllWork() }

        workManager.enqueueUniquePeriodicWork(NOTIONS_REMINDER_TAG,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest)
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
