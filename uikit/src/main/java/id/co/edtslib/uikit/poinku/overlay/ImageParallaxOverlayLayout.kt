package id.co.edtslib.uikit.poinku.overlay

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.Dimension
import androidx.core.content.res.use
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import id.co.edtslib.uikit.poinku.R
import id.co.edtslib.uikit.poinku.recyclerView.HorizontalSlidingRecyclerView
import id.co.edtslib.uikit.poinku.utils.color
import id.co.edtslib.uikit.poinku.utils.createGradientDrawable
import id.co.edtslib.uikit.poinku.utils.deviceWidth
import id.co.edtslib.uikit.poinku.utils.dp

class ImageParallaxOverlayLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var overlayImageView: ShapeableImageView? = null
    var imageUrl: String? = null
        set(value) {
            field = value

            if (overlayType != OverlayType.BackgroundOnly) bindImageUrl()
        }

    @Dimension
    var drawableWidth: Float = 0f
    var drawableWidthPercentage: Float = 0.33f

    @Dimension
    var drawableHorizontalMargin: Float = 0.dp
        set(value) {
            field = value

            updateImageViewLayoutParams()
        }

    var overlayType = OverlayType.IllustrationWithBackground
        set(value) {
            field = value

            if (overlayType == OverlayType.BackgroundOnly) {
                initImageView()
            } else {
                if (imageUrl == null) {
                    Log.e(this.javaClass.simpleName, "Img URL shouldn't be null")
                } else {
                    bindImageUrl()
                }
            }
        }

    var gradientColor = GradientColor.Blue
        set(value) {
            field = value

            if (overlayType == OverlayType.BackgroundOnly) {
                createGradientDrawable(
                    value.startColor, value.endColor,
                    value.startX, value.startY,
                    value.endY, value.endX,
                ).let { gradient ->
                    overlayImageView?.background = gradient
                }
            }
        }


    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.ImageOverlayLayout, 0, 0).use {
            drawableWidth = it.getDimension(R.styleable.ImageOverlayLayout_drawableWidth, 0f)
            overlayType = OverlayType.values()[it.getInt(R.styleable.ImageOverlayLayout_overlayType, 1)]
            gradientColor = GradientColor.values()[it.getInt(R.styleable.ImageOverlayLayout_gradientColor, 0)]
        }
    }

    private fun initImageView() {
        if (overlayImageView == null) {
            overlayImageView = ShapeableImageView(context).apply {
                scaleType = ImageView.ScaleType.FIT_XY
                addView(this, 0)
            }
        }

        updateImageViewLayoutParams()
        attachToCarousel()
    }

    fun bindImageUrl(
        imageLoader: (ImageView, Any?) -> Unit = { imageView, url ->
            Glide.with(imageView.context).load(url).into(imageView)
        }
    ) {
        initImageView()
        overlayImageView?.let { imageLoader(it, imageUrl) }
    }

    private fun attachToCarousel() {
        if (childCount > 1) {
            val carouselRecyclerView = getChildAt(1) as? HorizontalSlidingRecyclerView<*, *>
            if (carouselRecyclerView is HorizontalSlidingRecyclerView<*, *>) {
                carouselRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        val linearLayoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                        val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()

                        if (firstVisibleItemPosition == 0) {
                            val firstVisibleView = linearLayoutManager.findViewByPosition(firstVisibleItemPosition)
                            val recyclerViewPaddingStart = recyclerView.paddingStart.toFloat()
                            val maxAlphaDistance = recyclerViewPaddingStart * 2f / 3f

                            val viewLeftPosition = firstVisibleView?.left?.coerceAtLeast(0) ?: 0
                            val offsetFromPadding = recyclerViewPaddingStart - viewLeftPosition
                            overlayImageView?.alpha = viewLeftPosition / maxAlphaDistance

                            if (overlayType == OverlayType.IllustrationWithBackground) {
                                extractDominantColorAndSetBackground()
                            }

                            val translationOffset = -offsetFromPadding / 5f
                            overlayImageView?.translationX = translationOffset
                        } else {
                            val fullyHiddenTranslation = -overlayImageView?.width?.toFloat()!!
                            if (overlayImageView?.translationX != fullyHiddenTranslation) {
                                overlayImageView?.translationX = fullyHiddenTranslation
                            }
                        }
                    }
                })
            }
        }
    }

    private fun extractDominantColorAndSetBackground() {
        overlayImageView?.let { imageView ->
            imageUrl?.let { url ->
                Glide.with(imageView.context)
                    .asBitmap()
                    .load(url)
                    .listener(object : RequestListener<Bitmap> {
                        override fun onLoadFailed(
                            e: GlideException?, model: Any?, target: Target<Bitmap>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Bitmap?, model: Any?, target: Target<Bitmap>?,
                            dataSource: DataSource?, isFirstResource: Boolean
                        ): Boolean {
                            resource?.let { bitmap ->
                                val palette = Palette.from(bitmap).generate()
                                val dominantColor = palette.getDominantColor(context.color(R.color.primary_30))

                                val gradientDrawable = GradientDrawable(
                                    GradientDrawable.Orientation.RIGHT_LEFT,
                                    intArrayOf(dominantColor, getLighterColor(dominantColor))
                                )

                                this@ImageParallaxOverlayLayout.background = gradientDrawable
                            }
                            return false
                        }
                    })
                    .submit()
            }
        }
    }

    private fun getLighterColor(color: Int): Int {
        val alpha = Color.alpha(color)
        val red = (Color.red(color) * 1.2f).toInt().coerceAtMost(255)
        val green = (Color.green(color) * 1.2f).toInt().coerceAtMost(255)
        val blue = (Color.blue(color) * 1.2f).toInt().coerceAtMost(255)

        return Color.argb(alpha, red, green, blue)
    }

    private fun updateImageViewLayoutParams() {
        val carouselView = getChildAt(1) as? HorizontalSlidingRecyclerView<*, *>

        overlayImageView?.let { imageView ->
            val illustrationWidth =
                if (drawableWidth == 0f) { (drawableWidthPercentage * context.deviceWidth).toInt() }
                else { drawableWidth.toInt() }

            val illustrationWidthWithIntrinsicsBounds = illustrationWidth + drawableHorizontalMargin.plus(drawableHorizontalMargin.div(2)).toInt()

            imageView.updateLayoutParams<MarginLayoutParams> {
                setMargins(drawableHorizontalMargin.toInt(),0, 0,0)
            }

            when (overlayType) {
                OverlayType.IllustrationOnly -> {
                    imageView.shapeAppearanceModel = ShapeAppearanceModel.builder()
                        .setAllCornerSizes(8.dp)
                        .build()

                    imageView.updateLayoutParams<ViewGroup.LayoutParams> {
                        width = illustrationWidth

                        carouselView?.apply {
                            setPaddingRelative(illustrationWidthWithIntrinsicsBounds, carouselView.paddingTop, carouselView.paddingEnd, carouselView.paddingBottom)
                            doOnLayout {
                                this@updateLayoutParams.height = carouselView.height.minus(totalVerticalPadding())
                            }

                            scrollBy(-illustrationWidthWithIntrinsicsBounds, 0)
                        }
                    }

                    imageView.updateLayoutParams<LayoutParams> {
                        gravity = android.view.Gravity.CENTER_VERTICAL
                    }
                }

                OverlayType.IllustrationWithBackground -> {
                    imageView.updateLayoutParams<ViewGroup.LayoutParams> {
                        width = LayoutParams.MATCH_PARENT
                    }

                    carouselView?.let { cv ->
                        cv.setPaddingRelative(
                            illustrationWidth,
                            cv.paddingTop,
                            cv.paddingEnd,
                            cv.paddingBottom
                        )

                        cv.scrollBy(-illustrationWidth, 0)
                    }
                }

                OverlayType.BackgroundOnly -> {
                    imageView.updateLayoutParams<ViewGroup.LayoutParams> {
                        width = LayoutParams.MATCH_PARENT
                    }
                }
            }
        }
    }

    // Helper function to calculate total vertical padding
    private fun HorizontalSlidingRecyclerView<*, *>.totalVerticalPadding(): Int {
        return paddingTop + paddingBottom
    }


    enum class OverlayType {
        IllustrationOnly,
        IllustrationWithBackground,
        BackgroundOnly,
    }

    enum class GradientColor(
        val startColor: Int,
        val endColor: Int,
        val startX: Float,
        val endX: Float,
        val startY: Float,
        val endY: Float
    ) {
        Blue(Color.parseColor("#1178D4"), Color.parseColor("#6CB8FC"), 40f, 12f, 120f, 12f),
        Yellow(Color.parseColor("#FAC714"), Color.parseColor("#FA9E14"), 40f, 12f, 120f, 12f),
        Black(Color.parseColor("#6A6D77"), Color.parseColor("#171924"), 40f, 12f, 120f, 12f),
        Red(Color.parseColor("#D41111"), Color.parseColor("#FC6C6C"), 40f, 12f, 120f, 12f)
    }
}
