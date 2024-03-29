package mhashim6.android.putback.ui.notionsDetailFragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import mhashim6.android.putback.R
import mhashim6.android.putback.ui.marquee

class NotionDetailFragment : AppCompatDialogFragment() {

    private lateinit var container: View
    private lateinit var contentText: AppCompatEditText
    private lateinit var intervalSpinner: AppCompatSpinner
    private lateinit var unitSpinner: AppCompatSpinner
    private lateinit var dateMetaDataText: AppCompatTextView

    private val intervalUpdates: PublishSubject<Pair<Int, Int>> = PublishSubject.create()
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
        contentText.movementMethod = LinkMovementMethod.getInstance()
        intervalSpinner = view.findViewById<AppCompatSpinner>(R.id.intervalSpinner).apply {
            adapter = ArrayAdapter<Int>(requireContext(), android.R.layout.simple_spinner_dropdown_item, (1..15).toList())
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    intervalUpdates.onNext(position to unitSpinner.selectedItemPosition)
                }
            }
        }

        unitSpinner = view.findViewById(R.id.unitSpinner)
        unitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                intervalUpdates.onNext(intervalSpinner.selectedItemPosition to position)
            }

        }
        dateMetaDataText = view.findViewById(R.id.dateMetaData)
        dateMetaDataText.marquee() //TODO it doesn't look right on the emulator.
    }

    override fun onResume() {
        super.onResume()
        //because android is mean.
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        showData()
    }

    override fun onPause() {
        super.onPause()
        notionUpdate.onNext(NotionUpdate(
                contentText.text.toString(),
                intervalSpinner.selectedItemPosition,
                unitSpinner.selectedItemPosition
        ))
        subscriptions.clear()
    }

    private fun showData() {
        val viewModel = present(arguments, intervalUpdates, notionUpdate, resources)
        with(viewModel) {
            render(notion)
            subscriptions.addAll(
                    backgroundColor.subscribe(::updateBackgroundColor),
                    update
            )
        }
    }

    private fun render(notion: NotionDetailViewModel) {
        with(notion) {
            container.background = backgroundColor
            contentText.setText(content)
            intervalSpinner.setSelection(intervalSpinnerIndex)
            unitSpinner.setSelection(timeUnit)
            dateMetaDataText.text = dateMetaData
        }
    }

    private fun updateBackgroundColor(color: ColorDrawable) {
        container.background = color
    }

    companion object {
        const val NOTION_DETAIL_NOTION_ID = "NOTION_DETAIL_NOTION_ID"
        const val NOTION_DETAIL_NOTION_CONTENT = "NOTION_DETAIL_NOTION_CONTENT"

        fun create(notionId: String? = null, content: String? = null): NotionDetailFragment {
            return NotionDetailFragment().apply {
                arguments = bundleOf(
                        NOTION_DETAIL_NOTION_ID to notionId,
                        NOTION_DETAIL_NOTION_CONTENT to content)
            }
        }
    }
}