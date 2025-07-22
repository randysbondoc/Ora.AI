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

## ✨ A Class Apart: Features & Benefits

Ora.AI isn't just another clock app; it's a complete style engine for your screen. We provide a level of control and personalization that you won't find anywhere else.

### 🎨 Unmatched Customization
This is the core of Ora.AI. We believe you should have total control over every pixel.

* **Dynamic Positioning:** Unlike other clock apps that are fixed in place, Ora.AI lets you move the **Clock and Date independently**. Use simple sliders to position them exactly where you want, both vertically and horizontally.
* **Total Text Control:** Customize the **font, size, color, and highlight style** (Shadow or Outline) for every single text element—the main clock, the date, and the AM/PM indicator.
* **Your Photos as a Backdrop:** Go beyond our built-in wallpapers. Select **any image from your device's gallery** to use as a personal, high-resolution background.
* **Digit Background Styling:** Fine-tune the look of the digit containers with **Filled, Outline, or Transparent** styles, complete with color and opacity controls.

### 💡 Smart & Effortless Styling
For moments when you need inspiration, Ora.AI offers powerful tools to discover new looks without the effort.

* **Style Presets:** Found a look you love? **Save up to 5 complete styles** as presets. Load them back instantly from a simple dropdown menu, letting you switch moods in a single tap.
* **Instant Discovery:** Feeling adventurous? The **"Randomize Style"** button instantly generates a new, unique theme, discovering combinations you might not have thought of.
* **Automated Color Cycling:** Enable **"Auto-change Text Colors"** for a dynamic, ever-changing display that cycles through the color palette at an interval you set.

### ⚙️ Powerful Utility Features
Beyond looks, Ora.AI is packed with useful features designed for a seamless experience.

* **Built-in Screenshot Tool:** Capture your perfect design. A dedicated button in the settings saves a high-quality screenshot of your clock screen directly to your photo gallery.
* **OLED Optimized & Battery Conscious:** The default **Amoled Black** theme is perfect for OLED screens, turning off pixels to save power and provide stunning contrast. The screen also **auto-dims** after a period of inactivity to conserve battery life.
* **Immersive, Distraction-Free UI:** Ora.AI provides a true full-screen experience. The settings button **automatically hides** after a customizable delay, leaving you with a clean and elegant display.

---

## 📜 Version History

### Version 1.7.1
* **Code Modernization & Stability Update:** Replaced all `Handler` based timers throughout the application with modern, lifecycle-aware Kotlin Coroutines. This major internal refactoring improves app stability, reduces the potential for memory leaks, and makes the asynchronous code cleaner and more efficient.

### Version 1.7
* **Advanced Positioning Controls:** Added vertical and horizontal position sliders for the Clock and Date.
* **Complete UI Control:** Added a toggle switch to show or hide the entire clock.
* **Security & Build Fixes:** Moved GitHub token to a secure file and fixed related build errors in the `:fontspan` module. Integrated `keystore.properties` for release signing.

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