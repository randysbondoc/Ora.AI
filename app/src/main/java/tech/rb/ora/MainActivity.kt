package tech.rb.ora

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val timeHandler = Handler(Looper.getMainLooper())
    private val dimHandler = Handler(Looper.getMainLooper())
    private val timeFormatter = DateTimeFormatter.ofPattern("HHmmss")
    private val dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())
    private val UPDATE_INTERVAL_MS = 200L
    private val IDLE_DELAY_MS = 3 * 60 * 1000L

    private val settingsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == "show_date" || key == "clock_font" || key == "date_font" || key == "background_select") {
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

    private fun setupSettingsButton() {
        binding.settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun applyAllSettings() {
        binding.dateTextView.visibility = if (sharedPreferences.getBoolean("show_date", true)) View.VISIBLE else View.GONE

        val clockFontFile = sharedPreferences.getString("clock_font", "orbitron_regular.ttf")
        loadAndApplyFont(clockFontFile, binding.hour1, binding.hour2, binding.minute1, binding.minute2, binding.second1, binding.second2)
        val dateFontFile = sharedPreferences.getString("date_font", "josefin_sans_regular.ttf")
        loadAndApplyFont(dateFontFile, binding.dateTextView)

        val background = sharedPreferences.getString("background_select", "black")
        updateBackgroundAndTextColor(background)
    }

    private fun updateBackgroundAndTextColor(backgroundValue: String?) {
        val allDigitTextViews = listOf(binding.hour1, binding.hour2, binding.minute1, binding.minute2, binding.second1, binding.second2)
        val whiteColor = ContextCompat.getColor(this, R.color.white)
        val blackColor = ContextCompat.getColor(this, R.color.black)
        val greyColor = ContextCompat.getColor(this, R.color.text_grey)
        val digitBgColor = ContextCompat.getColor(this, R.color.digit_background)

        when (backgroundValue) {
            "white" -> {
                binding.mainContainer.setBackgroundColor(whiteColor)
                // Invert colors for readability on white
                allDigitTextViews.forEach {
                    it.setTextColor(blackColor)
                    it.setBackgroundColor(whiteColor) // Set digit background to white
                }
                binding.dateTextView.setTextColor(blackColor)
            }
            "galaxy", "stars", "lab", "universe" -> {
                // Set the appropriate background resource
                val bgResId = when (backgroundValue) {
                    "galaxy" -> R.drawable.background_galaxy
                    "stars" -> R.drawable.stars
                    "lab" -> R.drawable.lab
                    "universe" -> R.drawable.universe
                    else -> R.color.black
                }
                binding.mainContainer.setBackgroundResource(bgResId)

                // Set standard text and digit background colors for dark themes
                allDigitTextViews.forEach {
                    it.setTextColor(whiteColor)
                    it.setBackgroundColor(digitBgColor)
                }
                binding.dateTextView.setTextColor(greyColor)
            }
            else -> { // "black" is the default
                binding.mainContainer.setBackgroundColor(blackColor)
                // Set standard text and digit background colors
                allDigitTextViews.forEach {
                    it.setTextColor(whiteColor)
                    it.setBackgroundColor(digitBgColor)
                }
                binding.dateTextView.setTextColor(greyColor)
            }
        }
    }

    private fun loadAndApplyFont(fontFileName: String?, vararg textViews: TextView) {
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

    override fun onDestroy() {
        super.onDestroy()
        timeHandler.removeCallbacksAndMessages(null)
        dimHandler.removeCallbacksAndMessages(null)
    }

    // ... (All other functions are unchanged) ...
    private fun startClock() {
        val timeUpdater = object : Runnable {
            override fun run() {
                val now = LocalDateTime.now()
                val timeString = now.format(timeFormatter)
                updateDigit(binding.hour1, timeString[0].toString())
                updateDigit(binding.hour2, timeString[1].toString())
                updateDigit(binding.minute1, timeString[2].toString())
                updateDigit(binding.minute2, timeString[3].toString())
                updateDigit(binding.second1, timeString[4].toString())
                updateDigit(binding.second2, timeString[5].toString())
                binding.dateTextView.text = now.format(dateFormatter)
                timeHandler.postDelayed(this, UPDATE_INTERVAL_MS)
            }
        }
        timeHandler.post(timeUpdater)
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