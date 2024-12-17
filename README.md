# UI Kits and Components Library

This library provides a set of UI Kits and Components designed for Poinku Project use in Android applications. The library is currently under development, with ongoing efforts to expand its features and improve stability. Below is an overview of the components and utilities that have been developed so far.

## Installation

To include this library in your project, add the following dependency to your `build.gradle` file:

[![](https://jitpack.io/v/shidiq-uxe/poinku_ui_kit.svg)](https://jitpack.io/#shidiq-uxe/poinku_ui_kit)

```groovy
dependencies {
    implementation 'com.github.shidiq-uxe:poinku_ui_kit:$latestVersion'
}
```

## Table of Contents
1. [Typography & TextView](docs/Typography.md)
2. [Button](docs/Button.md)
3. [Ribbon](docs/Ribbon.md)
4. [AnimationWrapperAdapter](docs/AnimationWrapperAdapter.md)
5. [Interpolator](docs/Interpolator.md)
6. [Liquid Refresh Layout](docs/LiquidRefreshLayout.md)
7. [ImageParallaxOverlayLayout](docs/ImageParallaxOverlayLayout.md)

## Pages Reference
1. [I-Kupon Page](docs/pages/IkuponPage.md)
1. [Homepage Reward Widget /w Parallax](docs/pages/HomePageRewardWidget.md)

## Basic setup and initialization :

You could replace your theme under module manifest file for Activity Tag with themes down bellow to ensure all component used the same Theme :

```xml
  <activity
        android:name=".App"
        android:configChanges="orientation|keyboardHidden|screenSize"
        android:exported="true"
        android:screenOrientation="portrait"
        android:theme="@style/Theme.EDTS.UIKit.Poinku.ActionBar" 
        android:windowSoftInputMode="adjustResize"
        tools:ignore="LockedOrientationActivity"/>
```


| **Theme**                                                | **Material3**                     | **Description**          |
|----------------------------------------------------------|-----------------------------------|--------------------------|
| [Theme.EDTS.UIKit.Poinku](docs/ActionBar.md)             | Theme.Material3.Light             | With Action Bar Included |
| [Theme.EDTS.UIKit.Poinku.NoActionBar](docs/ActionBar.md) | Theme.Material3.Light.NoActionBar | Without Action Bar       |

    
## Roadmap

- **Animation & Interaction on Several Component** 
- **More Programmatic Style Customization**

## Contributing
Contributions are welcome! If you'd like to contribute, please fork the repository and submit a pull request.