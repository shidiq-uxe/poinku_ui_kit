package id.co.edtslib.uikit.poinku.chip

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.res.use
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import id.co.edtslib.uikit.poinku.R
import id.co.edtslib.uikit.poinku.databinding.ChipGroupDividerBinding
import id.co.edtslib.uikit.poinku.utils.dp
import id.co.edtslib.uikit.poinku.utils.inflater
import id.co.edtslib.uikit.poinku.utils.viewBinding
import androidx.core.view.isNotEmpty

class DividerChipGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ChipGroupDividerBinding.inflate(context.inflater, this, true)

    private val assistChip = binding.assistChip
    private val divider = binding.assistChipDivider
    private val chipGroup = binding.cgChips

    var delegate: DividerChipGroupDelegate? = null

    var items: List<Pair<String, String>> = emptyList()
        set(value) {
            field = value
            addBucketItems(value)
        }

    var assistChipText: String? = null
        set(value) {
            field = value
            assistChip.text = value
        }

    var assistChipIcon: Drawable? = null
        set(value) {
            field = value
            assistChip.chipIcon = value
        }

    val checkedChipId get() = chipGroup.checkedChipId

    private var dividerWidth = 1.dp
    private var dividerHeight = 24.dp
    private var dividerColor = Color.LTGRAY
    private var dividerMarginStart = 8.dp
    private var dividerMarginEnd = 8.dp

    init {
        context.obtainStyledAttributes(attrs, R.styleable.DividerChipGroup).use {
            dividerWidth = it.getDimension(R.styleable.DividerChipGroup_dividerWidth, dividerWidth)
            dividerHeight = it.getDimension(R.styleable.DividerChipGroup_dividerHeight, dividerHeight)
            dividerColor = it.getColor(R.styleable.DividerChipGroup_dividerColor, dividerColor)
            dividerMarginStart = it.getDimension(R.styleable.DividerChipGroup_dividerMarginStart, dividerMarginStart)
            dividerMarginEnd = it.getDimension(R.styleable.DividerChipGroup_dividerMarginEnd, dividerMarginEnd)

            assistChipText = it.getString(R.styleable.DividerChipGroup_assistChipText)
            assistChipIcon = it.getDrawable(R.styleable.DividerChipGroup_assistChipIcon)
        }

        initAssistChip()
    }

    private fun initAssistChip() {
        assistChip.setOnClickListener {
            delegate?.onAssistChipClicked(assistChip)
        }
    }

    private fun addBucketItems(chipItems: List<Pair<String, String>>) {
        chipItems.forEachIndexed { position, (id, text) ->
            val chip = Chip(context, null, R.attr.chipFilterStyle).apply {
                this.id = generateViewId()
                this.text = text

                setOnCheckedChangeListener { _, isChecked ->
                    delegate?.onFilterChipChecked(position, this, isChecked)
                }
            }

            chipGroup.addView(chip)
        }
    }

    fun checkChipByPosition(position: Int) {
        if (chipGroup.isNotEmpty()) {
            (chipGroup.getChildAt(position) as? Chip)?.isChecked = true
        }
    }
}