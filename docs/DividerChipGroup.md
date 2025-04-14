# DividerChipGroup

## Components Overview

| Component  | Description                                                   |
|------------|---------------------------------------------------------------|
| AssistChip | Optional chip to trigger an action (Bottom Sheet for Example) |
| Divider    | A visual line separator with customizable style               |
| ChipGroup  | Group of filter chips created from item list                  |


## XML Attributes

You can customize the view via XML:

```xml
<id.co.edtslib.uikit.poinku.chip.DividerChipGroup
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:dividerWidth="8dp"
    app:dividerHeight="24dp"
    app:dividerColor="@color/gray"
    app:dividerMarginStart="8dp"
    app:dividerMarginEnd="8dp"
    app:assistChipText="More"
    app:assistChipIcon="@drawable/ic_more" />
```

| Attribute Name       | Type        | Description              |
|----------------------|-------------|--------------------------|
| `assistChipText`     | `String`    | Text for the AssistChip  |
| `assistChipIcon`     | `Drawable`  | Icon for the AssistChip  |
| `dividerWidth`       | `Dimension` | Width of divider         |
| `dividerHeight`      | `Dimension` | Height of divider        |
| `dividerColor`       | `Color`     | Color of divider         |
| `dividerMarginStart` | `Dimension` | Start margin for divider |
| `dividerMarginEnd`   | `Dimension` | End margin for divider   |


## Public API (Kotlin)

### Properties

```kotlin 
var items: List<Pair<String, String>>
```

Sets list of chips. First = ID, Second = Display Text.

```kotlin
var assistChipText: String?
var assistChipIcon: Drawable?
val checkedChipId: Int
var delegate: DividerChipGroupDelegate?
```

### Methods

```kotlin
fun checkChipByPosition(position: Int)
```

Programmatically checks a chip by its index.

### Delegate Callbacks

Implement `DividerChipGroupDelegate` to listen to chip interactions:

```kotlin
interface DividerChipGroupDelegate {
    fun onAssistChipClicked(chip: Chip)
    fun onFilterChipChecked(position: Int, chip: Chip, isChecked: Boolean)
}
```

💡 Example (Programmatically)
-----------------------------

```kotlin
val chipGroup = findViewById<DividerChipGroup>(R.id.dividerChipGroup)
chipGroup.assistChipText = "Filters"
chipGroup.assistChipIcon = ContextCompat.getDrawable(this, R.drawable.ic_filter)

chipGroup.items = listOf(
    "popular" to "Popular", 
    "latest" to "Latest", 
    "nearby" to "Nearby"
)

chipGroup.delegate = object : DividerChipGroupDelegate {
    override fun onAssistChipClicked(chip: Chip) {
        Toast.makeText(this@MainActivity, "Assist clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onFilterChipChecked(position: Int, chip: Chip, isChecked: Boolean) {
        Log.d("ChipCheck", "Chip at $position isChecked = $isChecked")
    }
}
```