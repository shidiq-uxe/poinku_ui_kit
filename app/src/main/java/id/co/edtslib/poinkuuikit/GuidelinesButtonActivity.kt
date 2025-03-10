package id.co.edtslib.poinkuuikit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.co.edtslib.poinkuuikit.databinding.ActivityGuidelinesButtonBinding
import id.co.edtslib.uikit.poinku.utils.color
import id.co.edtslib.uikit.poinku.utils.viewBinding
import id.co.edtslib.uikit.poinku.R as UIKitR

class GuidelinesButtonActivity : AppCompatActivity() {

    private val binding by viewBinding<ActivityGuidelinesButtonBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guidelines_button)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.ribbon2.apply {
            ribbonText = "Hot Product"
            containerStartColor = context.color(UIKitR.color.gradient_red_start)
            containerEndColor = context.color(UIKitR.color.gradient_red_end)
        }
    }
}