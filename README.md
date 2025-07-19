# Ora - Retro Flip Clock ⏰

A minimalist, full-screen flip clock application for Android, designed with a retro aesthetic and an AMOLED-friendly interface. This project was built to explore core Android development concepts, including custom UI, a full settings menu with `PreferenceScreen`, and modern Android APIs like the Photo Picker.

![Ora Screenshot](https://i.imgur.com/L8a1uRk.png)
> **Note:** You should replace this with an updated screenshot of your app running with a custom background from your device!

---

## ✨ Features (v1.4)

* **Full-Screen Display:** Hides the system status and navigation bars for an immersive experience.
* **Live Time & Date:** Displays the current time and date, updating every second.
* **Screen Always On:** Prevents the device from sleeping while the app is active.
* **Auto-Dimming:** Dims the screen after 3 minutes of inactivity to prevent screen burn-in.
* **Complete Customization via a Reorganized Settings Menu:**
  * **Custom Backgrounds:** Select any image from your device's photo gallery to use as a custom background.
  * **Time Format:** Choose between 12-hour (AM/PM) and 24-hour formats.
  * **Font Selection:** Choose from 10 different fonts for the clock and the date.
  * **Text Size & Color:** Independently control the size and color for the clock, date, and AM/PM indicator.
  * **Display Toggles:** Show or hide the date and the AM/PM indicator.
  * **Reset Option:** A button to restore all settings to their default state.

---

## 📜 Version History

### Version 1.4 (Latest)
* Added the ability to select a custom background image from the device's photo gallery.
* Implemented runtime permissions for media access.
* The app now makes a secure, private copy of the selected image to ensure it's always available.

### Version 1.3
* Added 12/24 hour time format selection.
* Added a toggle to show/hide the AM/PM indicator with separate size/color controls.
* Added a "Reset to Default" option.
* Reorganized the settings menu into logical categories.

### Version 1.2
* Added text size sliders to control the font size for the clock and date.
* Added color pickers to choose custom colors for the clock and date text.

### Version 1.1
* Introduced the main settings screen.
* Added font selection with live previews.
* Added background selection with pre-defined options.
* Added a toggle to show or hide the date display.

### Version 1.0
* Initial release with the core flip clock functionality.
* Implemented full-screen, always-on display, and auto-dimming.

---

## 🛠️ Built With

* **Language:** [Kotlin](https://kotlinlang.org/)
* **UI:** Android XML Layouts with `PreferenceScreen`.
* **Image Loading:** [Coil](https://coil-kt.github.io/coil/)
* **Build System:** [Gradle](https://gradle.org/)

---

## 🚀 Getting Started

1.  Clone the repository:
    ```sh
    git clone [https://github.com/randysbondoc/Retro-Flip-Clock.git](https://github.com/randysbondoc/Retro-Flip-Clock.git)
    ```
2.  Open the project in Android Studio and let Gradle sync.
3.  Build and run the app.

---

## 🗺️ Roadmap

* **[v1.5]** Integrate a simple weather and temperature display.
* **[v1.5]** Add an option to change the ":" separator style or color.
* **[v2.0]** Investigate home screen widget functionality.

---

## 📄 License

This project is licensed under the MIT License.