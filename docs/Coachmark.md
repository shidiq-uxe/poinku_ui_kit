Coachmark
================

`CoachMarkOverlay` is a customizable coachmark system for Android that helps guide users through an interface by highlighting key UI elements with descriptive text and animations.
It has 2 elements: Spotlight as the Highlighter and Coachmark that behaves as floating information dialog anchored by Target Location.

Features
--------

-   Supports multiple coachmark targets.

-   Highlights UI elements with a title and description.

-   Uses an overlay to guide users step by step.

-   Fully customizable for different UI scenarios.

Usage
-----

### 1\. Add Placeholder Views in XML

Each target for the coachmark requires a `View` in the XML layout. These views act as imaginary boxes to define the target areas for the coachmark.

```xml
<!-- Placeholder for the 2nd Coachmark Target -->
<View
    android:id="@+id/coachMarkPlaceholder2"
    android:layout_width="0dp"
    android:layout_height="0dp"
    app:layout_constraintTop_toTopOf="@id/ivBucketStampImage"
    app:layout_constraintStart_toStartOf="@id/ivBucketStampImage"
    app:layout_constraintEnd_toEndOf="@id/coachmarkBarrier"
    app:layout_constraintBottom_toBottomOf="@id/ivBucketStampImage" />
```

### 2\. Define Coachmark Targets in Code

Prepare a list of target views where the coachmarks should appear:

```kotlin
private val placeholderTargets = mutableListOf<View>()

private fun showCoachmark() {
    val coachmarkTitles = resources.getStringArray(R.array.stamp_bucket_coachmark_titles)
    val coachmarkDescriptions = resources.getStringArray(R.array.stamp_bucket_coachmark_descriptions)

    // Target 1 - Bucket Chip Group
    placeholderTargets.add(binding.coachMarkPlaceholder1)

    binding.rvBucket.post {
        binding.rvBucket.findViewHolderForAdapterPosition(0)?.apply {
            // Target 2 - Bucket RecyclerView Item
            itemView.findViewById<View>(R.id.coachMarkPlaceholder2)?.let {
                placeholderTargets.add(it)
            }

            // Target 3 - Stamp Card
            itemView.findViewById<View>(R.id.rvStamps)?.let {
                placeholderTargets.add(it)
            }

            // Target 4 - Bucket RecyclerView See All
            itemView.findViewById<View>(R.id.tvSeeAll)?.let {
                placeholderTargets.add(it)
            }
        }

        // Map into coachmark items
        val coachmarkItems = placeholderTargets.indices.map { index ->
            CoachMarkData(
                target = placeholderTargets[index],
                title = coachmarkTitles[index],
                description = coachmarkDescriptions[index]
            )
        }

        // 1 Second Delay before showing Coachmark & Spotlight or after loading all the API
        Handler(Looper.getMainLooper()).postDelayed({
            CoachMarkOverlay.Builder(this)
                .setCoachMarkItems(coachmarkItems)
                .setCoachMarkDelegate(delegate) // Optional 
                .build()
        }, 1000L)
    }
}
```

### 3\. Result
![Coachmark](https://res.cloudinary.com/dmduc9apd/image/upload/v1741588540/Coachmark/b4xujq7de7i3fqldejhr.gif)

### 4\. Explanation

-   `**CoachMarkData**` is used to store information about each coachmark target.

-   **Imaginary Placeholder Views** are added in XML to define target areas for coachmarks.

-   **RecyclerView Targets**: Ensure that views inside `RecyclerView` are fetched after it is fully loaded using `post {}`.

-   `**Handler**` **with Delay**: Used to make sure the coachmark appears after API loading or UI updates.

Notes
-----

-   The imaginary `View` placeholders are necessary when targeting elements that don't have fixed boundaries or are dynamically created.

-   Ensure that the coachmark overlay is only triggered after all UI elements are available and measured.