package id.co.edtslib.poinkuuikit

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import id.co.edtslib.poinkuuikit.databinding.ActivityGuidelinesHomeCoachmarkBinding
import id.co.edtslib.poinkuuikit.databinding.ActivityGuidelinesHomeCouponBinding
import id.co.edtslib.uikit.poinku.coachmark.CoachMarkData
import id.co.edtslib.uikit.poinku.coachmark.CoachMarkOverlay
import id.co.edtslib.uikit.poinku.utils.viewBinding

class GuidelinesHomeCoachmark : GuidelinesBaseActivity() {

    private val binding by viewBinding< ActivityGuidelinesHomeCoachmarkBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_guidelines_home_coachmark)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        showCoachmark()
    }

    private val placeholderTargets = mutableListOf<View>()

    private fun showCoachmark() {
        val coachmarkTitles = listOf("Scan QR dan Kumpulkan Reward!", "Tukar Poin Loyalty dan Stamp jadi i-Kupon!","Lihat i-Kupon yang Kamu Punya!", "Cek Riwayat Transaksi dan Struk Lebih Mudah!","Nikmati Banyak Promo Setiap Hari!")
        val coachmarkDescriptions = listOf("Saat belanja, jangan lupa scan QR untuk mendapatkan poin dan stamp.", "Kumpulkan Poin Loyalty dan Stamp, lalu tukarkan jadi i-Kupon di sini.","Poin Loyalty & Stamp yang sudah kamu tukarkan jadi i-Kupon bisa kamu lihat dan gunakan di sini.","Kamu juga bisa melihat riwayat transaksi serta struk belanja kamu pada halaman ini.","Cek seluruh promo yang sedang berjalan di sini.")

        // Target 1 - Bucket Chip Group
        placeholderTargets.add(binding.placeholder1)
        placeholderTargets.add(binding.placeholder2)
        placeholderTargets.add(binding.placeholder3)
        placeholderTargets.add(binding.placeholder4)
        placeholderTargets.add(binding.placeholder5)

        // Map into coachmark items
        val coachmarkItems = placeholderTargets.indices.map { index ->
            CoachMarkData(
                target = placeholderTargets[index],
                title = coachmarkTitles[index],
                description = coachmarkDescriptions[index]
            )
        }

        // 1 Second Delay before showing Coachmark & Spotlight or after loading all the api
        Handler(Looper.getMainLooper()).postDelayed({
            CoachMarkOverlay.Builder(this)
                .setDismissibleOnBack(true)
                .setCoachMarkItems(coachmarkItems)
                .build()
        }, 1000L)
    }
}