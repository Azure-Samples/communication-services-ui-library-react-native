//
//  Copyright (c) Microsoft Corporation. All rights reserved.
//  Licensed under the MIT License.
//

import React from 'react';
import { View, StyleSheet, TouchableOpacity, Image } from 'react-native';

export function AvatarsView(props) {
  const selectedAvatarStyle = avatar => {
    if (props.setAvatar === avatar) {
      return styles.avatarSelected;
    } else {
      return styles.settingsAvatar;
    }
  };

  return (
    <View style={styles.hStack}>
      <TouchableOpacity onPress={() => props.onAvatarSet('cat')}>
        <Image
          style={selectedAvatarStyle('cat')}
          source={require('./images/cat.png')}
        />
      </TouchableOpacity>
      <TouchableOpacity onPress={() => props.onAvatarSet('fox')}>
        <Image
          style={selectedAvatarStyle('fox')}
          source={require('./images/fox.png')}
        />
      </TouchableOpacity>
      <TouchableOpacity onPress={() => props.onAvatarSet('koala')}>
        <Image
          style={selectedAvatarStyle('koala')}
          source={require('./images/koala.png')}
        />
      </TouchableOpacity>
      <TouchableOpacity onPress={() => props.onAvatarSet('monkey')}>
        <Image
          style={selectedAvatarStyle('monkey')}
          source={require('./images/monkey.png')}
        />
      </TouchableOpacity>
      <TouchableOpacity onPress={() => props.onAvatarSet('mouse')}>
        <Image
          style={selectedAvatarStyle('mouse')}
          source={require('./images/mouse.png')}
        />
      </TouchableOpacity>
      <TouchableOpacity onPress={() => props.onAvatarSet('octopus')}>
        <Image
          style={selectedAvatarStyle('octopus')}
          source={require('./images/octopus.png')}
        />
      </TouchableOpacity>
    </View>
  )
}

const styles = StyleSheet.create({
  avatar: {
    height: 40,
    width: 40,
    margin: 12,
  },
  avatarSelected: {
    height: 40,
    width: 40,
    margin: 2,
    borderColor: '#000',
    borderWidth: 1,
  },
  settingsAvatar: {
    height: 40,
    width: 40,
    margin: 2,
  },
  hStack: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
});