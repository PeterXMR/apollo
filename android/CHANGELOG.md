# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and we also try to
follow [https://changelog.md/](https://changelog.md/) guidelines.

## [Unreleased]

## [50.2] - 2023-01-19

### ADDED

- Background notification processing improvements

### FIXED

- A crash when exporting Emergency Kit with poor connectivity

### CHANGED

- Kotlinized Http Interceptors
- Kotlinized and simplified BaseClient
- Kotlinized ClipboardProvider and GooglePlayServicesHelper


## [50.1] - 2023-01-06

### ADDED

- The option to copy to clipboard the separate parts of a unified qr.

### FIXED

- A problem with Android Keystore (secure storage) in J_MODE (api levels between 19 and 22)
when storing more than 256 bytes.
- A problem with screen scrolling in Unified QR screen in devices with small screens.
- Handling of "skipped email setup" preference which could lead users to a poor UX when recovering
a wallet with an unverified Recovery Code.

### CHANGED

- Added support for mixed cased query params (someone mentioned this was a problem for Alby) AND
  for bitcoin uris FULLY in uppercase
- Added better support for "unified qr" bitcoin uris and several test cases for bitcoin uris
- Removed deprecated lightningDefaultForReceiving preference (dead code)
- Removed deprecated payment analyzer code (dead code)

## [50] - 2022-12-8

### ADDED
- Support for "Lightning First" preference! Users can now choose to make lightning their preferred
way of receiving payments.
- Support for Unified QRs! Users can choose to activate our first experimental feature: a unified QR
that can enabled them to receive both on-chain (via bitcoin address) and offchain (via ln invoice).

### FIXED

- A problem with an inlined "What's this?" link in Recommended Fees screen
- Currency search in Select Currency screen was case-sensitive, now its insensitive.
- An issue that allowed users to submit incomplete or empty RCs using the softkeyboard.

## [49.12] - 2022-11-17

### FIXED
- A problem when reporting an inconsistency error with the user NTS state, which resulted in those
users unable to process notifications and use the app.
- A rare and infrequent crash in Emergency Kit export flow

## [49.11] - 2022-11-10

### ADDED
- Support for VES (Venezuelan Bolívar) currency.

### FIXED
- A problem with "copy to clipboard" of Payment Detail's Network Fee cell that resulted in just a
fraction of the total fee amount getting copied to the clipboard.
- A problem that allowed screenshots of a screen supposed to be "blocked for screenshots"
- Periodic task worker config. Probably broken since apollo-49.7 (upgrade targetSdkVersion to 31)
- Reliability problems during wallet recovery regarding activity destroy while app in background
- Handling of wallet recovery when Recovery Code Setup flow is interrupted or aborted half way
- A problem where we inadvertently allowed "unsafe" (not https) urls to be used for lnurl withdraws

### CHANGED
- Grouped related error classes in packages

## [49.10] - 2022-09-16

### FIXED
- Reproducible builds! We finally fixed issues in SqlDelight gradle plugin and
Go Mobile that messed up build reproducibility
- A problem where we didn't correctly expire invoices for unfulfillable swaps

### CHANGED
- Moved away from using personal forks and started using forks from official
Muun org
- Upgraded protobuf go package (really dropped github.com/golang/protobuf) and re-generate
bip70 Go code base on its proto file

## [49.9] - 2022-07-31

### FIXED
- Reverted home balance resize fix due to unexpected ANRs on some devices

## [49.8] - 2022-07-21

### CHANGED
- Enhanced errors' metadata with Crashlytics Breadcrumbs (e.g detecting when app was updated).
- Enhanced LNURL Withdraw error handling for 403 errors returned by some providers (e.g special
handling only for ZEBEDEE).
- Enhanced Receive Lightning screen for low/no connection scenarios

### FIXED
- Right margin on several dialog titles.
- Home balance not fitting screen when amount is to large (in btc) or system font size is enlarged.
- "Open email app" button not working (and other attempts to detect if an email client is installed
in the device) in Android 11 (30) or newer (by declaring package visibility needs).
- A proguard-related problem when adding new feature flags which resulted in apps getting bricked.

### ADDED
- Apps logs now get attached to error report emails to have extra metadata for resolving issues
and customer support issues.

## [49.7] - 2022-06-13

### CHANGED
- Upgraded several testing libs
- Upgraded compiledSdkVersion and targetSdkVersion to 31
- Upgraded workmanager library to 2.7.1 for compat with targetSdkVersion 31
- Upgrade Navigation library to 2.4.1
- Refactor to add OS classes to centralize checks for android version and supported capabilities

### FIXED
- Several crashes and problems regarding concurrent access to Android Keystore
- A few activity/fragment lifecycle related crashes
- Added a workaround for a rare, non-deterministic crash when trying to exit app by pressing back
- A problem in Emergency Kit export flow that could sometimes result in a dead end after a
 successful manual export

### ADDED
- An option to always be able to save the Emergency Kit manually in the file system.

## [49.6] - 2022-06-08

### FIXED
- Removed unused AD_ID permission, added by Firebase SDK in latest upgrade (in 49.5)

## [49.5] - 2022-06-02

### CHANGED
- Upgrade Firebase dependencies to start using BOM (Bill Of Materials). firebase-bom:30.1.0
- Upgrade Firebase Messaging as part of firebase-bom:30.1.0
- Upgrade Firebase Crashlytics as part of firebase-bom:30.1.0
- Upgrade Firebase Analytics as part of firebase-bom:30.1.0
- Upgrade Google Services Gradle Plugin to 4.3.10
- Changed splash screen english copy
- Increased FcmToken fetch timeout and add extra retry to improve reliability in Fcm Token registration

### FIXED
- Removed extra query param requestId for lnurl withdraw requests to avoid errors with certain
LNURL service providers (e.g ln.cash)

## [49.4] - 2022-05-23

### CHANGED
- Receiving Node cell behavior in Operation Detail. Instead of opening 1ML site (private nodes
return a 404 not found), we copy node public key to clipboard.

### FIXED
- A bug that triggered LN invoice regeneration multiple times when a payment was received
- Added a workaround for a strange BadPaddingException coming from Android's Keystore

## [49.3] - 2022-04-26

### ADDED
- Better error reporting and extra metadata for MoneyDecoration (MuunAmountInput) crash
- Show LN alias for outgoing payments in payment history and payment Detail

### FIXED
- Added missing error metadata to some crashlytics errors (e.g for background task of anon users)
- Invoice expiration time label getting cut off due to very long expiration time formatting
- Avoid crashing and add debug snapshot with audit trail for SecureStorageErrors

## [49.2] - 2022-03-22

### FIXED
- A crash when you have contacts with old address versions
- An error processing operation updates for lightning payments that made the look as if stuck pending

## [49.1] - 2022-03-17

### FIXED
- Bug when fetching legacy Contact model after SQLDelight upgrade (on 49)
- Use of Math.toIntExact() which isn't supported on lower api levels (introduced in SQLDelight
upgrade)
- MuunAmountInput handling of SATs (currencies without decimals)
- Incorrect handling of changeCurrency and useAllFunds in send flow, introduced in our send payment
flow rewrite (48.2)
- Minor copy change when copying a LN payment hash to clipboard

