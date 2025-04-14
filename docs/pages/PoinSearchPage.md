# GuidelinesPoinSearch

This guide demonstrates a responsive, animated interface designed for loyalty point discovery and filtering. It leverages custom components like `PoinSearchToolbar` and `DividerChipGroup` within a collapsing layout to create an engaging user experience.

## Layout Hierarchy
[XML Layout -> `activity_guidelines_poin_search.xml`](https://github.com/shidiq-uxe/poinku_ui_kit/blob/main/app/src/main/res/layout/activity_guidelines_poin_search.xml)
```
CoordinatorLayout 
  └── AppBarLayout  
    └── CollapsingToolbarLayout 
      ├── MaterialToolbar  
        └── LinearLayoutCompat  
          ├── PoinSearchToolbar // Custom search toolbar  
          ├── DividerChipGroup // Custom filter chips  
          └── tvPointAnchor // Animated points display 
      └── LinearLayoutCompat // Additional header content (loyalty widget, section title) 
  └── RecyclerView // Content list with StaggeredGridLayoutManager
```
- [PoinSearchToolbar](https://github.com/shidiq-uxe/poinku_ui_kit/blob/main/uikit/src/main/java/id/co/edtslib/uikit/poinku/toolbar/poin/PoinSearchToolbar.kt)

- [DividerChipGroup](https://github.com/shidiq-uxe/poinku_ui_kit/blob/main/uikit/src/main/java/id/co/edtslib/uikit/poinku/chip/DividerChipGroup.kt)

## Key Code Snippets
[Class Implementation](https://github.com/shidiq-uxe/poinku_ui_kit/blob/main/app/src/main/java/id/co/edtslib/poinkuuikit/poin_guidelines/GuidelinesPoinSearch.kt)

### 1. Collapsing Behavior

The AppBar listens to scroll offsets and adjusts the transparency and translation of the points anchor accordingly:

```kotlin
binding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
    val totalScrollRange = appBarLayout.totalScrollRange
    val collapseProgress = abs(verticalOffset).toFloat() / totalScrollRange

    // Example: update widget alpha based on scroll progress
    binding.widget.root.alpha = 1 - collapseProgress

    // Compute target values for tvPointAnchor animations
    val anchorViewHeight = binding.tvPointAnchor.height

    val targetTranslation = if (collapseProgress < 0.8f) -anchorViewHeight.toFloat() else 0f
    val targetAlpha = if (collapseProgress < 0.8f) 0f else 1f

    // Animate tvPointAnchor for a smooth transition
    AnimatorSet().apply {
        playTogether(
            ObjectAnimator.ofFloat(binding.tvPointAnchor, View.ALPHA, targetAlpha),
            ObjectAnimator.ofFloat(binding.tvPointAnchor, View.TRANSLATION_Y, targetTranslation)
        )
        start()
    }
}
```

### 2. Initializing Filter Chips

Set up the filter chips with sample items and select the default chip:

```kotlin
binding.cgFilter.apply {
  items = listOf(
    "1" to "Semua",
    "2" to "Food & Beverage",
    "3" to "Detergent",
    "4" to "Yummy Choice",
    "5" to "Diamond"
  )}.also {
    binding.cgFilter.checkChipByPosition(0)
}
```

### 3. Highlighting Loyalty Points

Use a utility to emphasize key text portions in the loyalty point display:

```kotlin
binding.tvPointAnchor.text = buildHighlightedMessage(
    context = this,
    message = "Kamu Punya: 2480 Poin Loyalty",
    defaultTextAppearance = TextStyle.h3Style(
        context = this,
        color = color(UIKitR.color.grey_50),
        fontFamily = UIKitR.font.rubik
    ),
    highlightedMessages = listOf("2480 Poin Loyalty"),
    highlightedTextAppearance = listOf(
        TextStyle.h3Style(
            context = this,
            color = color(UIKitR.color.grey_70),
            fontFamily = UIKitR.font.rubik_semibold
        )
    )
)
```

## Guide

### Layout:

Use a CoordinatorLayout with an AppBarLayout and a CollapsingToolbarLayout to enable smooth scroll-based transitions.

### Components:

- PoinSearchToolbar handles search input and navigation.

- DividerChipGroup offers filter chips for category selection.

- tvPointAnchor displays the loyalty points with dynamic animation as the user scrolls.

### Animations:

The scroll listener adjusts alpha and translation properties to ensure the points display appears and disappears smoothly based on scroll progression.