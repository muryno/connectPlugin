
import 'dart:async';
import 'dart:ffi';

import 'package:flutter/services.dart';

class Connect {
  static const MethodChannel _channel =
      const MethodChannel('connect');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }


  static Future<String?>  ConnectFromWc(String wcUrl) async {
    try {
      final String? version = await _channel.invokeMethod("connectFromWc", wcUrl);
      return version;

    } on PlatformException catch (e) {
      print(e.toString());
    }
  }

  static Future<String?>  generateWCConnect(String bridgeURL) async {
    try {
      final String? version = await _channel.invokeMethod("connectToWcWithDeepLink", bridgeURL);
      return version;

    } on PlatformException catch (e) {
      print(e.toString());
    }
  }

  static Future<Void?>  diconnectConnection() async {
    final String? version = await _channel.invokeMethod('disconnectFromWc');

  }


  static Future<Void?>  Transfer(String wallectAddress) async {
    final String? version = await _channel.invokeMethod('mainTransfer',wallectAddress);

  }



}
