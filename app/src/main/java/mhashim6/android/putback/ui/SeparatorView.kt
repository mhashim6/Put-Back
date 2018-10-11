package mhashim6.android.putback.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import mhashim6.android.putback.R

class SeparatorView : View {

    private val solidLine = Paint()

    private val dashedLine = Paint().apply {
        pathEffect = DashPathEffect(floatArrayOf(10f, 15f), 0f)
    }

    private val dottedLine = Paint().apply {
        pathEffect = DashPathEffect(floatArrayOf(2f, 10f), 0f)
    }

    var lineStyle = LineStyle.SOLID
        set(value) {
            field = value
            invalidate()
        }

    var lineColor = Color.BLACK
        set(value) {
            field = value
            invalidate()
        }

    var dashGap = 10f
        set(value) {
            field = value
            updateDashesStyle()
        }

    var dashWidth = 15f
        set(value) {
            field = value
            updateDashesStyle()
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null) //because developing for android is such a joy.

        context.withStyledAttributes(
                attributeSet,
                R.styleable.SeparatorView,
                0, 0) {
            lineStyle = resolveStyle(getInt(R.styleable.SeparatorView_lineStyle, 0))
            lineColor = getColor(R.styleable.SeparatorView_lineColor, Color.BLACK)

            dashWidth = getDimension(R.styleable.SeparatorView_dashWidth, 15f)
            dashGap = getDimension(R.styleable.SeparatorView_dashGap, 10f)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = dpToPx(2, context)

        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.resolveSize(desiredHeight, heightMeasureSpec)

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawLine(0f, 0f, width.toFloat(), 0f, resolvePaint(lineStyle).apply { color = lineColor })
    }

    private fun updateDashesStyle() {
        dashedLine.pathEffect = DashPathEffect(floatArrayOf(dashWidth, dashGap), 0f)
        invalidate()
    }

    private fun resolvePaint(style: LineStyle): Paint = when (style) {
        LineStyle.SOLID -> solidLine
        LineStyle.DASHED -> dashedLine
        LineStyle.DOTTED -> dottedLine
    }

    companion object {
        enum class LineStyle {
            SOLID,
            DASHED,
            DOTTED
        }

        private fun resolveStyle(index: Int): LineStyle = when (index) {
            1 -> LineStyle.DASHED
            2 -> LineStyle.DOTTED
            else -> LineStyle.SOLID
        }
    }
}