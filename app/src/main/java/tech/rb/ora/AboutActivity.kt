package tech.rb.ora

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import tech.rb.ora.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding
    private val idleHandler = Handler(Looper.getMainLooper())
    private val idleRunnable = Runnable { finish() }
    private val IDLE_DELAY_MS = 20000L // 20 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "About Ora"

        // Set Version Name
        try {
            val version = packageManager.getPackageInfo(packageName, 0).versionName
            binding.versionTextView.text = "Version $version"
        } catch (e: Exception) {
            binding.versionTextView.text = "Unknown Version"
        }

        // Set HTML text
        val aboutAppString = getString(R.string.about_app_text)
        binding.aboutAppTextView.text = Html.fromHtml(aboutAppString, Html.FROM_HTML_MODE_LEGACY)

        // Set button listeners
        binding.settingsGuideButton.setOnClickListener {
            startActivity(Intent(this, SettingsGuideActivity::class.java))
        }

        binding.batteryButton.setOnClickListener {
            startActivity(Intent(this, BatteryInfoActivity::class.java))
        }

        binding.coffeeButton.setOnClickListener {
            startActivity(Intent(this, CoffeeActivity::class.java))
        }

        binding.githubRepoButton.setOnClickListener {
            openUrl("https://github.com/randysbondoc/Retro-Flip-Clock")
        }

        binding.githubReleasesButton.setOnClickListener {
            openUrl("https://github.com/randysbondoc/Retro-Flip-Clock/releases")
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
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