package id.co.edtslib.poinkuuikit.boarding

import android.os.Build
import android.os.Build.VERSION.SDK
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.co.edtslib.poinkuuikit.GuidelinesBaseActivity
import id.co.edtslib.poinkuuikit.R
import id.co.edtslib.uikit.poinku.R as UIKitR
import id.co.edtslib.poinkuuikit.databinding.ActivityOnBoardingBinding
import id.co.edtslib.uikit.poinku.boarding.Boarding
import id.co.edtslib.uikit.poinku.boarding.ContentAlignment
import id.co.edtslib.uikit.poinku.boarding.IndicatorAlignment
import id.co.edtslib.uikit.poinku.utils.drawable
import id.co.edtslib.uikit.poinku.utils.setDarkStatusBar
import id.co.edtslib.uikit.poinku.utils.viewBinding

class OnBoardingActivity : GuidelinesBaseActivity() {

    private val binding by viewBinding<ActivityOnBoardingBinding>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        setDarkStatusBar()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT <= 34) {
            window.apply {
                statusBarColor = android.graphics.Color.TRANSPARENT
                navigationBarColor = android.graphics.Color.TRANSPARENT

                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            }

        }

        setBoardingItems()
    }

    private fun setBoardingItems() {
        binding.boardingPagerView.apply {
            items = listOf<Boarding>(
                Boarding(
                    image = drawable(R.drawable.ill1),
                    title = "Kumpulkan Poin dan Stamp Buat Dapetin Kejutan!",
                    description = "Kumpulin poin serta stamp dari setiap transaksi dan tukarkan dengan kupon menarik di sini!"
                ),Boarding(
                    image = drawable(R.drawable.ill1),
                    title = "Dapetin Diskon, Bonus, Sampai Gratisan!",
                    description = "Jangan lupa untuk gunakan kupon untuk mendapatkan banyak keuntungan!"
                ),Boarding(
                    image = drawable(R.drawable.ill1),
                    title = "Semakin Sering Belanja, Semakin Untung!",
                    description = "Makin sering kamu belanja semakin banyak bonus, serta diskon yang bisa kamu dapetin."
                ),
            )

            this.contentAlignment = ContentAlignment.Center()
            this.indicatorAlignment = IndicatorAlignment.Center()

        }
    }

}