package com.challenge.fidoreader.frag.auth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.Context
import com.challenge.fidoreader.R
import com.challenge.fidoreader.bthid.HidDataSender
import com.challenge.fidoreader.bthid.HidDeviceProfile
import com.challenge.fidoreader.bthid.canUseAuthenticatorViaBluetooth

class AuthenticatorHostDeviceEntry(context: Context, device: BluetoothDevice, private val hidDeviceProfile: HidDeviceProfile) : BluetoothDevicePreference(context, device) {

    init {
        updateProfileConnectionState()
    }

    fun updateProfileConnectionState() {
        if (!device.canUseAuthenticatorViaBluetooth || !HidDataSender.isAppRegistered) {
            isEnabled = false
            summary = null
            notifyChanged()
            return
        }
        when (hidDeviceProfile.getConnectionState(device)) {
            BluetoothProfile.STATE_DISCONNECTED -> {
                isEnabled = true
                summary = null
            }
            BluetoothProfile.STATE_CONNECTING -> {
                isEnabled = false
                setSummary(R.string.status_bluetooth_connecting)
            }
            BluetoothProfile.STATE_CONNECTED -> {
                isEnabled = true
                setSummary(R.string.status_bluetooth_connected)
            }
            BluetoothProfile.STATE_DISCONNECTING -> {
                isEnabled = false
                summary = null
            }
        }
        notifyChanged()
    }

    override fun onClick() {
        HidDataSender.requestConnect(device)
    }
}

