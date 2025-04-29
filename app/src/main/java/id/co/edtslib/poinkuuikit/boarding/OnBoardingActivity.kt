package id.co.edtslib.poinkuuikit.boarding

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnAttach
import androidx.core.view.updatePadding
import id.co.edtslib.poinkuuikit.GuidelinesBaseActivity
import id.co.edtslib.poinkuuikit.R
import id.co.edtslib.poinkuuikit.databinding.ActivityOnBoardingBinding
import id.co.edtslib.uikit.poinku.boarding.Boarding
import id.co.edtslib.uikit.poinku.boarding.ContentAlignment
import id.co.edtslib.uikit.poinku.boarding.IndicatorAlignment
import id.co.edtslib.uikit.poinku.utils.SystemBarStyle
import id.co.edtslib.uikit.poinku.utils.drawable
import id.co.edtslib.uikit.poinku.utils.seconds
import id.co.edtslib.uikit.poinku.utils.setDarkStatusBar
import id.co.edtslib.uikit.poinku.utils.setSystemBarStyle
import id.co.edtslib.uikit.poinku.utils.viewBinding


class OnBoardingActivity : GuidelinesBaseActivity() {

    private val binding by viewBinding<ActivityOnBoardingBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_on_boarding)

        setSystemBarStyle(
            statusBarStyle = SystemBarStyle.Dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.Dark(Color.TRANSPARENT)
        )

        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.decorView.setOnApplyWindowInsetsListener { view, insets ->
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
                binding.root.updatePadding(0, 0, 0, navBarInsets.bottom)
            }
            insets
        }

        setBoardingItems()
    }

    private fun setBoardingItems() {
        binding.boardingPagerView.apply {
            autoScrollInterval = 3.seconds.toInt()
            items = listOf<Boarding>(
                Boarding(
                    image = drawable(R.drawable.ill1),
                    title = "Kumpulkan Poin dan Stamp Buat Dapetin Kejutan!",
                    description = "Kumpulin poin serta stamp dari setiap transaksi dan tukarkan dengan kupon menarik di sini!"
                ),Boarding(
                    image = drawable(R.drawable.ill2),
                    title = "Dapetin Diskon, Bonus, Sampai Gratisan!",
                    description = "Jangan lupa untuk gunakan kupon untuk mendapatkan banyak keuntungan!"
                ),Boarding(
                    image = drawable(R.drawable.ill3),
                    title = "Semakin Sering Belanja, Semakin Untung!",
                    description = "Makin sering kamu belanja semakin banyak bonus, serta diskon yang bisa kamu dapetin."
                ),
            )

            this.contentAlignment = ContentAlignment.Center()
            this.indicatorAlignment = IndicatorAlignment.Center()

        }
    }

}