package id.co.edtslib.uikit.poinku.boarding

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.FrameLayout
import androidx.core.content.res.use
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import id.co.edtslib.uikit.poinku.R
import id.co.edtslib.uikit.poinku.boarding.adapter.BoardingAdapter
import id.co.edtslib.uikit.poinku.databinding.ViewBoardingPagerBinding
import id.co.edtslib.uikit.poinku.indicator.IndicatorDelegate
import id.co.edtslib.uikit.poinku.indicator.IndicatorOptions
import id.co.edtslib.uikit.poinku.indicator.IndicatorSlideMode
import id.co.edtslib.uikit.poinku.indicator.IndicatorStyle
import id.co.edtslib.uikit.poinku.utils.dimen
import id.co.edtslib.uikit.poinku.utils.horizontalBias
import id.co.edtslib.uikit.poinku.utils.marginEnd
import id.co.edtslib.uikit.poinku.utils.marginHorizontal
import id.co.edtslib.uikit.poinku.utils.marginStart
import id.co.edtslib.uikit.poinku.utils.setCurrentItem
import id.co.edtslib.uikit.poinku.utils.transformer.ScalePageTransformer
import kotlin.math.abs

class BoardingPagerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val adapter = BoardingAdapter()
    var delegate: BoardingPageListener? = null

    private val binding: ViewBoardingPagerBinding =
        ViewBoardingPagerBinding.inflate(LayoutInflater.from(context), this, true)

    // Per Second Integer
    var autoScrollInterval = 0
    private var runnable: Runnable? = null

    var canBackOnFirstPosition = true
    var circular = true
        set(value) {
            field = value
        }

    val root = binding.root
    val indicatorView = binding.indicatorView
    val viewPager = binding.vpContent

    var contentAlignment: ContentAlignment = ContentAlignment.Center()
        set(value) {
            field = value
        }

    var indicatorAlignment: IndicatorAlignment = IndicatorAlignment.Center()
        set(value) {
            val parent = binding.root

            field = value
            when (value) {
                is IndicatorAlignment.Center -> {
                    binding.indicatorView.horizontalBias(0.5f)

                    if (value.endMargin > 0) {
                        binding.indicatorView.marginStart(parent, value.endMargin)
                    }
                    if (value.startMargin > 0) {
                        binding.indicatorView.marginEnd(parent, value.startMargin)
                    }
                }
                is IndicatorAlignment.End -> {
                    binding.indicatorView.horizontalBias(1f)

                    if (value.horizontalMargin > 0) {
                        binding.indicatorView.marginHorizontal(parent, value.horizontalMargin)
                    }
                }
                is IndicatorAlignment.Start -> {
                    binding.indicatorView.horizontalBias(0f)

                    if (value.horizontalMargin > 0) {
                        binding.indicatorView.marginHorizontal(parent, value.horizontalMargin)
                    }
                }
                else -> return
            }
        }

    var items: List<Boarding> = emptyList()
        set(value) {
            field = value
            if (value.isNotEmpty()) {

                indicatorView.setPageSize(value.size)
                adapter.setItems(value)
                viewPager.setCurrentItem(adapter.getStartPosition(), false)
                changeSlidingContentDescription(0)

                if (autoScrollInterval > 0) {
                    startAutoScroll()
                }

            } else {
                removeAutoScroll()
            }
        }

    init {
        init(attrs)
    }

    private fun startAutoScroll() {
        removeAutoScroll()

        if (adapter.getRealCount() <= 1) return

        runnable = Runnable {
            val currentItem = viewPager.currentItem
            val nextItem = currentItem + 1
            viewPager.setCurrentItem(nextItem, 500L)

            viewPager.postDelayed(runnable!!, autoScrollInterval.toLong())
        }

        viewPager.postDelayed(runnable!!, autoScrollInterval.toLong())
    }

    private fun removeAutoScroll() {
        runnable?.let {
            viewPager.removeCallbacks(it)
            runnable = null
        }
    }

    private fun init(attrs: AttributeSet?) {
        setup()

        if (attrs != null) {
            context.theme.obtainStyledAttributes(attrs, R.styleable.BoardingPagerView, 0, 0).use {
                val indicatorGap = it.getDimension(
                    R.styleable.BoardingPagerView_indicator_gap,
                    resources.getDimensionPixelSize(R.dimen.dimen_10).toFloat()
                )

                autoScrollInterval = it.getInt(R.styleable.BoardingPagerView_autoScrollInterval, 0)
                circular = it.getBoolean(R.styleable.BoardingPagerView_circular, circular)
                canBackOnFirstPosition = it.getBoolean(R.styleable.BoardingPagerView_canBackOnFirstPosition, canBackOnFirstPosition)

                val iAlignment = it.getInt(R.styleable.BoardingPagerView_indicator_alignment, 1)
                indicatorAlignment = IndicatorAlignment.Companion.entries[iAlignment]

                val cAlignment = it.getInt(R.styleable.BoardingPagerView_content_alignment, 1)

                val verticalOffsetPercentage = it.getFloat(R.styleable.BoardingPagerView_content_vertical_offset_percentage, contentAlignment.verticalBias)
                contentAlignment = ContentAlignment.Companion.entries[cAlignment].apply {
                    verticalBias = verticalOffsetPercentage
                }

                indicatorView.setIndicatorGap(indicatorGap)
            }
        }
    }

    val titleInterpolator = DecelerateInterpolator(1.5f)
    val descInterpolator = AccelerateInterpolator(1.2f)
    val delayFactor = 0.2f

    private fun delayedInterpolation(
        interpolator: Interpolator,
        rawOffset: Float,
        delay: Float,
        scale: Float = 1f
    ): Float {
        val adjusted = ((rawOffset - delay) / (1f - delay)).coerceIn(0f, 1f)
        return interpolator.getInterpolation(adjusted) * scale
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setup() {
        viewPager.adapter = adapter

        val recyclerView = viewPager.getChildAt(0) as? RecyclerView
        recyclerView?.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> removeAutoScroll()
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> startAutoScroll()
            }
            false
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 0 || position == adapter.itemCount.minus(1)) return

                val realPosition = adapter.getRealPosition(position)
                delegate?.onPageSelected(realPosition, position)
                indicatorView.onPageSelected(realPosition)
            }

            override fun onPageScrolled(position: Int, offset: Float, offsetPx: Int) {
                super.onPageScrolled(position, offset, offsetPx)
                animateTextsDuringPageScroll(position, offset)

                if (position == 0 || position == adapter.itemCount.minus(1)) return

                val realPosition = adapter.getRealPosition(position)
                delegate?.onPageScrolled(realPosition, offset, offsetPx, position)
                indicatorView.onPageScrolled(realPosition, offset, offsetPx)
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    val position = viewPager.currentItem
                    val lastIndex = adapter.itemCount - 1

                    when (position) {
                        0 -> viewPager.setCurrentItem(lastIndex - 1, false)
                        lastIndex -> viewPager.setCurrentItem(1, false)
                    }
                }
                val realPosition = adapter.getRealPosition(viewPager.currentItem)
                changeSlidingContentDescription(realPosition)
                indicatorView.onPageScrollStateChanged(state)
            }
        })

        binding.btnRegister.setOnClickListener { delegate?.onRegisterButtonClicked(it) }
        binding.btnLogin.setOnClickListener { delegate?.onLoginButtonClicked(it) }
    }

    private fun animateTextsDuringPageScroll(position: Int, positionOffset: Float) {
        if (positionOffset <= 0.01f) return

        val nextIndex = position + 1
        val nextReal = adapter.getRealPosition(nextIndex)

        val titleEnterProgress = titleInterpolator.getInterpolation(positionOffset)
        val descEnterProgress = delayedInterpolation(descInterpolator, positionOffset, delayFactor)

        if (nextReal < items.size) {
            binding.tvBoardingTitle.apply {
                text = items[nextReal].title
                translationY = 100 * (1 - titleEnterProgress)
                alpha = titleEnterProgress
            }
            binding.tvBoardingDescription.apply {
                text = items[nextReal].description
                translationY = 100 * (1 - descEnterProgress)
                alpha = descEnterProgress
            }
        }
    }

    private fun changeSlidingContentDescription(
        position: Int,
    ) {
        binding.tvBoardingTitle.alpha = 1f
        binding.tvBoardingDescription.alpha = 1f
        binding.tvBoardingTitle.translationY = 0f
        binding.tvBoardingDescription.translationY = 0f

        binding.tvBoardingTitle.text = items[position].title
        binding.tvBoardingDescription.text = items[position].description
    }
}