package mhashim.android.putback.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import mhashim.android.putback.R
import mhashim.android.putback.data.Notion

class MainActivity : AppCompatActivity() {


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
//		writeDummyData()

		setContentView(R.layout.activity_main)
		//setSupportActionBar(toolbar)

		/*	bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

				override fun onSlide(bottomSheet: View, slideOffset: Float) {


				}

				override fun onStateChanged(bottomSheet: View, newState: Int) {

					if (newState == STATE_EXPANDED) {
						val fragmentView = nav_host_fragment.view!!
						val backgroundToBlur = fragmentView.captureBitmap()

						val startX = 0
						val startY = bottomSheet.y.toInt() - toolbar.height

						Log.d("asd", backgroundToBlur.height.toString())
						Log.d("root", root.height.toString())
						Log.d("bottom", startY.toString())
						Log.d("toolbar", toolbar.height.toString())
						Log.d("hmm", (root.height - toolbar.height).toString())
						Log.d("shit", ((toolbar.height + backgroundToBlur.height) - bottomSheet.height).toString())
						Log.d("yousef", (backgroundToBlur.height - startY).toString())

						val newBG = backgroundToBlur.crop(0, startY = startY, endX = backgroundToBlur.width, endY = backgroundToBlur.height)
						Blurry.with(this@MainActivity)
								//	.color(ResourcesCompat.getColor(resources, R.color.pale_yellow,null))
								.from(newBG)
								.into(bottomSheetBackground)
					}
				}

			})
	*/
	}

	private fun writeDummyData() {
		val realm = Realm.getDefaultInstance()
		realm.executeTransaction {
			it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor sit amet.", interval = 2))
			it.copyToRealmOrUpdate(Notion(content = "Lorem.", interval = 8))
			it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor.", interval = 4))
			it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor sit amet consectetur adipiscing elit, suscipit interdum phasellus penatibus sagittis ullamcorper, orci ridiculus tellus quis ut libero.", interval = 60))
			it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor sit amet consectetur adipiscing elit, suscipit interdum phasellus penatibus sagittis ullamcorper, orci ridiculus tellus quis ut libero.", interval = 15))
			it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor sit amet consectetur adipiscing elit, suscipit interdum phasellus penatibus sagittis ullamcorper, orci ridiculus tellus quis ut libero.", interval = 1))
			it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor sit amet consectetur adipiscing elit, suscipit interdum phasellus penatibus sagittis ullamcorper, orci ridiculus tellus quis ut libero."))
			it.copyToRealmOrUpdate(Notion(content = "Lorem ipsum dolor sit amet consectetur adipiscing elit, suscipit interdum phasellus penatibus sagittis ullamcorper, orci ridiculus tellus quis ut libero."))
		}
		realm.close()
	}


/*  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
			menuInflater.inflate(R.menu.main, menu)
			return super.onCreateOptionsMenu(menu)
	}
	*/

//	override fun onSupportNavigateUp() = findNavController(this, R.id.nav_host_fragment).navigateUp()
}

