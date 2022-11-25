//
//  Copyright (c) Microsoft Corporation. All rights reserved.
//  Licensed under the MIT License.
//

import React, { useState } from "react";
import { 
    SafeAreaView,
    ScrollView,
    StyleSheet,
    Text,
    Pressable,
    NativeModules
} from "react-native";
import AppBar from "./components/AppBar";
import InputField from "./components/InputField";

const Launcher = () => {
    const [tokenInput, onChangeTokenInput] = useState('');
    const [displayName, onChangeDisplayName] = useState('');
    const [meetingInput, onChangeMeetingInput] = useState('');
    const [indentity, onChangeIdentityInput] = useState('');
    const [endpoint, onChangeEndpointInput] = useState('');

    const { RNAzureCommunicationUIChat } = NativeModules

    startCallComposite = () => {
        console.log('' + tokenInput);
        try {
          //RNAzureCommunicationUIChat.startChatComposite(tokenInput, indentity, meetingInput, endpoint, displayName)
          RNAzureCommunicationUIChat.startChatComposite(
            "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEwNiIsIng1dCI6Im9QMWFxQnlfR3hZU3pSaXhuQ25zdE5PU2p2cyIsInR5cCI6IkpXVCJ9.eyJza3lwZWlkIjoiYWNzOmI2YWFkYTFmLTBiMWQtNDdhYy04NjZmLTkxYWFlMDBhMWQwMV8wMDAwMDAxNS00YWE0LTJkM2YtY2ViMS1hNDNhMGQwMGM3ZTQiLCJzY3AiOjE3OTIsImNzaSI6IjE2NjkzMTIxODYiLCJleHAiOjE2NjkzOTg1ODYsImFjc1Njb3BlIjoiY2hhdCx2b2lwIiwicmVzb3VyY2VJZCI6ImI2YWFkYTFmLTBiMWQtNDdhYy04NjZmLTkxYWFlMDBhMWQwMSIsInJlc291cmNlTG9jYXRpb24iOiJ1bml0ZWRzdGF0ZXMiLCJpYXQiOjE2NjkzMTIxODZ9.FLJSoStl3Fe5PObWd3a9Z6dUbm9tiC-CTIR480FVFguPwDf_jiTI20Y33xUPtVPAqEopEqHm4zrVYyli6j9ivAG0Py_FW5IeJWOcZniGri-PQJW2WYWBFnVTJQqU_bk6tpXkQB6veLD4lI6wIboCrP_JGz-g5SkhJumB3-zs3b5hHgXWC-F7NGH5gJpN7DIybExsLVxzzdeFp8rSk6DOh5YdlK5sMdONRiBa_wluDE4PGE2xc7v2GEM_yqryfvlqpYMdf_3XZ8YvI1KEppc8NP4wxkckip2F8-sRBZDrmKZkio05CETeNSAITUO0P9bnnebxhoQBR9FwTrMTqMr28A",
            "8:acs:b6aada1f-0b1d-47ac-866f-91aae00a1d01_00000015-4aa4-2d3f-ceb1-a43a0d00c7e4",
            "19:meeting_OWUxNTdjNWItNTg2Yy00OGYzLTk4YTEtMmRjZjc1Mjk3OTJj@thread.v2",
            "https://acs-ui-dev.communication.azure.com/",
            "Mohtasim mohtasim"
          )
        } catch (error) {
          console.log('startCallComposite error: ' + error)
        }
    }
    return (
        <SafeAreaView style={{ flex: 1, backgroundColor: '#F8F8F8' }}>
            <ScrollView contentInsetAdjustmentBehavior="automatic">
                <AppBar />
                
                <InputField value={tokenInput}
                    stateFunction={onChangeTokenInput}
                    inputTitle="ACS Token"
                    placeholderText="Enter ACS Auth Token"/>
                
                <InputField value={indentity}
                    stateFunction={onChangeIdentityInput}
                    inputTitle="Identity"
                    placeholderText="Identity"/>
                
                <InputField value={displayName}
                    stateFunction={onChangeDisplayName}
                    inputTitle="Display Name"
                    placeholderText="Enter your display name"/>
                
                <InputField value={meetingInput}
                    stateFunction={onChangeMeetingInput}
                    inputTitle="Chat Thread ID/Teams Meeting URL"
                    placeholderText="Enter Chat Thread ID/Teams Meeting URL"/>
                
                <InputField value={endpoint}
                    stateFunction={onChangeEndpointInput}
                    inputTitle="Endpoint URL"
                    placeholderText="Your Communication Resource Endpoint URL"/>
                
                <Pressable
                    style={[styles.button, (!tokenInput || !meetingInput || !indentity || !endpoint) ? styles.buttonDisabled : styles.buttonOpen]}
                    disabled={(!tokenInput || !meetingInput || !indentity || !endpoint)}
                    onPress={startCallComposite}
                    backgroundColor={{}} >
                        <Text style={!tokenInput || !meetingInput ? styles.textCloseStyle : styles.textStyle}>Launch</Text>
                </Pressable>
            </ScrollView>
        </SafeAreaView>
    )
}

const styles = StyleSheet.create({
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
    }, 
    inputTitle: {
      paddingStart: 16,
      paddingEnd: 16,
      paddingBottom: 8,
      paddingTop: 24,
      fontSize: 13,
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
  });

  export default Launcher;