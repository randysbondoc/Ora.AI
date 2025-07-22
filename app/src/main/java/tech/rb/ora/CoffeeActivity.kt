package tech.rb.ora

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import tech.rb.ora.databinding.ActivityCoffeeBinding
import kotlin.random.Random

class CoffeeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoffeeBinding
    private val idleHandler = Handler(Looper.getMainLooper())
    private val IDLE_DELAY_MS = 30000L // 30 seconds

    private val idleRunnable = Runnable {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoffeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Support Development"

        val introString = getString(R.string.buy_me_a_coffee_intro_text)
        binding.coffeeIntroTextView.text = Html.fromHtml(introString, Html.FROM_HTML_MODE_LEGACY)

        binding.gcashImageView.setOnClickListener {
            showFullScreenImage(R.drawable.gcash)
        }
        binding.mayaImageView.setOnClickListener {
            showFullScreenImage(R.drawable.maya)
        }
        binding.seabankImageView.setOnClickListener {
            showFullScreenImage(R.drawable.seabank)
        }

        binding.gcashLabel.setOnClickListener {
            launchApp("com.globe.gcash.android", "GCash")
        }
        binding.mayaLabel.setOnClickListener {
            launchApp("com.paymaya", "Maya")
        }
        binding.seabankLabel.setOnClickListener {
            launchApp("ph.seabank.seabank", "SeaBank")
        }

        setRandomQuote()
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

    private fun launchApp(packageName: String, appName: String) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "$appName app is not installed on this device.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setRandomQuote() {
        val quotes = resources.getStringArray(R.array.support_quotes)
        val authors = resources.getStringArray(R.array.support_quote_authors)
        val randomIndex = Random.nextInt(quotes.size)

        binding.quoteTextView.text = quotes[randomIndex]
        binding.quoteAuthorTextView.text = authors[randomIndex]
    }

    private fun showFullScreenImage(imageResId: Int) {
        val intent = Intent(this, FullScreenImageActivity::class.java).apply {
            putExtra(FullScreenImageActivity.EXTRA_IMAGE_RES_ID, imageResId)
        }
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}