![Hero Image](/mobile-ui-library-hero-image.png)

# Azure Communication UI Mobile Library for React Native

![node](https://img.shields.io/badge/node-%3E%3D14.0.0-brightgreen)

This project demonstrates the integration of Azure Communication UI library into a React Native that utilizes the native Azure Communication UI library and Azure Communication Services to build a calling experience that features both voice and video calling.

## Features

Please refer to our native [UI Library overview](https://docs.microsoft.com/en-us/azure/communication-services/concepts/ui-library/ui-library-overview?pivots=platform-mobile)

## ❤️ Feedback

We appreciate your feedback and energy helping us improve our services. [If you&#39;ve tried the service, please give us feedback through this survey](https://microsoft.qualtrics.com/jfe/form/SV_9WTOR2ItSo0oFee).

## Prerequisites

An Azure account with an active subscription. Create an account for free.
A deployed Communication Services resource. Create a Communication Services resource.
An Authentication Endpoint that will return the Azure Communication Services Token. See example or clone the code.

Node, Watchman, and React Native CLI: please refer to [React Native environment setup guide](https://reactnative.dev/docs/environment-setup).

Yarn: refer to [installation guide](https://classic.yarnpkg.com/lang/en/docs/install)

iOS: Xcode, CocoaPods: refer to [iOS requirements for UI library](https://github.com/Azure/communication-ui-library-ios#requirements)

Android: Android Studio, JDK: refer to [Android prerequisites](https://github.com/Azure/communication-ui-library-android#prerequisites)

Link to Authentication Endpoint Sample: [link](https://docs.microsoft.com/en-us/azure/communication-services/quickstarts/identity/quick-create-identity)

## Run Sample App

Navigate to `demo/`:

1. Run `yarn install`

Install iOS app dependencies:

1. In Terminal, navigate to `demo/ios/`:
2. Run `pod install --repo-update`

Build android app dependencies:

1. In Terminal, navigate to `demo/android/`:
2. Run `./gradlew build`

Navigate back to `demo/`

1. Run `yarn react-native start`
2. Open another Terminal, navigate to `demo/` folder, and run `yarn react-native run-ios` or `yarn react-native run-android`

Alternatively, you can also run the iOS app by launching Xcode from the `.xcworkspace` file, and run the app with scheme `demo` on your simulator or iOS device.

To run Android app, you can also launch Android Studio and run on Android emulator or Android device after syncing up gradle. There are two ways to sync gradle either with a command in the android folder `./gradlew build` or via android studio.

## Key Sample Highlights

To integrate the native UI Library with React Native in this sample, a few key steps were taken as described below:

### iOS

After installing the package and dependencies with CocoaPods from the step above, modify the Podfile in the `/ios` filder as such:

```ruby
platform :ios, '16.0' 
target 'demo' do 
  use_frameworks! 
  pod 'AzureCommunicationUICalling', '1.12.0' 
  ... 

  # Note: disable the line below since we've enabled use_frameworks! 
  # use_flipper!() 
  ... 
end 
```

Navigate to the `ios/` folder and open the `.xcworkspace` file with Xcode.

Set iOS Deployment Target in Build Settings for the main project to minimum iOS 16.0:

![ae0f2bf7-17f3-435c-828a-e7bfaf1b3e2e](https://user-images.githubusercontent.com/9044372/180568611-71d671c2-6bd4-4542-9d66-87fc9da8eddd.jpg)

Request access to the microphone, camera, etc.
To access the device's hardware, update your app's Information Property List (`Info.plist`). Set the associated value to a `string` that will be included in the dialog the system uses to request access from the user.

Right-click the `Info.plist` entry of the project tree and select **Open As** > **Source Code**. Add the following lines the top level `<dict>` section, and then save the file.

```xml
<key>NSCameraUsageDescription</key> 
<string></string> 
<key>NSMicrophoneUsageDescription</key> 
<string></string> 
```

To verify requesting the permission is added correctly, view the `Info.plist` as **Open As** > **Property List** and should expect to see the following:

![abcca137-6463-4e9a-8db4-b68df6db5ce8](https://user-images.githubusercontent.com/9044372/180568964-71348562-e9a6-4a5e-847e-537e58e376ce.jpg)

Turn off Bitcode
Set `Enable Bitcode` option to `No` in the project `Build Settings`. To find the setting, you have to change the filter from `Basic` to `All`, you can also use the search bar on the right.

![MicrosoftTeams-image](https://user-images.githubusercontent.com/9044372/180569028-f3d86bdf-7016-4f37-8c3f-49332b0c7ef3.png)

### Android

In your app level (**app folder**) `build.gradle`, add the following lines to the dependencies and android sections.

```groovy
android {
    ...
    packagingOptions {
        pickFirst  'META-INF/*'
    }
    ...
}
```

```groovy
dependencies {
    ...
    implementation 'com.azure.android:azure-communication-ui-calling:1.2.0'
    ...
}
```

In your project gradle scripts add following lines to `repositories`. For `Android Studio (2020.*)` the `repositories` are in `settings.gradle` `dependencyResolutionManagement(Gradle version 6.8 or greater)`. If you are using old versions of `Android Studio (4.*)` then the `repositories` will be in project level `build.gradle` `allprojects{}`.

```groovy
repositories {
    ...
    mavenCentral()
    maven {
        url "https://pkgs.dev.azure.com/MicrosoftDeviceSDK/DuoSDK-Public/_packaging/Duo-SDK-Feed/maven/v1"
    }
    ...
}
```

Sync project with gradle files. Either run `./gradlew build` or open the project in Android Studio (Android Studio -> File -> Sync Project With Gradle Files)

## Launching Composite

The React native library supports all the same features as the native [UI composite](https://github.com/Azure/communication-ui-library-android). Call `startCallComposite` on the `RNAzureCommunicationUICalling` module from your React Native Javascript code, wrapping with `try-catch` statement to handle any errors.

```cs
try {
    await RNAzureCommunicationUICalling.startCallComposite(
       // local options
       {"displayName": displayName, "title": title, "subtitle": subtitle},
       localAvatarImageResource,
       // remote options
       {"token": tokenInput, "meeting": meetingInput},
       remoteAvatarImageResource,
       // localization options
       {"locale": selectedLanguage, "layout": isRightToLeft} 
     );
   } catch (e) {
     console.log(`startCallComposite error: ${e.message}`)
   }
};
```

### Setup group call or Teams meeting options

Depending on what type of Call/Meeting you would like to setup, use the appropriate meeting input. Replace `meetingInput` with either your group call ID or Teams meeting url.

## React native - native app bridging

In order to support the communication between React Native and native Azure Communication UI library, bridging is needed for both iOS and Android. Please refer to the following bridging file guide for iOS and Android.

[iOS bridging file guide](demo/ios/README.md)

[Android bridging file guide](demo/android/README.md)

## Data Collection

The software may collect information about you and your use of the software and send it to Microsoft. Microsoft may use this information to provide services and improve our products and services. This sample collect information about users and their use of the software that cannot be opted out of. Do not use this sample if you wish to avoid telemetry. You can learn more about data collection and use in the help documentation and Microsoft’s [privacy statement](https://go.microsoft.com/fwlink/?LinkID=824704). For more information on the data collected by the Azure SDK, please visit the [Telemetry Policy](https://learn.microsoft.com/azure/communication-services/concepts/privacy) page.
