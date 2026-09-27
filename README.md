# AlphabetLauncher

A modern, minimal Android launcher built with **Jetpack Compose** featuring a smooth A–Z alphabet bar with a Gaussian curve displacement animation, live clock, favourites list, and instant letter-based app filtering.

---

## Features

- **Gaussian Curve A–Z Index Bar**: Interactive vertical scrollbar with a dynamic wave distortion that bulges outward under your touch point and snaps back using spring physics.
- **Floating Letter Magnifier**: Enlarged floating bubble showing the currently selected letter as you drag along the index bar, accompanied by subtle haptic feedback.
- **Smart App Filtering**: Filter installed device applications instantly by tapping or dragging through the alphabet bar.
- **Resting Home Screen**: Displays a live clock, date, and a quick-access favourites list when no letter is selected.
- **Fast Package Caching**: Thread-safe caching repository leveraging Android's `PackageManager` for zero-lag application fetching.

---

## Setup Steps

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/VasupariSaikumar/AlphabetLauncher.git
   cd AlphabetLauncher
   ```
2. **Open in Android Studio**:
   Open Android Studio (Ladybug / 2024.2.1 or newer recommended) and select **Open** -> navigate to the `AlphabetLauncher` folder.
3. **Configure JDK**:
   Ensure Gradle is using JDK 11 or higher in **Settings > Build, Execution, Deployment > Build Tools > Gradle**.
4. **Sync Project**:
   Let Gradle sync dependencies automatically, or trigger it manually via **File > Sync Project with Gradle Files**.
5. **Run the App**:
   Connect an Android device or launch an emulator (Min API 28 / Android 9.0+) and click **Run 'app'** (`Shift + F10`).

---

## How the Curve Animation is Built

The wave distortion effect on the `AlphabetBar` is built purely in Jetpack Compose using a mathematical **Gaussian distribution curve**:

1. **Touch Tracking & Slot Mapping**:
   When dragging along the vertical A–Z bar, `detectDragGestures` tracks the touch $Y$-coordinate. The bar height is divided into 26 equal slots to determine the active letter slot.

2. **Gaussian Displacement Calculation**:
   For every letter slot $i$, the vertical distance $dy$ between the slot's center and the touch point $Y$ is calculated. A Gaussian bell-curve function determines the displacement factor:
   $$\text{influence} = e^{-\frac{dy^2}{2\sigma^2}}$$
   where $\sigma$ controls the width (spread) of the curve wave.

3. **Outward Displacement**:
   Each letter's horizontal $X$-offset is snapped to `-maxOffsetPx * influence`, causing letters near the touch point to smoothly bulge outwards toward the left, forming a natural wave shape centered on your finger.

4. **Spring Return Physics**:
   When the user releases their finger, `animateTo(targetValue = 0f)` is invoked on each letter's `Animatable` using Compose spring physics (`dampingRatio = MediumBouncy, stiffness = Low`), giving a natural, elastic snap-back effect.

---

## Third-Party Libraries & Dependencies

| Library | Version | Purpose / Why it was used |
| :--- | :--- | :--- |
| **`androidx.compose.bom`** | `2026.02.01` | Manages consistent, compatible versions across all Jetpack Compose artifacts. |
| **`androidx.compose.ui:ui`** | BOM Managed | Core Jetpack Compose framework for rendering UI elements, handling layouts, and pointer input. |
| **`androidx.compose.ui:ui-graphics`** | BOM Managed | Provides graphics primitives, colors, and drawing capabilities for Compose. |
| **`androidx.compose.material3:material3`** | BOM Managed | Implements Google's Material Design 3 component design system and dynamic color schemes. |
| **`androidx.compose.material:material-icons-extended`** | BOM Managed | Provides extended Material vector icons (such as `Icons.Filled.Star`) for UI decoration. |
| **`androidx.activity:activity-compose`** | `1.13.0` | Connects Android Activity lifecycles to Compose (`setContent`) and enables edge-to-edge support. |
| **`androidx.core:core-ktx`** | `1.19.0` | Provides idiomatic Kotlin extensions for standard Android framework APIs. |
| **`androidx.lifecycle:lifecycle-runtime-ktx`** | `2.11.0` | Integrates Android lifecycle states with Kotlin Coroutines and Compose UI state triggers. |
| **`com.google.accompanist:accompanist-drawablepainter`** | `0.37.3` | Renders native Android `android.graphics.drawable.Drawable` app icons directly inside Compose `Image` composables. |

