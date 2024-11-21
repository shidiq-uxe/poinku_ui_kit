package id.co.edtslib.uikit.poinku.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import id.co.edtslib.uikit.poinku.R
import id.co.edtslib.uikit.poinku.utils.hapticfeedback.HapticFeedback

fun Activity.setLightStatusBar() {
    window?.statusBarColor = color(R.color.white)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window?.decorView?.windowInsetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    } else {
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}

fun Activity.setDarkStatusBar() {
    window?.statusBarColor = colorAttr(android.R.attr.colorPrimary)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window?.decorView?.windowInsetsController?.setSystemBarsAppearance(
            0,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    } else {
        window?.decorView?.systemUiVisibility = 0
    }
}

private val Context?.vibrator get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val vibratorManager = this?.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    vibratorManager.defaultVibrator
} else {
    @Suppress("DEPRECATION")
    this?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
}

fun Context?.vibratePhone(rule: HapticFeedback) {
    if (Build.VERSION.SDK_INT >= 26) {
        vibrator.vibrate(VibrationEffect.createOneShot(rule.duration, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(200)
    }
}

fun Window.setScreenBrightness(brightness: Float) {
    attributes = attributes.apply {
        screenBrightness = brightness
    }
}

fun Window.resetScreenBrightness() {
    attributes = attributes.apply {
        screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
    }
}
