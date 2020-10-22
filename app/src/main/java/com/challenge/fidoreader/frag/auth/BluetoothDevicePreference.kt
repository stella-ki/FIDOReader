package com.challenge.fidoreader.frag.auth

import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.preference.Preference
import com.challenge.fidoreader.R
import com.challenge.fidoreader.bthid.canUseAuthenticatorViaBluetooth
import com.challenge.fidoreader.bthid.identifier

open class BluetoothDevicePreference(context: Context, protected val device: BluetoothDevice) : Preference(context) {

    init {
        key = device.address
        isPersistent = false

        updateName()
        updateClass()
    }

    fun updateName() {
        title = device.identifier
        notifyChanged()
    }

    fun updateClass() {
        setIcon(
            when (device.bluetoothClass?.majorDeviceClass) {
                BluetoothClass.Device.Major.AUDIO_VIDEO -> R.drawable.ic_btn_headset
                BluetoothClass.Device.Major.COMPUTER -> R.drawable.ic_btn_computer
                BluetoothClass.Device.Major.PHONE -> R.drawable.ic_btn_phone
                BluetoothClass.Device.Major.WEARABLE -> R.drawable.ic_btn_watch
                else -> R.drawable.ic_btn_bluetooth
            }
        )
        updateCompatibility()
        notifyChanged()
    }

    private fun updateCompatibility() {
        isEnabled = device.canUseAuthenticatorViaBluetooth
        if (isEnabled) {
            order = context.resources.getInteger(R.integer.order_device_list_compatible_device)
            summary = null
        } else {
            order = context.resources.getInteger(R.integer.order_device_list_incompatible_device)
            setSummary(R.string.status_bluetooth_use_via_nfc_instead)
        }
        notifyChanged()
        notifyHierarchyChanged()
    }
}
