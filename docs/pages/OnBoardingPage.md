# On Boarding

This guide demonstrates a responsive, animated interface designed for Boarding page. 

## Layout Hierarchy
[XML Layout -> `activity_guidelines_poin_search.xml`](https://github.com/shidiq-uxe/poinku_ui_kit/blob/main/app/src/main/res/layout/activity_on_boarding.xml)
```
ViewGroup
  └── BoardingPagerView  
```
- [BoardingPagerView](https://github.com/shidiq-uxe/poinku_ui_kit/blob/main/uikit/src/main/java/id/co/edtslib/uikit/poinku/boarding/BoardingPagerView.kt)

## Key Code Snippets
[Class Implementation](https://github.com/shidiq-uxe/poinku_ui_kit/blob/main/app/src/main/java/id/co/edtslib/poinkuuikit/boarding/OnBoardingActivity.kt)

### 1. Fullscreen Mode

There are several ways to toggle the fullscreen mode, but i suggest to use these two instead:

#### Using Edge-to-Edge
```kotlin
enableEdgeToEdge(
    statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
)
setContentView(R.layout.activity_on_boarding)

ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
    val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
    v.updatePadding(navigationBars.left, navigationBars.top, navigationBars.right, navigationBars.bottom)
    insets
}
```

#### Alternative Way (Using decorView InsetsListener)
```kotlin
setSystemBarStyle(
    statusBarStyle = SystemBarStyle.Dark(Color.TRANSPARENT),
    navigationBarStyle = SystemBarStyle.Dark(Color.TRANSPARENT)
)

WindowCompat.setDecorFitsSystemWindows(window, false)

window.decorView.setOnApplyWindowInsetsListener { view, insets ->
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        binding.root.updatePadding(0, 0, 0, navBarInsets.bottom)
    }
    insets
}
```

### 2. Initializing Items & Auto Scroll

Set up the Boarding Item Slides by assigning `items`, and auto scrolling by setting `autoScrollInterval`

```kotlin
binding.boardingPagerView.apply {
    // Auto Scrolling 
    autoScrollInterval = 3.seconds.toInt()
    // Sliding Items
    items = listOf<Boarding>(
        Boarding(
            image = drawable(R.drawable.ill1),
            title = "Kumpulkan Poin dan Stamp Buat Dapetin Kejutan!",
            description = "Kumpulin poin serta stamp dari setiap transaksi dan tukarkan dengan kupon menarik di sini!"
        ),
        Boarding(
            image = drawable(R.drawable.ill2),
            title = "Dapetin Diskon, Bonus, Sampai Gratisan!",
            description = "Jangan lupa untuk gunakan kupon untuk mendapatkan banyak keuntungan!"
        ),
        Boarding(
            image = drawable(R.drawable.ill3),
            title = "Semakin Sering Belanja, Semakin Untung!",
            description = "Makin sering kamu belanja semakin banyak bonus, serta diskon yang bisa kamu dapetin."
        ),
    )
}
```

### 3. Listening to Delegations

```kotlin
delegate = this@OnBoardingActivity // or any other implementations
```

```kotlin
override fun onPageSelected(position: Int, fakePosition: Int) {
    TODO("Not yet implemented")
}

override fun onPageScrolled(
    position: Int,
    positionOffset: Float,
    positionOffsetPixels: Int,
    fakePosition: Int
) {
    TODO("Not yet implemented")
}

override fun onRegisterButtonClicked(view: View) {
    TODO("Not yet implemented")
}

override fun onLoginButtonClicked(view: View) {
    TODO("Not yet implemented")
}
```

### Animations & Preview:
![OnBoarding](https://res.cloudinary.com/dmduc9apd/image/upload/v1745913613/ezgif-724f73f3312a91_k2puhk.gif)

