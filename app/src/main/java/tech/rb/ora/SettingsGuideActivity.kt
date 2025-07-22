package tech.rb.ora

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import tech.rb.ora.databinding.ActivitySettingsGuideBinding

class SettingsGuideActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsGuideBinding
    private val idleHandler = Handler(Looper.getMainLooper())
    private val idleRunnable = Runnable { finish() }
    private val IDLE_DELAY_MS = 10000L // 10 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Settings Guide"

        val guideString = getString(R.string.settings_guide_text)
        binding.settingsGuideTextView.text = Html.fromHtml(guideString, Html.FROM_HTML_MODE_LEGACY)
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