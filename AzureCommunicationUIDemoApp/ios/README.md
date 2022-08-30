# iOS Bridging
Create a new Swift File `RNAzureCommunicationUICalling` in the `AzureCommunicationUIDemoApp` folder. 
See [RNAzureCommunicationUICalling.swift](./RNAzureCommunicationUICalling.swift)

You’ll be prompted for configuring an Objective-C bridging header. Create one and include the following import: 

![e1dcf14a-d7b4-415c-a5a5-92db0c20a1b8](https://user-images.githubusercontent.com/9044372/180570930-215c52a3-beea-4311-bb55-2d04b9ababe2.jpg)

See [RNAzureCommunicationUICalling.m](./RNAzureCommunicationUICalling.m)

# Running the Code
Directly from within Xcode, you can build and run your app on iOS simulator by selecting Product > Run or by using the (⌘-R) keyboard shortcut. 
Tap Start Experience. 

Accept audio permissions and select device, mic, and video settings. 

Tap Start Call. 
Alternatively, you could also run the React Native application through CLI. First, you need to start Metro, the JavaScript bundler that ships with React Native. 

To start the Metro Bundler, go to the root folder of the React Native project (root folder) and run the following: 

```ruby
npx react-native start 
```

Let Metro Bundler run in its own terminal. Open a new terminal in the same directory and run the following: 
```ruby
npx react-native run-ios --simulator="iPhone SE (2nd generation)" 
```
or simply 

```ruby
npx react-native run-ios 
```

# Limitations 

Communication between JavaScript and Swift/Kotlin is limited by the available methods (Callbacks/Promises/Events) provided by the React Native framework. Implementing TokenRefresher in JavaScript is not possible due to this limitation. 
Only one of the callback events (resolver / rejector) can be called. If one is called the other one will not be called.  

The React Native team is currently working on the re-architecture of the Native Module system, replacing it with TurboModules. It helps facilitate more efficient type-safe communication between JavaScript and native without relying on the React Native bridge. We could revisit our bridging module and update our wrapper API in the future once they have released TurboModules. More info in the highlighted section here: [iOS Native Modules · React Native](https://reactnative.dev/docs/native-modules-ios)

 
