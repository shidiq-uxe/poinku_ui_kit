package id.co.edtslib.uikit.poinku.ribbon

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.StyleRes
import androidx.core.view.doOnLayout
import id.co.edtslib.uikit.poinku.R
import id.co.edtslib.uikit.poinku.utils.color
import id.co.edtslib.uikit.poinku.utils.dimen
import id.co.edtslib.uikit.poinku.utils.font
import id.co.edtslib.uikit.poinku.utils.px

class Ribbon @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    @ColorInt
    var triangleColor = context.color(R.color.primary_50)
        set(value) {
            field = value
            invalidate()
        }

    @ColorInt
    var containerColor = context.color(R.color.primary_30)
        set(value) {
            field = value
            invalidate()
        }

    @ColorInt
    var containerEndColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            invalidate()
        }

    @ColorInt
    var containerStartColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            invalidate()
        }

    @ColorInt
    var textColor = context.color(R.color.white)
        set(value) {
            field = value
            textPaint.color = value
            invalidate()
        }

    // Gravity for positioning the triangle and container
    var gravity: Gravity = Gravity.START
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    @Dimension
    var cornerRadius: Float = context.dimen(R.dimen.xxxs)

    @Dimension
    var textVerticalPadding = context.dimen(R.dimen.dimen_2)
    @Dimension
    var textHorizontalPadding = context.dimen(R.dimen.xxxs)

    @StyleRes
    var textAppearanceRes = R.style.TextAppearance_Rubik_Semibold_H3
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    private val trianglePaint = Paint().apply {
        color = triangleColor
        style = Paint.Style.FILL
    }

    private val containerPaint = Paint().apply {
        style = Paint.Style.FILL
    }

    private val textPaint = Paint().apply {
        color = textColor
        textSize = 14.px
        textAlign = Paint.Align.CENTER
    }

    enum class Gravity {
        START,
        END
    }

    var ribbonText: String? = null
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var triangleWidth = 6f * resources.displayMetrics.density // Widiuth of the triangle
        private set
    var triangleHeight = 8f * resources.displayMetrics.density // Height of the triangle
        private set
    var textContainerHeight = textHeight + textVerticalPadding
        private set

    private val containerPath = Path()
    private val trianglePath = Path()
    private val rectF = RectF()

    private val textWidth get() = textPaint.measureText(ribbonText) + textHorizontalPadding.px
    private val textHeight get() = textPaint.fontMetrics.descent - textPaint.fontMetrics.ascent

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.Ribbon, 0, 0).use {
            triangleColor = it.getColor(R.styleable.Ribbon_triangleColor, triangleColor)
            containerColor = it.getColor(R.styleable.Ribbon_containerColor, containerColor)

            containerStartColor = it.getColor(R.styleable.Ribbon_containerStartColor, containerStartColor)
            containerEndColor = it.getColor(R.styleable.Ribbon_containerEndColor, containerEndColor)

            textColor = it.getColor(R.styleable.Ribbon_textColor, textColor)
            gravity = Gravity.values()[it.getInt(R.styleable.Ribbon_gravity, gravity.ordinal)]
            ribbonText = it.getString(R.styleable.Ribbon_text) ?: ribbonText

            textAppearanceRes = it.getResourceId(R.styleable.Ribbon_textAppearance, textAppearanceRes)
            if (textAppearanceRes != NO_ID) {
                applyTextAppearance(context, textAppearanceRes)
            }
        }

        outlineProvider = RibbonOutlineProvider(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val totalWidth = textWidth
        val totalHeight = triangleWidth + textContainerHeight + 2.px

        setMeasuredDimension(
            resolveSize(totalWidth.toInt(), widthMeasureSpec),
            resolveSize(totalHeight.toInt(), heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawContainer(canvas)
        drawText(canvas)
        drawTriangle(canvas)
    }

    private fun drawText(canvas: Canvas) {
        val x = (textWidth) / 2
        val y = (textContainerHeight + textHeight) / 2 - textPaint.fontMetrics.descent

        ribbonText?.let { canvas.drawText(it, x, y, textPaint) }
    }


    private fun drawContainer(canvas: Canvas) {
        val containerPath = if (gravity == Gravity.START) drawStartContainer()  else drawEndContainer()

        if (containerStartColor != Color.TRANSPARENT && containerEndColor != Color.TRANSPARENT) {
            containerPaint.shader = LinearGradient(
                0f, 0f,
                width.toFloat(), 0f,
                containerStartColor, containerEndColor,
                Shader.TileMode.CLAMP
            )
        } else {
            containerPaint.color = containerColor
        }

        canvas.drawPath(containerPath, containerPaint)
    }

    fun drawStartContainer(): Path {
        return containerPath.apply {
            moveTo(0f, cornerRadius)
            lineTo(0f, textContainerHeight)
            lineTo(textWidth - cornerRadius, textContainerHeight)

            rectF.set(textWidth - 2 * cornerRadius,textContainerHeight - 2 * cornerRadius, textWidth,textContainerHeight)
            arcTo(rectF, 90f, -90f)

            lineTo(textWidth, cornerRadius)
            rectF.set(textWidth - 2 * cornerRadius, 0f, textWidth, 2 * cornerRadius)
            arcTo(rectF, 0f, -90f)

            lineTo(cornerRadius, 0f)
            rectF.set(0f, 0f, 2 * cornerRadius, 2 * cornerRadius)
            arcTo(rectF, 270f, -90f)

            this.close()
        }
    }

    fun drawEndContainer(): Path {
        return containerPath.apply {
            moveTo(0f, cornerRadius)
            lineTo(0f, textContainerHeight)

            rectF.set(0f, textContainerHeight - 2 * cornerRadius, 2 * cornerRadius, textContainerHeight)
            arcTo(rectF, 180f, -90f)

            lineTo(textWidth, textContainerHeight)
            lineTo(textWidth, cornerRadius)

            rectF.set(textWidth - 2 * cornerRadius, 0f, textWidth, 2 * cornerRadius)
            arcTo(rectF, 0f, -90f)

            lineTo(cornerRadius, 0f)

            rectF.set(0f, 0f, 2 * cornerRadius, 2 * cornerRadius)
            arcTo(rectF, 270f, -90f)

            this.close()
        }
    }

    private fun drawTriangle(canvas: Canvas) {
       val trianglePath = if (gravity == Gravity.START) { drawStartTriangle() } else drawEndTriangle()

        canvas.drawPath(trianglePath, trianglePaint.apply { this.color = triangleColor })
    }

    private fun drawStartTriangle(): Path {
        return trianglePath.apply {
            moveTo(0f, textContainerHeight)
            lineTo(triangleWidth, textContainerHeight)
            lineTo(triangleWidth, textContainerHeight + triangleHeight)
            close()
        }
    }

    private fun drawEndTriangle(): Path {
        return trianglePath.apply {
            moveTo(textWidth, textContainerHeight)
            lineTo(textWidth - triangleWidth, textContainerHeight)
            lineTo(textWidth - triangleWidth, textContainerHeight + triangleHeight)
            close()
        }
    }

    @SuppressLint("CustomViewStyleable")
    private fun applyTextAppearance(context: Context, resId: Int) {
        context.obtainStyledAttributes(resId, androidx.appcompat.R.styleable.TextAppearance).use { typedArray ->
            textPaint.textSize = typedArray.getDimension(androidx.appcompat.R.styleable.TextAppearance_android_textSize, textPaint.textSize)

            if (typedArray.hasValue(androidx.appcompat.R.styleable.TextAppearance_android_fontFamily) || typedArray.hasValue(androidx.appcompat.R.styleable.TextAppearance_fontFamily)) {
                val fontResId = typedArray.getResourceId(androidx.appcompat.R.styleable.TextAppearance_android_fontFamily, NO_ID)
                    .takeIf { it != NO_ID } ?: typedArray.getResourceId(androidx.appcompat.R.styleable.TextAppearance_fontFamily, R.font.rubik_medium)

                if (!isInEditMode) {
                    textPaint.typeface = context.font(fontResId)
                }
            }

            if (typedArray.hasValue(androidx.appcompat.R.styleable.TextAppearance_android_textStyle)) {
                val textStyle = typedArray.getInt(androidx.appcompat.R.styleable.TextAppearance_android_textStyle, Typeface.NORMAL)
                textPaint.typeface = Typeface.create(textPaint.typeface, textStyle)
            }
        }
    }

    enum class VerticalAlignment {
        Top, Center, Bottom
    }

    fun anchorToView(
        rootParent: ViewGroup,
        targetView: View,
        verticalAlignment: VerticalAlignment = VerticalAlignment.Center,
        offsetX: Int = 0,
        offsetY: Int = 0
    ) = rootParent.post {
        // Get the screen position of the target view
        val targetLocation = IntArray(2)
        targetView.getLocationOnScreen(targetLocation)

        // Get the screen position of the root parent
        val parentLocation = IntArray(2)
        rootParent.getLocationOnScreen(parentLocation)

        // Calculate the position of the target view relative to the root parent
        val relativeX = if (gravity == Gravity.START) {
            (targetLocation[0] - parentLocation[0]).toFloat() - triangleWidth + offsetX
        } else {
            (targetLocation[0] - parentLocation[0]).toFloat() + targetView.width - width + triangleWidth + offsetX
        }

        val relativeY = when (verticalAlignment) {
            VerticalAlignment.Top -> {
                (targetLocation[1] - parentLocation[1]).toFloat() - textContainerHeight + offsetY
            }
            VerticalAlignment.Center -> {
                (targetLocation[1] - parentLocation[1]).toFloat() + offsetY
            }
            VerticalAlignment.Bottom -> {
                (targetLocation[1] - parentLocation[1]).toFloat() + targetView.height + offsetY
            }
        }

        // Set translation to position the RibbonView correctly
        this.translationX = relativeX
        this.translationY = relativeY

        // Detach the RibbonView from any existing parent before adding it to rootParent
        if (this.parent != null) {
            (this.parent as ViewGroup).removeView(this)
        }

        // Add the RibbonView to the rootParent if it's not already added
        if (rootParent.indexOfChild(this) == -1) {
            rootParent.addView(this)
        }
    }
}
