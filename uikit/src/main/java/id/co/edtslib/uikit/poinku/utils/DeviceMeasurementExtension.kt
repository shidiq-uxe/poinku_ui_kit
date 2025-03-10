package id.co.edtslib.uikit.poinku.utils

import android.app.ActivityManager
import android.content.Context
import android.opengl.GLES20

fun isLowEndDevice(): Boolean {
    val cores = Runtime.getRuntime().availableProcessors()
    return cores <= 4
}

fun isLowRamDevice(context: Context): Boolean {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)
    return memoryInfo.totalMem / (1024 * 1024) <= 2000 // Less than 2GB RAM
}

fun isLowGPUDevice(): Boolean {
    val glVersion = GLES20.glGetString(GLES20.GL_VERSION)
    return glVersion.contains("OpenGL ES 2.0") // Older GPUs
}

fun isLowEndDevice(context: Context): Boolean {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    return activityManager.isLowRamDevice // True for low-end devices
}

fun isDeviceStruggling(): Boolean {
    val startTime = System.nanoTime()
    Thread.sleep(16) // Simulate a frame render
    val elapsedTime = (System.nanoTime() - startTime) / 1_000_000 // Convert to ms
    return elapsedTime > 20 // If a frame takes longer than 20ms, it's struggling
}

fun isLowPerformanceDevice(context: Context): Boolean {
    return isLowEndDevice() ||
            isLowRamDevice(context) ||
            isLowGPUDevice() ||
            isDeviceStruggling()
}




