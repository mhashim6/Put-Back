package mhashim6.android.putback.ui.notionsFragment

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import kotlinx.android.synthetic.main.notion_compact.view.*

class NotionCompactView : CardView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val content: AppCompatTextView by lazy { notionContent }
    private val statusIcon: AppCompatImageView by lazy { statusIconId }
    private val intervalText: AppCompatTextView by lazy { intervalTextId }

    fun render(notion: NotionCompactViewModel) {
        content.text = notion.content
        intervalText.text = notion.interval
        statusIcon.setImageResource(notion.statusIcon)
        setCardBackgroundColor(notion.color)
    }
}
