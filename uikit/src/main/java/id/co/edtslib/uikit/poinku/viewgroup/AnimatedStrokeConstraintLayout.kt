package id.co.edtslib.uikit.poinku.viewgroup

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.ViewGroup
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
    private var isInfinite = false

    var isGlowing = true
    var onlyGlow = false
        set(value) {
            field = value
            if (value) {
                glowAnimator.start()
            } else {
                glowAnimator.cancel()
            }
        }

    private var glowRadius = 8.dp

    // paint & geometry
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    // Glow Paint
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = context.color(R.color.primary_30)
        maskFilter = BlurMaskFilter(glowRadius, BlurMaskFilter.Blur.OUTER)
    }

    private val glowAnimator = ValueAnimator.ofFloat(0.05f, 12.dp).apply {
        duration = 2000L
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener {
            glowRadius = it.animatedValue as Float
            glowPaint.maskFilter = BlurMaskFilter(glowRadius, BlurMaskFilter.Blur.OUTER)
            invalidate()
        }
    }

    private var gradient: SweepGradient? = null
    private val matrix = Matrix()
    private val rect = RectF()

    // animation state
    private var angle = 0f
    private var totalMeter = 0f

    // finite parameters
    private val rotations = 5
    private val totalDeg = 360f * rotations
    private val fadeDeg = 360f // last rotation is fade

    private var animator: ValueAnimator? = null

    init {
        setWillNotDraw(false)
        context.obtainStyledAttributes(attrs, R.styleable.AnimatedStrokeConstraintLayout).use {
            cornerRadius = it.getDimension(
                R.styleable.AnimatedStrokeConstraintLayout_cornerRadius,
                cornerRadius
            )
            strokeWidth =
                it.getDimension(R.styleable.AnimatedStrokeConstraintLayout_strokeWidth, strokeWidth)
            strokeColor =
                it.getColor(R.styleable.AnimatedStrokeConstraintLayout_strokeColor, strokeColor)
        }

        setupAnimator()
    }


    /** Call this if you want to toggle infinite/spinning mode at runtime */
    fun setInfinite(infinite: Boolean) {
        if (infinite != isInfinite) {
            isInfinite = infinite
            animator?.cancel()
            setupAnimator()
        }
    }

    private fun setupAnimator() {
        animator = if (isInfinite) {
            // infinite 0→360
            ValueAnimator.ofFloat(0f, 360f).apply {
                duration = 3000L
                interpolator = LinearInterpolator()
                repeatCount = ValueAnimator.INFINITE
                addUpdateListener {
                    angle = it.animatedValue as Float
                    invalidate()
                }
                start()
            }
        } else {
            // finite 0→5×360 with fade on last rotation
            ValueAnimator.ofFloat(0f, totalDeg).apply {
                duration = (3000L * rotations)
                interpolator = LinearInterpolator()
                addUpdateListener {
                    totalMeter = it.animatedValue as Float
                    angle = totalMeter % 360f

                    if (totalMeter >= totalDeg) {
                        // end: drop gradient, paint solid
                        strokePaint.shader = null
                        it.cancel()
                    }
                    invalidate()
                }
                start()
            }
        }
        // make sure shader is (re)applied when animator starts
        strokePaint.shader = gradient
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        strokePaint.strokeWidth = strokeWidth
        glowPaint.strokeWidth = strokeWidth
        val half = strokeWidth / 2
        rect.set(half, half, w - half, h - half)

        gradient = SweepGradient(
            w / 2f, h / 2f,
            intArrayOf(Color.WHITE, strokeColor, Color.WHITE),
            floatArrayOf(0f, 0.5f, 1f)
        )
        // always re-attach shader in case we restarted
        if (isInfinite || (animator?.isRunning == true)) {
            strokePaint.shader = gradient
        }
    }

    override fun onDrawForeground(canvas: Canvas) {
        if (onlyGlow) {
            val glowInset = strokeWidth * 0.5f
            val glowRect = RectF(
                rect.left + glowInset,
                rect.top + glowInset,
                rect.right - glowInset,
                rect.bottom - glowInset
            )
            if (isGlowing) {
                canvas.drawRoundRect(glowRect, cornerRadius, cornerRadius, glowPaint)
            }
        } else {
            val glowInset = strokeWidth * 0.5f
            val glowRect = RectF(
                rect.left + glowInset,
                rect.top + glowInset,
                rect.right - glowInset,
                rect.bottom - glowInset
            )
            if (isGlowing) {
                canvas.drawRoundRect(glowRect, cornerRadius, cornerRadius, glowPaint)
            }
            when {
                // infinite mode: just rotate gradient
                isInfinite -> {
                    matrix.setRotate(angle, width / 2f, height / 2f)
                    gradient?.setLocalMatrix(matrix)
                    strokePaint.shader = gradient
                    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, strokePaint)
                }

                // finite + fade mode: gradient for first 4 spins, fade over last spin
                strokePaint.shader != null -> {
                    val fadeStart = totalDeg - fadeDeg
                    val p = ((totalMeter - fadeStart) / fadeDeg).coerceIn(0f, 1f)

                    // 1) gradient layer
                    matrix.setRotate(angle, width / 2f, height / 2f)
                    gradient?.setLocalMatrix(matrix)
                    strokePaint.shader = gradient
                    strokePaint.alpha = ((1 - p) * 255).toInt()
                    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, strokePaint)

                    // 2) solid layer
                    strokePaint.shader = null
                    strokePaint.color = strokeColor
                    strokePaint.alpha = (p * 255).toInt()
                    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, strokePaint)

                    // restore
                    strokePaint.alpha = 255
                    strokePaint.shader = gradient
                }

                // after finite completes: pure solid
                else -> {
                    strokePaint.shader = null
                    strokePaint.color = strokeColor
                    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, strokePaint)
                }
            }
        }

        super.onDrawForeground(canvas)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        animator = null
        glowAnimator.cancel()
    }
}




