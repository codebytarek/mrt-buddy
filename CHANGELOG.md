# Changelog
## v0.0.50

- Removed the live backdrop shader from the large Balance card to address a possible rendering crash on affected devices.
- Kept the Balance card's opaque frosted appearance, shape, header colors, and shadow-free edges. The floating navigation retains its live blur.

## v0.0.49

- Applied the shared live backdrop blur and lens effect to the Balance card, with a near-opaque frosted finish so recent activity does not show through it.
- Preserved the Balance card's current shape, existing card header colors, and shadow-free edge treatment.

## v0.0.48

- Matched the bottom navigation selection animation to the HTML preview with a smoother 450 ms eased transition and a single soft overshoot.
- Set the launcher activity's display name and icon explicitly so Android uses the MRT Buddy branding during installation and launch.
- Refined the Balance card's frosted-glass fill and removed its box-like external shadow while preserving its shape.

## v0.0.47

- Restored app screens, behavior, and launcher assets to the v0.0.40 baseline.
- Preserved the v0.0.40 bottom navigation size and frosted color treatment, with floating backdrop blur while content scrolls behind it.
- Added a full-width animated selection capsule sized to each tab slot, including History.

## v0.0.46

- Added a shared floating Liquid Glass bottom navigation bar with backdrop blur, animated selection, and consistent spacing across screens.
- Refined the Balance card with a brighter opaque frosted surface and removed its external/drop shadow.
- Restored the MRT Buddy app label and the round green train launcher icon.

## v0.0.45

- Restored the Balance card's frosted Liquid Glass surface to match the bottom navigation, without an external shadow or lower shadow fade.
- Set the Android package and launcher labels explicitly to MRT Buddy and declared the round launcher icon for consistent install and app info metadata.
- Kept the green round train launcher artwork shown in the app icon reference.

## v0.0.44

- Removed the Balance display's gradient, tint, and outline layers that created an inset panel effect.
- Replaced the Android template robot launcher icon with the existing MRT train artwork and set the launcher activity label to MRT Buddy.

## v0.0.43

- Disabled Balance card elevation for every interaction state and softened its lower surface edge to remove the remaining shadow-like halo.

## v0.0.42

- Removed the Balance card's explicit drop shadow while preserving its frosted surface, shape, and outline.

## v0.0.41

- Softened the Balance card shadow in light and dark themes to reduce the box-like appearance around the balance display.

## v0.0.40

- Standardized low-balance warnings to use the app's fixed theme-aware red across Balance, History, and Fare.
- Brightened the Balance card with a more opaque frosted-glass surface in light and dark themes.

## v0.0.39

- Use Android Monet colors for the app backdrop and frosted glass surfaces, including the scanned balance card.
- Soften upper-edge reflections and outlines to make glass surfaces feel frosted rather than metallic.
- Use consistent recharge green and theme-appropriate spending red colors, independent of wallpaper colors.

## v0.0.38

- Removed the History page's card-deleted success notification; card list updates remain visible immediately.
- Removed unused English and Bengali card-deleted success strings and success events.

## v0.0.37

- Upgraded Android Gradle Plugin and Gradle to supported releases for Android API 37.
- Migrated the Android application entry point into a dedicated app module, keeping shared Kotlin Multiplatform code and platform behavior intact.
- Updated Kotlin, Compose Multiplatform, and KSP to compatible current releases.
- Refined balance warning presentation, fare selector surfaces, and card history visuals.

## v0.0.36

- Enlarged the low-balance warning animation for better visibility.
- Localized the Balance screen transaction count digits for Bengali.

## v0.0.35

- Added low, critical, and empty balance states with orange and red warning animations.
- Refined balance warnings and localized their messages in English and Bengali.
- Updated saved-card summaries and recharge/journey icons, and standardized signed amount formatting.

## v0.0.34

- Updated the English card placement instruction and restored the previous Bengali wording.

## v0.0.33

- Removed the recharge plus sign, transaction timestamp icons, and minus sign from the spent summary total.
- Enlarged the low-balance warning indicator for better visibility.
- Updated the card placement instruction in English and Bengali.

## v0.0.32

- Evenly spaced Card Summary metrics and improved the transaction summary header on narrow screens.

## v0.0.31

- Kept the train icon for journey transactions and use the Bengali Taka icon only for recharge transactions.
- Improved alignment of the low-balance warning beside the card balance.
- Use orange for low balances and red for critical balances, including a zero balance.
- Added scalable finance icons and themed scan warning/error animations.

### Download

The production release workflow publishes a release-signed APK and Android App Bundle (AAB).

## v0.0.30

- Removed the success check icon from the current balance display while keeping the amount centered.
- Refined saved-card history refresh and state restoration when returning to the app.
- Improved English and Bengali text consistency, station route spacing, and scalable navigation/NFC icons.
- Localized the NFC “Tag was lost” error as “Card removed too soon” in English and “কার্ডটি দ্রুত সরিয়ে নিয়েছেন” in Bengali.
- Kept saved cards observable across tab navigation and improved the History loading state.
- Refined the shared Liquid Glass bottom navigation and card surfaces, including content scrolling behind the navigation bar.
- Improved language persistence, Bengali UI text, and transaction/history state handling.

### Download

The GitHub release workflow publishes a directly installable debug APK for testing and sharing. It is not signed with a production release key.

## v0.0.29

- Updated the fare screen card prompt to: “Tap your card to check if you have enough balance.”
- Unified recharge and balance update transactions as the same add-money category in transaction summaries and history.
- Applied consistent transaction amount colors: add-money entries are green and spending entries are red.
- Improved NFC reading animation resources, theme handling, and playback integration.
- Refined balance, fare, history, and settings presentation, including dark mode and navigation surfaces.
- Added transaction parsing, fare calculator, and screen-state regression coverage.

### Download

The GitHub release includes an installable debug APK. It is suitable for direct testing and sharing; it is not signed with a production release key.

