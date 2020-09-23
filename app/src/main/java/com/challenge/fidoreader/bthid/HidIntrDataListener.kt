package com.challenge.fidoreader.bthid

import android.bluetooth.BluetoothDevice

interface HidIntrDataListener {
    fun onIntrData(device: BluetoothDevice, reportId: Byte, data: ByteArray, host: InputHostWrapper)
}