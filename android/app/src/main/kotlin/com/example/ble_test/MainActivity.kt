package com.example.ble_test

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.os.ParcelUuid
import android.content.Context
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanResult
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.util.ArrayList

class MainActivity: FlutterActivity() {
    private val CHANNEL = "samples.flutter.dev/ble"

    private val bluetoothAdapter: BluetoothAdapter by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private var mScanning: Boolean = false
    private var targetDeviceAddress: String? = null

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
            call, result ->
            when (call.method) {
                "startScan" -> {
                    startScanning()
                    result.success("Scanning started")
                }
                "stopScan" -> {
                    stopScanning()
                    result.success("Scanning stopped")
                }
                "startScanForDevice" -> {
                    targetDeviceAddress = call.argument<String>("deviceAddress")
                    startScanning()
                    result.success("Scanning started")
                }
                else -> {
                    result.notImplemented()
                }
            }
        }
    }

    private fun startScanning() {
        if (!mScanning) {
            val filters: MutableList<ScanFilter> = ArrayList()
            val scanSettings = ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build()

            bluetoothAdapter.bluetoothLeScanner.startScan(filters, scanSettings, leScanCallback)
            mScanning = true
        }
    }

    private fun stopScanning() {
        if (mScanning) {
            bluetoothAdapter.bluetoothLeScanner.stopScan(leScanCallback)
            mScanning = false
        }
    }

    private val leScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            if (result.device.address == targetDeviceAddress) {
                val rssi = result.rssi
                MethodChannel(flutterEngine!!.dartExecutor.binaryMessenger, CHANNEL).invokeMethod("receiveRSSI", rssi)
            }
        }
    }
}