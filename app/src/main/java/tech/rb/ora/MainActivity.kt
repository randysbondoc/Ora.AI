package tech.rb.ora

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import tech.rb.ora.databinding.ActivityMainBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // --- Constants ---
    private val UPDATE_INTERVAL_MS = 200L // Update interval in milliseconds
    private val IDLE_DELAY_MS = 3 * 60 * 1000L // 3 minutes

    // --- Views & System ---
    private lateinit var binding: ActivityMainBinding
    private val timeHandler = Handler(Looper.getMainLooper())
    private val dimHandler = Handler(Looper.getMainLooper())

    // --- Date Formatter ---
    private val timeFormatter = DateTimeFormatter.ofPattern("HHmmss")
    private val dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup app behaviors
        setupFullScreen()
        keepScreenOn()
        startClock()
        startDimTimer()
    }

    private fun startClock() {
        val timeUpdater = object : Runnable {
            override fun run() {
                val now = LocalDateTime.now()
                val timeString = now.format(timeFormatter)

                // Update digits only if they have changed
                updateDigit(binding.hour1, timeString[0].toString())
                updateDigit(binding.hour2, timeString[1].toString())
                updateDigit(binding.minute1, timeString[2].toString())
                updateDigit(binding.minute2, timeString[3].toString())
                updateDigit(binding.second1, timeString[4].toString())
                updateDigit(binding.second2, timeString[5].toString())

                // Update the date text
                binding.dateTextView.text = now.format(dateFormatter)

                timeHandler.postDelayed(this, UPDATE_INTERVAL_MS)
            }
        }
        timeHandler.post(timeUpdater)
    }

    private fun updateDigit(textView: TextView, newDigit: String) {
        // Do nothing if the digit is already correct
        if (textView.text == newDigit) return

        // Simple fade out/in animation for the digit change
        val fadeOut = ObjectAnimator.ofFloat(textView, View.ALPHA, 1f, 0f).apply {
            duration = 150
            doOnEnd {
                textView.text = newDigit
                // Chain the fade in animation
                ObjectAnimator.ofFloat(textView, View.ALPHA, 0f, 1f).setDuration(150).start()
            }
        }
        fadeOut.start()
    }

    // --- Fullscreen and Screen-On Logic ---
    private fun setupFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    private fun keepScreenOn() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    // --- Screen Dimming Logic ---
    private val dimRunnable = Runnable {
        val layoutParams = window.attributes
        layoutParams.screenBrightness = 0.4f // 40% brightness
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

    override fun onDestroy() {
        super.onDestroy()
        timeHandler.removeCallbacksAndMessages(null)
        dimHandler.removeCallbacksAndMessages(null)
    }
}