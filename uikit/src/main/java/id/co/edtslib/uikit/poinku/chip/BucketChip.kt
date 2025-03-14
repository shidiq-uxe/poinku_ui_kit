package id.co.edtslib.uikit.poinku.chip

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.res.use
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import id.co.edtslib.uikit.poinku.databinding.ChipStampBucketBinding
import id.co.edtslib.uikit.poinku.utils.inflater
import id.co.edtslib.uikit.poinku.R
import id.co.edtslib.uikit.poinku.utils.color
import id.co.edtslib.uikit.poinku.utils.drawable

class BucketChip @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ChipStampBucketBinding.inflate(context.inflater, this, true)

    val chipCard = binding.root

    var textColor: Int = context.color(R.color.grey_60)
        set(value) {
            field = value
            binding.tvBucketStampCount.setTextColor(value)
            binding.tvBucketStamp.setTextColor(value)
        }

    var stampCount: Int? = null
        set(value) {
            field = value

            binding.tvBucketStampCount.text = value.toString()
        }

    /*
    This params could contain, String, Drawable Resource name, or anything glide supports
     */
    var image: Any? = null
        set(value) {
            field = value
            setImage(binding.ivBucketImage)
        }

    fun setImageResource(action: (ShapeableImageView) -> Unit = { setImage(it) }) {
        action(binding.ivBucketImage)
    }

    private fun setImage(imageView: ShapeableImageView) {
        val activity = (context as? FragmentActivity)

        if (activity != null && (activity.isDestroyed || activity.isFinishing)) {
            return
        }

        val image  = image.let { image ->
            when(image) {
                is Int -> context.drawable(image)
                is String -> if (image.startsWith("http")) { image } else {
                    context.resources.getIdentifier(
                        image, "drawable",
                        context.packageName
                    )
                }
                else -> image
            }
        }

        Glide.with(context).load(image).into(imageView)
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.BucketChip, 0, 0).use {
            stampCount = it.getInt(R.styleable.BucketChip_stampCount, 0)
            textColor = it.getColor(R.styleable.BucketChip_chipTextColor, textColor)
        }
    }


    fun setOnCheckedChangeListener(action: (MaterialCardView, Boolean) -> Unit) {
        binding.root.setOnCheckedChangeListener { view, isChecked ->
            action.invoke(view, isChecked)
        }
    }
}