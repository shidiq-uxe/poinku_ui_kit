package id.co.edtslib.uikit.poinku.progressindicator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.core.animation.doOnEnd
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.google.android.material.progressindicator.LinearProgressIndicator
import id.co.edtslib.uikit.poinku.R
import id.co.edtslib.uikit.poinku.badge.Badge
import id.co.edtslib.uikit.poinku.databinding.ViewProgressTrackerGroupBinding
import id.co.edtslib.uikit.poinku.utils.color
import id.co.edtslib.uikit.poinku.utils.disableClippingUpToRoot
import id.co.edtslib.uikit.poinku.utils.inflater
import kotlinx.coroutines.delay

// Todo : Use Progress Tracker instead of creating its layout manually
class RegisterProgressTrackerGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(ContextThemeWrapper(context, R.style.Theme_EDTS_UIKit), attrs, defStyleAttr) {

    private val binding = ViewProgressTrackerGroupBinding.inflate(this.context.inflater, this, true)

    var tabItems: Triple<String, String, String> = Triple("Isi Data Diri", "Verifikasi", "Buat Pin")
        set(value) {
            field = value
            value.let {
                binding.tvProgressTracker1.text = it.first
                binding.tvProgressTracker2.text = it.second
                binding.tvProgressTracker3.text = it.third
            }
        }

    private val badges = listOf(binding.badge1, binding.badge2, binding.badge3)
    private val trackerTextViews = listOf(binding.tvProgressTracker1, binding.tvProgressTracker2, binding.tvProgressTracker3)
    private val connectors = listOf(binding.track1, binding.track2)

    @StyleRes
    var badgeTextAppearance = R.style.TextAppearance_Rubik_B3_Medium
        set(value) {
            field = value
            badges.forEach {
                it.badgeTextAppearance = value
            }
        }

    @StyleRes
    var trackerTextAppearance = R.style.TextAppearance_Rubik_B4_Medium
        set(value) {
            field = value
            trackerTextViews.forEach {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    it.setTextAppearance(value)
                }
            }
        }

    private var currentStep = 0

    init {
        badges.forEach {
            it.badgeColor = context.color(R.color.grey_40)
        }
    }

    fun selectStep(index: Int) {
        currentStep = index

        onStepSelected(
            badge = binding.badge1,
            textView = binding.tvProgressTracker1,
            track = null,
            isSelected = index == 0,
            isCompleted = index > 0,
        )

        onStepSelected(
            badge = binding.badge2,
            textView = binding.tvProgressTracker2,
            track = binding.track1,
            isSelected = index == 1,
            isCompleted = index > 1,
        )

        onStepSelected(
            badge = binding.badge3,
            textView = binding.tvProgressTracker3,
            track = binding.track2,
            isSelected = index == 2,
            isCompleted = false,
        )
    }

    private fun onStepSelected(
        badge: Badge,
        textView: TextView,
        track: LinearProgressIndicator?,
        isSelected: Boolean,
        isCompleted: Boolean,
    ) {
        val color = when {
            isCompleted -> context.color(R.color.primary_30)
            isSelected -> context.color(R.color.primary_30)
            else -> context.color(R.color.grey_40)
        }

        badge.badgeColor = color
        textView.setTextColor(color)

        if (track == null && !isCompleted) {
            Handler(Looper.getMainLooper()).postDelayed({
                animateStepSelection(badge.parent as ViewGroup) }
                , 750)
        }

        track?.let { progressIndicator ->
            val trackColor = context.color(R.color.grey_40)
            val indicatorColor = context.color(R.color.primary_30)

            progressIndicator.trackColor = trackColor
            progressIndicator.setIndicatorColor(indicatorColor)

            if (isCompleted) {
                connectors.forEach {
                    if (it == track){
                        it.progress = 100
                    }
                }
            } else if (isSelected) {
                (badge.parent as ViewGroup).disableClippingUpToRoot()
                animateConnectorProgress(progressIndicator, 100) {
                    animateStepSelection(badge.parent as ViewGroup)
                }
            }
        }
    }

    fun animateStepSelection(view: View) {
        view.let { view ->
            val targetScale = 1.15f
            view.scaleX = targetScale
            view.scaleY = targetScale

            val spring = SpringForce(1f).apply {
                stiffness = 75F
                dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
            }

            SpringAnimation(view, DynamicAnimation.SCALE_X).setSpring(spring).setStartVelocity(1.5f).start()
            SpringAnimation(view, DynamicAnimation.SCALE_Y).setSpring(spring).setStartVelocity(1.5f).start()
        }
    }

    fun animateConnectorProgress(connector: LinearProgressIndicator?, targetProgress: Int, onComplete: (() -> Unit)? = null) {
        connector?.let { indicator ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val animator = ValueAnimator.ofInt(0, targetProgress).apply {
                    startDelay = 750
                    duration = 500
                    interpolator = AccelerateDecelerateInterpolator()

                    addUpdateListener { animation ->
                        val animatedValue = animation.animatedValue as Int
                        val clamp = animatedValue.coerceIn(0, targetProgress)
                        indicator.progress = clamp
                    }

                    doOnEnd {
                        onComplete?.invoke()
                    }
                }
                animator.start()
            }
        }
    }

    fun getCurrentStep(): Int = currentStep

    fun goToNextStep() {
        if (currentStep < 2) {
            selectStep(currentStep + 1)
        }
    }

    fun goToPreviousStep() {
        if (currentStep > 0) {
            selectStep(currentStep - 1)
        }
    }
}