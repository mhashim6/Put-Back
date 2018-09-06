package mhashim6.android.putback.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import io.realm.Realm
import mhashim6.android.putback.R
import mhashim6.android.putback.data.Notion
import mhashim6.android.putback.debug

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        if (intent.action == MAIN_ACTIVITY_SHOW_NOTION_ACTION) {
            //TODO pass notionId to detail fragment.
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.notionDetailFragment, Bundle())
        }
    }

    private fun writeDummyData() {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor sit amet.", interval = 1, timeUnit = Notion.DAY))
       /*     it.copyToRealmOrUpdate(Notion(content = "Lorem.", interval = 8, timeUnit = Notion.DAY))
            it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor.", interval = 4, timeUnit = Notion.DAY))
            it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor sit amet consectetur adipiscing elit, suscipit interdum phasellus penatibus sagittis ullamcorper, orci ridiculus tellus quis ut libero.", interval = 2, timeUnit = Notion.MONTH))
            it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor sit amet consectetur adipiscing elit, suscipit interdum phasellus penatibus sagittis ullamcorper, orci ridiculus tellus quis ut libero.", interval = 1, timeUnit = Notion.MONTH))
            it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor sit amet consectetur adipiscing elit, suscipit interdum phasellus penatibus sagittis ullamcorper, orci ridiculus tellus quis ut libero.", interval = 15, timeUnit = Notion.DAY))
            it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor sit amet consectetur adipiscing elit, suscipit interdum phasellus penatibus sagittis ullamcorper, orci ridiculus tellus quis ut libero.", interval = 1, timeUnit = Notion.YEAR))
            it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor sit amet consectetur adipiscing elit, suscipit interdum phasellus penatibus sagittis ullamcorper, orci ridiculus tellus quis ut libero."))
            it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor sit amet consectetur adipiscing elit, suscipit interdum phasellus penatibus sagittis ullamcorper, orci ridiculus tellus quis ut libero."))*/
        }
        realm.close()
    }

//	override fun onSupportNavigateUp() = findNavController(this, R.id.nav_host_fragment).navigateUp()


    companion object {
        const val MAIN_ACTIVITY_SHOW_NOTION_ACTION = "MAIN_ACTIVITY_SHOW_NOTION_ACTION"
    }

}