## [49] - 2022-03-16

### ADDED
- Option to select SAT as input currency for receive and send screens' amount input
- Extra metadata for rare crash scenario
- Multiple route hints support in our invoices

### CHANGED
- Upgrade gradle to 7.3.3 to support JDK17 (ARM support)
- Upgrade AGP to 7.0.4 for gradle 7.3 compat
- Upgrade SQDelight to 1.5.3 for gradle 7.3 compat (huge refactor and rework of data layer)
- Upgrade Kotlin to 1.6.10 for gradle 7.3 compat
- Upgrade Dagger to 2.40.5 for gradle 7.3 compat
- Upgrade checkstyle to 9.2.1
- Make libwallet it's own gradle project to work nicely with AndroidStudio

### FIXED
- Show new outgoing operation badge animation when using deeplink + process death/app not started
- Rare crash probably due to quick flurry of click events
- "Fixed rate window" feature, to keep exchange rates fixed/constant through a flow
- Rather unlikely error during sign in where, after a process death, we navigated to SYNC step
 while also recoding SYNC step as previous step.
- Home Screen Back Navigation. Issue reported: https://github.com/muun/apollo/issues/63

## [48.4] - 2022-01-17

### FIXED

- Race condition in MuunAmountInput to avoid random crashes
- Added missing null check in ExternalResultExtension, to avoid rather frequent crashes

