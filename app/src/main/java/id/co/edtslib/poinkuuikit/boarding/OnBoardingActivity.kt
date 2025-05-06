package id.co.edtslib.poinkuuikit.boarding

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnAttach
import androidx.core.view.updatePadding
import id.co.edtslib.poinkuuikit.GuidelinesBaseActivity
import id.co.edtslib.poinkuuikit.R
import id.co.edtslib.poinkuuikit.databinding.ActivityOnBoardingBinding
import id.co.edtslib.uikit.poinku.boarding.Boarding
import id.co.edtslib.uikit.poinku.boarding.BoardingPageListener
import id.co.edtslib.uikit.poinku.boarding.ContentAlignment
import id.co.edtslib.uikit.poinku.boarding.IndicatorAlignment
import id.co.edtslib.uikit.poinku.utils.drawable
import id.co.edtslib.uikit.poinku.utils.seconds
import id.co.edtslib.uikit.poinku.utils.viewBinding


class OnBoardingActivity : GuidelinesBaseActivity(), BoardingPageListener {

    private val binding by viewBinding<ActivityOnBoardingBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContentView(R.layout.activity_on_boarding)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.updatePadding(navigationBars.left, navigationBars.top, navigationBars.right, navigationBars.bottom)
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
                    title = "Kumpulin Poin, Dapat Hadiah!",
                    description = "Kumpulin Poin & Stamp dari setiap transaksi, tukarkan dengan hadiah yang cuma ada di sini!"
                ),Boarding(
                    image = drawable(R.drawable.ill2),
                    title = "Anti Ribet, Nyaman Banget!",
                    description = "Simpan struk belanja & cek riwayat transaksi kapan saja, semua dalam satu aplikasi."
                ),Boarding(
                    image = drawable(R.drawable.ill3),
                    title = "Member Lebih Untung!",
                    description = "Nikmati berbagai Promo Khusus Member, Diskon, Bonus, sampai Belanja Gratis, yang dijamin untung banget!"
                ),
            )

            this.contentAlignment = ContentAlignment.Center()
            this.indicatorAlignment = IndicatorAlignment.Center()

            delegate = this@OnBoardingActivity
        }
    }

    override fun onPageSelected(position: Int, fakePosition: Int) {}

    override fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int,
        fakePosition: Int
    ) {}

    override fun onRegisterButtonClicked(view: View) {}

    override fun onLoginButtonClicked(view: View) {}
}