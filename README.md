# Ora - Retro Flip Clock ⏰

A minimalist, full-screen flip clock application for Android, designed with a retro aesthetic and an AMOLED-friendly interface. This project was built to explore core Android development concepts, including custom UI, a full settings menu with `PreferenceScreen`, and modern build configurations with Gradle.

![Ora Screenshot](https://i.imgur.com/L8a1uRk.png)
> **Note:** You should replace this with an updated screenshot of your app running with your favorite new theme!

---

## ✨ Features (v1.3)

* **Full-Screen Display:** Hides the system status and navigation bars for an immersive experience.
* **Live Time & Date:** Displays the current time and date, updating every second.
* **Screen Always On:** Prevents the device from sleeping while the app is active.
* **Auto-Dimming:** Dims the screen after 3 minutes of inactivity to prevent screen burn-in.
* **Complete Customization via a Reorganized Settings Menu:**
  * **Time Format:** Choose between 12-hour (AM/PM) and 24-hour formats.
  * **Font Selection:** Choose from 10 different fonts for the clock and the date.
  * **Background Selection:** Select from multiple backgrounds, including solid colors and custom images.
  * **Text Size:** Independently control the font size for the clock, date, and AM/PM indicator.
  * **Text Color:** Pick a custom color for the clock, date, and AM/PM indicator.
  * **Display Toggles:** Show or hide the date and the AM/PM indicator.
  * **Reset Option:** A button to restore all settings to their default state.

---

## 📜 Version History

### Version 1.3 (Latest)
* Added 12/24 hour time format selection.
* Added a toggle to show/hide the AM/PM indicator.
* Added separate size and color customization for the AM/PM text.
* Added a "Reset to Default" option in the settings.
* Reorganized the settings menu into logical categories (Clock, Date, Display).

### Version 1.2
* Added text size sliders to control the font size for the clock and date.
* Added color pickers to choose custom colors for the clock and date text.
* Made the layout fully responsive to size changes.

### Version 1.1
* Introduced the main settings screen.
* Added font selection with live previews in the menu.
* Added background selection with multiple options.
* Added a toggle to show or hide the date display.

### Version 1.0
* Initial release with the core flip clock functionality.
* Implemented full-screen, always-on display, and auto-dimming.

---

## 🛠️ Built With

* **Language:** [Kotlin](https://kotlinlang.org/)
* **UI:** Android XML Layouts with `PreferenceScreen`.
* **Build System:** [Gradle](https://gradle.org/) with Version Catalogs.

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

* **[v1.4]** Integrate a simple weather and temperature display.
* **[v1.4]** Add option to change the ":" separator style or color.
* **[v2.0]** Investigate home screen widget functionality.

---

## 📄 License

This project is licensed under the MIT License.