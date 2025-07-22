package tech.rb.ora

import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tech.rb.ora.databinding.ActivityBatteryInfoBinding

class BatteryInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBatteryInfoBinding
    private var idleJob: Job? = null
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
        idleJob?.cancel()
        idleJob = lifecycleScope.launch {
            delay(IDLE_DELAY_MS)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        resetIdleTimer()
    }

    override fun onPause() {
        super.onPause()
        idleJob?.cancel()
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