## [48.3] - 2022-01-14

### CHANGED
- Improved send payment flow reliability by fixing a Fragment lifecycle issue

### FIXED

- Missing tracking event param for send payment flow
- Visual glitch while waiting for bip72 uris to load
- Set correct text color for amount input after useAllFunds + back
- Filter empty outpoints strings when submitting a payment to Houston
- UI issues with tricky send payment case where useAllFunds ends up with amount < DUST
- Typo in english string text, shout out to @BBlackwo

## [48.2] - 2022-01-11

### CHANGED
- Re-wrote entire send payment flow to move it to libwallet (shared lib), solve many issues with
 previous implementation and enhance testability and flexibility
- Increased invoice expiration time to 24 hours! Merry Christmas y'all!

### FIXED
- Ukrainian flag in select currency screen

### REMOVED
- Deprecated Salvadoran Colon, no longer used currency

## [47.3] - 2021-11-15

### FIXED
- Emergency Kit manual share flow for oems that behave badly and break our broadcast receiver
- Taproot by default Bitcoin setting (show taproot addresses for receive by default)
- Several minor visual issues

## [47.2] - 2021-11-11

### FIXED
- Signing of taproot musig transactions
- Several visual issues

## [47.1] - 2021-11-10

### ADDED
- New Emergency Kit Update flow! Users can now update their Emergency Kit to activate useful new
features like Taproot
- Taproot support! Users are now fully equipped to received from and send to taproot addresses.

### FIXED
- Several minor bugs

## [46.11] - 2021-10-18

### ADDED
- Exact date time to error reports to have more granularity (of seconds)
- Request ids for LNURL requests to enhance error reports and troubleshooting with LNURL service
 providers.

### CHANGED
- Data visibility in operation detail screen. Hide confirmations for failed transactions

### FIXED
- Crash in MuunAmountInput introduced with newest amount formatting impl (46.9)
- Null support ids on error reports

## [46.10] - 2021-09-22

### FIXED
- Android context handling in NewOperationErrorFragment for locale use for amount formatting

## [46.9] - 2021-09-21

### ADDED
- Locale to errors and error reports
- Custom handling of service provider specific lnurl errors
- Pull notifications in lnurl screen to speed up ln payments using lnurl

### CHANGED
- Enhanced InvalidActionLink errors
- Updated 'Google Play Services not available in device' error explanation
- Enhanced RxPreference 3rd party library code to workaround enum prefs with null values

### FIXED
- Amount formatting issues with different locales/languages
Note: MuunAmountInput still uses THIN_SPACE as grouping separator, due to android bug.
- Lnurl payments with synchronous lnurl service providers (e.g. AZTE.CO)
- Fragment manager lifecycle issues in ErrorFragment

## [46.8] - 2021-08-30

### CHANGED
- Enriched certain non-standard LNURL errors to help debug/fix certain integrations (e.g zebedee)
- Made recovery code always fully visible (disable system font size based scaling for RC box)

### FIXED
- Algerian Flag icon

## [46.7] - 2021-07-27

### CHANGED
- Kotlinized MuunView
- Kotlinized AlertDialogExtension
- Refactor to add dependency injection in our custom views (children of MuunView)
- Enhanced connectivity error message and remove contact us call to action

### FIXED
- Ignore multiple quick taps in EK method selection
- NPE when clipboard is empty. Solve error when entering LNURL Scan QR screen via receive screen
- Tracking of "Non-LNURL Uri in LNURL UriPaster" error
- Bug in FulfillIncomingSwap action
- Lateinit property has not been initialized in SelectAmountPresenter

## [46.6] - 2021-07-14

### CHANGED
- Mitigated bug when receiving lightning payments that might leave app unresponsive
- Improved soft keyboard focus handling in several forms
- Improved error handling for failed LNURL flows
- Avoid showing shortcut to address or invoice in clipboard when on LNURL scan QR screen

