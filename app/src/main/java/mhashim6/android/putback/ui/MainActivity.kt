package mhashim6.android.putback.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import io.realm.Realm
import mhashim6.android.putback.R
import mhashim6.android.putback.data.Notion
import mhashim6.android.putback.data.PreferencesRepository
import mhashim6.android.putback.debug
import mhashim6.android.putback.ui.notionsDetailFragment.NotionDetailFragment
import mhashim6.android.putback.ui.notionsDetailFragment.NotionDetailFragment.Companion.NOTION_DETAIL_ACTION_CREATE
import mhashim6.android.putback.ui.notionsDetailFragment.NotionDetailFragment.Companion.NOTION_DETAIL_ACTION_DISPLAY
import mhashim6.android.putback.ui.notionsDetailFragment.NotionDetailFragment.Companion.NOTION_DETAIL_ACTION_TYPE
import mhashim6.android.putback.ui.notionsDetailFragment.NotionDetailFragment.Companion.NOTION_DETAIL_NOTION_CONTENT
import mhashim6.android.putback.ui.notionsDetailFragment.NotionDetailFragment.Companion.NOTION_DETAIL_NOTION_ID
import mhashim6.android.putback.work.NotificationBroadcastReceiver.Companion.NOTION_ID_EXTRA

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setBackgroundDrawableResource(PreferencesRepository.theme)

        writeDummyData()
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) //so the intent is not handled again if the device was rotated.
            handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        debug(intent.action)
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == MAIN_ACTIVITY_SHOW_NOTION_ACTION)
            showNotionDetail(intent.getStringExtra(NOTION_ID_EXTRA), null)
        else if (intent.action == Intent.ACTION_SEND) {
            showNotionDetail(bundleOf(
                    NOTION_DETAIL_ACTION_TYPE to NOTION_DETAIL_ACTION_CREATE,
                    NOTION_DETAIL_NOTION_CONTENT to intent.getStringExtra(Intent.EXTRA_TEXT)))
        }
    }

    private fun showNotionDetail(notionId: String, content: String?) {
        showNotionDetail(bundleOf(NOTION_DETAIL_ACTION_TYPE to NOTION_DETAIL_ACTION_DISPLAY,
                NOTION_DETAIL_NOTION_ID to notionId,
                NOTION_DETAIL_NOTION_CONTENT to content))
    }

    private fun showNotionDetail(args: Bundle) {
        NotionDetailFragment.create(args).show(supportFragmentManager, NotionDetailFragment::class.java.simpleName)
    }

    private fun writeDummyData() {
        if (PreferencesRepository.firstLaunch.not())
            return

        PreferencesRepository.firstLaunch = false
        val realm = Realm.getDefaultInstance()
        realm.executeTransactionAsync {
            it.copyToRealmOrUpdate(Notion(content = resources.getString(R.string.family_gathering), interval = 6, timeUnit = Notion.MONTH))
            it.copyToRealmOrUpdate(Notion(content = resources.getString(R.string.skin_routine), interval = 1, timeUnit = Notion.WEEK))
            it.copyToRealmOrUpdate(Notion(content = resources.getString(R.string.charity), interval = 1, timeUnit = Notion.MONTH))
            it.copyToRealmOrUpdate(Notion(content = resources.getString(R.string.chocolate_for_mom), interval = 2, timeUnit = Notion.WEEK))
        }
        realm.close()
    }

//	override fun onSupportNavigateUp() = findNavController(this, R.id.nav_host_fragment).navigateUp()


    companion object {
        const val MAIN_ACTIVITY_SHOW_NOTION_ACTION = "MAIN_ACTIVITY_SHOW_NOTION_ACTION"
    }

}

