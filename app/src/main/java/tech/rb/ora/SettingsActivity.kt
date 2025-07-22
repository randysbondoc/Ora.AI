package tech.rb.ora

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
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
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import androidx.preference.SwitchPreferenceCompat
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random

class SettingsActivity : AppCompatActivity(), PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    private var idleJob: Job? = null
    private val IDLE_DELAY_MS = 20000L // 20 seconds

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

    private fun resetIdleTimer() {
        idleJob?.cancel()
        idleJob = lifecycleScope.launch {
            delay(IDLE_DELAY_MS)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        resetIdleTimer()
    }

    override fun onPause() {
        super.onPause()
        idleJob?.cancel()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        resetIdleTimer()
    }

    override fun onPreferenceStartScreen(caller: PreferenceFragmentCompat, ps: PreferenceScreen): Boolean {
        val fragment = SettingsFragment().apply {
            arguments = Bundle().apply {
                putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, ps.key)
            }
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, fragment)
            .addToBackStack(null)
            .commit()
        return true
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val sharedPrefs = preferenceManager.sharedPreferences ?: return@registerForActivityResult
                val newFilePath = copyImageToInternalStorage(uri, "custom_background.jpg")
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

        private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                Toast.makeText(requireContext(), "Permission denied to read photos", Toast.LENGTH_SHORT).show()
            }
        }

        private fun launchPickerWithPermissionCheck() {
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

            when {
                ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
                else -> {
                    requestPermissionLauncher.launch(permission)
                }
            }
        }

        private fun copyImageToInternalStorage(uri: Uri, targetFileName: String): String? {
            return try {
                val inputStream = requireActivity().contentResolver.openInputStream(uri)
                val file = File(requireContext().filesDir, targetFileName)
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                file.absolutePath
            } catch (e: Exception) {
                Log.e("SettingsFragment", "Failed to copy image from URI", e)
                null
            }
        }

        private fun copyInternalFile(sourcePath: String, targetFileName: String): String? {
            return try {
                val sourceFile = File(sourcePath)
                val targetFile = File(requireContext().filesDir, targetFileName)
                sourceFile.copyTo(targetFile, overwrite = true)
                targetFile.absolutePath
            } catch (e: Exception) {
                Log.e("SettingsFragment", "Failed to copy internal file", e)
                null
            }
        }

        private val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPrefs, key ->
            when (key) {
                "clock_font", "date_font", "ampm_font" -> updateFontSummary(key)
                "time_format" -> {
                    updateAmPmSwitchState(sharedPrefs)
                    activity?.finish()
                }
                "show_ampm", "show_date", "clock_size", "date_size", "clock_color", "date_color", "ampm_size", "ampm_color",
                "clock_shadow", "date_shadow", "ampm_shadow", "hide_button_delay",
                "show_separator", "separator_color", "digit_padding",
                "digit_bg_style", "digit_bg_color", "digit_bg_alpha",
                "auto_color_change_toggle", "auto_color_change_interval",
                "clock_vertical_position", "date_vertical_position",
                "date_horizontal_position", "show_clock" -> {
                    activity?.finish()
                }
            }
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val applyPresetPref = findPreference<ListPreference>("apply_preset")
            setupPresetDropdown(applyPresetPref)
            applyPresetPref?.setOnPreferenceChangeListener { _, newValue ->
                applyPreset(newValue.toString().toInt())
                false
            }

            findPreference<Preference>("save_preset")?.setOnPreferenceClickListener { showSavePresetDialog(); true }
            findPreference<Preference>("clear_preset")?.setOnPreferenceClickListener { showClearPresetDialog(); true }
            findPreference<Preference>("reset_settings")?.setOnPreferenceClickListener { showResetConfirmationDialog(); true }
            findPreference<Preference>("about_screen")?.setOnPreferenceClickListener {
                startActivity(Intent(requireContext(), AboutActivity::class.java))
                true
            }
            findPreference<Preference>("take_screenshot")?.setOnPreferenceClickListener {
                preferenceManager.sharedPreferences?.edit()?.putBoolean("take_screenshot_flag", true)?.apply()
                activity?.finish()
                true
            }

            findPreference<ListPreference>("background_select")?.setOnPreferenceChangeListener { _, newValue ->
                if (newValue == "custom") {
                    launchPickerWithPermissionCheck()
                    false
                } else {
                    activity?.finish()
                    true
                }
            }

            findPreference<Preference>("randomize_settings")?.setOnPreferenceClickListener { randomizeSettings(); true }

            findPreference<Preference>("clock_color")?.setOnPreferenceClickListener { showColorSelectionDialog(title = "Select Clock Color", preferenceKey = "clock_color"); true }
            findPreference<Preference>("date_color")?.setOnPreferenceClickListener { showColorSelectionDialog(title = "Select Date Color", preferenceKey = "date_color"); true }
            findPreference<Preference>("ampm_color")?.setOnPreferenceClickListener { showColorSelectionDialog(title = "Select AM/PM Color", preferenceKey = "ampm_color"); true }
            findPreference<Preference>("separator_color")?.setOnPreferenceClickListener { showColorSelectionDialog(title = "Select Separator Color", preferenceKey = "separator_color"); true }
            findPreference<Preference>("digit_bg_color")?.setOnPreferenceClickListener { showColorSelectionDialog(title = "Select Digit BG Color", preferenceKey = "digit_bg_color"); true }

            findPreference<Preference>("clock_font")?.setOnPreferenceClickListener { showFontSelectionDialog(title = "Select Clock Font", preferenceKey = "clock_font"); true }
            findPreference<Preference>("date_font")?.setOnPreferenceClickListener { showFontSelectionDialog(title = "Select Date Font", preferenceKey = "date_font"); true }
            findPreference<Preference>("ampm_font")?.setOnPreferenceClickListener { showFontSelectionDialog(title = "Select AM/PM Font", preferenceKey = "ampm_font"); true }

            updateFontSummary("clock_font")
            updateFontSummary("date_font")
            updateFontSummary("ampm_font")
            updateAmPmSwitchState(preferenceManager.sharedPreferences)
        }

        private fun setupPresetDropdown(listPreference: ListPreference?) {
            val entries = mutableListOf<CharSequence>()
            val entryValues = mutableListOf<CharSequence>()
            for (i in 1..5) {
                val prefs = requireContext().getSharedPreferences("preset_$i", Context.MODE_PRIVATE)
                val status = if (prefs.all.isEmpty()) "(Empty)" else "(Saved)"
                entries.add("Preset $i $status")
                entryValues.add(i.toString())
            }
            listPreference?.entries = entries.toTypedArray()
            listPreference?.entryValues = entryValues.toTypedArray()
        }

        private fun showSavePresetDialog() {
            val presetSlots = arrayOf("Preset 1", "Preset 2", "Preset 3", "Preset 4", "Preset 5")
            AlertDialog.Builder(requireContext())
                .setTitle("Save Current Style To...")
                .setItems(presetSlots) { _, which ->
                    val slotNumber = which + 1
                    val presetPrefs = requireContext().getSharedPreferences("preset_$slotNumber", Context.MODE_PRIVATE)
                    if (presetPrefs.all.isNotEmpty()) {
                        showOverwriteDialog(slotNumber)
                    } else {
                        savePreset(slotNumber)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        private fun showOverwriteDialog(slotNumber: Int) {
            AlertDialog.Builder(requireContext())
                .setTitle("Overwrite Preset?")
                .setMessage("Preset $slotNumber already has a saved style. Do you want to overwrite it?")
                .setPositiveButton("Overwrite") { _, _ ->
                    savePreset(slotNumber)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        private fun showClearPresetDialog() {
            val presetSlots = arrayOf("Preset 1", "Preset 2", "Preset 3", "Preset 4", "Preset 5")
            AlertDialog.Builder(requireContext())
                .setTitle("Clear Which Preset?")
                .setItems(presetSlots) { _, which ->
                    val slotNumber = which + 1
                    AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Clear")
                        .setMessage("Are you sure you want to permanently delete Preset $slotNumber?")
                        .setPositiveButton("Clear") { _, _ ->
                            clearPreset(slotNumber)
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        private fun clearPreset(slotNumber: Int) {
            val presetPrefs = requireContext().getSharedPreferences("preset_$slotNumber", Context.MODE_PRIVATE)
            presetPrefs.edit().clear().apply()

            val backgroundFile = File(requireContext().filesDir, "preset_${slotNumber}_background.jpg")
            if (backgroundFile.exists()) {
                backgroundFile.delete()
            }

            Toast.makeText(requireContext(), "Preset $slotNumber cleared", Toast.LENGTH_SHORT).show()
            setupPresetDropdown(findPreference("apply_preset"))
        }

        private fun savePreset(slotNumber: Int) {
            val mainPrefs = preferenceManager.sharedPreferences ?: return
            val presetPrefs = requireContext().getSharedPreferences("preset_$slotNumber", Context.MODE_PRIVATE)
            val presetEditor = presetPrefs.edit()

            presetEditor.clear()

            var customImagePath: String? = null
            if (mainPrefs.getString("background_select", "") == "custom") {
                val currentPath = mainPrefs.getString("custom_background_path", null)
                if (currentPath != null && File(currentPath).exists()) {
                    val newFileName = "preset_${slotNumber}_background.jpg"
                    customImagePath = copyInternalFile(currentPath, newFileName)
                }
            }

            mainPrefs.all.forEach { (key, value) ->
                if (key == "custom_background_path" && customImagePath != null) {
                    presetEditor.putString(key, customImagePath)
                } else {
                    when (value) {
                        is String -> presetEditor.putString(key, value)
                        is Int -> presetEditor.putInt(key, value)
                        is Boolean -> presetEditor.putBoolean(key, value)
                        is Float -> presetEditor.putFloat(key, value)
                        is Long -> presetEditor.putLong(key, value)
                    }
                }
            }
            presetEditor.apply()
            Toast.makeText(requireContext(), "Style saved to Preset $slotNumber", Toast.LENGTH_SHORT).show()
            setupPresetDropdown(findPreference("apply_preset"))
        }

        private fun applyPreset(slotNumber: Int) {
            val mainPrefs = preferenceManager.sharedPreferences ?: return
            val presetPrefs = requireContext().getSharedPreferences("preset_$slotNumber", Context.MODE_PRIVATE)

            if (presetPrefs.all.isEmpty()) {
                Toast.makeText(requireContext(), "Preset $slotNumber is empty", Toast.LENGTH_SHORT).show()
                return
            }

            val mainEditor = mainPrefs.edit()

            presetPrefs.all.forEach { (key, value) ->
                when (value) {
                    is String -> mainEditor.putString(key, value)
                    is Int -> mainEditor.putInt(key, value)
                    is Boolean -> mainEditor.putBoolean(key, value)
                    is Float -> mainEditor.putFloat(key, value)
                    is Long -> mainEditor.putLong(key, value)
                }
            }
            mainEditor.apply()
            activity?.finish()
        }

        private fun randomizeSettings() {
            val sharedPrefs = preferenceManager.sharedPreferences ?: return
            val editor = sharedPrefs.edit()

            val fontFiles = resources.getStringArray(R.array.font_file_values)
            val colors = resources.getIntArray(R.array.color_picker_palette)
            val backgroundValues = resources.getStringArray(R.array.background_values).filter { it != "custom" }

            val randomFont = fontFiles.random()
            editor.putString("clock_font", randomFont)
            editor.putString("date_font", fontFiles.random())
            editor.putString("ampm_font", randomFont)

            val currentBackground = sharedPrefs.getString("background_select", "black")
            val colorBlack = ContextCompat.getColor(requireContext(), R.color.black)
            val availableColors = if (currentBackground == "black") {
                colors.filter { it != colorBlack }
            } else {
                colors.toList()
            }
            val randomColor = availableColors.random()
            editor.putInt("clock_color", randomColor)
            editor.putInt("date_color", availableColors.random())
            editor.putInt("ampm_color", randomColor)
            editor.putInt("separator_color", randomColor)

            editor.putInt("clock_size", Random.nextInt(40, 91))
            editor.putInt("date_size", Random.nextInt(14, 31))
            editor.putInt("ampm_size", Random.nextInt(16, 41))

            editor.putString("background_select", backgroundValues.random())

            editor.apply()
            activity?.finish()
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
                    onCreatePreferences(null, null)
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
            val colors = resources.getIntArray(R.array.color_picker_palette).toList()
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
            val defaultFont = when (key) {
                "clock_font" -> "orbitron_regular.ttf"
                "date_font" -> "josefin_sans_regular.ttf"
                "ampm_font" -> "orbitron_regular.ttf"
                else -> ""
            }
            val fontFileName = sharedPrefs.getString(key, defaultFont) ?: defaultFont
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