package mhashim6.android.putback.ui

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import kotlinx.android.synthetic.main.notion_compact.view.*
import mhashim6.android.putback.ui.notionsFragment.NotionCompactViewModel

class NotionCompactView : CardView {

	constructor(context: Context) : this(context, null)
	constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

	val content: AppCompatTextView by lazy { notionContent }
	val archiveIcon: AppCompatImageView by lazy { archiveIconId }
//	val priorityRibbon:View by lazy { priority }
}

fun NotionCompactView.render(notion: NotionCompactViewModel) {
	content.text = notion.content
	archiveIcon.visibility = notion.archivedIconVisibility
	setCardBackgroundColor(notion.color)
}