### FIXED
- Error that caused background process to fail after a cold start
- Handled compatibility with some non-standard LNURL impls (e.g AZTE.CO's)
- Crash while handling errors during the wallet creation flow
- Bug when scanning strange  QR inputs (e.g iCalendar events)
- Highly unlikely crash on createPassword due to fragment's lifecycle issues
- Highly unlikely crash due to multiple getSessionStatus calls

## [46.5] - 2021-06-24

### FIXED
- Crash in platforms with fake/replaced Google Play Services (e.g CalyxOS includes microG)

## [46.4] - 2021-06-24

### CHANGED
- Enhanced LNURL error handling
- Handle LNURL Fallback Mode
- Increased LNURL requests timeouts for better reliability with slow services

### FIXED
- Use new ImageDecoderApi for MuunPictureInput, fix crash Huawei devices
- Handling of FulfillIncomingSwap notifications. More reliable receive lightning
- Error due to double destroy wallet on session expired
- Crash in scan QR with strange input
- Glitches and issues with soft keyboard display in some screens
- Crash regarding paymentContext state saving and activity reconstruction in New Operation

## [46.3] - 2021-06-07

### CHANGED
- Small additions to crashlytics noise-reducing blacklist

### FIXED
- Crash when loading null image urls
- LNURL handling when used as a deeplink
- Compatibility with certain LNURL services as lnpay.co y lnbits.com


## [46.2] - 2021-06-04

### FIXED
- Dark mode color for some buttons
- Crashlytics noise-reducing blacklist
- ErrorFragment problems with description clicks

## [46.1] - 2021-06-04

### FIXED
- An issue with our image loading library (Glide) after light/dark mode changes

## [46] - 2021-06-02

### ADDED
- LNURL withdraw support: Users can now withdraw from services using LNURLs.
- Enhanced Dark Mode color palette to better adapt to different devices renderings.

### CHANGED
- Enhanced crashlytics reporting: silence or avoid reporting noisy errors

### FIXED
- Some problems when creating or recovering a wallet in low end devices (related to
repository wiping/clearing logic pre-login/signup)
- Some spanish translations
- South African flag icon
- A problem where the home chevron animation froze
- Several other minor visual issues

### REMOVED
- Display of the fee for incoming TXs: Users will no longer be confused by the detail of an
 incoming operation showing a fee.
- Autofocus feature to be compat with more devices

## [45.5] - 2021-05-03

### FIXED
- Downgrade segwit addresses QR impl to be compat with some services like blockchain.info

### REMOVED
- Bintray/JCenter dependencies to avoid build problems in Open Source repository

## [45.4] - 2021-04-22

### FIXED
- Add missing default for mempool's MinFeeRate repository

## [45.3] - 2021-04-20

### FIXED
- Handle UseAllFunds swaps for really small amounts properly
- Several bugs and enhancements regarding lightning payments

## [45.2] - 2021-03-31

### ADDED
- Logging for incoming lightning payments errors

### CHANGED
- Enhanced email error reporting (rooted device hint, breadcrumbs, presenter name, etc...)

### FIXED
- Some issues in our custom views regarding rate provider initialization
- Avoid showing paypal as send EK email option
- EmergencyKit sliders background in Dark Mode

## [45.1] - 2021-03-27

### FIXED
- Some issues with reproducible builds (dockerfiles)
- Crash on btc receive screen when confirming 0 amount

## [45] - 2021-03-26

### ADDED
- Edit Invoice amount feature:
Users can now set and edit an amount for a LN invoice.
They can also set an amount to their BTC address, turning it into a Bitcoin Uri.
- Dark Mode User Setting:
New user setting with up to 3 options: Dark, Light, Follow System.

### CHANGED
- Migrate to use Mempool.Space Block Explorer:
Changed Blockstream (a bit unreliable) block explorer for Mempool.Space (new kid on the block).
- Completely redesigned the receive screen to add advanced settings section with stuff like
edit amount option, expiration time, address type picker.

### FIXED
- Several missing background and screen overlapping (e.g new op error, scan QR error, etc...)
- New operation badge animation logic in home
- Repository wiping/clearing logic on logout
