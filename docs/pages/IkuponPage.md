### **i-Kupon Page**

#### **1\. Overview**

This has visual implementation of i-Kupon Page with dynamic layout transitions between **Grid (2 or 3 columns)** and **List** views in a RecyclerView. The transition between these layouts is seamless, thanks to `AutoTransition`. Each view type corresponds to a distinct visual style for displaying coupons.

* * * * *

#### **2\. XML Layouts**

-   **Grid Layout (2 or 3 columns)**: `item_grid_poinku_i_kupon.xml`
    -   Displays coupon items compactly.
    -   Used for `LayoutViewType.GRID_2` and `LayoutViewType.GRID_3`.
-   **List Layout**: `item_list_poinku_i_kupon.xml`
    -   Focuses on detailed information.
    -   Used for `LayoutViewType.LINEAR`.

* * * * *

#### **3\. Transition Implementation**

`AutoTransition` is used for smooth switching between the layouts.

```kotlin

private var layoutViewType = LayoutViewType.GRID_2
    set(value) {
        field = value
        binding.rvIKupon.addViewTransition() // Applies AutoTransition

        binding.rvIKupon.layoutManager = when (value) {
            LayoutViewType.GRID_2 -> StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            LayoutViewType.GRID_3 -> StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            LayoutViewType.LINEAR -> LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }
    }
```

**Example Transition Trigger**:

```kotlin
menuItem.setOnMenuItemClickListener {
    layoutViewType = when (layoutViewType) {
        LayoutViewType.LINEAR -> LayoutViewType.GRID_2
        LayoutViewType.GRID_2 -> LayoutViewType.GRID_3
        LayoutViewType.GRID_3 -> LayoutViewType.LINEAR
    }
    true
}
```

* * * * *

#### **4\. Reference Implementation**

-   **Class**: `GuidelinesIkuponActivity`\
    The implementation includes:
    -   **Adapter Configuration**: Uses `multiTypeAdapter` (a Custom Created Adapter to simplify Multiple TypeView adapter creation) with `DiffUtil` for efficient item updates.
    -   **View Pooling**: Optimizes performance with `RecycledViewPool`.
    -   **Dynamic Ribbon Display**: Adds ribbons (`ribbonNew`, `ribbonCouponLeft`) to highlight item properties.
    -   **Dynamic Dummy Data**: Uses random generation for demo purposes.

##### Key Adapter Setup:

The adapter uses three registered view types (`GRID_2`, `GRID_3`, and `LINEAR`) with specific binding logic for each:

```kotlin

registerViewType(
    viewType = LayoutViewType.GRID_2.ordinal,
    bindingInflater = { layoutInflater, viewGroup, attachToParent ->
        ItemGridPoinkuIcouponBinding.inflate(layoutInflater, viewGroup, attachToParent)
    },
    bind = { position, itemBinding, item ->
        // Binding logic for Grid (2 columns)
    }
)

registerViewType(
    viewType = LayoutViewType.GRID_3.ordinal,
    bindingInflater = { layoutInflater, viewGroup, attachToParent ->
        ItemGridPoinkuIcouponBinding.inflate(layoutInflater, viewGroup, attachToParent)
    },
    bind = { position, itemBinding, item ->
        // Binding logic for Grid (3 columns)
    }
)

registerViewType(
    viewType = LayoutViewType.LINEAR.ordinal,
    bindingInflater = { layoutInflater, viewGroup, attachToParent ->
        ItemListPoinkuIcouponBinding.inflate(layoutInflater, viewGroup, attachToParent)
    },
    bind = { position, itemBinding, item ->
        // Binding logic for List layout
    }
)
```

##### RecyclerView Setup:
-   **Optional Animations**: Uses `AnimationWrapperAdapter` for smooth item animations.
-   **Layout Management**: Dynamically switches layout managers based on `LayoutViewType`.

```kotlin
binding.rvIKupon.adapter = AnimationWrapperAdapter(adapter, AnimationType.Scale()).apply {
    shouldAnimateDuringScroll(false)
}
// Without Scrolling Animation
binding.rvIKupon.adapter = adapter
```

* * * * *

#### **5\. Notes for Engineers**
-   Optimize image loading with Glide or a similar library to reduce lag.
-   You Could replace the multiTypeAdapter with your own created adapter.
* * * * *

### **i-Kupon Bottom Tray**

#### **1\. Overview**

