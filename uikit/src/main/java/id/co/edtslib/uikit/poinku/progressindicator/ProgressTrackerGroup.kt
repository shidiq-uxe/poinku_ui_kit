package id.co.edtslib.uikit.poinku.progressindicator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.core.util.Pools
import androidx.core.view.size
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FloatPropertyCompat
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.google.android.material.progressindicator.LinearProgressIndicator
import id.co.edtslib.uikit.poinku.R
import id.co.edtslib.uikit.poinku.utils.color
import id.co.edtslib.uikit.poinku.utils.deviceWidth
import id.co.edtslib.uikit.poinku.utils.dp
import java.lang.ref.WeakReference

// For Now, let's set expectation for this component to use MODE_FIXED to catch Sprint time
class ProgressTrackerGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(ContextThemeWrapper(context, R.style.Theme_EDTS_UIKit), attrs, defStyleAttr) {

    // Create main container layout
    private val containerLayout: LinearLayout = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        gravity = Gravity.CENTER
    }
    private val progressTrackers = mutableListOf<WeakReference<ProgressTracker>>()

    // Object pool for ProgressTracker instances to reduce garbage collection
    private val progressTrackerPool: Pools.SynchronizedPool<ProgressTracker> =
        Pools.SynchronizedPool(MAX_POOL_SIZE)

    // Object pool for LinearProgressIndicator instances
    private val progressIndicatorPool: Pools.SynchronizedPool<LinearProgressIndicator> =
        Pools.SynchronizedPool(MAX_POOL_SIZE)

    init {
        addView(containerLayout)
    }

    @ProgressTrackerGroupScrollMode
    var scrollMode = MODE_FIXED
        set(value) {
            field = value
            requestLayout()
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return isScrollable() && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return isScrollable() && super.onInterceptTouchEvent(ev)
    }

    private fun isScrollable() = scrollMode == MODE_SCROLLABLE || scrollMode == MODE_AUTO

    /**
     * Adds a ProgressTracker to the group
     * @param progressTracker The ProgressTracker instance to add
     */
    private fun addProgressTracker(
        progressTracker: ProgressTracker,
        view: View,
        showConnector: Boolean = true,
    ) {
        progressTrackers.add(WeakReference(progressTracker))

        progressTracker.apply {
            this.addStep(view, showConnector)
        }
        containerLayout.addView(progressTracker)

        // Update last item status
        updateLastItemStatus()
    }

    /**
     * Creates and adds a new ProgressTracker to the group using object pooling
     * @return The created ProgressTracker instance
     */
    fun addStep(
        view: View,
        showConnector: Boolean = true
    ): ProgressTracker {
        // Try to get from pool first
        val progressTracker = progressTrackerPool.acquire() ?: ProgressTracker(context)

        // Reset the tracker to default state
        progressTracker.reset()

        addProgressTracker(progressTracker, view, showConnector)
        return progressTracker
    }

    /**
     * Removes a ProgressTracker from the group and returns it to the pool
     * @param progressTracker The ProgressTracker to remove
     */
    fun removeProgressTracker(progressTracker: ProgressTracker) {
        // Remove from weak reference list
        progressTrackers.removeAll { it.get() == progressTracker || it.get() == null }

        containerLayout.removeView(progressTracker)
        updateLastItemStatus()

        // Return to pool for reuse
        progressTracker.reset()
        progressTrackerPool.release(progressTracker)
    }

    /**
     * Gets all active ProgressTracker instances in the group
     * @return List of active ProgressTracker instances
     */
    fun getProgressTrackers(): List<ProgressTracker> {
        // Clean up null references and return active instances
        val activeTrackers = mutableListOf<ProgressTracker>()
        val iterator = progressTrackers.iterator()

        while (iterator.hasNext()) {
            val weakRef = iterator.next()
            val tracker = weakRef.get()
            if (tracker != null) {
                activeTrackers.add(tracker)
            } else {
                // Remove null references
                iterator.remove()
            }
        }

        return activeTrackers
    }

    /**
     * Gets a specific ProgressTracker by index
     * @param index The index of the ProgressTracker
     * @return ProgressTracker instance or null if index is invalid
     */
    fun getProgressTracker(index: Int): ProgressTracker? {
        val activeTrackers = getProgressTrackers()
        return if (index in 0 until activeTrackers.size) {
            activeTrackers[index]
        } else null
    }

    /**
     * Gets the count of active ProgressTracker instances
     * @return Number of active ProgressTracker instances
     */
    fun getProgressTrackersCount(): Int = getProgressTrackers().size

    /**
     * Clears all ProgressTracker instances from the group and returns them to pool
     */
    fun clearProgressTrackers() {
        val activeTrackers = getProgressTrackers()

        // Return all trackers to pool
        activeTrackers.forEach { tracker ->
            tracker.reset()
            progressTrackerPool.release(tracker)
        }

        progressTrackers.clear()
        containerLayout.removeAllViews()
    }

    /**
     * Updates the last item status for all progress trackers
     */
    private fun updateLastItemStatus() {
        val activeTrackers = getProgressTrackers()
        activeTrackers.forEachIndexed { index, tracker ->
            tracker.setIsLastItem(index == activeTrackers.size - 1)
        }
    }

    /**
     * Selects a specific step by index
     * @param stepIndex The index of the step to select (0-based)
     * @param animate Whether to animate the transition or set immediately
     */
    fun selectStep(stepIndex: Int, animate: Boolean = true) {
        val activeTrackers = getProgressTrackers()

        if (stepIndex < 0 || stepIndex >= activeTrackers.size) {
            return
        }

        if (!animate) {
            // Set immediately without animations
            activeTrackers.forEachIndexed { index, tracker ->
                when {
                    index < stepIndex -> tracker.setStepState(STATE_COMPLETED)
                    index == stepIndex -> tracker.setStepState(STATE_ACTIVE)
                    index > stepIndex -> tracker.setStepState(STATE_INACTIVE)
                }
            }
            return
        }

        // Find current active step for direction detection
        val currentActiveIndex = activeTrackers.indexOfFirst { it.getStepState() == STATE_ACTIVE }

        when {
            currentActiveIndex == -1 || stepIndex > currentActiveIndex -> {
                // Moving forward or no active step
                animateForward(activeTrackers, stepIndex)
            }
            stepIndex < currentActiveIndex -> {
                // Moving backward
                animateBackward(activeTrackers, currentActiveIndex, stepIndex)
            }
            else -> {
                // Same step, just bounce
                activeTrackers[stepIndex].animateStepSelection()
            }
        }
    }

    /**
     * Handles forward animation sequence
     * @param trackers List of all progress trackers
     * @param targetIndex Target step index
     */
    private fun animateForward(trackers: List<ProgressTracker>, targetIndex: Int) {
        // Reset steps after target to inactive
        for (i in targetIndex + 1 until trackers.size) {
            trackers[i].setStepState(STATE_INACTIVE)
        }

        // Start connector animation sequence
        animateConnectorsSequentially(trackers, 0, targetIndex) {
            // After all connectors are done, bounce only the target step
            trackers[targetIndex].animateStepSelection()
        }
    }

    /**
     * Handles backward animation sequence
     * @param trackers List of all progress trackers
     * @param currentIndex Current active step index
     * @param targetIndex Target step index
     */
    private fun animateBackward(
        trackers: List<ProgressTracker>,
        currentIndex: Int,
        targetIndex: Int
    ) {
        // Animate connectors backward from (target+1) to current
        animateConnectorsBackward(trackers, targetIndex + 1, currentIndex) {
            // Set target step as active and bounce
            trackers[targetIndex].setStepState(STATE_ACTIVE)
            trackers[targetIndex].animateStepSelection()
        }
    }

    /**
     * Animates connectors sequentially forward
     * @param trackers List of progress trackers
     * @param currentIndex Current connector being animated
     * @param targetIndex Final target step
     * @param onComplete Callback when all connectors finish
     */
    private fun animateConnectorsSequentially(
        trackers: List<ProgressTracker>,
        currentIndex: Int,
        targetIndex: Int,
        onComplete: () -> Unit
    ) {
        if (currentIndex >= targetIndex) {
            // Set target step state and call completion
            trackers[targetIndex].setStepState(STATE_ACTIVE)
            onComplete()
            return
        }

        val currentTracker = trackers[currentIndex]
        currentTracker.setStepState(STATE_COMPLETED)

        // Animate connector to 100%
        currentTracker.animateConnectorProgress(100) {
            // Continue to next connector
            animateConnectorsSequentially(trackers, currentIndex + 1, targetIndex, onComplete)
        }
    }

    /**
     * Animates connectors sequentially backward
     * @param trackers List of progress trackers
     * @param currentIndex Current connector being animated
     * @param endIndex Last connector to animate
     * @param onComplete Callback when all connectors finish
     */
    private fun animateConnectorsBackward(
        trackers: List<ProgressTracker>,
        currentIndex: Int,
        endIndex: Int,
        onComplete: () -> Unit
    ) {
        if (currentIndex > endIndex) {
            onComplete()
            return
        }

        if (currentIndex < trackers.size) {
            val currentTracker = trackers[currentIndex]

            // Animate connector to 0%
            currentTracker.animateConnectorProgress(0) {
                // Set step as inactive after connector animates
                currentTracker.setStepState(STATE_INACTIVE)

                // Continue to next connector
                animateConnectorsBackward(trackers, currentIndex + 1, endIndex, onComplete)
            }
        } else {
            onComplete()
        }
    }

    /**
     * Animates bounce effects sequentially
     * @param trackers List of progress trackers
     * @param currentIndex Current step being bounced
     * @param targetIndex Final target step
     */
    private fun animateBounceSequentially(
        trackers: List<ProgressTracker>,
        currentIndex: Int,
        targetIndex: Int
    ) {
        if (currentIndex > targetIndex) return

        val currentTracker = trackers[currentIndex]

        // Bounce the current step
        currentTracker.animateStepSelection()

        // Continue to next step after bounce completes
        if (currentIndex < targetIndex) {
            postDelayed({
                animateBounceSequentially(trackers, currentIndex + 1, targetIndex)
            }, 300) // Delay for bounce animation to complete
        }
    }

    /**
     * Clean up method to release resources
     * Call this when the view is being destroyed
     */
    fun cleanup() {
        clearProgressTrackers()

        // Clear pools
        for (i in 0 until MAX_POOL_SIZE) {
            progressTrackerPool.acquire()?.let { /* pools will be cleared */ }
            progressIndicatorPool.acquire()?.let { /* pools will be cleared */ }
        }
    }

    companion object {
        const val MODE_SCROLLABLE = 0
        const val MODE_FIXED = 1
        const val MODE_AUTO = 2

        const val STATE_INACTIVE = 0
        const val STATE_ACTIVE = 1
        const val STATE_COMPLETED = 2

        // Pool configuration
        private const val MAX_POOL_SIZE = 10
    }

    inner class ProgressTracker @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : LinearLayout(context, attrs, defStyleAttr) {

        private var connector: LinearProgressIndicator? = null
        private var isLastItem = false
        private var currentState = STATE_INACTIVE
        private var stepView: View? = null

        /**
         * Sets up the connector (LinearProgressIndicator) for this progress tracker item
         * Uses object pooling for better performance
         */
        private fun setupConnectorProperties(indicator: LinearProgressIndicator) {
            indicator.apply {
                layoutParams = LinearLayout.LayoutParams(
                    70.dp.toInt(),
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8.dp.toInt(), 0, 8.dp.toInt(), 0)
                }

                isIndeterminate = false
                max = 100
                setIndicatorColor(context.color(R.color.primary_30))
                trackColor = context.color(R.color.grey_40)
                trackThickness = 2.dp.toInt()
                gravity = Gravity.CENTER
            }
        }

        /**
         * Resets the ProgressTracker to its default state for reuse
         * This is called when returning the object to the pool
         */
        fun reset() {
            // Remove all child views
            removeAllViews()

            // Return connector to pool if it exists
            connector?.let {
                progressIndicatorPool.release(it)
                setupConnectorProperties(it)
            }

            // Reset state
            isLastItem = false
            currentState = STATE_INACTIVE
            stepView = null
            connector = null
        }

        init {
            orientation = HORIZONTAL
        }

        /**
         * Adds a step to this progress tracker
         * @param stepView The view representing the step (e.g., circle, icon, text)
         * @param showConnector Whether to show the connector after this step
         */
        fun addStep(stepView: View, showConnector: Boolean = true) {
            this.stepView = stepView
            addView(stepView)
            disableClippingUpToRoot(stepView)

            if (showConnector) {
                connector = progressIndicatorPool.acquire() ?: LinearProgressIndicator(context).apply {
                    setupConnectorProperties(this)
                }
                addView(connector)
                // Initialize visibility based on last-item status
                connector?.visibility = if (isLastItem) View.GONE else View.VISIBLE
            }
        }

        /**
         * Sets the visual state of the step with automatic color handling
         * @param state The state to set (STATE_INACTIVE, STATE_ACTIVE, STATE_COMPLETED)
         */
        fun setStepState(state: Int) {
            currentState = state

            when (state) {
                STATE_INACTIVE -> {
                    setConnectorProgress(0)
                    setConnectorColor(context.color(R.color.grey_40))
                }
                STATE_ACTIVE -> {
                    setConnectorProgress(0)
                    setConnectorColor(context.color(R.color.primary_30))
                }
                STATE_COMPLETED -> {
                    setConnectorProgress(100)
                    setConnectorColor(context.color(R.color.primary_30))
                }
            }
        }

        /**
         * Gets the current state of the step
         * @return Current state
         */
        fun getStepState(): Int = currentState

        /**
         * Animates the connector progress with completion callback
         * @param targetProgress Target progress value (0-100)
         * @param onComplete Callback when animation completes
         */
        fun animateConnectorProgress(targetProgress: Int, onComplete: (() -> Unit)? = null) {
            connector?.let { indicator ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val currentProgress = indicator.progress
                    val animator = ValueAnimator.ofInt(currentProgress, targetProgress).apply {
                        duration = 500 // 500ms for connector animation
                        interpolator = AccelerateDecelerateInterpolator()

                        addUpdateListener { animation ->
                            val animatedValue = animation.animatedValue as Int
                            indicator.setProgress(animatedValue, false)
                        }

                        addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                onComplete?.invoke()
                            }
                        })
                    }
                    animator.start()
                } else {
                    // Fallback for older Android versions
                    indicator.progress = targetProgress
                    onComplete?.invoke()
                }
            } ?: run {
                onComplete?.invoke()
            }
        }

        /**
         * Animates the step selection with bouncing effect
         */
        fun animateStepSelection() {
            stepView?.let { view ->
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

        fun disableClippingUpToRoot(view: View) {
            if (view is ViewGroup) view.clipChildren = false
            var current = view.parent
            while (current is ViewGroup) {
                current.clipChildren = false
                current = current.parent
            }
        }

        /**
         * Sets the progress of the connector
         * @param progress Progress value (0-100)
         */
        fun setConnectorProgress(progress: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connector?.setProgress(progress.coerceIn(0, 100), true)
            }
        }

        /**
         * Sets whether this is the last item (affects connector visibility)
         */
        fun setIsLastItem(isLast: Boolean) {
            isLastItem = isLast
            // Toggle visibility instead of removing view
            connector?.visibility = if (isLast) View.GONE else View.VISIBLE
        }

        /**
         * Sets the connector color
         * @param color Color resource ID
         */
        fun setConnectorColor(color: Int) {
            connector?.setIndicatorColor(color)
        }

        /**
         * Sets the connector track color
         * @param color Color resource ID
         */
        fun setConnectorTrackColor(color: Int) {
            connector?.trackColor = color
        }

        /**
         * Gets the current connector progress
         * @return Current progress (0-100)
         */
        fun getConnectorProgress(): Int = connector?.progress ?: 0

        /**
         * Shows or hides the connector
         * @param visible True to show, false to hide
         */
        fun setConnectorVisible(visible: Boolean) {
            connector?.visibility = if (visible && !isLastItem) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }
        }

        /**
         * Sets connector thickness
         * @param thickness Thickness in dp
         */
        fun setConnectorThickness(thickness: Int) {
            connector?.trackThickness = thickness
        }
    }
}