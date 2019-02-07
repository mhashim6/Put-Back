package mhashim6.android.putback.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import mhashim6.android.putback.R
import mhashim6.android.putback.data.Notion
import mhashim6.android.putback.data.PreferencesRepository
import mhashim6.android.putback.debug
import mhashim6.android.putback.ui.notionsDetailFragment.NotionDetailFragment
import mhashim6.android.putback.reminder.NotificationBroadcastReceiver
import mhashim6.android.putback.wtf

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setBackgroundDrawableResource(PreferencesRepository.theme)

        writeDummyData()
        setContentView(R.layout.activity_main)

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        wtf(intent.action)
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        debug(intent.getStringExtra(NotificationBroadcastReceiver.NOTION_ID_EXTRA))
        if (intent.action == MAIN_ACTIVITY_SHOW_NOTION_ACTION || intent.action == Intent.ACTION_SEND)
            showNotionDetail(notionId = intent.getStringExtra(NotificationBroadcastReceiver.NOTION_ID_EXTRA),
                    content = intent.getStringExtra(Intent.EXTRA_TEXT))

        intent.action = MAIN_ACTIVITY_CONSUMED_ACTION //consume the intent.
    }

    private fun showNotionDetail(notionId: String? = null, content: String? = null) {
        NotionDetailFragment.create(notionId, content).show(supportFragmentManager, NotionDetailFragment::class.java.simpleName)
    }

    private fun writeDummyData() {
        if (PreferencesRepository.firstLaunch.not())
            return

        PreferencesRepository.firstLaunch = false
        val realm = Realm.getDefaultInstance()
        realm.executeTransactionAsync {
            it.copyToRealmOrUpdate(Notion(content = resources.getString(R.string.family_gathering), interval = 6, timeUnit = Notion.MONTH))
            it.copyToRealmOrUpdate(Notion(content = resources.getString(R.string.skin_routine), interval = 1, timeUnit = Notion.WEEK))
            it.copyToRealmOrUpdate(Notion(content = resources.getString(R.string.charity), interval = 2, timeUnit = Notion.MONTH))
            it.copyToRealmOrUpdate(Notion(content = resources.getString(R.string.chocolate_for_mom), interval = 2, timeUnit = Notion.WEEK))
        }
        realm.close()
    }

//	override fun onSupportNavigateUp() = findNavController(this, R.id.nav_host_fragment).navigateUp()


    companion object {
        const val MAIN_ACTIVITY_SHOW_NOTION_ACTION = "MAIN_ACTIVITY_SHOW_NOTION_ACTION"
        const val MAIN_ACTIVITY_CONSUMED_ACTION = "MAIN_ACTIVITY_CONSUMED_ACTION"
    }

}

