package mhashim6.android.putback.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import io.realm.Realm
import mhashim6.android.putback.R
import mhashim6.android.putback.data.Notion
import mhashim6.android.putback.debug
import mhashim6.android.putback.ui.notionsDetailFragment.NotionDetailFragment
import mhashim6.android.putback.ui.notionsDetailFragment.NotionDetailFragment.Companion.NOTION_DETAIL_ACTION_DISPLAY
import mhashim6.android.putback.ui.notionsDetailFragment.NotionDetailFragment.Companion.NOTION_DETAIL_ACTION_TYPE
import mhashim6.android.putback.ui.notionsDetailFragment.NotionDetailFragment.Companion.NOTION_DETAIL_NOTION_ID
import mhashim6.android.putback.work.NotificationBroadcastReceiver.Companion.NOTION_ID_EXTRA

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//		writeDummyData()
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
        if (intent.action == MAIN_ACTIVITY_SHOW_NOTION_ACTION) {
            showNotionDetail(intent.getStringExtra(NOTION_ID_EXTRA))
        }
    }

    private fun showNotionDetail(notionId: String) {
        NotionDetailFragment.create(
                bundleOf(NOTION_DETAIL_ACTION_TYPE to NOTION_DETAIL_ACTION_DISPLAY,
                        NOTION_DETAIL_NOTION_ID to notionId))
                .show(supportFragmentManager, NotionDetailFragment::class.java.simpleName)
    }


    private fun writeDummyData() {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor sit amet.", interval = 2, timeUnit = Notion.WEEK))
            it.copyToRealmOrUpdate(Notion(content = "Lorem.", interval = 8, timeUnit = Notion.DAY))
            it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor.", interval = 4, timeUnit = Notion.DAY))
            it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor sit amet consectetur adipiscing elit, suscipit interdum phasellus penatibus sagittis ullamcorper, orci ridiculus tellus quis ut libero.", interval = 2, timeUnit = Notion.MONTH))
            it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor sit amet consectetur adipiscing elit, suscipit interdum phasellus penatibus sagittis ullamcorper, orci ridiculus tellus quis ut libero.", interval = 1, timeUnit = Notion.MONTH))
            it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor sit amet consectetur adipiscing elit, suscipit interdum phasellus penatibus sagittis ullamcorper, orci ridiculus tellus quis ut libero.", interval = 15, timeUnit = Notion.DAY))
            it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor sit amet consectetur adipiscing elit, suscipit interdum phasellus penatibus sagittis ullamcorper, orci ridiculus tellus quis ut libero.", interval = 1, timeUnit = Notion.YEAR))
            it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor sit amet consectetur adipiscing elit, suscipit interdum phasellus penatibus sagittis ullamcorper, orci ridiculus tellus quis ut libero."))
            it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor sit amet consectetur adipiscing elit, suscipit interdum phasellus penatibus sagittis ullamcorper, orci ridiculus tellus quis ut libero."))
        }
        realm.close()
    }

//	override fun onSupportNavigateUp() = findNavController(this, R.id.nav_host_fragment).navigateUp()


    companion object {
        const val MAIN_ACTIVITY_SHOW_NOTION_ACTION = "MAIN_ACTIVITY_SHOW_NOTION_ACTION"
    }

}

