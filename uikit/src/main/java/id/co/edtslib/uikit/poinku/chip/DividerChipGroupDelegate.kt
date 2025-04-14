package id.co.edtslib.uikit.poinku.chip

import com.google.android.material.chip.Chip

interface DividerChipGroupDelegate {
    fun onAssistChipClicked(chip: Chip)
    fun onFilterChipChecked(position: Int, chip: Chip, isChecked: Boolean)
}