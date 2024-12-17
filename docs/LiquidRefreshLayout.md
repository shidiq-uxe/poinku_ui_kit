LiquidRefreshLayout
===================

**LiquidRefreshLayout** is a custom pull-to-refresh layout for Android that provides a visually engaging liquid animation during refresh actions.

* * * * *

Features
--------

-   Fully customizable header background and foreground colors.
-   Adjustable circle radius for liquid effect.
-   Customizable loading icon.
-   Callbacks for refresh events: `refreshing` and `completeRefresh`.

* * * * *

## Demonstration
![LRL](https://res.cloudinary.com/dmduc9apd/image/upload/v1734076870/Liquid%20Refresh%20Layout/wcoaz2qupsgcoh8a87ix.gif)

Usage
-----

### 1\. Adding LiquidRefreshLayout in XML

``` xml
<com.yourpackage.LiquidRefreshLayout
    android:id="@+id/liquidRefreshLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:backgroundColor="@color/headerBackground"
    app:foregroundColor="@color/headerForeground"
    app:circleRadius="50dp"
    app:icon="@drawable/ic_loading_icon">

    <!-- Your scrollable content -->
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</com.yourpackage.LiquidRefreshLayout>
```

### 2\. Custom Attributes

| Attribute         | Description                        | Default Value |
|-------------------|------------------------------------|---------------|
| `backgroundColor` | Sets the header's background color | Transparent   |
| `foregroundColor` | Sets the header's foreground color | Transparent   |
| `circleRadius`    | Adjusts the radius of the liquid   | 40dp          |
| `icon`            | Sets the loading icon drawable     | None          |

### 3\. Setting Up Listeners

Use `setOnRefreshListener` to handle refresh events.

```kotlin
liquidRefreshLayout.setOnRefreshListener(object : LiquidRefreshLayout.OnRefreshListener {
    override fun refreshing() {
        // Start your data loading or refresh logic here
    }

    override fun completeRefresh() {
        // Call this when the refresh is done
        liquidRefreshLayout.isRefreshing = false
    }
})
```

* * * * *

Example Implementation
----------------------

### Kotlin Example

```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var liquidRefreshLayout: LiquidRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        liquidRefreshLayout = findViewById(R.id.liquidRefreshLayout)
        liquidRefreshLayout.setOnRefreshListener(object : LiquidRefreshLayout.OnRefreshListener {
            override fun refreshing() {
                // Simulate data loading
                Handler(Looper.getMainLooper()).postDelayed({
                    liquidRefreshLayout.setRefreshing(false)
                }, 2000)
            }

            override fun completeRefresh() {
                // Add custom completion logic if needed
            }
        })
    }
}
```

* * * * *

Notes
-----

-   Ensure the child content within `LiquidRefreshLayout` is scrollable (e.g., `RecyclerView`, `ScrollView`).
-   The `circleRadius` attribute expects a dimension (e.g., `dp`).
-   For dynamic updates, use the following setters in your Kotlin/Java code:

    ```kotlin
    liquidRefreshLayout.headerBackgroundColor = Color.RED
    liquidRefreshLayout.headerForegroundColor = Color.WHITE
    liquidRefreshLayout.headerCircleRadius = 60
    liquidRefreshLayout.loadingIcon = ContextCompat.getDrawable(this, R.drawable.new_icon)
    ```