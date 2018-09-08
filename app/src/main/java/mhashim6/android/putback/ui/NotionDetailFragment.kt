package mhashim6.android.putback.ui

import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import mhashim6.android.putback.R
import mhashim6.android.putback.data.Notion
import mhashim6.android.putback.data.NotionsRealm
import mhashim6.android.putback.ui.NotionDetailPresenter.NotionDetailViewModel
import mhashim6.android.putback.ui.NotionDetailPresenter.NotionUpdate
import mhashim6.android.putback.ui.NotionDetailPresenter.present

class NotionDetailFragment : AppCompatDialogFragment() {

    private lateinit var container: View
    private lateinit var contentText: AppCompatEditText
    private lateinit var intervalText: AppCompatEditText
    private lateinit var unitSpinner: AppCompatSpinner
    private lateinit var dateMetaDataText: AppCompatTextView

    private val intervalUpdates: PublishSubject<Pair<String, Int>> = PublishSubject.create()
    private val notionUpdate: PublishSubject<NotionUpdate> = PublishSubject.create()

    private val subscriptions: CompositeDisposable = CompositeDisposable()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_notion_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews(view)
    }

    private fun setUpViews(view: View) {
        container = view
        contentText = view.findViewById(R.id.contentId)
        intervalText = view.findViewById(R.id.intervalTextId)
        intervalText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                intervalUpdates.onNext(s.toString() to unitSpinner.selectedItemPosition)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })
        unitSpinner = view.findViewById(R.id.unitSpinner)
        unitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                intervalUpdates.onNext(intervalText.text.toString() to position)
            }

        }
        dateMetaDataText = view.findViewById(R.id.dateMetaData)
        dateMetaDataText.marquee()
    }

    override fun onResume() {
        super.onResume()
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT) //because fuck android.
        showData()
    }

    override fun onPause() {
        super.onPause()
        notionUpdate.onNext(NotionUpdate(
                contentText.text.toString(),
                intervalText.text.toString(),
                unitSpinner.selectedItemPosition
        ))
        subscriptions.clear()
    }

    private fun showData() {
        val notionId = arguments?.getString(NOTION_DETAIL_NOTION_ID)
        val viewModel = present(notionId, intervalUpdates, notionUpdate, resources)
        with(viewModel) {
            show(notion)
            subscriptions.addAll(
                    backgroundColor.subscribe(::updateBackgroundColor),
                    update
            )
        }
    }

    private fun show(notion: NotionDetailViewModel) {
        with(notion) {
            container.background = backgroundColor
            contentText.setText(content)
            intervalText.setText(interval)
            unitSpinner.setSelection(timeUnit)
            dateMetaDataText.text = dateMetaData
        }
    }

    private fun updateBackgroundColor(color: ColorDrawable) {
        container.background = color
    }

    companion object {
        const val NOTION_DETAIL_ACTION_TYPE = "NOTION_DETAIL_ACTION_TYPE"
        const val NOTION_DETAIL_NOTION_ID = "NOTION_DETAIL_NOTION_ID"
        const val NOTION_DETAIL_ACTION_CREATE = 0
        const val NOTION_DETAIL_ACTION_DISPLAY = 1

        fun create(args: Bundle? = null) = NotionDetailFragment().apply {
            arguments = args
        }
    }
}

object NotionDetailPresenter {

    class ViewModel(
            val notion: NotionDetailViewModel,
            val backgroundColor: Observable<ColorDrawable>,
            val update: Disposable
    )

    class NotionUpdate(
            val content: String,
            val interval: String,
            val timeUnit: Int
    )

    class NotionDetailViewModel(
            notion: Notion,
            resources: Resources,
            val notionId: String = notion.id,
            val content: String = notion.content,
            val interval: String = notion.interval.toString(),
            val timeUnit: Int = indexByUnit(notion.timeUnit),
            val backgroundColor: ColorDrawable = ColorDrawable(colorSelector(notion, resources)),
            val dateMetaData: String = dateMetaDataString(notion.createdAt, notion.lastRunAt, resources)
    )

    fun present(notionId: String?,
                intervals: Observable<Pair<String, Int>>,
                update: Observable<NotionUpdate>,
                resources: Resources): ViewModel {

        val notion = NotionsRealm.findOne(notionId) ?: Notion()

        val colors = intervals.map { pair ->
            val count = pair.first.takeIf(String::isNotEmpty)?.toInt() ?: 0
            val unit = unitByIndex(pair.second)
            ColorDrawable(colorSelector(count, unit, resources))
        }

        val updateDisposable = update.subscribe {
            NotionsRealm.update(notionId ?: "",
                    it.content,
                    it.interval.takeIf(String::isNotEmpty)?.toInt() ?: 0,
                    unitByIndex(it.timeUnit))
        }

        return ViewModel(NotionDetailViewModel(notion, resources), colors, updateDisposable)
    }

}