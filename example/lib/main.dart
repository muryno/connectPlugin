import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:connect/connect.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion =
          await Connect.platformVersion ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

/// pass qrcode wc url
  Future<void> connectFromScannedWc(String wc) async {
    String result;
    try {
      result =
          await Connect.ConnectFromWc(wc) ?? 'Unknown  ';
    } on PlatformException {
      result = 'Failed to get platform version.';
    }
    if (!mounted) return;

   print(result);
  }

  /// pass qrcode wc url
  Future<void> passBridgeUrlToCreateWCUrl(String wc) async {
    String result;
    try {
      result =
          await Connect.generateWCConnect(wc) ?? 'Unknown  ';
    } on PlatformException {
      result = 'Failed to get platform version.';
    }
    if (!mounted) return;

    print(result);
  }

  //discoonect
 disconnectConnection() async {
    String result;
    try {
           Connect.diconnectConnection() ;
    } on PlatformException {
      result = 'Failed to get platform version.';
    }
    if (!mounted) return;
  }


  passWallectAddress(String wd) async {
    String result;
    try {
           Connect.Transfer(wd);
    } on PlatformException {
      result = 'Failed to get platform version.';
    }
    if (!mounted) return;


  }

  ///passWallet Address


  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('Running on: $_platformVersion\n'),
        ),
      ),
    );
  }
}
