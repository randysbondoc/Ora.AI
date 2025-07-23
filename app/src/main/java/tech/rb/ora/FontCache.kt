package tech.rb.ora

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import java.util.concurrent.ConcurrentHashMap

/**
 * A singleton object that acts as an in-memory cache for Typeface objects.
 * This prevents the app from repeatedly loading font files from disk, improving performance.
 */
object FontCache {

    private val fontCache = ConcurrentHashMap<String, Typeface>()

    /**
     * Retrieves a Typeface from the cache. If not present, it loads the font from
     * the assets, adds it to the cache, and then returns it.
     *
     * @param context The application context.
     * @param fontFileName The name of the font file (e.g., "orbitron_regular.ttf").
     * @return The requested Typeface, or null if the font cannot be loaded.
     */
    fun getTypeface(context: Context, fontFileName: String): Typeface? {
        // Return the cached typeface if it exists
        fontCache[fontFileName]?.let {
            return it
        }

        // If not in cache, try to load it
        try {
            val fontName = fontFileName.substringBeforeLast(".")
            val fontResId = context.resources.getIdentifier(fontName, "font", context.packageName)
            if (fontResId != 0) {
                ResourcesCompat.getFont(context, fontResId)?.let { typeface ->
                    // Store the newly loaded typeface in the cache
                    fontCache[fontFileName] = typeface
                    return typeface
                }
            }
        } catch (e: Exception) {
            Log.e("FontCache", "Could not load font: $fontFileName", e)
        }

        // Return null if font loading fails
        return null
    }
}