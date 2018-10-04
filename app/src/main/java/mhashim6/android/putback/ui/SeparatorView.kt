package mhashim6.android.putback.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import mhashim6.android.putback.R

class SeparatorView : View {

    var lineStyle: LineStyle = LineStyle.NORMAL
        set(value) {
            field = value
            invalidate()
        }

    var lineColor = Color.BLACK
        set(value) {
            field = value
            invalidate()
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
            setLayerType(View.LAYER_TYPE_SOFTWARE, null) //TODO check this.

        context.theme.obtainStyledAttributes(
                attributeSet,
                R.styleable.SeparatorView,
                0, 0).apply {
            lineStyle = resolveStyle(getInt(R.styleable.SeparatorView_lineStyle, 0))
            lineColor = getColor(R.styleable.SeparatorView_lineColor, Color.BLACK)
            recycle()
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = 10

        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.resolveSize(desiredHeight, heightMeasureSpec)

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawLine(0f, 0f, width.toFloat(), 0f, resolvePaint(lineStyle).apply { color = lineColor })
    }

    companion object {
        enum class LineStyle {
            NORMAL,
            DASHED,
            DOTTED
        }

        private val normalLine: Paint = Paint()

        private val dashedLine: Paint = Paint().apply {
            pathEffect = DashPathEffect(floatArrayOf(10f, 15f), 0f)
        }

        private val dottedLine: Paint = Paint().apply {
            pathEffect = DashPathEffect(floatArrayOf(2f, 10f), 0f)
        }

        private fun resolvePaint(style: LineStyle): Paint = when (style) {
            LineStyle.NORMAL -> normalLine
            LineStyle.DASHED -> dashedLine
            LineStyle.DOTTED -> dottedLine
        }

        private fun resolveStyle(value: Int): LineStyle = when (value) {
            1 -> LineStyle.DASHED
            2 -> LineStyle.DOTTED
            else -> LineStyle.NORMAL
        }
    }
}