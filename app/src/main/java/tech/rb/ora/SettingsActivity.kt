package tech.rb.ora

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        // This listener now correctly handles font previews and auto-closing for ALL selections.
        private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                "clock_font", "date_font" -> {
                    updateFontSummary(key)
                    // The dialog click listener will handle finishing
                }
                "background_select" -> {
                    // When the background is changed from its standard dialog, just close the screen.
                    activity?.finish()
                }
            }
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val clockFontPref = findPreference<Preference>("clock_font")
            val dateFontPref = findPreference<Preference>("date_font")

            // Set initial summaries
            updateFontSummary("clock_font")
            updateFontSummary("date_font")

            // Set click listeners to show our custom font dialog
            clockFontPref?.setOnPreferenceClickListener {
                showFontSelectionDialog(
                    title = "Select Clock Font",
                    preferenceKey = "clock_font"
                )
                true
            }

            dateFontPref?.setOnPreferenceClickListener {
                showFontSelectionDialog(
                    title = "Select Date Font",
                    preferenceKey = "date_font"
                )
                true
            }
        }

        // This function updates the summary on the main settings screen
        private fun updateFontSummary(key: String?) {
            val preference = findPreference<Preference>(key ?: return) ?: return
            val sharedPrefs = preferenceManager.sharedPreferences ?: return

            val defaultValue = if (key == "clock_font") "orbitron_regular.ttf" else "josefin_sans_regular.ttf"
            val fontFileName = sharedPrefs.getString(key, defaultValue) ?: defaultValue
            val fontFileValues = resources.getStringArray(R.array.font_file_values)
            val fontDisplayNames = resources.getStringArray(R.array.font_display_names)
            val selectedIndex = fontFileValues.indexOf(fontFileName)
            val fontDisplayName = if (selectedIndex != -1) fontDisplayNames[selectedIndex] else "Default"

            preference.summary = fontDisplayName
        }

        private fun showFontSelectionDialog(title: String, preferenceKey: String) {
            val context = requireContext()
            val sharedPrefs = preferenceManager.sharedPreferences ?: return
            val fontDisplayNames = resources.getStringArray(R.array.font_display_names)
            val fontFileValues = resources.getStringArray(R.array.font_file_values)

            val adapter = FontAdapter(context, fontDisplayNames, fontFileValues)

            AlertDialog.Builder(context)
                .setTitle(title)
                .setAdapter(adapter) { dialog, which ->
                    sharedPrefs.edit().putString(preferenceKey, fontFileValues[which]).apply()
                    dialog.dismiss()
                    activity?.finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        override fun onResume() {
            super.onResume()
            preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(listener)
        }

        override fun onPause() {
            super.onPause()
            preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(listener)
        }

        private class FontAdapter(
            context: Context,
            private val displayNames: Array<String>,
            private val fileNames: Array<String>
        ) : ArrayAdapter<String>(context, R.layout.list_item_font, displayNames) {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                val fontFileName = fileNames[position]

                try {
                    val fontName = fontFileName.substringBeforeLast(".")
                    val fontResId = context.resources.getIdentifier(fontName, "font", context.packageName)
                    if (fontResId != 0) {
                        val typeface = ResourcesCompat.getFont(context, fontResId)
                        view.typeface = typeface
                    }
                } catch (e: Exception) {
                    Log.e("FontAdapter", "Failed to load font: $fontFileName", e)
                    view.typeface = Typeface.DEFAULT
                }
                return view
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}