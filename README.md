# Ora.AI - Retro Flip Clock ⏰

Ora.AI is a modern, full-screen clock designed for elegance, functionality, and deep personalization. Turn any device into a stunning desk clock, a nightstand companion, or a focused work timer. You have complete control to create a look that is uniquely yours. Full customization is at your fingertips, including the ability to select your own wallpaper from your device's gallery. Ora.AI is on a path of continuous development. Future updates will bring exciting new functions and features, including planned integration with Google Gemini and Google Assistant.

<table align="center">
  <tr>
    <td align="center">
      <img src="./assets/ora_logo.png" alt="Ora.AI Logo" width="200"/>
    </td>
    <td align="center">
      <img src="./assets/ora_screenshot_v1_6.png" alt="Ora.AI Screenshot" width="400"/>
    </td>
  </tr>
</table>

---

## ✨ Features (Version 1.7)

* **Advanced Positioning Controls:** Take full control of your layout with independent vertical sliders for the Clock and Date, plus a horizontal slider for the Date.
* **Complete UI Control:** A new toggle switch allows you to completely hide the main clock display for a minimalist, wallpaper-focused view.
* **New Fonts:** Added 12 new custom fonts to the selection list for greater personalization, with display names updated for uniformity.
* **Enhanced "About" Section:**
  * All informational pages ("About", "Battery", "GitHub", "Settings Guide", "Buy me a coffee") are now on their own dedicated screens for a cleaner UI.
  * The "Buy me a coffee" page has been redesigned with clickable app links, tappable QR codes, and 30 randomly changing inspirational quotes.
* **Numerous Bug Fixes & Fine-Tuning:**
  * Fixed a critical bug that caused the app to crash when taking a screenshot with a custom background by implementing the modern `PixelCopy` API.
  * Fixed a persistent bug preventing the correct package names from being used to launch external payment apps (GCash, Maya, SeaBank).
  * Resolved various layout and styling issues, including the GitHub logo visibility and informational screen auto-close timers.

---

## 📜 Version History

### Version 1.6
* Added a full preset system with 5 save slots.
* Completely reorganized the settings menu into logical sub-screens.
* Added advanced options for digit backgrounds (Filled, Outline, None, Color, Transparency).
* Added a "Randomize Style" button and an "Auto-change Text Colors" feature.
* Added a screenshot-to-gallery feature.
* Redesigned the "About" screen with more details and sub-pages.
* Fixed numerous bugs, including issues with custom backgrounds in presets.

### Version 1.5
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

### Version 1.2
* Added ten custom fonts for the clock, date, and AM/PM.
* Implemented a custom font picker dialog in the settings menu.

### Version 1.1
* Added a full color picker for the clock, date, and AM/PM text.
* Added five pre-defined background images and a plain white background option.

### Version 1.0
* Initial release.
* Features a full-screen, retro-style flip clock with seconds.
* Includes date and AM/PM display.
* Basic settings for text size and color.

---

## 🚀 The Future of Ora.AI

Ora.AI is constantly evolving. We are committed to adding more powerful functions and features, with a focus on creating the best user experience and integrating exciting new AI capabilities.

---

## 🛠️ Built With

* **Language:** [Kotlin](https://kotlinlang.org/)
* **UI:** Android XML Layouts with `PreferenceScreen`.
* **Image Loading:** [Coil](https://coil-kt.github.io/coil/)
* **Build System:** [Gradle](https://gradle.org/)

---

## 📦 Getting Started

1.  Clone the repository.
2.  Open the project in Android Studio and let Gradle sync.
3.  Build and run the app.