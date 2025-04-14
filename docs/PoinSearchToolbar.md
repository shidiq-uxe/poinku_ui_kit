# PoinSearchToolbar

`PoinSearchToolbar` is a custom `FrameLayout` that provides a MotionLayout-based search toolbar with animated transitions and integrated back navigation handling. It delegates text input and navigation behavior via `PoinSearchToolbarDelegate`.

## Overview

This component consists of:

-   A `SearchBar` input field with built-in clear functionality

-   A back navigation icon with motion-aware behavior

-   Optional override for back navigation handling

-   MotionLayout transition from collapsed to expanded search

## XML Usage

You can add it directly to your layout XML:

```xml
<id.co.edtslib.uikit.poinku.toolbar.poin.PoinSearchToolbar
    android:id="@+id/poinSearchToolbar"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

## Public API

### Properties

```kotlin
val searchBar: SearchBar
var shouldOverrideBack: Boolean
```

-   `searchBar`: Access to the internal `SearchBar` view.

-   `shouldOverrideBack`: Set this to `true` to prevent the default back behavior and handle it manually through `delegate`.

* * * * *

### Delegate

```kotlin
private var delegate: PoinSearchToolbarDelegate?
```

To handle actions, implement `PoinSearchToolbarDelegate`:

```kotlin
interface PoinSearchToolbarDelegate {
    fun onSearchBarType(text: String)
    fun onBackNavigationClick(view: View)
}
```

Set the delegate:

```kotlin
poinSearchToolbar.delegate = object : PoinSearchToolbarDelegate {
    override fun onSearchBarType(text: String) {
        // Called when user types
    }

    override fun onBackNavigationClick(view: View) {
        // Called on back icon click
    }
}
```

## Behavior

### Text Input

-   Typing into the search bar triggers `onSearchBarType(text: String)`

-   Clicking the close icon clears the text

### Back Navigation

-   If `shouldOverrideBack == false`:

    -   If toolbar is expanded: it will collapse and hide keyboard

    -   If already collapsed: it triggers `onBackPressed()`

-   If `shouldOverrideBack == true`:

    -   You must handle all back logic in the delegate

## Transition

-   When the MotionLayout reaches the `end` state, the input field automatically gains focus and shows the keyboard.

## Example Usage

```kotlin
binding.poinSearchToolbar.delegate = object : PoinSearchToolbarDelegate {
    override fun onSearchBarType(text: String) {
        viewModel.filterList(text)
    }

    override fun onBackNavigationClick(view: View) {
        // Optional: analytics, logging, or custom back handling
    }
}

binding.poinSearchToolbar.shouldOverrideBack = false
```

* * * * *

## Notes

-   `searchBar.editText` can be accessed directly if more control is needed (e.g. to change IME options).