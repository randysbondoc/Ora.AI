package tech.rb.ora

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.preference.PreferenceManager
import tech.rb.ora.databinding.ActivityMainBinding
import java.io.File
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())
    private val formatter24h = DateTimeFormatter.ofPattern("HHmmss")
    private val formatter12h = DateTimeFormatter.ofPattern("hhmmss")
    private val formatterAmPm = DateTimeFormatter.ofPattern("a", Locale.getDefault())
    private val timeHandler = Handler(Looper.getMainLooper())
    private val dimHandler = Handler(Looper.getMainLooper())
    private val UPDATE_INTERVAL_MS = 200L
    private val IDLE_DELAY_MS = 3 * 60 * 1000L

    private val settingsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key != null) {
            applyAllSettings()
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
        startDimTimer()
        setupSettingsButton()
    }

    override fun onResume() {
        super.onResume()
        sharedPreferences.registerOnSharedPreferenceChangeListener(settingsListener)
        applyAllSettings()
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(settingsListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        timeHandler.removeCallbacksAndMessages(null)
        dimHandler.removeCallbacksAndMessages(null)
    }

    private fun setupSettingsButton() {
        binding.settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startClock() {
        val timeUpdater = object : Runnable {
            override fun run() {
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

                timeHandler.postDelayed(this, UPDATE_INTERVAL_MS)
            }
        }
        timeHandler.post(timeUpdater)
    }

    private fun applyAllSettings() {
        binding.dateTextView.visibility = if (sharedPreferences.getBoolean("show_date", true)) View.VISIBLE else View.GONE

        val allDigitTextViews = listOf(binding.hour1, binding.hour2, binding.minute1, binding.minute2, binding.second1, binding.second2)

        val clockFontFile = sharedPreferences.getString("clock_font", "orbitron_regular.ttf")
        loadAndApplyFont(clockFontFile, allDigitTextViews + binding.ampmTextView)

        val dateFontFile = sharedPreferences.getString("date_font", "josefin_sans_regular.ttf")
        loadAndApplyFont(dateFontFile, listOf(binding.dateTextView))

        val clockSize = sharedPreferences.getInt("clock_size", 50)
        val dateSize = sharedPreferences.getInt("date_size", 18)
        val ampmSize = sharedPreferences.getInt("ampm_size", 24)
        allDigitTextViews.forEach { it.setTextSize(TypedValue.COMPLEX_UNIT_SP, clockSize.toFloat()) }
        binding.dateTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, dateSize.toFloat())
        binding.ampmTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, ampmSize.toFloat())

        val background = sharedPreferences.getString("background_select", "black")
        updateBackgroundAndTextColor(background)
    }

    private fun updateBackgroundAndTextColor(backgroundValue: String?) {
        val allDigitTextViews = listOf(binding.hour1, binding.hour2, binding.minute1, binding.minute2, binding.second1, binding.second2)
        val whiteColor = ContextCompat.getColor(this, R.color.white)
        val blackColor = ContextCompat.getColor(this, R.color.black)
        val greyColor = ContextCompat.getColor(this, R.color.text_grey)
        val digitBgColor = ContextCompat.getColor(this, R.color.digit_background)

        val userClockColor = sharedPreferences.getInt("clock_color", whiteColor)
        val userDateColor = sharedPreferences.getInt("date_color", greyColor)
        val userAmPmColor = sharedPreferences.getInt("ampm_color", whiteColor)

        when (backgroundValue) {
            "white" -> {
                binding.mainContainer.setBackgroundColor(whiteColor)
                (allDigitTextViews + binding.ampmTextView).forEach { it.setTextColor(blackColor) }
                binding.dateTextView.setTextColor(blackColor)
                allDigitTextViews.forEach { it.setBackgroundColor(whiteColor) }
            }
            "custom" -> {
                val path = sharedPreferences.getString("custom_background_path", null)
                if (path != null) {
                    try {
                        val imageFile = File(path)
                        val drawable = Drawable.createFromPath(imageFile.absolutePath)
                        binding.mainContainer.background = drawable
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Failed to load custom background from path", e)
                        binding.mainContainer.setBackgroundColor(blackColor)
                    }
                } else {
                    binding.mainContainer.setBackgroundColor(blackColor)
                }

                allDigitTextViews.forEach { it.setTextColor(userClockColor); it.setBackgroundColor(digitBgColor) }
                binding.ampmTextView.setTextColor(userAmPmColor)
                binding.dateTextView.setTextColor(userDateColor)
            }
            else -> {
                val bgResId = when (backgroundValue) {
                    "galaxy" -> R.drawable.background_galaxy
                    "stars" -> R.drawable.stars
                    "lab" -> R.drawable.lab
                    "universe" -> R.drawable.universe
                    else -> R.color.black
                }
                if (backgroundValue == "black") {
                    binding.mainContainer.setBackgroundColor(blackColor)
                } else {
                    binding.mainContainer.setBackgroundResource(bgResId)
                }

                allDigitTextViews.forEach { it.setTextColor(userClockColor); it.setBackgroundColor(digitBgColor) }
                binding.ampmTextView.setTextColor(userAmPmColor)
                binding.dateTextView.setTextColor(userDateColor)
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

    private val dimRunnable = Runnable {
        val layoutParams = window.attributes
        layoutParams.screenBrightness = 0.4f
        window.attributes = layoutParams
    }

    private fun startDimTimer() {
        dimHandler.postDelayed(dimRunnable, IDLE_DELAY_MS)
    }

    private fun resetDimTimer() {
        dimHandler.removeCallbacks(dimRunnable)
        val layoutParams = window.attributes
        layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        window.attributes = layoutParams
        startDimTimer()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        resetDimTimer()
    }
}