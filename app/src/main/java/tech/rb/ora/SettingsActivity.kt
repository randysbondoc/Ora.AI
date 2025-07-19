package tech.rb.ora

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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

        // Updated the listener to handle the new date_color key
        private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                "clock_font", "date_font" -> {
                    updateFontSummary(key)
                }
                "background_select", "clock_size", "date_size", "clock_color", "date_color" -> {
                    activity?.finish()
                }
            }
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val clockFontPref = findPreference<Preference>("clock_font")
            val dateFontPref = findPreference<Preference>("date_font")
            val clockColorPref = findPreference<Preference>("clock_color")
            val dateColorPref = findPreference<Preference>("date_color") // Find the new preference

            updateFontSummary("clock_font")
            updateFontSummary("date_font")

            clockFontPref?.setOnPreferenceClickListener {
                showFontSelectionDialog(title = "Select Clock Font", preferenceKey = "clock_font")
                true
            }
            dateFontPref?.setOnPreferenceClickListener {
                showFontSelectionDialog(title = "Select Date Font", preferenceKey = "date_font")
                true
            }
            clockColorPref?.setOnPreferenceClickListener {
                showColorSelectionDialog(title = "Select Clock Color", preferenceKey = "clock_color")
                true
            }
            // Set the click listener for the new date color preference
            dateColorPref?.setOnPreferenceClickListener {
                showColorSelectionDialog(title = "Select Date Color", preferenceKey = "date_color")
                true
            }
        }

        private fun showColorSelectionDialog(title: String, preferenceKey: String) {
            val context = requireContext()
            val sharedPrefs = preferenceManager.sharedPreferences ?: return

            val colorGridView = LayoutInflater.from(context).inflate(R.layout.dialog_color_picker, null) as GridView

            // Added black to the color palette
            val colors = listOf(
                ContextCompat.getColor(context, R.color.white),
                ContextCompat.getColor(context, R.color.picker_red),
                ContextCompat.getColor(context, R.color.picker_pink),
                ContextCompat.getColor(context, R.color.picker_purple),
                ContextCompat.getColor(context, R.color.picker_blue),
                ContextCompat.getColor(context, R.color.picker_cyan),
                ContextCompat.getColor(context, R.color.picker_green),
                ContextCompat.getColor(context, R.color.picker_lime),
                ContextCompat.getColor(context, R.color.picker_yellow),
                ContextCompat.getColor(context, R.color.picker_orange),
                ContextCompat.getColor(context, R.color.black)
            )

            val adapter = ColorAdapter(context, colors)
            colorGridView.adapter = adapter

            val dialog = AlertDialog.Builder(context)
                .setTitle(title)
                .setView(colorGridView)
                .setNegativeButton("Cancel", null)
                .create()

            colorGridView.setOnItemClickListener { _, _, position, _ ->
                val selectedColor = colors[position]
                sharedPrefs.edit().putInt(preferenceKey, selectedColor).apply()
                dialog.dismiss()
            }

            dialog.show()
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

        override fun onResume() {
            super.onResume()
            preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(listener)
        }
        override fun onPause() {
            super.onPause()
            preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(listener)
        }
        private class FontAdapter(context: Context, private val displayNames: Array<String>, private val fileNames: Array<String>) : ArrayAdapter<String>(context, R.layout.list_item_font, displayNames) {
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

    class ColorAdapter(context: Context, private val colors: List<Int>) :
        ArrayAdapter<Int>(context, R.layout.grid_item_color, colors) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.grid_item_color, parent, false)
            val color = colors[position]
            // We need to mutate the drawable to change its color, otherwise all circles will have the same color
            val drawable = ContextCompat.getDrawable(context, R.drawable.color_preview_circle)?.mutate() as GradientDrawable
            drawable.setColor(color)
            view.background = drawable
            return view
        }
    }
}