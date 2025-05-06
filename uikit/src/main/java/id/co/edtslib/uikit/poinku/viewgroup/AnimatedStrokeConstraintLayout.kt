package id.co.edtslib.uikit.poinku.viewgroup

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.content.res.use
import androidx.core.graphics.toColorInt
import id.co.edtslib.uikit.poinku.utils.color
import id.co.edtslib.uikit.poinku.utils.dp
import id.co.edtslib.uikit.poinku.R
import id.co.edtslib.uikit.poinku.utils.interpolator.EaseInterpolator
import id.co.edtslib.uikit.poinku.utils.seconds

class AnimatedStrokeConstraintLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private var strokeWidth = 1.dp
    private var cornerRadius = 0f
    private var strokeColor = context.color(R.color.primary_30)

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        this.strokeWidth = this@AnimatedStrokeConstraintLayout.strokeWidth
    }
    private lateinit var gradient: SweepGradient

    private val matrix = Matrix()
    private val rect = RectF()

    private var angle = 0f

    init {
        setWillNotDraw(false)

        context.obtainStyledAttributes(attrs, R.styleable.AnimatedStrokeConstraintLayout).use {
            cornerRadius = it.getDimension(R.styleable.AnimatedStrokeConstraintLayout_cornerRadius, cornerRadius)
            strokeWidth = it.getDimension(R.styleable.AnimatedStrokeConstraintLayout_strokeWidth, strokeWidth)
            strokeColor = it.getColor(R.styleable.AnimatedStrokeConstraintLayout_strokeColor, strokeColor)
        }

        ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 3.seconds
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                angle = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        val half = strokeWidth / 2
        rect.set(half, half, w - half, h - half)

        gradient = SweepGradient(
            w / 2f, h / 2f,
            intArrayOf(
                Color.WHITE,
                strokeColor,
                Color.WHITE
            ),
            floatArrayOf(0f, 0.5f, 1f)
        )
        strokePaint.shader = gradient
    }

    override fun onDraw(canvas: Canvas) { super.onDraw(canvas) }

    override fun onDrawForeground(canvas: Canvas) {
        matrix.setRotate(angle, width / 2f, height / 2f)
        gradient.setLocalMatrix(matrix)

        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, strokePaint)

        super.onDrawForeground(canvas)
    }
}
