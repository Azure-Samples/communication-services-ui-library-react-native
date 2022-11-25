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

const AppBar = () => {
    return (
        <SafeAreaView >
            <Text style={styles.title}>
                RN ACS Chat UI Library Demo App
            </Text>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    title: {
      color: "#6E6E6E",
      fontWeight: 'bold',
      textAlign: 'center',
      padding: 10,
      marginTop: 10,
      fontSize: 17
    },
});

export default AppBar;



