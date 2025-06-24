# RegisterProgressTrackerGroup

![ProgressTracker](https://res.cloudinary.com/dmduc9apd/image/upload/v1750754342/ProgressTracker_s6vowk.gif)

## Basic Usage

### XML Layout

```xml
<id.co.edtslib.uikit.poinku.progressindicator.RegisterProgressTrackerGroup
    android:id="@+id/ptgTracker"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginTop="@dimen/xs"
    app:layout_constraintTop_toBottomOf="@id/lavIntroIllustration"
    android:paddingHorizontal="@dimen/s"
    android:clipToPadding="false"/>
```

### Kotlin Code

```kotlin
class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Start with step 1 (index 0)
        binding.progressTracker.selectStep(0)
    }
    // Optional 
    private fun moveToNextStep() {
        progressTracker.goToNextStep()
    }
    // Optional
    private fun moveToPreviousStep() {
        progressTracker.goToPreviousStep()
    }
}
```

## Properties and Customization

### Step Labels

You can customize the labels for each step:

```kotlin
progressTracker.tabItems = Triple(
    "Personal Info",
    "Verification", 
    "Create PIN"
)
```

| Step | Default Value |
|------|---------------|
| Step 1 | "Isi Data Diri" |
| Step 2 | "Verifikasi" |
| Step 3 | "Buat Pin" |

### Text Appearance

Customize the text styling for badges and labels:

```kotlin
// Badge text appearance
progressTracker.badgeTextAppearance = R.style.TextAppearance_Rubik_B3_Medium
// Tracker label text appearance  
progressTracker.trackerTextAppearance = R.style.TextAppearance_Rubik_B4_Medium
```

## Animation Behavior

### Step Selection Animation

When a step is selected, the component performs these animations:

| Step             | Animation Behavior                             |
|------------------|------------------------------------------------|
| Step 1 (index 0) | Immediate bounce animation after 750ms delay   |
| Step 2 (index 1) | Progress bar animates first, then step bounces |
| Step 3 (index 2) | Progress bar animates first, then step bounces |

### Animation Timing

| Animation Type         | Duration | Details                                 |
|------------------------|----------|-----------------------------------------|
| Progress bar animation | 500ms    | 750ms start delay                       |
| Bounce animation       | Variable | Spring-based with medium bouncy damping |
| Scale effect           | Variable | 1.15x scale during bounce               |

## Color Theming

The component uses these color resources:

| Color Resource       | Usage                        |
|----------------------|------------------------------|
| `R.color.primary_30` | Active/completed state color |
| `R.color.grey_40`    | Inactive state color         |

## Notes


- Future implementation | Will be changed to `ProgressTrackerGroup` instead 
- Step indices | 0-based (0, 1, 2)
- Navigation bounds | Component prevents navigation beyond valid step ranges
- View binding | Uses `ViewProgressTrackerGroupBinding` for view binding 