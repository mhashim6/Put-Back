package mhashim.android.putback.ui

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import io.realm.OrderedCollectionChangeSet
import mhashim.android.putback.R
import mhashim.android.putback.data.Notion
import mhashim.android.putback.ui.notionsFragment.BaseAdapter
import mhashim.android.putback.ui.notionsFragment.NotionCompactViewModel


/**
 * Created by mhashim6 on 22/08/2018.
 */

fun View.captureBitmap(): Bitmap {
	val empty = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
	val canvas = Canvas(empty)
	this.background?.draw(canvas)
	this.draw(canvas)
	return empty
}

fun Bitmap.crop(startX: Int, startY: Int, endX: Int, endY: Int): Bitmap {
	return Bitmap.createBitmap(this, startX, startY, endX - startX, endY - startY)
}

@ColorRes
fun colorSelector(notion: Notion, resources: Resources): Int {

	val colorRes = when {
		notion.interval in 0..4 -> R.color.muted_red
		notion.interval > 15 -> R.color.muted_green
		else -> R.color.muted_blue
	}

	return ResourcesCompat.getColor(resources, colorRes, null)
}


val Boolean.visibility
	get() = if (this) View.VISIBLE else View.GONE


fun BaseAdapter<NotionCompactView, NotionCompactViewModel>.handleChanges(collectionChange: Pair<List<NotionCompactViewModel>, OrderedCollectionChangeSet?>) {
	val (collection, changeset) = collectionChange
	replaceAll(collection)
	if (changeset == null)
		notifyDataSetChanged()
	else {
		for (change in changeset.changeRanges)
			notifyItemRangeChanged(change.startIndex, change.length)

		for (insertion in changeset.insertionRanges)
			notifyItemRangeInserted(insertion.startIndex, insertion.length)

		for (deletion in changeset.deletionRanges)
			notifyItemRangeRemoved(deletion.startIndex, deletion.length)
	}
}