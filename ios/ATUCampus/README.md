# ATU Campus iOS

Native SwiftUI version scaffold for ATU Campus.

## What is included

- SwiftUI app shell with the same onboarding flow as Android
- Native camera capture via `AVFoundation`
- Native OCR helper via `Vision`
- Local profile storage via `UserDefaults`
- ATU news scraping from `atu.edu.az`
- AI chat and backend verification using the existing Node backend
- Light/dark ready premium design system

## How to open

1. Install [XcodeGen](https://github.com/yonaskolb/XcodeGen) on macOS.
2. Open Terminal in `ios/ATUCampus`.
3. Run:

```bash
xcodegen generate
open ATUCampus.xcodeproj
```

## How to build IPA

1. Open the generated Xcode project.
2. Set your Apple Team in Signing.
3. Select a physical iPhone or `Any iOS Device`.
4. Product -> Archive
5. In Organizer -> Distribute App -> Ad Hoc/TestFlight/App Store depending on your need.

Windows cannot export a signed `ipa` directly. The source project here is the required step before Xcode archive/export.
