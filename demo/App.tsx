//
//  Copyright (c) Microsoft Corporation. All rights reserved.
//  Licensed under the MIT License.
//

import React, { useState } from 'react';

import {
  Alert,
  SafeAreaView,
  ScrollView,
  StyleSheet,
  Button,
  Text,
  Image,
  TextInput,
  View,
  Switch,
  Modal,
  Pressable,
  Platform,
  PlatformColor
} from 'react-native';
import { AvatarsView } from './AvatarsView';
import RNAzureCommunicationUICalling from './native/RNAzureCommunicationUICalling';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import RNPickerSelect from 'react-native-picker-select';

const Stack = createNativeStackNavigator();

const App = () => {

  function HomeScreen({ navigation }) {
    const [tokenInput, onChangeTokenInput] = useState('');
    const [displayName, onChangeDisplayName] = useState('');
    const [title, onChangeTitle] = useState('');
    const [subtitle, onChangeSubtitle] = useState('');
    const [meetingInput, onChangeMeetingInput] = useState('');
    const [isRightToLeft, onChangeIsRightToLeft] = useState(false);
    const [disableLeaveCallConfirmation, onChangeDisableLeaveCallConfirmation] = useState(false);
    const [localAvatar, onLocalAvatarSet] = useState('');
    const [remoteAvatar, onRemoteAvatarSet] = useState('');
    const [modalVisible, setModalVisible] = useState(false);
    const [selectedLanguage, setSelectedLanguage] = useState('en');
    const [isGroupCall, setIsGroupCall] = useState(true);
    const [isTeamsCall, setIsTeamsCall] = useState(false);
    const [isOneToNCall, setIsOneToNCall] = useState(false);
    const [isRoomsCall, setIsRoomsCall] = useState(false);
    const [localesArray, setLocalesArray] = useState([]);
    const toggleIsRightToLeftSwitch = () => onChangeIsRightToLeft(!isRightToLeft);
    const disableLeaveCallConfirmationSwitch = () => onChangeDisableLeaveCallConfirmation(!disableLeaveCallConfirmation);

    React.useLayoutEffect(() => {
      navigation.setOptions({
        headerRight: () => (
          <Button onPress={() => setModalVisible(!modalVisible)} title="Settings" color="#0078D4" />
        ),
      });
    }, [navigation, setModalVisible]);

    const setLocalAvatar = avatar => {
      if (avatar !== localAvatar) {
        onLocalAvatarSet(avatar);
      } else {
        onLocalAvatarSet('');
      }
    };

    const setRemoteAvatar = avatar => {
      if (avatar !== remoteAvatar) {
        onRemoteAvatarSet(avatar);
      } else {
        onRemoteAvatarSet('');
      }
    };

    const resolveAssetSource = require('react-native/Libraries/Image/resolveAssetSource');

    const getsupportedLocales = async () => {
      try {
        const locals = await RNAzureCommunicationUICalling?.getSupportedLocales();
        setLocalesArray(locals)
      } catch (e) {
        Alert.alert('Error', e.message, [{ text: 'Dismiss' }]);
      }
    }

    getsupportedLocales()

    const resolveAvatarSource = (avatar) => {
      let source = '';
      if (avatar === 'cat') {
        source = require('./images/cat.png');
      } else if (avatar === 'fox') {
        source = require('./images/fox.png');
      } else if (avatar === 'koala') {
        source = require('./images/koala.png');
      } else if (avatar === 'monkey') {
        source = require('./images/monkey.png');
      } else if (avatar === 'mouse') {
        source = require('./images/mouse.png');
      } else if (avatar === 'octopus') {
        source = require('./images/octopus.png');
      }
      return resolveAssetSource(source)
    }

    const startCallComposite = async () => {
      // if (Platform.OS === 'ios') {
      //   const themeColor = PlatformColor('systemTeal'); // set null for default theme color
      //   RNAzureCommunicationUICalling.setThemeColor(themeColor);
      // }

      try {
        const localAvatarImageResource = resolveAvatarSource(localAvatar);
        const remoteAvatarImageResource = resolveAvatarSource(remoteAvatar);
        await RNAzureCommunicationUICalling.startCallComposite(
          // local options
          {"displayName": displayName, "title": title, "subtitle": subtitle, "disableLeaveCallConfirmation": disableLeaveCallConfirmation},
          localAvatarImageResource,
          // remote options
          {"token": tokenInput, "meeting": meetingInput},
          remoteAvatarImageResource,
          // localization options
          {"locale": selectedLanguage, "layout": isRightToLeft},
          {"setupOrientation": "PORTRAIT", "callOrientation": "PORTRAIT"} 
        );
      } catch (e) {
        Alert.alert('Error', e.message, [{ text: 'Dismiss' }]);
      }
    };

    const changeCallMeetingOption = () => {
      onChangeMeetingInput('')
      setIsGroupCall(true)
      setIsTeamsCall(false)
      setIsOneToNCall(false)
      setIsRoomsCall(false)
    };

    const changeCallTeamsMeetingOption = () => {
      onChangeMeetingInput('')
      setIsGroupCall(false)
      setIsTeamsCall(true)
      setIsOneToNCall(false)
      setIsRoomsCall(false)
    };

    const changeCallOneToNMeetingOption = () => {
      onChangeMeetingInput('')
      setIsGroupCall(false)
      setIsTeamsCall(false)
      setIsOneToNCall(true)
      setIsRoomsCall(false)
    };

    const changeCallRoomsCallOption = () => {
      onChangeMeetingInput('')
      setIsGroupCall(false)
      setIsTeamsCall(false)
      setIsOneToNCall(false)
      setIsRoomsCall(true)
    }

    return (
      <SafeAreaView style={{ flex: 1, backgroundColor: '#F8F8F8' }}>
        <ScrollView contentInsetAdjustmentBehavior="automatic" >
          <View style={{ flexDirection: "row", margin: 8, backgroundColor: "#FFFFFF" }}>
            <Pressable
              style={[styles.tabButton, isGroupCall ? styles.buttonOpen : styles.buttonDisabled]}
              disabled={isGroupCall}
              onPress={changeCallMeetingOption}
            >
              <Text style={!isGroupCall ? styles.textCloseStyle : styles.textStyle}>Group Call</Text>
            </Pressable>
            <Pressable
              style={[styles.tabButton, isTeamsCall ? styles.buttonOpen : styles.buttonDisabled]}
              disabled={isTeamsCall}
              onPress={changeCallTeamsMeetingOption}
            >
              <Text style={!isTeamsCall ? styles.textCloseStyle : styles.textStyle}>Teams Meeting</Text>
            </Pressable>
            <Pressable
              style={[styles.tabButton, isOneToNCall ? styles.buttonOpen : styles.buttonDisabled]}
              disabled={isOneToNCall}
              onPress={changeCallOneToNMeetingOption}
            >
              <Text style={!isOneToNCall ? styles.textCloseStyle : styles.textStyle}>One To N Call</Text>
            </Pressable>
            <Pressable
              style={[styles.tabButton, isRoomsCall ? styles.buttonOpen : styles.buttonDisabled]}
              disabled={isRoomsCall}
              onPress={changeCallRoomsCallOption}
            >
              <Text style={!isRoomsCall ? styles.textCloseStyle : styles.textStyle}>Rooms Call</Text>
            </Pressable>
          </View>

          {isGroupCall && (
            <View>
              <Text style={styles.inputTitle}>
                Group call ID
              </Text>
              <TextInput
                style={styles.textInput}
                onChangeText={onChangeMeetingInput}
                value={meetingInput}
                placeholderTextColor={"#6E6E6E"}
                placeholder="Enter call ID"
              />
              <Text style={styles.inputDescription}>
                Start a call to get a call ID.
              </Text>
            </View>
          )}
          {isTeamsCall && (
            <View>
              <Text style={styles.inputTitle}>
                Teams meeting
              </Text>
              <TextInput
                style={styles.textInput}
                onChangeText={onChangeMeetingInput}
                value={meetingInput}
                placeholderTextColor={"#6E6E6E"}
                placeholder="Enter invite link"
              />
              <Text style={styles.inputDescription}>
                Get link from the meeting invite or anyone in the call.
              </Text>
            </View>
          )}
          {isOneToNCall && (
            <View>
              <Text style={styles.inputTitle}>
                One-to-N call
              </Text>
              <TextInput
                style={styles.textInput}
                onChangeText={onChangeMeetingInput}
                value={meetingInput}
                placeholderTextColor={"#6E6E6E"}
                placeholder="Enter participants MRIs separated by comma"
              />
              <Text style={styles.inputDescription}>
                Outgoing Call.
              </Text>
            </View>
          )}
          {
            isRoomsCall && (
              <View>
                <Text style={styles.inputTitle}>
                  Rooms Call
                </Text>
                <TextInput
                  style={styles.textInput}
                  onChangeText={onChangeMeetingInput}
                  value={meetingInput}
                  placeholderTextColor={"#6E6E6E"}
                  placeholder="Enter Rooms ID"
                />
                <Text style={styles.inputDescription}>
                  Get Room ID created by admin to join rooms call.
                </Text>
              </View>
            )
          }

          <Text style={styles.inputTitle}>
            ACS Token
          </Text>
          <TextInput
            style={styles.textInput}
            onChangeText={onChangeTokenInput}
            value={tokenInput}
            placeholderTextColor={"#6E6E6E"}
            placeholder="Token Function URL or ACS Token"
          />

          <Text style={styles.inputDescription}>
            Identify token used for authentication.
          </Text>


          <Text style={styles.inputTitle}>
            Your display name
          </Text>
          <View style={styles.inputContainer}>
            <Image style={styles.icon} source={require('./images/user.png')} />

            <TextInput
              style={styles.textInput}
              onChangeText={onChangeDisplayName}
              value={displayName}
              placeholderTextColor={"#6E6E6E"}
              placeholder="Enter a name"
            />
          </View>


          <Text style={styles.inputDescription}>
            Name shown to the others on the call.
          </Text>

          <Pressable
            style={[styles.button, !tokenInput || !meetingInput ? styles.buttonDisabled : styles.buttonOpen]}
            disabled={!tokenInput || !meetingInput}
            onPress={startCallComposite}
          >
            <Text style={!tokenInput || !meetingInput ? styles.textCloseStyle : styles.textStyle}>Launch</Text>
          </Pressable>

        </ScrollView>

        <Modal
          animationType="slide"
          transparent={true}
          visible={modalVisible}
          onRequestClose={() => {
            setModalVisible(!modalVisible);
          }}
        >
          <ScrollView contentInsetAdjustmentBehavior="automatic">
            <View style={styles.modalView}>
              <View style={styles.closeContainerView}>
                <Pressable
                  style={styles.buttonClose}
                  onPress={() => setModalVisible(!modalVisible)}
                >
                  <Text style={styles.textCloseStyle}>X</Text>
                </Pressable>
              </View>
              <Text style={styles.modalTitleText}>UI Library - React Native Sample - Settings</Text>
              <View style={styles.settingsSectionContainerView}>
                <Text style={styles.settingsHeaderText}>Setup Screen View Data</Text>
                <TextInput
                  style={styles.textInput}
                  onChangeText={onChangeTitle}
                  value={title}
                  placeholderTextColor={"#6E6E6E"}
                  placeholder="Navigation Bar Title"
                />
                <TextInput
                  style={styles.textInput}
                  onChangeText={onChangeSubtitle}
                  value={subtitle}
                  placeholderTextColor={"#6E6E6E"}
                  placeholder="Navigation Bar Subtitle"
                />
              </View>
              <View style={styles.settingsSectionContainerView}>
                <Text style={styles.settingsHeaderText}>Localization</Text>
                <RNPickerSelect
                  items={
                    localesArray.map(item => {
                      return { label: item, value: item }
                    })
                  }
                  onValueChange={value => {
                    setSelectedLanguage(value)
                  }}
                  style={pickerSelectStyles}
                  value={selectedLanguage}
                  useNativeAndroidPickerStyle={false}
                />
                <View style={styles.settingsSwitchToggleContainer}>
                  <Text>Is Right to Left</Text>
                  <Switch
                    onValueChange={toggleIsRightToLeftSwitch.bind(this)}
                    value={isRightToLeft}
                  />
                </View>
              </View>

              <View style={styles.settingsSectionContainerView}>
                <Text style={styles.settingsHeaderText}>Local Participant View Data</Text>
                <AvatarsView setAvatar={localAvatar} onAvatarSet={setLocalAvatar} />
              </View>

              <View style={styles.settingsSectionContainerView}>
                <Text style={styles.settingsHeaderText}>Remote Participant View Data</Text>
                <AvatarsView setAvatar={remoteAvatar} onAvatarSet={setRemoteAvatar} />
              </View>

              <View style={styles.settingsSwitchToggleContainer}>
                  <Text>Disable leave call confirmation</Text>
                  <Switch
                    onValueChange={disableLeaveCallConfirmationSwitch.bind(this)}
                    value={isRightToLeft}
                  />
                </View>
            </View>
          </ScrollView>
        </Modal>
      </SafeAreaView>
    );
  }

  return (
    <NavigationContainer>
      <Stack.Navigator>
        <Stack.Screen
          name="Join"
          component={HomeScreen}
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

const styles = StyleSheet.create({
  title: {
    textAlign: 'center',
    padding: 10,
    marginTop: 10,
    fontSize: 17
  },
  textInput: {
    height: 48,
    backgroundColor: "white",
    color: "#212121",
    fontSize: 17,
    paddingStart: 16,
    paddingEnd: 16,
    paddingTop: 13,
    paddingBottom: 13,
  }, inputContainer: {
    flex: 1,
    backgroundColor: 'white',
    flexDirection: 'row',
    justifyContent: 'flex-start',
  },
  modalView: {
    margin: 10,
    backgroundColor: "white",
    borderRadius: 8,
    padding: 20,
    shadowColor: "#000",
    shadowOffset: {
      width: 0,
      height: 2
    },
    shadowOpacity: 0.25,
    shadowRadius: 4,
    elevation: 5,
  }, icon: {
    width: 16,
    marginStart: 20,
    alignSelf: 'center',
    height: 20,

  },
  tabButton: {
    flex: 1,
    borderRadius: 50,
    padding: 10,
    elevation: 2,
  },
  button: {
    marginTop: 64,
    borderRadius: 8,
    padding: 10,
    elevation: 2,
    margin: 10
  },
  buttonOpen: {
    backgroundColor: "#0078D4",
  },
  buttonDisabled: {
    backgroundColor: "#F1F1F1",
  },
  buttonClose: {
    width: 24,
    height: 24,
    padding: 4,
    borderRadius: 12,
    textAlign: "center"
  }, inputTitle: {
    paddingStart: 16,
    paddingEnd: 16,
    paddingBottom: 8,
    paddingTop: 24,
    fontSize: 13,
    color: "#6E6E6E"
  }, inputDescription: {
    paddingStart: 16,
    paddingEnd: 16,
    fontSize: 12,
    color: "#6E6E6E"
  },
  textStyle: {
    color: "white",
    fontWeight: "normal",
    textAlign: "center",
    fontSize: 15,
  },
  textCloseStyle: {
    color: "#6E6E6E",
    fontSize: 15,
    textAlign: "center"
  },
  modalTitleText: {
    marginVertical: 15,
    textAlign: "center",
    fontSize: 17,
  },
  settingsSectionContainerView: {
    backgroundColor: "#F8F8F8",
    flexDirection: "column",
    padding: 10,
    borderRadius: 4,
    marginVertical: 8,
  },
  settingsHeaderText: {
    color: "grey"
  },
  settingsSwitchToggleContainer: {
    flexDirection: 'row',
    height: 40,
    marginVertical: 10,
    padding: 10,
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  closeContainerView: {
    alignItems: "flex-end",
  },
});

const pickerSelectStyles = StyleSheet.create({
  inputIOS: {
    height: 40,
    fontSize: 16,
    paddingVertical: 12,
    paddingHorizontal: 10,
    marginVertical: 8,
    borderWidth: 1,
    borderColor: 'black',
    borderRadius: 4,
    color: 'black',
    paddingRight: 30, // to ensure the text is never behind the icon
  },
  inputAndroid: {
    height: 40,
    fontSize: 16,
    paddingHorizontal: 10,
    paddingVertical: 8,
    marginVertical: 8,
    borderWidth: 1,
    borderColor: 'black',
    borderRadius: 4,
    color: 'black',
    paddingRight: 30, // to ensure the text is never behind the icon
  },
});

export default App;