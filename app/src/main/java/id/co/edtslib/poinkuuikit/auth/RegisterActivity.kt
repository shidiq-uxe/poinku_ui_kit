package id.co.edtslib.poinkuuikit.auth

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.buildSpannedString
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.co.edtslib.poinkuuikit.GuidelinesBaseActivity
import id.co.edtslib.poinkuuikit.R
import id.co.edtslib.poinkuuikit.databinding.ActivityRegisterBinding
import id.co.edtslib.uikit.poinku.R as UIKitR
import id.co.edtslib.uikit.poinku.badge.Badge
import id.co.edtslib.uikit.poinku.utils.TextStyle
import id.co.edtslib.uikit.poinku.utils.buildHighlightedMessage
import id.co.edtslib.uikit.poinku.utils.color
import id.co.edtslib.uikit.poinku.utils.dp
import id.co.edtslib.uikit.poinku.utils.setLightStatusBar
import id.co.edtslib.uikit.poinku.utils.viewBinding
import id.co.edtslib.uikit.textfield.TextFieldDelegate

class RegisterActivity : GuidelinesBaseActivity() {

    private val binding by viewBinding<ActivityRegisterBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        setLightStatusBar()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initClickableTnC()
        createProgressTracker(this)
        activateButton()
        bindClickAction()
    }

    private fun initClickableTnC() {
        binding.tvTermsAndCondition.movementMethod = LinkMovementMethod()
        binding.tvTermsAndCondition.text = buildHighlightedMessage(
            context = this@RegisterActivity,
            message = getString(R.string.authentication_terms_n_conditions),
            defaultTextAppearance = TextStyle.b3Style(
                context = this@RegisterActivity,
                color = color(id.co.edtslib.uikit.poinku.R.color.grey_80),
            ),
            highlightedMessages = listOf(getString(R.string.terms_n_conditions)),
            highlightedTextAppearance = listOf(
                TextStyle.b3Style(
                    context = this@RegisterActivity,
                    color = color(id.co.edtslib.uikit.poinku.R.color.primary_30)
                )
            ),
            highlightClickAction = listOf {
                Toast.makeText(this@RegisterActivity, "Open TnC Page", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun activateButton() {
        binding.tfName.delegate = object : TextFieldDelegate {
            override fun onValueChange(value: String?) {
                binding.btnAuthenticate.isEnabled = !value.isNullOrEmpty()
            }
        }
    }

    private fun bindClickAction() {
        binding.btnAuthenticate.setOnClickListener {
            Intent(this@RegisterActivity, VerificationActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    fun createProgressTracker(context: Context) {
        val progressGroup = binding.ptgTracker

        progressGroup.selectStep(0)

        /*repeat(3) { index ->
            val tracker = progressGroup.createAndAddProgressTracker()
            val stepView = createStepView(context, completed = false) // Your step view (circle, text, etc.)
            tracker.addStep(stepView, showConnector = index < 2) // No connector for last item
        }*/
    }

    // Todo : Set as Default ProgressTracker View
    private fun createStepView(
        context: Context,
        text: String,
        badgeText: String,
        completed: Boolean
    ): View {
        val linearLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val color = context.color(if (completed) UIKitR.color.primary_30 else UIKitR.color.grey_40)

        val badge = Badge(context).apply {
            this.text = badgeText
            this.badgeColor = color
        }


        val textView = TextView(context).apply {
            this.text = text
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setTextAppearance(UIKitR.style.TextAppearance_Rubik_B4_Medium)
            }

            this.setTextColor(color)

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8.dp.toInt(), 0, 0, 0)
            }
        }

        linearLayout.addView(badge)
        linearLayout.addView(textView)
        return linearLayout
    }
}