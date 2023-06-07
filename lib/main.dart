import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  static const platform = const MethodChannel('samples.flutter.dev/ble');

  MyApp() {
    platform.setMethodCallHandler(_handleMethod);
  }

  Future<void> _handleMethod(MethodCall call) async {
    switch (call.method) {
      case "receiveRSSI":
        int rssi = call.arguments;
        print("RSSI: $rssi");
        break;
    }
  }

  Future<void> _startScan() async {
    try {
      final String response = await platform.invokeMethod('startScan');
      print(response);
    } on PlatformException catch (e) {
      print("Failed to start scan: '${e.message}'.");
    }
  }

  Future<void> _stopScan() async {
    try {
      final String response = await platform.invokeMethod('stopScan');
      print(response);
    } on PlatformException catch (e) {
      print("Failed to stop scan: '${e.message}'.");
    }
  }

  Future<void> _startScanForDevice(String deviceAddress) async {
    try {
      await platform.invokeMethod('startScanForDevice',
          <String, dynamic>{'deviceAddress': deviceAddress});
      print('Scanning started for device $deviceAddress');
    } on PlatformException catch (e) {
      print("Failed to start scan: '${e.message}'.");
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Flutter BLE RSSI'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              ElevatedButton(
                onPressed: () => _startScanForDevice('DD:33:11:00:0B:95'),
                child: Text('Start Scan for Specific Device'),
              ),
              ElevatedButton(
                onPressed: _stopScan,
                child: Text('Stop Scan'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
