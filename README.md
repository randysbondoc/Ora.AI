# Ora - Retro Flip Clock ⏰

A minimalist, full-screen flip clock application for Android, designed with a retro aesthetic and an AMOLED-friendly interface. This project was built to explore core Android development concepts, including custom UI, settings management with `PreferenceScreen`, and modern build configurations with Gradle.

![Ora Screenshot](https://i.imgur.com/L8a1uRk.png)
> **Note:** You should replace this with an updated screenshot of your app running with one of the new backgrounds!

---

## ✨ Features (v1.1)

* **Full-Screen Display:** Hides the system status and navigation bars for an immersive experience.
* **Customizable Clock Face:** Displays the time with individual components for hours, minutes, and seconds.
* **Live Date Display:** Shows the current date below the clock.
* **Screen Always On:** Prevents the device from sleeping while the app is active.
* **Auto-Dimming:** Dims the screen to 40% brightness after 3 minutes of inactivity to prevent screen burn-in.
* **Complete Customization via Settings Menu:**
    * **Font Selection:** Choose from 10 different fonts for both the clock and the date, with a live preview in the settings menu.
    * **Background Selection:** Choose from multiple backgrounds, including solid colors (AMOLED Black, White), custom images, and gradients.
    * **Text Color Inversion:** Text automatically inverts to be readable on light backgrounds.
    * **Show/Hide Date:** Toggle the visibility of the date display.

---

## 🛠️ Built With

This project leverages a modern Android development stack:

* **Language:** [Kotlin](https://kotlinlang.org/)
* **UI:** Android XML Layouts with custom preference screens (`PreferenceScreen`).
* **Build System:** [Gradle](https://gradle.org/) with the Kotlin DSL and Version Catalogs.
* **Architecture Components:**
    * **View Binding:** For safe and efficient interaction with UI views.
    * **`SharedPreferences`:** For persisting all user settings.
* **Minimum SDK:** API 26 (Android 8.0 Oreo)
* **Target SDK:** API 34 (Android 14)

---

## 🚀 Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

* [Android Studio](https://developer.android.com/studio) (Iguana or newer recommended)
* An Android device or emulator running API 26 or higher.

### Installation

1.  Clone the repository:
    ```sh
    git clone [https://github.com/randysbondoc/Retro-Flip-Clock.git](https://github.com/randysbondoc/Retro-Flip-Clock.git)
    ```
2.  Open the project in Android Studio.
3.  Let Gradle sync and download the necessary dependencies.
4.  Build and run the app.

---

## 🗺️ Roadmap

* **[v1.2]** Explore adding weather and temperature display.
* **[v1.2]** Add an option for a 12-hour (AM/PM) vs 24-hour clock format.
* **[v2.0]** Investigate home screen widget functionality.

---

## 📄 License

This project is licensed under the MIT License. See the `LICENSE` file for details.

---

## 👤 Contact

Randy S. Bondoc - [randysbondoc](https://github.com/randysbondoc)

Project Link: [https://github.com/randysbondoc/Retro-Flip-Clock](https://github.com/randysbondoc/Retro-Flip-Clock)