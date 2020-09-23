package com.challenge.fidoreader.bthid

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.os.ParcelUuid
import android.text.TextUtils

private val HOGP_UUID = ParcelUuid.fromString("00001812-0000-1000-8000-00805f9b34fb")
private val HID_UUID = ParcelUuid.fromString("00001124-0000-1000-8000-00805f9b34fb")

val BluetoothDevice.identifier: String
    get() = if (TextUtils.isEmpty(name)) address else name

val BluetoothDevice.canUseAuthenticator: Boolean
    get() {
        // If a device reports itself as a HID Device, then it isn't a HID Host.
        val uuidArray = uuids
        if (uuidArray != null) {
            for (uuid in uuidArray) {
                if (HID_UUID == uuid || HOGP_UUID == uuid) {
                    return false
                }
            }
        }
        return bluetoothClass.majorDeviceClass in setOf(
                BluetoothClass.Device.Major.COMPUTER,
                BluetoothClass.Device.Major.MISC,
                BluetoothClass.Device.Major.PHONE,
                BluetoothClass.Device.Major.UNCATEGORIZED
        )
    }

val BluetoothDevice.canUseAuthenticatorViaBluetooth: Boolean
    get() {
        return canUseAuthenticator && bluetoothClass.majorDeviceClass != BluetoothClass.Device.Major.PHONE
    }

val defaultAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

val isBluetoothEnabled
    get() = defaultAdapter.isEnabled

val hasCompatibleBondedDevice
    get() = defaultAdapter.bondedDevices.any { device -> device.canUseAuthenticatorViaBluetooth }
