package id.co.edtslib.poinkuuikit.auth

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.Toast
import androidx.core.text.buildSpannedString
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.co.edtslib.poinkuuikit.GuidelinesBaseActivity
import id.co.edtslib.poinkuuikit.R
import id.co.edtslib.poinkuuikit.databinding.ActivityRegisterBinding
import id.co.edtslib.uikit.poinku.utils.TextStyle
import id.co.edtslib.uikit.poinku.utils.buildHighlightedMessage
import id.co.edtslib.uikit.poinku.utils.color
import id.co.edtslib.uikit.poinku.utils.viewBinding
import id.co.edtslib.uikit.textfield.TextFieldDelegate

class RegisterActivity : GuidelinesBaseActivity() {

    private val binding by viewBinding<ActivityRegisterBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initClickableTnC()
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
            Log.e("Is Clicked", "i don't know man")
            Intent(this@RegisterActivity, VerificationActivity::class.java).also {
                startActivity(it)
            }
        }
    }
}