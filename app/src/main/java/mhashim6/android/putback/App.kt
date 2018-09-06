package mhashim6.android.putback

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import io.realm.Realm
import io.realm.RealmConfiguration
import mhashim6.android.putback.work.NotionsReminder
import java.util.concurrent.TimeUnit


/**
 * Created by mhashim6 on 24/08/2018.
 */

class App : Application() {

	override fun onCreate() {
		super.onCreate()
		initRealm()
		createNotionsReminderNotificationChannel()
		submitWorks()
	}

	private fun initRealm() {
		Realm.init(applicationContext)
		Realm.setDefaultConfiguration(RealmConfiguration.Builder()
				.build())
	}

	private fun submitWorks() {

		val workRequest = PeriodicWorkRequest
				.Builder(NotionsReminder::class.java, 15, TimeUnit.MINUTES, 5, TimeUnit.MINUTES) //temp for testing.
				.build()

		workRequest.apply {
			tags.add(NOTIONS_REMINDER_TAG)
		}

		WorkManager.getInstance()
				.enqueueUniquePeriodicWork(NOTIONS_REMINDER_TAG,
						ExistingPeriodicWorkPolicy.KEEP,
						workRequest)
	}

	@SuppressLint("NewApi")
	private fun createNotionsReminderNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val name = getString(R.string.channel_name)
			val description = getString(R.string.channel_description)
			val importance = NotificationManager.IMPORTANCE_HIGH
			val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
			channel.description = description
			val notificationManager = getSystemService(NotificationManager::class.java)
			notificationManager!!.createNotificationChannel(channel)
		}
	}

	companion object {
		const val NOTIFICATION_CHANNEL_ID = "NOTIONS_REMINDER_CHANNEL"
		const val NOTIONS_REMINDER_TAG = "NOTIONS_REMINDER_TAG"
	}

}
