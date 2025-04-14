package id.co.edtslib.uikit.poinku.ribbon

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Trace
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.StyleRes
import androidx.core.content.res.use
import androidx.core.view.doOnLayout
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.ShapeAppearancePathProvider
import id.co.edtslib.uikit.poinku.R
import id.co.edtslib.uikit.poinku.utils.color
import id.co.edtslib.uikit.poinku.utils.dimen
import id.co.edtslib.uikit.poinku.utils.font
import id.co.edtslib.uikit.poinku.utils.dp
import id.co.edtslib.uikit.poinku.utils.sp
import androidx.core.graphics.createBitmap
import com.google.android.material.bottomappbar.BottomAppBarTopEdgeTreatment
import com.google.android.material.shape.MarkerEdgeTreatment
import com.google.android.material.shape.OffsetEdgeTreatment
import com.google.android.material.shape.TriangleEdgeTreatment
import id.co.edtslib.uikit.poinku.coachmark.RoundTipTriangleEdgeTreatment

class Ribbon @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    @ColorInt
    var triangleColor = context.color(R.color.primary_50)
        set(value) {
            field = value
            markDirty()
        }

    @ColorInt
    var containerColor = context.color(R.color.primary_30)
        set(value) {
            field = value
            markDirty()
        }

    @ColorInt
    var containerStartColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            updateGradientShader()
            markDirty()
        }

    @ColorInt
    var containerEndColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            updateGradientShader()
            markDirty()
        }

    private var gradientShader: Shader? = null

    @ColorInt
    var textColor = context.color(R.color.white)
        set(value) {
            field = value
            textPaint.color = value
            markDirty()
        }

    // Gravity for positioning the triangle and container
    var gravity: Gravity = Gravity.START
        set(value) {
            field = value
            requestLayout()
            markDirty()
        }

    @Dimension
    var cornerRadius: Float = context.dimen(R.dimen.xxxs)

    @Dimension
    var textVerticalPadding = context.dimen(R.dimen.dimen_0)
    @Dimension
    var textHorizontalPadding = context.dimen(R.dimen.xxxs)

    @StyleRes
    var textAppearanceRes = R.style.TextAppearance_Rubik_B3_Medium
        set(value) {
            field = value
            requestLayout()
            markDirty()
        }

    private val trianglePaint = Paint().apply {
        color = triangleColor
        style = Paint.Style.FILL
        flags = Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG
    }

    private val containerPaint = Paint().apply {
        style = Paint.Style.FILL
        flags = Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG
    }

    private val textPaint = Paint().apply {
        color = textColor
        textSize = 14.dp
        textAlign = Paint.Align.CENTER
    }

    enum class Gravity {
        START,
        END
    }

    var ribbonText: String? = ""
        set(value) {
            field = value
            updateTextMeasurements()
            requestLayout()
            markDirty()
        }

    private var textWidth: Float = 0f
    private var textHeight: Float = 0f
    private var textLineHeight: Float = 0f

    var triangleWidth = 6f * resources.displayMetrics.density // Width of the triangle
        private set
    var triangleHeight = 8f * resources.displayMetrics.density // Height of the triangle
        private set
    var textContainerHeight = textWidth + textVerticalPadding
        private set

    // Preallocate reusable objects
    private val containerPath = Path()
    private val trianglePath = Path()
    private val rectF = RectF()

    private val startContainerShapeAppearance = ShapeAppearanceModel.Builder()
        .setTopLeftCorner(CornerFamily.ROUNDED, cornerRadius)
        .setTopRightCorner(CornerFamily.ROUNDED, cornerRadius)
        .setBottomRightCorner(CornerFamily.ROUNDED, cornerRadius)
        .build()

    private val endContainerShapeAppearance = ShapeAppearanceModel.Builder()
        .setTopLeftCorner(CornerFamily.ROUNDED, cornerRadius)
        .setTopRightCorner(CornerFamily.ROUNDED, cornerRadius)
        .setBottomLeftCorner(CornerFamily.ROUNDED, cornerRadius)
        .build()

    private val materialPathProvider = ShapeAppearancePathProvider()

    // Bitmap caching properties
    private var cachedBitmap: Bitmap? = null
    // Reuse the same Canvas to draw into the bitmap
    private val cachedCanvas = Canvas()
    private var needsBitmapUpdate = true

    private var prevTriangleColor: Int? = null
    private var prevContainerColor: Int? = null
    private var prevContainerStartColor: Int? = null
    private var prevContainerEndColor: Int? = null
    private var prevTextColor: Int? = null
    private var prevGravity: Gravity? = null
    private var prevRibbonText: String? = null
    private var prevTextAppearanceRes: Int? = null


    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.Ribbon, 0, 0).use {
            val newTriangleColor = it.getColor(R.styleable.Ribbon_triangleColor, triangleColor)
            val newContainerColor = it.getColor(R.styleable.Ribbon_containerColor, containerColor)
            val newContainerStartColor = it.getColor(R.styleable.Ribbon_containerStartColor, containerStartColor)
            val newContainerEndColor = it.getColor(R.styleable.Ribbon_containerEndColor, containerEndColor)
            val newTextColor = it.getColor(R.styleable.Ribbon_ribbonTextColor, textColor)
            val newGravity = Gravity.values()[it.getInt(R.styleable.Ribbon_gravity, gravity.ordinal)]
            val newRibbonText = it.getString(R.styleable.Ribbon_text) ?: ribbonText
            val newTextAppearanceRes = it.getResourceId(R.styleable.Ribbon_textAppearance, textAppearanceRes)

            // Apply only if changed
            if (newTriangleColor != prevTriangleColor) {
                triangleColor = newTriangleColor
                prevTriangleColor = newTriangleColor
            }
            if (newContainerColor != prevContainerColor) {
                containerColor = newContainerColor
                prevContainerColor = newContainerColor
            }
            if (newContainerStartColor != prevContainerStartColor) {
                containerStartColor = newContainerStartColor
                prevContainerStartColor = newContainerStartColor
            }
            if (newContainerEndColor != prevContainerEndColor) {
                containerEndColor = newContainerEndColor
                prevContainerEndColor = newContainerEndColor
            }
            if (newTextColor != prevTextColor) {
                textColor = newTextColor
                prevTextColor = newTextColor
            }
            if (newGravity != prevGravity) {
                gravity = newGravity
                prevGravity = newGravity
            }
            if (newRibbonText != prevRibbonText) {
                ribbonText = newRibbonText
                prevRibbonText = newRibbonText
            }
            if (newTextAppearanceRes != prevTextAppearanceRes) {
                textAppearanceRes = newTextAppearanceRes
                prevTextAppearanceRes = newTextAppearanceRes
                if (textAppearanceRes != NO_ID) {
                    applyTextAppearance(context, textAppearanceRes)
                }
            }
        }

        // outlineProvider = RibbonOutlineProvider(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val totalWidth = textWidth
        val totalHeight = triangleWidth + textContainerHeight + 2.dp

        setMeasuredDimension(
            resolveSize(totalWidth.toInt(), widthMeasureSpec),
            resolveSize(totalHeight.toInt(), heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateGradientShader()
        // When size changes, allocate a new bitmap if needed and update our cached canvas.
        if (w > 0 && h > 0) {
            if (cachedBitmap == null || cachedBitmap!!.width != w || cachedBitmap!!.height != h) {
                cachedBitmap = createBitmap(w, h)
            }
            cachedCanvas.setBitmap(cachedBitmap)
            needsBitmapUpdate = true
        }
    }

    override fun onDraw(canvas: Canvas) {
        // Update the bitmap cache if needed
        if (needsBitmapUpdate && width > 0 && height > 0) {
            // Instead of allocating a new Canvas, reuse the preallocated one.
            cachedCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            drawContainer(cachedCanvas)
            drawText(cachedCanvas)
            drawTriangle(cachedCanvas)
            needsBitmapUpdate = false
        }
        cachedBitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }
    }

    private fun drawText(canvas: Canvas) {
        val x = textWidth / 2
        val y = (textContainerHeight + textHeight) / 2 - textPaint.fontMetrics.descent
        ribbonText?.let { canvas.drawText(it, x, y, textPaint) }
    }

    private fun drawContainer(canvas: Canvas) {
        val containerPath = if (gravity == Gravity.START) drawStartContainer() else drawEndContainer()

        if (gradientShader != null) {
            containerPaint.shader = gradientShader
        } else {
            containerPaint.shader = null
            containerPaint.color = containerColor
        }

        canvas.drawPath(containerPath, containerPaint)
    }

    internal fun drawStartContainer(): Path {
        containerPath.reset()
        materialPathProvider.calculatePath(
            startContainerShapeAppearance,
            1f,
            rectF,
            containerPath
        )
        containerPath.close()
        return containerPath
    }

    internal fun drawEndContainer(): Path {
        containerPath.reset()
        materialPathProvider.calculatePath(
            endContainerShapeAppearance,
            1f,
            rectF,
            containerPath
        )
        containerPath.close()
        return containerPath
    }

    private fun drawTriangle(canvas: Canvas) {
        val trianglePath = if (gravity == Gravity.START) drawStartTriangle() else drawEndTriangle()
        trianglePaint.color = triangleColor
        canvas.drawPath(trianglePath, trianglePaint)
    }

    private fun drawStartTriangle(): Path {
        trianglePath.reset()
        trianglePath.moveTo(0f, textContainerHeight)
        trianglePath.lineTo(triangleWidth, textContainerHeight)
        trianglePath.lineTo(triangleWidth, textContainerHeight + triangleHeight)
        trianglePath.close()
        return trianglePath
    }

    private fun drawEndTriangle(): Path {
        trianglePath.reset()
        trianglePath.moveTo(textWidth, textContainerHeight)
        trianglePath.lineTo(textWidth - triangleWidth, textContainerHeight)
        trianglePath.lineTo(textWidth - triangleWidth, textContainerHeight + triangleHeight)
        trianglePath.close()
        return trianglePath
    }

    @SuppressLint("CustomViewStyleable", "ResourceType")
    private fun applyTextAppearance(context: Context, resId: Int) {
        context.obtainStyledAttributes(resId, androidx.appcompat.R.styleable.TextAppearance).use { typedArray ->
            textPaint.textSize = typedArray.getDimension(
                androidx.appcompat.R.styleable.TextAppearance_android_textSize,
                textPaint.textSize
            )
            if (typedArray.hasValue(androidx.appcompat.R.styleable.TextAppearance_android_fontFamily) ||
                typedArray.hasValue(androidx.appcompat.R.styleable.TextAppearance_fontFamily)
            ) {
                val fontResId = typedArray.getResourceId(
                    androidx.appcompat.R.styleable.TextAppearance_android_fontFamily,
                    NO_ID
                ).takeIf { it != NO_ID } ?: typedArray.getResourceId(
                    androidx.appcompat.R.styleable.TextAppearance_fontFamily,
                    R.font.rubik_medium
                )
                if (!isInEditMode) {
                    textPaint.typeface = context.font(fontResId)
                }
            }
            if (typedArray.hasValue(androidx.appcompat.R.styleable.TextAppearance_android_textStyle)) {
                val textStyle = typedArray.getInt(
                    androidx.appcompat.R.styleable.TextAppearance_android_textStyle,
                    Typeface.NORMAL
                )
                textPaint.typeface = Typeface.create(textPaint.typeface, textStyle)
            }
        }
        context.obtainStyledAttributes(resId, intArrayOf(androidx.appcompat.R.attr.lineHeight, android.R.attr.lineHeight))
            .use { lineHeightArray ->
                if (lineHeightArray.hasValue(0) || lineHeightArray.hasValue(1)) {
                    textLineHeight = lineHeightArray.getDimension(0, 0f)
                }
            }
    }

    enum class VerticalAlignment {
        Top, Center, Bottom
    }

    fun anchorToView(
        targetView: View,
        verticalAlignment: VerticalAlignment = VerticalAlignment.Center,
        offsetX: Int = 0,
        offsetY: Int = 0
    ) {
        // Compute the relative X based on the target view's local coordinates.
        // targetView.left/right are relative to the parent's coordinate system.
        val relativeX = if (gravity == Gravity.START) {
            targetView.left.toFloat() - triangleWidth + offsetX
        } else {
            targetView.right.toFloat() - width + triangleWidth + offsetX
        }

        // Compute the relative Y based on the target view's local coordinates.
        val relativeY = when (verticalAlignment) {
            VerticalAlignment.Top -> targetView.top.toFloat() - textContainerHeight + offsetY
            VerticalAlignment.Center -> targetView.top.toFloat() + (targetView.height - height) / 2f + offsetY
            VerticalAlignment.Bottom -> targetView.bottom.toFloat() - height + offsetY
        }

        // Simply update translation properties.
        this.x = relativeX
        this.y = relativeY
    }


    private fun updateGradientShader() {
        gradientShader = if (containerStartColor != Color.TRANSPARENT && containerEndColor != Color.TRANSPARENT) {
            LinearGradient(
                0f, 0f,
                width.toFloat(), 0f,
                containerStartColor, containerEndColor,
                Shader.TileMode.CLAMP
            )
        } else {
            null
        }
    }

    @SuppressLint("ResourceType")
    private fun updateTextMeasurements() {
        textWidth = textPaint.measureText(ribbonText) + textHorizontalPadding.dp
        textHeight = textPaint.fontMetrics.descent - textPaint.fontMetrics.ascent
        textContainerHeight = textHeight + textLineHeight.sp + textVerticalPadding
        rectF.set(0f, 0f, textWidth, textContainerHeight)
    }

    /**
     * Marks the cached bitmap as dirty so it will be recreated on the next draw.
     */
    private fun markDirty() {
        needsBitmapUpdate = true
        invalidate()
    }
}


