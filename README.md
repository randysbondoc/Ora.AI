# Ora - Retro Flip Clock ⏰

A minimalist, full-screen flip clock application for Android, designed with a retro aesthetic and an AMOLED-friendly interface. This project was built to explore core Android development concepts, including custom views, animations, and modern build configurations with Gradle.

![Ora Screenshot](https://i.imgur.com/L8a1uRk.png)
> **Note:** You should replace the image link above with a real screenshot of your app running. Take a screenshot from your tablet, upload it to the GitHub issue tracker or a service like Imgur, and paste the new link.

---

## ✨ Features

The current version (v1.0) of Ora includes:

* **Full-Screen Display:** Hides the system status and navigation bars for an immersive experience.
* **Retro Clock Face:** Displays the current time with individual components for hours, minutes, and seconds, using a simple animation to represent the flip.
* **Live Date Display:** Shows the current date below the clock in `Month Day, Year` format.
* **AMOLED-Friendly:** Features a pure black background (`#000000`) to save power on OLED screens.
* **Screen Always On:** Prevents the device from sleeping while the app is active.
* **Auto-Dimming:** Dims the screen to 40% brightness after 3 minutes of inactivity to prevent screen burn-in. Tapping the screen restores full brightness.

---

## 🛠️ Built With

This project leverages a modern Android development stack:

* **Language:** [Kotlin](https://kotlinlang.org/)
* **UI:** Android XML Layouts with `ConstraintLayout` and `LinearLayout`.
* **Build System:** [Gradle](https://gradle.org/) with the Kotlin DSL and Version Catalogs for dependency management.
* **Architecture Components:**
    * **View Binding:** For safe and efficient interaction with UI views.
    * **Animations:** Simple `ObjectAnimator` for digit transitions.
* **Minimum SDK:** API 26 (Android 8.0 Oreo)
* **Target SDK:** API 36 (Android 15)

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
4.  Build and run the app on your device or emulator.

---

## 🗺️ Roadmap

This project is in active development. Planned features for future versions include:

* **[v1.1]** Implement a settings button.
* **[v1.1]** Add a font selection menu to customize the date display.
* **[v1.2]** Persist user settings (like font choice) across app sessions using `SharedPreferences`.
* **[v2.0]** Explore more advanced clock face themes and animations.

---

## 📄 License

This project is licensed under the MIT License. See the `LICENSE` file for details.

> **Note:** You should create a file named `LICENSE` in your project's root directory and paste the text of the MIT license into it. You can find the text [here](https://opensource.org/license/mit).

---

## 👤 Contact

Randy S. Bondoc - [randysbondoc](https://github.com/randysbondoc)

Project Link: [https://github.com/randysbondoc/Retro-Flip-Clock](https://github.com/randysbondoc/Retro-Flip-Clock)