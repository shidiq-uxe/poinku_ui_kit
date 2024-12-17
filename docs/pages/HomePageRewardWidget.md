# Documentation for GuidelinesHomeCouponActivity

### Overview
GuidelinesHomeCouponActivity is an Android Activity that displays a dynamic list of sections, each representing a different type of promotional content, such as recommendations, favorite promos, and expiring coupons. The activity uses a RecyclerView with multiple view types, managed by a HomeSectionAdapter, to ensure smooth and scalable UI updates.

## Key Features:

- Dynamic UI updates using DiffUtil.
- Support for skeleton loading views.
- Integration with a custom LiquidRefreshLayout for refreshing content.
- Parallax effects for enhancing visual appeal. (Based on Klik Project)

## XML Layouts

- The layout for this activity is defined in activity_guidelines_home_coupon.xml. The key components include:
- A root container with ViewCompat.setOnApplyWindowInsetsListener for managing system insets.
- A RecyclerView for displaying dynamic sections, bound to the adapter.
- A LiquidRefreshLayout for handling refresh gestures.

### Key XML Attributes:

```xml

<edts.poinku.uikit.LiquidRefreshLayout
    android:id="@+id/lpr"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" >

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rvMain"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />

</edts.poinku.uikit.LiquidRefreshLayout>
```

## Transition Implementations

This activity uses DiffUtil for efficient updates to the RecyclerView items. Changes to the homeSections list trigger adapter updates via:

```kotlin
private fun updateDiffer() {
    sectionAdapter.items = homeSections
}
```
Skeleton views are displayed while content loads, and transitions occur seamlessly without requiring notifyDataSetChanged() calls.

## Adapter Setup
`HomeSectionAdapter` is a multi-type adapter with the following view types:

- PROMO_RECOMMENDATION_SKELETON
- PROMO_RECOMMENDATION
- FAVORITE_PROMO
- NEARLY_EXPIRE_PROMO

### Example View Type Binding:
```kotlin
registerViewType(
    viewType = HomeSectionType.PROMO_RECOMMENDATION,
    bindingInflater = { inflater, parent, attachToParent ->
        ItemSectionIKuponCarouselBinding.inflate(inflater, parent, attachToParent)
    },
    bind = { position, itemBinding, item ->
        setPromoRecommendation(position, itemBinding as ItemSectionIKuponCarouselBinding, item as HomeSectionType.PromoRecommendation)
    }
)
```

## DiffUtil Configuration:

The adapter uses HomeDiffCallback for determining item changes:

```kotlin
private class HomeDiffCallback : DiffUtil.ItemCallback<HomeSectionType>() {
    override fun areItemsTheSame(oldItem: HomeSectionType, newItem: HomeSectionType) = oldItem == newItem
    override fun areContentsTheSame(oldItem: HomeSectionType, newItem: HomeSectionType ) = oldItem.ordinal == newItem.ordinal
}
```

## UX Principles

1. Parallax Effects: Enhances the user experience by adding a visual overlay to section headers.
```kotlin
binding.carouselOverlay.overlayType = item.type
binding.carouselOverlay.imageUrl = item.imageUrl
```

2. Skeleton Loading: Displays placeholders while data is being fetched or updated, ensuring a responsive feel.
3. Dynamic Refreshing: The LiquidRefreshLayout provides visual feedback during refresh gestures.
```kotlin
binding.main.setOnRefreshListener(object : LiquidRefreshLayout.OnRefreshListener {
    override fun refreshing() {
        homeSections = homeSections.toMutableList().apply {
            removeAt(0)
            add(0, HomeSectionType.PromoRecommendationSkeleton)
        }
        
        updateDiffer()

        Handler(Looper.getMainLooper()).postDelayed(
            { binding.main.finishRefreshing() }, 
            3000
        ) 
    }
    override fun completeRefresh() { 
        homeSections = homeSections.toMutableList().apply { 
            removeAt(0) 
            add(0, HomeSectionType.PromoRecommendation( imageUrl = "https://res.cloudinary.com/...", title = "Rekomendasi Kupon Akhir Tahun!", description = "Tukarkan poin loyalty jadi kupon.", type = ImageParallaxOverlayLayout.OverlayType.IllustrationOnly, data = dummyItems ))
        } 
        updateDiffer() 
    }
})
```
## Developer Notes

- Dummy Data: The activity uses dummyItems for testing purposes. Replace this with real data fetched from an API or database.
- Parallax Overlays: Configure ImageParallaxOverlayLayout.OverlayType for each section to control the visual style.
- Testing: Validate performance with large datasets, as parallax and animations might affect smooth scrolling.

## Future Enhancements

- Add pagination support for handling larger datasets.
- Implement swipe-to-refresh alongside the liquid refresh layout for user preference.
- Support dark mode styling for improved accessibility.