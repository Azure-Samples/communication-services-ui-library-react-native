# Android Bridging
 Create a new Java file `RNAzureCommunicationUICalling` in the `AzureCommunicationUIDemoApp` folder. This file will contain all of the methods that will call the native Android UI Library.
 See [RNAzureCommunicationUICalling.java](app/src/main/java/com/azurecommunicationuidemoapp/RNAzureCommunicationUICalling.java)
 

To allow the React Native javascript end to access a funciton, wrap it with `@ReactMethod`. This will allow the javascript end to call the function which will be used to launch the native UI Library.


 
 ```java
@ReactMethod
public void startCallComposite(String displayName, String tokenInput, String meetingInput ...) {

  CallComposite callComposite = new CallCompositeBuilder().build();

 ...
  
  //lauching native calling composite
  callComposite.launch(context, remoteOptions);
}
 ```
 

After creating `RNAzureCommunicationUICalling`, create another Java file called `RNAzureCommunicationUICallingPackage` in the `AzureCommunicationUIDemoApp` folder. 
 See [RNAzureCommunicationUICallingPackage.java](app/src/main/java/com/azurecommunicationuidemoapp/RNAzureCommunicationUICallingPackage.java)
 
`RNAzureCommunicationUICallingPackage` implements `ReactPackage` which provides us with an interface to register our `RNAzureCommunicationUICalling` module in React Native. This will allow us to reference the module from the javascript end.
 
  ```java
@Override
public List <NativeModule> createNativeModules(
ReactApplicationContext reactContext) {
  List <NativeModule> modules = new ArrayList < >();

  modules.add(new RNAzureCommunicationUICalling(reactContext));

  return modules;
}
  ```

 # Running the Code
 You can either run your app on an Android emulator with opening the project in Android Studio or run the app through CLI.
 
 

# Environment Variables
The React Native tools require some environment variables to be set up in order to build apps with native code.

### Mac

Add the following lines to your $HOME/.bash_profile or $HOME/.bashrc (if you are using zsh then ~/.zprofile or ~/.zshrc) config file:
- export ANDROID_SDK_ROOT=$HOME/Library/Android/sdk
- export PATH=$PATH:$ANDROID_SDK_ROOT/emulator
- export PATH=$PATH:$ANDROID_SDK_ROOT/platform-tools

### Windows
- Open the Windows Control Panel.
- Click on User Accounts, then click User Accounts again
- Click on Change my environment variables

Click on New... to create a new ANDROID_HOME user variable that points to the path to your Android SDK.

#### Example
```groovy
%LOCALAPPDATA%\Android\Sdk

```

Click on New... to create a new JAVE_HOME user variable that points to the path to your JDK:

#### Example
```groovy
org.gradle.java.home=C:\\Program Files\\OpenJDK\\openjdk-11.0.16_8

```


 
 ## Android Studio
 You can build and run your app on an Android emulator by creating a new Android Virtual Device (AVD) > Click Run.
 
 For more information: [Run Your App](https://developer.android.com/training/basics/firstapp/running-app)
 
 Tap Start Experience. 

 Accept audio permissions and select device, mic, and video settings. 

 Tap Start Call. 
 Alternatively, you could also run the React Native application through CLI. First, you need to start Metro, the JavaScript bundler that ships with React Native. 

 To start the Metro Bundler, go to the root folder of the React Native project (root folder) and run the following: 

 ```ruby
 npx react-native start 
 ```
 
 Start your Android Virtual Device (AVD) or connect your own. Check if the device is running: 
 ```ruby
 adb devices
 ```

 Let Metro Bundler run in its own terminal. Open a new terminal in the same directory and run the following: 
 ```ruby
 npx react-native run-android
 ```


# UI Library functionality


## Launching Composite
The React native library supports all the same features as the native [UI composite](https://github.com/Azure/communication-ui-library-android). Call `startCallComposite` on the `RNAzureCommunicationUICalling` module from your React Native Javascript code, wrapping with `try-catch` statement to handle any errors.

```cs
try {
    await RNAzureCommunicationUICalling.startCallComposite(
        displayName,
        tokenInput,
        meetingInput,
        localAvatarImageResource,
        selectedLanguage,
        isRightToLeft,
        remoteAvatarImageResource
    );
} catch (e) {
   console.log(`startCallComposite error: ${e.message}`)
} };
```

### Setup group call or Teams meeting options
Depending on what type of Call/Meeting you would like to setup, use the appropriate meeting input. Replace `meetingInput` with either your group call ID or Teams meeting url.

### Apply theme configuration

To change the primary color of composite, create a new theme style in `src/main/res/values/styles.xml` and `src/main/res/values-night/styles.xml` by considering AzureCommunicationUICalling.Theme as parent theme. To apply theme, inject the theme ID in CallCompositeBuilder. 
  
  
```xml
<style name="MyCompany.CallComposite" parent="AzureCommunicationUICalling.Theme">
    <item name="azure_communication_ui_calling_primary_color">#27AC22</item>
    <item name="azure_communication_ui_calling_primary_color_tint10">#5EC65A</item>
    <item name="azure_communication_ui_calling_primary_color_tint20">#A7E3A5</item>
    <item name="azure_communication_ui_calling_primary_color_tint30">#CEF0CD</item>
</style>
```
To apply theme, inject the theme ID in CallCompositeBuilder. 
```java
CallComposite callComposite = 
    new CallCompositeBuilder()
        .theme(R.style.MyCompany_CallComposite)
        .build();
```


 # Limitations 

 Communication between JavaScript and Swift/Kotlin is limited by the available methods (Callbacks/Promises/Events) provided by the React Native framework. Implementing TokenRefresher in JavaScript is not possible due to this limitation. 
 Only one of the callback events (resolver / rejector) can be called. If one is called the other one will not be called.  

 The React Native team is currently working on the re-architecture of the Native Module system, replacing it with TurboModules. It helps facilitate more efficient type-safe communication between JavaScript and native without relying on the React Native bridge. We could revisit our bridging module and update our wrapper API in the future once they have released TurboModules. More info in the highlighted section here: [Android Native Modules · React Native](https://reactnative.dev/docs/new-architecture-app-modules-android)
