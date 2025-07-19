# Ora - Retro Flip Clock ⏰

A minimalist, full-screen flip clock application for Android, designed with a retro aesthetic and an AMOLED-friendly interface. This project focuses on deep customization and a polished user experience.

![Ora Screenshot](https://i.imgur.com/L8a1uRk.png)
> **Note:** You should replace this with an updated screenshot of your app running with your favorite new theme!

---

## ✨ Features (v1.5)

* **Full-Screen & Immersive:** Hides system bars for a clean look and automatically re-hides them after 15 seconds of inactivity.
* **Complete Customization via a Reorganized Settings Menu:**
  * **Time Format:** Choose between 12-hour (AM/PM) and 24-hour formats.
  * **Font Selection:** Choose from 10 different fonts for the clock, date, and AM/PM indicator.
  * **Background Selection:** Select from pre-defined themes or any image from your device's photo gallery.
  * **Text Size & Color:** Independently control the size and color for the clock, date, and AM/PM indicator.
  * **Text Shadow:** Toggle shadows on or off for the clock, date, and AM/PM text for better contrast.
  * **Display Toggles:** Show or hide the date and the AM/PM indicator.
  * **Auto-Hide Settings Button:** The settings button now fades out after a user-defined delay (3-20 seconds).
  * **Reset Option:** A button to restore all settings to their default state.

---

## 📜 Version History

### Version 1.5 (Latest)
* Added an auto-hide feature for the settings button with a customizable delay.
* Added toggle switches to enable or disable text shadows on all text elements.
* The app now automatically returns to full-screen mode after 15 seconds of inactivity.
* Fixed layout issues to ensure the clock fits perfectly on narrow (phone) screens.
* Corrected various bugs with timers and settings not applying correctly.

### Version 1.4
* Added the ability to select a custom background image from the device's photo gallery.
* Implemented runtime permissions for media access and secure local copying of the image.

### Version 1.3
* Added 12/24 hour time format selection and AM/PM toggle.
* Added separate size and color customization for the AM/PM text.
* Added a "Reset to Default" option.

(Older versions omitted for brevity)

---

## 🛠️ Built With

* **Language:** [Kotlin](https://kotlinlang.org/)
* **UI:** Android XML Layouts with `PreferenceScreen`.
* **Image Loading:** [Coil](https://coil-kt.github.io/coil/)
* **Build System:** [Gradle](https://gradle.org/)

---

## 🚀 Getting Started

1.  Clone the repository.
2.  Open the project in Android Studio and let Gradle sync.
3.  Build and run the app.

---

## 🗺️ Roadmap

* **[v1.6]** Integrate a simple weather and temperature display.
* **[v1.6]** Add an option to change the ":" separator style or color.