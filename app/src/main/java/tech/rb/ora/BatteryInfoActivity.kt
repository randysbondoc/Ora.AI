package tech.rb.ora

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import tech.rb.ora.databinding.ActivityBatteryInfoBinding

class BatteryInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBatteryInfoBinding
    private val idleHandler = Handler(Looper.getMainLooper())
    private val idleRunnable = Runnable { finish() }
    private val IDLE_DELAY_MS = 10000L // 10 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBatteryInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Battery & Power Analysis"

        val batteryString = getString(R.string.about_battery_analysis_text)
        binding.batteryInfoTextView.text = Html.fromHtml(batteryString, Html.FROM_HTML_MODE_LEGACY)
    }

    private fun resetIdleTimer() {
        idleHandler.removeCallbacks(idleRunnable)
        idleHandler.postDelayed(idleRunnable, IDLE_DELAY_MS)
    }

    override fun onResume() {
        super.onResume()
        resetIdleTimer()
    }

    override fun onPause() {
        super.onPause()
        idleHandler.removeCallbacks(idleRunnable)
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        resetIdleTimer()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}