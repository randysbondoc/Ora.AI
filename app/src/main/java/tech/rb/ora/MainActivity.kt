package tech.rb.ora

import android.Manifest
import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.PixelCopy
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import coil.Coil
import coil.request.ImageRequest
import coil.target.Target
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import tech.rb.ora.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var backgroundTarget: Target? = null

    private val dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())
    private val formatter24h = DateTimeFormatter.ofPattern("HHmmss")
    private val formatter12h = DateTimeFormatter.ofPattern("hhmmss")
    private val formatterAmPm = DateTimeFormatter.ofPattern("a", Locale.getDefault())

    // Coroutine Jobs to replace Handlers
    private var clockJob: Job? = null
    private var dimJob: Job? = null
    private var hideButtonJob: Job? = null
    private var hideSystemBarsJob: Job? = null
    private var autoColorJob: Job? = null

    private var hideButtonDelayMs = 6000L
    private val UPDATE_INTERVAL_MS = 200L
    private val IDLE_DELAY_MS = 3 * 60 * 1000L
    private val HIDE_SYSTEM_BARS_DELAY_MS = 6 * 1000L

    private val settingsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        applyAllSettings()
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            captureAndSaveScreenshot()
        } else {
            Toast.makeText(this, "Permission denied. Cannot save screenshot.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        setupFullScreen()
        keepScreenOn()
        startClock()
        setupSettingsButton()
    }

    override fun onResume() {
        super.onResume()
        sharedPreferences.registerOnSharedPreferenceChangeListener(settingsListener)
        applyAllSettings()
        showAndResetHideButtonTimer()
        resetHideSystemBarsTimer()
        resetDimTimer()
        checkForScreenshotRequest()
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(settingsListener)
        // Cancel jobs to prevent them from running in the background
        hideButtonJob?.cancel()
        hideSystemBarsJob?.cancel()
        dimJob?.cancel()
        stopAutoColorChange()
    }


    override fun onDestroy() {
        super.onDestroy()
        // lifecycleScope is automatically cancelled, but it's good practice
        // to nullify for clarity and potential memory management benefits.
        clockJob = null
        backgroundTarget = null
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        resetDimTimer()
        showAndResetHideButtonTimer()
        resetHideSystemBarsTimer()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            resetHideSystemBarsTimer()
        }
    }

    private fun checkForScreenshotRequest() {
        if (sharedPreferences.getBoolean("take_screenshot_flag", false)) {
            sharedPreferences.edit().putBoolean("take_screenshot_flag", false).apply()
            // Using a coroutine for a simple delay
            lifecycleScope.launch {
                delay(300)
                requestScreenshotPermissionsAndCapture()
            }
        }
    }

    private fun requestScreenshotPermissionsAndCapture() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    captureAndSaveScreenshot()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        } else {
            captureAndSaveScreenshot()
        }
    }

    private fun captureAndSaveScreenshot() {
        val view = binding.root
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val locationOfViewInWindow = IntArray(2)
            view.getLocationInWindow(locationOfViewInWindow)
            val rect = Rect(locationOfViewInWindow[0], locationOfViewInWindow[1],
                locationOfViewInWindow[0] + view.width, locationOfViewInWindow[1] + view.height)

            PixelCopy.request(window, rect, bitmap, { copyResult ->
                if (copyResult == PixelCopy.SUCCESS) {
                    saveBitmapToStorage(bitmap)
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Screenshot failed with PixelCopy", Toast.LENGTH_SHORT).show()
                    }
                }
            }, Handler(Looper.getMainLooper())) // PixelCopy requires a Handler, this is a valid exception
        } else {
            try {
                val canvas = Canvas(bitmap)
                view.draw(canvas)
                saveBitmapToStorage(bitmap)
            } catch (t: Throwable) {
                Log.e("MainActivity", "Failed to capture screenshot with fallback", t)
                Toast.makeText(this, "Failed to save screenshot: ${t.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveBitmapToStorage(bitmap: Bitmap) {
        val timestamp = System.currentTimeMillis()
        val filename = "Ora_Screenshot_$timestamp.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Ora")
        }

        try {
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                contentResolver.openOutputStream(uri).use { outputStream ->
                    if (outputStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        runOnUiThread {
                            Toast.makeText(this, "Screenshot saved to Pictures/Ora", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        throw IOException("Failed to get output stream.")
                    }
                }
            } else {
                throw IOException("Failed to create new MediaStore record.")
            }
        } catch (t: Throwable) {
            Log.e("MainActivity", "Failed to save bitmap", t)
            runOnUiThread {
                Toast.makeText(this, "Failed to save screenshot: ${t.message}", Toast.LENGTH_LONG).show()
            }
        } finally {
            bitmap.recycle()
        }
    }

    private fun showAndResetHideButtonTimer() {
        binding.settingsButton.visibility = View.VISIBLE
        binding.settingsButton.alpha = 1f

        hideButtonJob?.cancel()
        hideButtonJob = lifecycleScope.launch {
            delay(hideButtonDelayMs)
            binding.settingsButton.animate().alpha(0f).setDuration(500).withEndAction {
                binding.settingsButton.visibility = View.GONE
            }.start()
        }
    }

    private fun resetHideSystemBarsTimer() {
        hideSystemBarsJob?.cancel()
        hideSystemBarsJob = lifecycleScope.launch {
            delay(HIDE_SYSTEM_BARS_DELAY_MS)
            setupFullScreen()
        }
    }

    private fun setupSettingsButton() {
        binding.settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startClock() {
        clockJob?.cancel()
        clockJob = lifecycleScope.launch {
            while (isActive) {
                val now = LocalDateTime.now()
                val is12Hour = sharedPreferences.getString("time_format", "24h") == "12h"
                val timeFormatter = if (is12Hour) formatter12h else formatter24h
                val timeString = now.format(timeFormatter)

                updateDigit(binding.hour1, timeString[0].toString())
                updateDigit(binding.hour2, timeString[1].toString())
                updateDigit(binding.minute1, timeString[2].toString())
                updateDigit(binding.minute2, timeString[3].toString())
                updateDigit(binding.second1, timeString[4].toString())
                updateDigit(binding.second2, timeString[5].toString())
                binding.dateTextView.text = now.format(dateFormatter)

                val showAmPm = sharedPreferences.getBoolean("show_ampm", true)
                binding.ampmTextView.visibility = if (is12Hour && showAmPm) View.VISIBLE else View.GONE
                if (is12Hour && showAmPm) {
                    binding.ampmTextView.text = now.format(formatterAmPm)
                }
                delay(UPDATE_INTERVAL_MS)
            }
        }
    }

    private fun applyAllSettings() {
        // This single function will now handle applying all settings on resume
        binding.dateTextView.visibility = if (sharedPreferences.getBoolean("show_date", true)) View.VISIBLE else View.GONE

        val allDigitTextViews = listOf(binding.hour1, binding.hour2, binding.minute1, binding.minute2, binding.second1, binding.second2)
        val separatorTextViews = listOf(binding.separator1, binding.separator2)

        val clockFontFile = sharedPreferences.getString("clock_font", "orbitron_regular.ttf")
        loadAndApplyFont(clockFontFile, allDigitTextViews + separatorTextViews + listOf(binding.ampmTextView))

        val dateFontFile = sharedPreferences.getString("date_font", "josefin_sans_regular.ttf")
        loadAndApplyFont(dateFontFile, listOf(binding.dateTextView))

        val ampmFontFile = sharedPreferences.getString("ampm_font", "orbitron_regular.ttf")
        loadAndApplyFont(ampmFontFile, listOf(binding.ampmTextView))

        val clockSize = sharedPreferences.getInt("clock_size", 50)
        val dateSize = sharedPreferences.getInt("date_size", 18)
        val ampmSize = sharedPreferences.getInt("ampm_size", 24)
        (allDigitTextViews + separatorTextViews).forEach { it.setTextSize(TypedValue.COMPLEX_UNIT_SP, clockSize.toFloat()) }
        binding.dateTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, dateSize.toFloat())
        binding.ampmTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, ampmSize.toFloat())

        hideButtonDelayMs = sharedPreferences.getInt("hide_button_delay", 6) * 1000L

        applyShadow(allDigitTextViews + separatorTextViews, sharedPreferences.getBoolean("clock_shadow", true))
        applyShadow(listOf(binding.dateTextView), sharedPreferences.getBoolean("date_shadow", true))
        applyShadow(listOf(binding.ampmTextView), sharedPreferences.getBoolean("ampm_shadow", true))

        val useDigitPadding = sharedPreferences.getBoolean("digit_padding", false)
        val paddingInDp = 4
        val paddingInPx = (paddingInDp * resources.displayMetrics.density).toInt()

        if (useDigitPadding) {
            allDigitTextViews.forEach { it.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx) }
        } else {
            allDigitTextViews.forEach { it.setPadding(0, 0, 0, 0) }
        }

        applyDigitBackgrounds(allDigitTextViews)

        val background = sharedPreferences.getString("background_select", "black")
        updateBackgroundAndTextColor(background)

        val showSeparator = sharedPreferences.getBoolean("show_separator", true)
        separatorTextViews.forEach { it.visibility = if (showSeparator) View.VISIBLE else View.INVISIBLE }

        if (sharedPreferences.getBoolean("auto_color_change_toggle", false)) {
            startAutoColorChange()
        } else {
            stopAutoColorChange()
        }

        binding.clockContainer.visibility = if (sharedPreferences.getBoolean("show_clock", true)) View.VISIBLE else View.GONE

        val clockVerticalPercent = sharedPreferences.getInt("clock_vertical_position", 50)
        val dateVerticalPercent = sharedPreferences.getInt("date_vertical_position", 60)
        val dateHorizontalPercent = sharedPreferences.getInt("date_horizontal_position", 50)

        val clockVerticalBias = clockVerticalPercent / 100f
        val dateVerticalBias = dateVerticalPercent / 100f
        val dateHorizontalBias = dateHorizontalPercent / 100f

        val clockParams = binding.clockContainer.layoutParams as ConstraintLayout.LayoutParams
        clockParams.verticalBias = clockVerticalBias
        binding.clockContainer.layoutParams = clockParams

        val dateParams = binding.dateTextView.layoutParams as ConstraintLayout.LayoutParams
        dateParams.verticalBias = dateVerticalBias
        dateParams.horizontalBias = dateHorizontalBias
        binding.dateTextView.layoutParams = dateParams
    }

    private fun startAutoColorChange() {
        if (autoColorJob?.isActive == true) return // Don't start a new one if it's already running

        val intervalSeconds = sharedPreferences.getInt("auto_color_change_interval", 10)
        val intervalMillis = intervalSeconds * 1000L
        val colors = resources.getIntArray(R.array.color_picker_palette)

        autoColorJob = lifecycleScope.launch {
            while (isActive) {
                delay(intervalMillis)
                val editor = sharedPreferences.edit()
                val randomColor = colors.random()
                editor.putInt("clock_color", randomColor)
                editor.putInt("date_color", colors.random())
                editor.putInt("ampm_color", randomColor)
                editor.putInt("separator_color", randomColor)
                editor.apply()
            }
        }
    }

    private fun stopAutoColorChange() {
        autoColorJob?.cancel()
        autoColorJob = null
    }

    private fun applyDigitBackgrounds(digitViews: List<TextView>) {
        val style = sharedPreferences.getString("digit_bg_style", "filled")
        val defaultColor = ContextCompat.getColor(this, R.color.digit_background)
        val color = sharedPreferences.getInt("digit_bg_color", defaultColor)
        val alphaPercent = sharedPreferences.getInt("digit_bg_alpha", 100)
        val alphaValue = (alphaPercent * 255) / 100
        val finalColor = ColorUtils.setAlphaComponent(color, alphaValue)

        val strokeWidth = (2 * resources.displayMetrics.density).toInt()

        digitViews.forEach {
            if (style == "none") {
                it.background = null
            } else {
                val drawable = GradientDrawable()
                drawable.shape = GradientDrawable.RECTANGLE

                if (style == "filled") {
                    drawable.setColor(finalColor)
                } else { // outline
                    drawable.setColor(Color.TRANSPARENT)
                    drawable.setStroke(strokeWidth, finalColor)
                }
                it.background = drawable
            }
        }
    }

    private fun applyShadow(textViews: List<TextView>, isEnabled: Boolean) {
        val radius = 10f
        val dx = 5f
        val dy = 5f
        val shadowColor = Color.BLACK
        if (isEnabled) {
            textViews.forEach { it.setShadowLayer(radius, dx, dy, shadowColor) }
        } else {
            textViews.forEach { it.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT) }
        }
    }

    private fun updateBackgroundAndTextColor(backgroundValue: String?) {
        val allDigitTextViews = listOf(binding.hour1, binding.hour2, binding.minute1, binding.minute2, binding.second1, binding.second2)
        val separatorTextViews = listOf(binding.separator1, binding.separator2)
        val whiteColor = ContextCompat.getColor(this, R.color.white)
        val blackColor = ContextCompat.getColor(this, R.color.black)
        val greyColor = ContextCompat.getColor(this, R.color.text_grey)
        val userClockColor = sharedPreferences.getInt("clock_color", whiteColor)
        val userDateColor = sharedPreferences.getInt("date_color", greyColor)
        val userAmPmColor = sharedPreferences.getInt("ampm_color", whiteColor)
        val userSeparatorColor = sharedPreferences.getInt("separator_color", userClockColor)

        backgroundTarget = null

        when (backgroundValue) {
            "white" -> {
                binding.mainContainer.setBackgroundColor(whiteColor)
                (allDigitTextViews + binding.ampmTextView + separatorTextViews).forEach { it.setTextColor(blackColor) }
                binding.dateTextView.setTextColor(blackColor)
            }
            "custom" -> {
                val path = sharedPreferences.getString("custom_background_path", null)
                if (path != null) {
                    val imageFile = File(path)
                    backgroundTarget = object : Target {
                        override fun onSuccess(result: Drawable) {
                            if (!isFinishing) binding.mainContainer.background = result
                        }
                        override fun onError(error: Drawable?) {
                            if (!isFinishing) binding.mainContainer.setBackgroundColor(blackColor)
                        }
                    }
                    val request = ImageRequest.Builder(this)
                        .data(imageFile)
                        .target(backgroundTarget)
                        .build()
                    Coil.imageLoader(this).enqueue(request)
                } else {
                    binding.mainContainer.setBackgroundColor(blackColor)
                }
                allDigitTextViews.forEach { it.setTextColor(userClockColor) }
                binding.ampmTextView.setTextColor(userAmPmColor)
                binding.dateTextView.setTextColor(userDateColor)
                separatorTextViews.forEach { it.setTextColor(userSeparatorColor) }
            }
            else -> {
                val bgResId = when (backgroundValue) { "galaxy" -> R.drawable.background_galaxy; "stars" -> R.drawable.stars; "lab" -> R.drawable.lab; "universe" -> R.drawable.universe; else -> R.color.black }
                if (backgroundValue == "black") binding.mainContainer.setBackgroundColor(blackColor) else binding.mainContainer.setBackgroundResource(bgResId)
                allDigitTextViews.forEach { it.setTextColor(userClockColor) }
                binding.ampmTextView.setTextColor(userAmPmColor)
                binding.dateTextView.setTextColor(userDateColor)
                separatorTextViews.forEach { it.setTextColor(userSeparatorColor) }
            }
        }
    }

    private fun loadAndApplyFont(fontFileName: String?, textViews: List<TextView>) {
        if (fontFileName == null) return
        try {
            val fontName = fontFileName.substringBeforeLast(".")
            val fontResId = resources.getIdentifier(fontName, "font", packageName)
            if (fontResId != 0) {
                val typeface = ResourcesCompat.getFont(this, fontResId)
                textViews.forEach { it.typeface = typeface }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Could not load font: $fontFileName", e)
        }
    }

    private fun updateDigit(textView: TextView, newDigit: String) {
        if (textView.text == newDigit) return
        val fadeOut = ObjectAnimator.ofFloat(textView, View.ALPHA, 1f, 0f).apply {
            duration = 150
            doOnEnd {
                textView.text = newDigit
                ObjectAnimator.ofFloat(textView, View.ALPHA, 0f, 1f).setDuration(150).start()
            }
        }
        fadeOut.start()
    }

    private fun setupFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    private fun keepScreenOn() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun resetDimTimer() {
        dimJob?.cancel()
        dimJob = lifecycleScope.launch {
            delay(IDLE_DELAY_MS)
            val layoutParams = window.attributes
            layoutParams.screenBrightness = 0.4f
            window.attributes = layoutParams
        }
        // Reset brightness immediately on interaction
        val layoutParams = window.attributes
        layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        window.attributes = layoutParams
    }
}