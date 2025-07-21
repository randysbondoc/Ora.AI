package tech.rb.ora

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import tech.rb.ora.databinding.ActivityFullScreenImageBinding

class FullScreenImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullScreenImageBinding
    private val idleHandler = Handler(Looper.getMainLooper())
    private val idleRunnable = Runnable { finish() }
    private val IDLE_DELAY_MS = 30000L // 30 seconds

    companion object {
        const val EXTRA_IMAGE_RES_ID = "extra_image_res_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageResId = intent.getIntExtra(EXTRA_IMAGE_RES_ID, 0)
        if (imageResId != 0) {
            binding.fullScreenImageView.setImageResource(imageResId)
        }

        // Clicking the image also closes it
        binding.fullScreenImageView.setOnClickListener {
            finish()
        }
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
}