# ATU Campus iOS build without Mac

You can build the iOS app without owning a Mac by using Codemagic's macOS runners.

## What you already have in this repo

- Native SwiftUI iOS source: `ios/ATUCampus`
- XcodeGen project spec: `ios/ATUCampus/project.yml`
- Codemagic pipeline: `codemagic.yaml`

## What you need

1. A Git repository for this project
2. A Codemagic account
3. An Apple account
4. For a signed `.ipa` installable on iPhone through normal Apple signing, an Apple Developer Program membership is the practical path

Apple says:
- Xcode is downloaded from the Mac App Store and requires macOS
- A free Apple developer account can test apps on your own device using Xcode
- Apple Developer Program membership unlocks distribution features like TestFlight and broader app distribution

## Fastest path

1. Push this project to GitHub
2. Sign in to Codemagic and connect the repo
3. In Codemagic, add an App Store Connect API key integration named:
   - `atu-campus-appstore`
4. Add iOS signing assets in Codemagic:
   - certificate
   - provisioning profile
5. Start the workflow:
   - `ios-signed-ipa`
6. Download the generated `.ipa` from the build artifacts

## If you do not have paid Apple Developer membership

You can still keep the iOS source code and continue development, but a properly signed iPhone-installable `.ipa` is the hard part without Apple's signing/distribution setup.

In that case, the practical options are:

- borrow/rent access to a Mac once
- join Apple Developer Program
- use Codemagic with valid Apple signing assets

## Important paths

- iOS source: `ios/ATUCampus`
- iOS zip: `ios/ATUCampus-ios-source.zip`
- Codemagic config: `codemagic.yaml`
