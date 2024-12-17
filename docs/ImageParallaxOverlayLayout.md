# ImageParallaxOverlayLayout

The `ImageParallaxOverlayLayout` is a customizable Android component for creating a parallax effect with an image overlay. It supports multiple overlay types, gradient backgrounds, and integrates seamlessly with a horizontal sliding carousel.

## Features
- Parallax effect for images.
- Supports multiple overlay types:
  - `IllustrationOnly`
  - `IllustrationWithBackground`
  - `IllustrationWithFullBackground`
  - `BackgroundOnly`
- Dynamic integration with `HorizontalSlidingRecyclerView`.

---

## Demonstration
| `IllustrationOnly`                                                                                                      | `IllustrationWithBackground`                                                                                            | `BackgroundOnly`                                                                                                        |
|-------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------|
| ![1](https://res.cloudinary.com/dmduc9apd/image/upload/v1734074819/Parallax%20Image%20Overlay/qxfdlpj0wxyapy4wgvoh.gif) | ![2](https://res.cloudinary.com/dmduc9apd/image/upload/v1734074818/Parallax%20Image%20Overlay/ajitjhozafdsryrmi2nd.gif) | ![3](https://res.cloudinary.com/dmduc9apd/image/upload/v1734074818/Parallax%20Image%20Overlay/l5nttkpjv03bnpf7eebv.gif) |


## Setup

### Add to Layout
Include `ImageParallaxOverlayLayout` in your XML layout file:
- You could either set the image width with static value using `drawableWidth` or dynamically using `drawableWidthPercentage` (Choose 1)
- You could ignore and remove the `gradientColor` since it's not used anymore. and use `overlayBackgroundColor` instead for background color

```xml
<id.co.edtslib.uikit.poinku.overlay.ImageParallaxOverlayLayout
        android:id="@+id/parallaxOverlay"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:drawableWidth="120dp"
        app:drawableWidthPercentage="0.33"
        app:gradientColor="Blue"
        app:overlayBackgroundColor="@color/primary_30"
        app:overlayType="IllustrationWithBackground" />
```

### Attributes

| Attribute                 | Type        | Description                                                                 |
|---------------------------|-------------|-----------------------------------------------------------------------------|
| `drawableWidth`           | `dimension` | Sets the fixed width of the image overlay.                                  |
| `drawableWidthPercentage` | `float`     | Sets the width of the image overlay as a percentage of screen width.        |
| `gradientColor`           | `enum`      | Sets the gradient color (options: `Blue`, `Yellow`, `Black`, `Red`).        |
| `overlayBackgroundColor`  | `color`     | Sets the background color of the overlay.                                   |
| `overlayType`             | `enum`      | Determines the overlay type (options: see [Overlay Types](#overlay-types)). |

Usage
-----

### Set Image URL

Bind an image URL to the overlay using the `imageUrl` property:

```kotlin
val parallaxOverlay = findViewById<ImageParallaxOverlayLayout>(R.id.parallaxOverlay)
parallaxOverlay.imageUrl = "https://example.com/image.jpg"
```

* * * * *

### Overlay Types

Choose an overlay type to define how the image and background are displayed:

-   **IllustrationOnly**: Displays the image without any background.
-   **IllustrationWithBackground**: Displays the image with a solid background.
-   **IllustrationWithFullBackground**: Extends the background to cover the entire layout.
-   **BackgroundOnly**: Displays only the gradient background, no image.

Set the overlay type programmatically:

```kotlin
parallaxOverlay.overlayType = ImageParallaxOverlayLayout.OverlayType.IllustrationWithBackground
```

* * * * *

### Customize Gradient Background

Change the gradient color for the background:

```kotlin
parallaxOverlay.gradientColor = ImageParallaxOverlayLayout.GradientColor.Red
```

* * * * *

### Integration with HorizontalSlidingRecyclerView

`ImageParallaxOverlayLayout` automatically attaches to a `HorizontalSlidingRecyclerView` child if present. Ensure your RecyclerView is inside the layout.

Example:

```xml
<id.co.edtslib.uikit.poinku.overlay.ImageParallaxOverlayLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

  <id.co.edtslib.uikit.poinku.recyclerview.HorizontalSlidingRecyclerView
          android:layout_width="match_parent"
          android:layout_height="wrap_content" />

</id.co.edtslib.uikit.poinku.overlay.ImageParallaxOverlayLayout>
```

* * * * *

### Programmatic Background Color

Set a solid background color dynamically:

```kotlin
parallaxOverlay.overlayBackgroundColor = context.color(R.color.primary_30)
```

* * * * *

### Image Loader

By default, the component uses Glide to load images. You can customize this behavior:

```kotlin
parallaxOverlay.bindImageUrl { imageView, url ->
  Glide.with(imageView.context).load(url).into(imageView)
}
```

* * * * *

Advanced
--------

### Extract Dominant Colors

If using `IllustrationWithFullBackground`, the layout extracts dominant colors from the image and applies them as a gradient background.