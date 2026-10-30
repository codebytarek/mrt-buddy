# Changelog

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

