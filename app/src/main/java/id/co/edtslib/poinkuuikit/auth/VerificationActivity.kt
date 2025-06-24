package id.co.edtslib.poinkuuikit.auth

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Guideline
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.co.edtslib.poinkuuikit.GuidelinesBaseActivity
import id.co.edtslib.poinkuuikit.R
import id.co.edtslib.poinkuuikit.databinding.ActivityVerificationBinding
import id.co.edtslib.uikit.poinku.textview.CountdownTextViewDelegate
import id.co.edtslib.uikit.poinku.utils.viewBinding

class VerificationActivity : GuidelinesBaseActivity() {

    private val binding by viewBinding<ActivityVerificationBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initCountDown()
        initProgressTracker()
    }

    private fun initProgressTracker() {
        binding.ptgTracker.selectStep(1)
    }

    private fun initCountDown() {
        binding.ctvResendIn.apply {
            intervalInMillis = 10000
            start()
        }


        binding.ctvResendIn.delegate = object : CountdownTextViewDelegate {
            override fun onExpired() {
                Toast.makeText(this@VerificationActivity, "Already Expired", Toast.LENGTH_SHORT).show()
                toggleCountdownView(true)
                binding.btnVerifyWhatsapp.isClickable = true
            }
        }

        binding.btnVerifyWhatsapp.setOnClickListener {
            it.isClickable = false
            toggleCountdownView(false)

            binding.ctvResendIn.apply {
                intervalInMillis = 10000
                start()
            }
        }
    }

    private fun toggleCountdownView(enabled: Boolean) {
        binding.btnMisscallVerification.isEnabled = enabled
        binding.btnSMSVerification.isEnabled = enabled
    }
}