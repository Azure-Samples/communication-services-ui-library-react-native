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
    TextInput,
} from "react-native"; 

const InputField = (props) => {
    return (
        <SafeAreaView>
            <Text style={styles.inputTitle}>
                    {props.inputTitle}
                </Text>
                <TextInput
                    style={styles.textInput}
                    value={props.value}
                    onChangeText={props.stateFunction}
                    placeholderTextColor={"#6E6E6E"}
                    placeholder={props.placeholderText}
                />
        </SafeAreaView>
    );
}


const styles = StyleSheet.create({
    textInput: {
      height: 48,
      backgroundColor: "white",
      color: "#212121",
      fontSize: 17,
      paddingStart: 16,
      paddingEnd: 16,
      paddingTop: 13,
      paddingBottom: 13,
    }, 
    inputTitle: {
      paddingStart: 16,
      paddingEnd: 16,
      paddingBottom: 8,
      paddingTop: 24,
      fontSize: 13,
      color: "#6E6E6E"
    },
  });

  export default InputField;