This class implements the **BottomSheetTray** functionality for the **i-Kupon List**, which is triggered based on the selected **codeType** (`QR`, `BARCODE`, `Code`) when a button in the list item is clicked. The tray displays additional content or actions related to the selected code type. This implementation uses a dynamic **BottomSheet** that doesn't rely on an XML layout, ensuring more flexibility and improved user interaction.

* * * * *

#### **2\. XML Layouts**
-   **i-Kupon Bottom Tray Layout**: `layout_icoupon_detail.xml`
-   **QR RecyclerView Layout**: `item_icoupon_qr.xml`
-   **Barcode RecyclerView Layout**: `item_icoupon_barcode.xml`
-   **Code Only RecyclerView Layout**: `item_icoupon_code.xml`

* * * * *

#### **3\. Tray Initialization**

When a button is clicked in the list item, the tray is displayed based on the `codeType`. Each type triggers different tray content, offering specific actions for **QR**, **BARCODE**, and **Code** scanning or information.

**Example to show Bottom Tray:**
```kotlin
itemBinding.btnUseCoupon.setOnClickListener {
    iKuponDetailTray.apply { this.codeType = item.codeType }.show(item.childItems)
}
```

**Example of bottomTray Initialization:**
```kotlin
fun show(items: List<DummyChildClass>) {
    dialog = BottomSheetTray.newInstance(
        title = context.getString(UIKitR.string.use_i_kupon),
        contentLayout = binding.root
    ).also {
        it.show(context.supportFragmentManager, javaClass.simpleName)
    }.apply {
        // To Show Close Icon
        this.shouldShowClose = true
        // To Show Divider
        this.titleDividerVisibility = true
        this.delegate = object : BottomTrayDelegate {
            override fun onShow(dialogInterface: DialogInterface) {
                this@GuidelinesIKuponBottomTray.binding.rvCoupon.apply {
                    this.layoutManager = object : LinearLayoutManager(context, HORIZONTAL, false) {
                        override fun canScrollHorizontally(): Boolean {
                            return if (items.size > 1) super.canScrollHorizontally() else false
                        }
                    }
                    this.adapter = this@GuidelinesIKuponBottomTray.adapter.apply {
                        this.items = items
                    }
                    this.removeItemDecoration(spacingItemDecorator)
                    this.addItemDecoration(spacingItemDecorator)

                    snapHelper.attachToRecyclerView(this)
                }
                this@GuidelinesIKuponBottomTray.binding.tvDescription.text = generateLoremIpsum(65)

                // To Adjust Screen Brightness during BottomSheet show()
                if (codeType == CodeType.QR || codeType == CodeType.Barcode) {
                    dialog?.window?.setScreenBrightness(1f)
                }
            }

            override fun onDismiss(dialogInterface: DialogInterface) {
                if (codeType == CodeType.QR || codeType == CodeType.Barcode) {
                    dialog?.window?.resetScreenBrightness()
                }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {}

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        }
    }

}
```


#### **4\. CodeType Handling**

-   **QR**: If the `codeType` is `QR`, the tray will display QR scanning options or QR-related actions.
-   **BARCODE**: For `BARCODE`, the tray displays barcode-related actions or scanning.
-   **Code**: For the generic `Code` type, the tray shows options or actions related to other code types or general scanning tasks.

Each type of tray content is dynamically adjusted based on the `codeType` passed when initializing the tray.

* * * * *

#### **5\. UX Principles for i-Kupon Tray**

-   **Brightness Adjustment**: To ensure clear visibility of scanned content (QR and Barcode), the tray includes a brightness adjustment. This prevents the content from being difficult to see under low lighting conditions.

    -   **Implementation**: The brightness level is adjusted programmatically within the `BottomSheetTray` to improve user experience during scanning.

    ```kotlin
    if (codeType == CodeType.QR || codeType == CodeType.Barcode) {
       dialog?.window?.setScreenBrightness(1f)
    }
    ```
-   **Snapping Behavior**: To create a smooth, fluid user experience when Sliding Multiple `QR`, `Barcode` and `Coupon Code`, a **snapping** behavior is applied. This involves snapping the horizontal list into place with a quick, subtle animation, making it feel more responsive and polished.

    ```kotlin
    private val snapHelper by lazy {
        LinearSnapHelper()
    }

    snapHelper.attachToRecyclerView(this)
    ```

* * * * *