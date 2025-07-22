package tech.rb.ora

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import tech.rb.ora.databinding.ActivityFullScreenImageBinding

class FullScreenImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullScreenImageBinding

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

        // Clicking the image closes the activity
        binding.fullScreenImageView.setOnClickListener {
            finish()
        }
    }
}