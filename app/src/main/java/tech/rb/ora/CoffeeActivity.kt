package tech.rb.ora

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import tech.rb.ora.databinding.ActivityCoffeeBinding
import kotlin.random.Random

class CoffeeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoffeeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoffeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Support Development"

        // Set the intro text
        val introString = getString(R.string.buy_me_a_coffee_intro_text)
        binding.coffeeIntroTextView.text = Html.fromHtml(introString, Html.FROM_HTML_MODE_LEGACY)

        // Set listeners for the QR code images
        binding.gcashImageView.setOnClickListener {
            showFullScreenImage(R.drawable.gcash)
        }
        binding.mayaImageView.setOnClickListener {
            showFullScreenImage(R.drawable.maya)
        }
        binding.seabankImageView.setOnClickListener {
            showFullScreenImage(R.drawable.seabank)
        }

        // Set a random quote
        setRandomQuote()
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