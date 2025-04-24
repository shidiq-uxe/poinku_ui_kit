package id.co.edtslib.uikit.poinku.textview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
import com.google.android.material.textview.MaterialTextView
import id.co.edtslib.uikit.poinku.R
import id.co.edtslib.uikit.poinku.utils.color
import id.co.edtslib.uikit.poinku.utils.dimen
import id.co.edtslib.uikit.poinku.utils.dp

class DividerTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : MaterialTextView(context, attrs, defStyleAttr) {

    var innerPadding: Float = 16.dp
        set(value) {
            field = value
            invalidate()
        }

    var dividerColor = context.color(R.color.grey_40)

    var dividerHeight = 1.dp

    private val dividerPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = dividerHeight
        strokeCap = Paint.Cap.ROUND
        color = dividerColor
    }

    init {
        if (attrs != null) {
            initAttrs(attrs)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val textWidth = paint.measureText(text.toString())
        val centerY = height / 2f

        val stopXLeft = (width - textWidth) / 2f - innerPadding / 2f

        val startXRight = (width + textWidth) / 2f + innerPadding / 2f
        val stopXRight = width.toFloat()

        canvas.drawLine(0f, centerY, stopXLeft, centerY, dividerPaint)
        canvas.drawLine(startXRight, centerY, stopXRight, centerY, dividerPaint)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.DividerTextView).use {
            innerPadding = it.getDimension(R.styleable.DividerTextView_dividerInnerPadding, innerPadding)
            dividerColor = it.getColor(R.styleable.DividerTextView_dividerColor, dividerColor)
            dividerHeight = it.getDimension(R.styleable.DividerTextView_dividerHeight, dividerHeight)
        }
    }
}
