package id.co.edtslib.uikit.poinku.searchbar

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.text.InputFilter
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StyleRes
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import id.co.edtslib.uikit.poinku.R
import id.co.edtslib.uikit.poinku.databinding.SearchBarBinding
import id.co.edtslib.uikit.poinku.utils.color
import id.co.edtslib.uikit.poinku.utils.colorStateList
import id.co.edtslib.uikit.poinku.utils.dp
import id.co.edtslib.uikit.poinku.utils.drawable
import id.co.edtslib.uikit.poinku.utils.fade
import id.co.edtslib.uikit.poinku.utils.inflater
import id.co.edtslib.uikit.poinku.utils.input.InputType
import id.co.edtslib.uikit.searchbar.SearchBarDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SearchBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var searchBarDelegate: SearchBarDelegate? = null

    private var placeholderIndex = 0
    private var placeholderJobHelper: Job = Job()

    private val endIconRes = R.drawable.ic_x_close_rounded

    private val binding = SearchBarBinding.inflate(context.inflater, this, true)

    val editText = binding.etInput
    private val startIconView = binding.ivStartIcon
    private val endIconView = binding.ivEndIcon
    private val prefixTextView = binding.tvSearchContent
    private val textSwitcher = binding.tsPlaceholder.apply {
        this.setFactory {
            TextView(context).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setTextAppearance(R.style.TextAppearance_Rubik_P1_Light)
                } else {
                    setTextAppearance(context, R.style.TextAppearance_Rubik_P1_Light)
                }

                post {
                    setTextColor(context.color(R.color.grey_50))
                }
            }
        }
    }

    var text = editText.text.toString()
        set(value) {
            field = value

            editText.setText(value)
        }

    var searchBarType = SearchBarType.BORDER
        set(value) {
            field = value

            if (value == SearchBarType.BORDER) {
                binding.inputBackground.background = context.drawable(R.drawable.bg_search_bar_border)
                elevation = 0f
            } else {
                binding.inputBackground.background = context.drawable(R.drawable.bg_search_bar_borderless)
                elevation = 4.dp
            }
        }

    var fieldMaxLength: Int? = null
        set(value) {
            field = value

            value?.let { maxLength ->
                editText.filters = arrayOf(InputFilter.LengthFilter(maxLength))
            }
        }

    var fieldInputType = InputType.Text
        set(value) {
            field = value

            val inputType = when (value) {
                InputType.Phone -> {
                    android.text.InputType.TYPE_CLASS_PHONE or android.text.InputType.TYPE_TEXT_VARIATION_PHONETIC
                }
                InputType.Email -> {
                    android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                }
                else -> {
                    android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_NORMAL
                }
            }

            editText.inputType = inputType
        }

    var isStartIconVisible = false
        set(value) {
            field = value

            startIconView.isVisible = value
        }

    var startIcon: Int? = null
        set(value) {
            field = value

            isStartIconVisible = value != null
            startIconView.setImageDrawable(value?.let { context.drawable(it) })
        }

    var isCancelVisible = true
        set(value) {
            field = value

            endIconView.apply {
                isVisible = value

                this.setImageDrawable(context.drawable(endIconRes))
            }
        }

    var prefixText: String? = null
        set(value) {
            field = value

            prefixTextView.isVisible = value.isNullOrEmpty().not()

            value?.let { hint ->
                if (shouldAnimatePlaceholder) {
                    prefixTextView.text = hint
                } else {
                    editText.hint = hint
                }
            }
        }

    var placeholderTexts: Array<String> = arrayOf()
        set(value) {
            field = value

            value.let {
                if (shouldAnimatePlaceholder) {
                    textSwitcher.setCurrentText(it.first())
                } else {
                    editText.hint = it.first()
                }
            }
        }

    @StyleRes
    var placeholderTextAppearance: Int? = null
        set(value) {
            field = value

            value?.let { textAppearance ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    (textSwitcher.currentView as TextView).setTextAppearance(textAppearance)
                } else {
                    (textSwitcher.currentView as TextView).setTextAppearance(context, textAppearance)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    editText.setTextAppearance(textAppearance)
                } else {
                    editText.setTextAppearance(context, textAppearance)
                }
            }
        }

    @ColorRes
    var placeholderTextColor: Int? = null
        set(value) {
            field = value

            value?.let { color ->
                (textSwitcher.currentView as TextView).setTextColor(context.color(color))
                editText.setHintTextColor(context.colorStateList(color))
            }
        }

    var shouldAnimatePlaceholder: Boolean = true
    var placeholderAnimationType: PlaceholderAnimationType = PlaceholderAnimationType.SlideUp
        set(value) {
            field = value

            if (placeholderTexts.isNotEmpty()) {
                when(value) {
                    PlaceholderAnimationType.SlideUp -> {
                        textSwitcher.inAnimation = TranslateAnimation(
                            Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, 1.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f
                        ).apply {
                            duration = 300
                        }

                        textSwitcher.outAnimation = TranslateAnimation(
                            Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, -1.0f
                        ).apply {
                            duration = 300
                        }

                        slideUpAnimation()
                    }
                    PlaceholderAnimationType.TypeWriter -> {
                        textSwitcher.inAnimation = AlphaAnimation(0f, 1f).apply {
                            duration = 300
                        }
                        textSwitcher.outAnimation = AlphaAnimation(1f, 0f).apply {
                            duration = 300
                        }

                        typeWriterAnimation()
                    }
                    PlaceholderAnimationType.TypeWriterWithPrefix -> {
                        this.post {
                            if (prefixText.isNullOrEmpty()) throw IllegalAccessException("You Should Set Prefix Text by using prefixText attribute")
                        }

                        val scaleUp = ScaleAnimation(
                            1.0f, 1.025f,  // Scale up from 100% to 102.5% in X-axis
                            1.0f, 1.025f,  // Scale up from 100% to 102.5% in Y-axis
                        ).apply {
                            duration = 150  // First phase duration
                        }

                        val scaleDown = ScaleAnimation(
                            1.025f, 1.0f,  // Scale down from 102.5% to 100% in X-axis
                            1.025f, 1.0f,  // Scale down from 102.5% to 100% in Y-axis
                        ).apply {
                            duration = 150  // Second phase duration
                            startOffset = 150  // Start after the scale up finishes
                        }

                        val scaleAnimationSet = AnimationSet(true).apply {
                            addAnimation(scaleUp)
                            addAnimation(scaleDown)
                        }

                        textSwitcher.outAnimation = scaleAnimationSet

                        typeWriterAnimation(300)
                    }
                }
            }
        }

    enum class PlaceholderAnimationType {
        SlideUp, TypeWriter, TypeWriterWithPrefix
    }

    private var slidingIndex = 0

    private fun slideUpAnimation() {
        placeholderJobHelper = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                delay(2000L) // Delay between text changes (2 seconds)
                slidingIndex = (slidingIndex + 1) % placeholderTexts.size // Ensure index loops back to 0
                textSwitcher.setText(placeholderTexts[slidingIndex]) // Change the text with animation
            }
        }
    }

    private var typeWriterIndex = 0

    private fun typeWriterAnimation(changeDelay: Long = 300) {
        if (typeWriterIndex > placeholderTexts.size.minus(1)) {
            typeWriterIndex = 0
        }

        placeholderJobHelper = CoroutineScope(Dispatchers.Main).launch {
            typeWriterEffect(
                text = placeholderTexts[typeWriterIndex],
                changeDelay = changeDelay
            ) {
                typeWriterIndex++

                delay(300L)
                typeWriterAnimation(changeDelay)
            }
        }
    }

    private suspend fun typeWriterEffect(
        text: String,
        changeDelay: Long,
        onTypeWriterEndAction: suspend () -> Unit
    ) {
        textSwitcher.setText("")
        var charIndex = 0

        delay(500)

        (textSwitcher.currentView as TextView).append("")
        while (charIndex < text.length) {
            (textSwitcher.currentView as TextView).append(text[charIndex].toString())
            charIndex++
            delay(80) // Use Coroutine delay for each character
        }

        delay(changeDelay)
        onTypeWriterEndAction.invoke()
    }

    init {
        initAttrs(attrs, defStyleAttr)
        enableHardwareAcceleration()

        editText.maxLines = 1

        onEditTextFocusChangeListener()
        onEditTextTextChangeListener()

        outlineProvider = SearchBarOutlineProvider()
        setShadowColor()

        endIconView.setOnClickListener {
            searchBarDelegate?.onCloseIconClicked(it)
        }
    }

    private fun initAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        if (attrs != null) {
            context.theme.obtainStyledAttributes(attrs, R.styleable.SearchBar, defStyleAttr, 0).apply {
                isCancelVisible = getBoolean(R.styleable.SearchBar_closeIconVisible, isCancelVisible)

                fieldMaxLength = getInt(R.styleable.SearchBar_fieldMaxLength, fieldMaxLength ?: Int.MAX_VALUE)
                fieldInputType = InputType.values()[getInt(R.styleable.SearchBar_fieldInputType, 0)]

                prefixText = getString(R.styleable.SearchBar_prefixText)
                placeholderTextAppearance = getResourceId(R.styleable.SearchBar_placeholderTextAppearance, placeholderTextAppearance ?: R.style.TextAppearance_Rubik_B2_Light)

                val startIconColorRes = getResourceId(R.styleable.SearchBar_startIcon, 0)

                if (startIconColorRes != 0) {
                    startIcon = startIconColorRes
                }

                val itemsArrayId = getResourceId(R.styleable.SearchBar_placeholderTextList, 0)

                if (itemsArrayId != 0) {
                    placeholderTexts = resources.getStringArray(itemsArrayId)
                }

                searchBarType = SearchBarType.values()[getInt(R.styleable.SearchBar_searchBarType, searchBarType.ordinal)]

                recycle()
            }
        }
    }

    private fun enableHardwareAcceleration() {
        textSwitcher.setLayerType(LAYER_TYPE_HARDWARE, null)
        textSwitcher.currentView?.setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    private fun onEditTextFocusChangeListener() {
        editText.setOnFocusChangeListener { view, hasFocus ->
            searchBarDelegate?.onFocusChange(view, hasFocus)

            binding.inputBackground.isActivated = hasFocus

            if (hasFocus) {
                if (shouldAnimatePlaceholder) {
                    if (placeholderAnimationType == PlaceholderAnimationType.TypeWriter || placeholderAnimationType == PlaceholderAnimationType.TypeWriterWithPrefix) {
                        textSwitcher.setCurrentText(placeholderTexts[typeWriterIndex])
                    }

                    placeholderJobHelper.cancel()
                }
            } else {
                if (shouldAnimatePlaceholder) {
                    placeholderAnimationType = placeholderAnimationType

                    placeholderJobHelper.start()
                }

            }
        }
    }

    private fun onEditTextTextChangeListener() {
        endIconView.alpha = 0f

        editText.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                animatePlaceholderDuringTextChange(text.toString())
            },
            afterTextChanged = { editable ->
                searchBarDelegate?.onTextChange(editable.toString())

                val input = editable.toString()
                animateEndIconDuringTextChange(input)
            }
        )
    }

    private fun animatePlaceholderDuringTextChange(input: String) {
        val placeholderList = listOf(textSwitcher, prefixTextView)

        placeholderList.forEach {
            if (input.isNotEmpty()) {
                if (it.alpha != 0f) {
                    it.fade(
                        duration = 10,
                        alphaStart = 1f,
                        alphaEnd = 0f
                    )
                }
            } else {
                it.fade(
                    duration = 250,
                    alphaStart = 0f,
                    alphaEnd = 1f
                )
            }
        }
    }

    private fun animateEndIconDuringTextChange(input: String) {
        if (input.isEmpty()) {
            // Fade Out
            animateEndIconDown(endIconView)
        } else {
            // Fade In
            if (endIconView.alpha < 1.0f) {
                animateEndIconUp(endIconView)
            }
        }
    }

    private fun animateEndIconUp(view: View) {
        // Scale and fade in animations
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f).apply {
            duration = 200
        }
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f).apply {
            duration = 200
        }
        val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
            duration = 200
        }

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, fadeIn)
            start()
        }
    }

    private fun animateEndIconDown(view: View) {
        // Scale and fade out animations
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f).apply {
            duration = 200
        }
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f).apply {
            duration = 200
        }
        val fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).apply {
            duration = 200
        }

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, fadeOut)
            start()
        }
    }

    val placeholder = (textSwitcher.currentView as TextView).text

    private fun setShadowColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            this.outlineSpotShadowColor = context.color(R.color.grey_50)
        }
    }

    enum class SearchBarType {
        BORDER, BORDERLESS
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        // Cancel the Placeholder animations job when the view Detached from Window
        placeholderJobHelper.cancel()
    }

}