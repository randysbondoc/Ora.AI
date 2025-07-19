package tech.rb.ora

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import java.io.File
import java.io.FileOutputStream
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable

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

        private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val sharedPrefs = preferenceManager.sharedPreferences ?: return@registerForActivityResult
                val newFilePath = copyImageToInternalStorage(uri)
                if (newFilePath != null) {
                    sharedPrefs.edit()
                        .putString("custom_background_path", newFilePath)
                        .putString("background_select", "custom")
                        .apply()
                } else {
                    Toast.makeText(requireContext(), "Failed to copy image", Toast.LENGTH_SHORT).show()
                    sharedPrefs.edit().putString("background_select", "black").apply()
                }
                activity?.finish()
            }
        }

        private fun copyImageToInternalStorage(uri: Uri): String? {
            return try {
                val inputStream = requireActivity().contentResolver.openInputStream(uri)
                val file = File(requireContext().filesDir, "custom_background.jpg")
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                file.absolutePath
            } catch (e: Exception) {
                Log.e("SettingsFragment", "Failed to copy image", e)
                null
            }
        }

        private val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPrefs, key ->
            when (key) {
                "clock_font", "date_font" -> updateFontSummary(key)
                "time_format" -> {
                    updateAmPmSwitchState(sharedPrefs)
                    activity?.finish()
                }
                "show_ampm", "show_date", "clock_size", "date_size", "clock_color", "date_color", "ampm_size", "ampm_color" -> {
                    activity?.finish()
                }
            }
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val backgroundPref = findPreference<ListPreference>("background_select")
            val clockFontPref = findPreference<Preference>("clock_font")
            val dateFontPref = findPreference<Preference>("date_font")
            val clockColorPref = findPreference<Preference>("clock_color")
            val dateColorPref = findPreference<Preference>("date_color")
            val ampmColorPref = findPreference<Preference>("ampm_color")
            val resetPref = findPreference<Preference>("reset_settings")

            updateFontSummary("clock_font")
            updateFontSummary("date_font")
            updateAmPmSwitchState(preferenceManager.sharedPreferences)

            backgroundPref?.setOnPreferenceChangeListener { _, newValue ->
                if (newValue == "custom") {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    false
                } else {
                    activity?.finish()
                    true
                }
            }

            clockFontPref?.setOnPreferenceClickListener { showFontSelectionDialog(title = "Select Clock Font", preferenceKey = "clock_font"); true }
            dateFontPref?.setOnPreferenceClickListener { showFontSelectionDialog(title = "Select Date Font", preferenceKey = "date_font"); true }
            clockColorPref?.setOnPreferenceClickListener { showColorSelectionDialog(title = "Select Clock Color", preferenceKey = "clock_color"); true }
            dateColorPref?.setOnPreferenceClickListener { showColorSelectionDialog(title = "Select Date Color", preferenceKey = "date_color"); true }
            ampmColorPref?.setOnPreferenceClickListener { showColorSelectionDialog(title = "Select AM/PM Color", preferenceKey = "ampm_color"); true }
            resetPref?.setOnPreferenceClickListener { showResetConfirmationDialog(); true }
        }
        private fun showResetConfirmationDialog() {
            AlertDialog.Builder(requireContext())
                .setTitle("Reset Settings?")
                .setMessage("Are you sure you want to restore all settings to their default values?")
                .setPositiveButton("Reset") { _, _ ->
                    val sharedPrefs = preferenceManager.sharedPreferences ?: return@setPositiveButton
                    sharedPrefs.edit().clear().apply()
                    preferenceScreen.removeAll()
                    addPreferencesFromResource(R.xml.root_preferences)
                    updateFontSummary("clock_font")
                    updateFontSummary("date_font")
                    updateAmPmSwitchState(sharedPrefs)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        private fun updateAmPmSwitchState(sharedPrefs: SharedPreferences?) {
            val is12HourFormat = sharedPrefs?.getString("time_format", "24h") == "12h"
            findPreference<SwitchPreferenceCompat>("show_ampm")?.isEnabled = is12HourFormat
        }
        private fun showColorSelectionDialog(title: String, preferenceKey: String) {
            val context = requireContext()
            val sharedPrefs = preferenceManager.sharedPreferences ?: return
            val colorGridView = LayoutInflater.from(context).inflate(R.layout.dialog_color_picker, null) as GridView
            val colors = listOf(ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.text_grey), ContextCompat.getColor(context, R.color.md_grey_300), ContextCompat.getColor(context, R.color.md_grey_500), ContextCompat.getColor(context, R.color.md_grey_700), ContextCompat.getColor(context, R.color.md_red_300), ContextCompat.getColor(context, R.color.md_red_500), ContextCompat.getColor(context, R.color.md_red_700), ContextCompat.getColor(context, R.color.md_pink_300), ContextCompat.getColor(context, R.color.md_pink_500), ContextCompat.getColor(context, R.color.md_pink_700), ContextCompat.getColor(context, R.color.md_purple_300), ContextCompat.getColor(context, R.color.md_purple_500), ContextCompat.getColor(context, R.color.md_purple_700), ContextCompat.getColor(context, R.color.md_deep_purple_500), ContextCompat.getColor(context, R.color.md_indigo_500), ContextCompat.getColor(context, R.color.md_blue_300), ContextCompat.getColor(context, R.color.md_blue_500), ContextCompat.getColor(context, R.color.md_blue_700), ContextCompat.getColor(context, R.color.md_light_blue_500), ContextCompat.getColor(context, R.color.md_cyan_500), ContextCompat.getColor(context, R.color.md_teal_500), ContextCompat.getColor(context, R.color.md_green_300), ContextCompat.getColor(context, R.color.md_green_500), ContextCompat.getColor(context, R.color.md_green_700), ContextCompat.getColor(context, R.color.md_light_green_500), ContextCompat.getColor(context, R.color.md_lime_500), ContextCompat.getColor(context, R.color.md_yellow_500), ContextCompat.getColor(context, R.color.md_amber_500), ContextCompat.getColor(context, R.color.md_orange_500), ContextCompat.getColor(context, R.color.md_deep_orange_500), ContextCompat.getColor(context, R.color.neon_pink), ContextCompat.getColor(context, R.color.neon_green), ContextCompat.getColor(context, R.color.neon_blue), ContextCompat.getColor(context, R.color.black))
            val adapter = ColorAdapter(context, colors)
            colorGridView.adapter = adapter
            val dialog = AlertDialog.Builder(context).setTitle(title).setView(colorGridView).setNegativeButton("Cancel", null).create()
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
        override fun onResume() { super.onResume(); preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(listener) }
        override fun onPause() { super.onPause(); preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(listener) }
        private class FontAdapter(context: Context, displayNames: Array<String>, private val fileNames: Array<String>) : ArrayAdapter<String>(context, R.layout.list_item_font, displayNames) {
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

    override fun onSupportNavigateUp(): Boolean { onBackPressedDispatcher.onBackPressed(); return true }
    class ColorAdapter(context: Context, colors: List<Int>) : ArrayAdapter<Int>(context, R.layout.grid_item_color, colors) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.grid_item_color, parent, false)
            val color = getItem(position)!!
            val drawable = ContextCompat.getDrawable(context, R.drawable.color_preview_circle)?.mutate() as GradientDrawable
            drawable.setColor(color)
            view.background = drawable
            return view
        }
    }
}