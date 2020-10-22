package com.challenge.fidoreader.frag.auth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.challenge.fidoreader.AuthenticatorAttachedActivity
import com.challenge.fidoreader.R
import com.challenge.fidoreader.bthid.HidDataSender
import com.challenge.fidoreader.bthid.HidDeviceProfile
import com.challenge.fidoreader.bthid.canUseAuthenticator
import com.challenge.fidoreader.bthid.hasCompatibleBondedDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

private const val TAG = "AuthPreferenceFragement"
class AuthPreferenceFragement : PreferenceFragmentCompat(), CoroutineScope {
    private lateinit var bluetoothSettingsPreference: Preference
    private lateinit var discoverableSwitchPreference: SwitchPreference
    private lateinit var manageCredentialsPreference: Preference

    private val REQUEST_CODE_ENABLE_BLUETOOTH = 1
    private val REQUEST_CODE_MAKE_DISCOVERABLE = 2

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + SupervisorJob()

    private val bondedDeviceEntries = mutableSetOf<AuthenticatorHostDeviceEntry>()

    private lateinit var hidDeviceProfile: HidDeviceProfile

    private val hidProfileListener = object : HidDataSender.ProfileListener {
        override fun onAppStatusChanged(registered: Boolean) {
            Log.i(TAG, "onAppStatusChanged($registered)")
            if (!registered)
                activity?.finish()
            for (entry in bondedDeviceEntries) {
                entry.updateProfileConnectionState()
            }
        }

        override fun onConnectionStateChanged(device: BluetoothDevice, state: Int) {
            Log.i(TAG, "onDeviceStateChanged(${device.name}, $state)")
            findEntryForDevice(device)?.updateProfileConnectionState()
            when (state) {
                BluetoothProfile.STATE_CONNECTED -> {
                    startActivityForResult(
                            Intent(
                                    this@AuthPreferenceFragement.context,
                                    AuthenticatorAttachedActivity::class.java
                            ), 1
                    )
                }
            }
        }

        override fun onServiceStateChanged(proxy: BluetoothProfile?) {
            Log.i(TAG, "onServiceStateChanged($proxy)")
            for (entry in bondedDeviceEntries) {
                entry.updateProfileConnectionState()
            }
        }
    }

    private val bluetoothBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.v(TAG, "bluetoothBroadcastReceiver")

            if (context == null || intent == null) {
                Log.w(TAG, "bluetoothBroadcastReceiver received null context or intent")
                return
            }
            if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                updateBluetoothStateAndDeviceEntries()
                return
            }
            val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE) ?: return
            Log.v(TAG, "bluetoothBroadcastReceiver - intent.action : " + intent.action)
            when (intent.action) {
                BluetoothAdapter.ACTION_SCAN_MODE_CHANGED -> {
                    val scanMode = intent.getIntExtra(
                            BluetoothAdapter.EXTRA_SCAN_MODE,
                            BluetoothAdapter.SCAN_MODE_NONE
                    )
                    updateDiscoverableState(scanMode)
                }
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    removeEntryForDevice(device)
                    if (device.bondState == BluetoothDevice.BOND_BONDED) {
                        addEntryForDevice(device)
                    }
                }
                BluetoothDevice.ACTION_CLASS_CHANGED -> {
                    findEntryForDevice(device)?.updateClass()
                }
                BluetoothDevice.ACTION_NAME_CHANGED -> {
                    findEntryForDevice(device)?.updateName()
                }
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_auth_main, rootKey)
        bluetoothSettingsPreference = findPreference(getText(R.string.preference_bluetooth_settings_key))!!
        discoverableSwitchPreference = findPreference(getText(R.string.preference_discoverable_key))!!
        manageCredentialsPreference = findPreference(getText(R.string.preference_credential_management_key))!!
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hidDeviceProfile = HidDataSender.register(context, hidProfileListener, null)
    }

    override fun onResume() {
        super.onResume()
        context?.registerReceiver(bluetoothBroadcastReceiver,
        IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
            addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_CLASS_CHANGED)
            addAction(BluetoothDevice.ACTION_NAME_CHANGED)
        })
        updateBluetoothStateAndDeviceEntries()
        updateUserVerificationPreferencesState()
        updateDiscoverableState(BluetoothAdapter.getDefaultAdapter().scanMode)
    }

    override fun onPause() {
        super.onPause()
        context?.unregisterReceiver(bluetoothBroadcastReceiver)
    }

    override fun onDetach() {
        super.onDetach()
        HidDataSender.unregister(hidProfileListener, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_CODE_ENABLE_BLUETOOTH -> {
                if(resultCode != Activity.RESULT_OK){
                    discoverableSwitchPreference.isChecked = false;
                    discoverableSwitchPreference.isEnabled = false;
                }
            }
        }
    }

    private fun addEntryForDevice(device: BluetoothDevice) {
        Log.d(TAG, "addEntryForDevice - Device name : " + device.name + " , Device type : " + device.bluetoothClass + ",  Device  status : " + device.canUseAuthenticator)
        if (!device.canUseAuthenticator)
            return
        AuthenticatorHostDeviceEntry(
                activity!!,
                device,
                hidDeviceProfile
        ).let { entry ->
            bondedDeviceEntries.add(entry)
            preferenceScreen.addPreference(entry)
        }
    }

    private fun findEntryForDevice(device: BluetoothDevice): AuthenticatorHostDeviceEntry? {
        return findPreference<AuthenticatorHostDeviceEntry>(device.address)
    }

    private fun removeEntry(entry: AuthenticatorHostDeviceEntry) {
        preferenceScreen.removePreference(entry)
        bondedDeviceEntries.remove(entry)
    }

    private fun removeEntryForDevice(device: BluetoothDevice) {
        findEntryForDevice(device)?.let { entry -> removeEntry(entry) }
    }

    private fun createBondedDeviceEntries() {
        for (device in BluetoothAdapter.getDefaultAdapter().bondedDevices) {
            addEntryForDevice(device)
        }
    }

    private fun clearBondedDeviceEntries() {
        for (entry in bondedDeviceEntries) {
            preferenceScreen.removePreference(entry)
        }
        bondedDeviceEntries.clear()
    }

    private fun updateBluetoothStateAndDeviceEntries() {
        clearBondedDeviceEntries()
        Log.v(TAG, "updateBluetoothStateAndDeviceEntries")
        bluetoothSettingsPreference.apply {
            Log.v(TAG, "bluetoothSettingsPreference - BluetoothAdapter.state : " + BluetoothAdapter.getDefaultAdapter().state)
            when (BluetoothAdapter.getDefaultAdapter().state) {
                BluetoothAdapter.STATE_ON -> {
                    if (hasCompatibleBondedDevice) {
                        summary = null
                    } else {
                        setSummary(R.string.status_bluetooth_tap_and_pair)
                    }
                    onPreferenceClickListener = null
                    createBondedDeviceEntries()
                    discoverableSwitchPreference.isEnabled = true
                }
                BluetoothAdapter.STATE_OFF, BluetoothAdapter.STATE_TURNING_OFF -> {
                    setSummary(R.string.status_bluetooth_tap_to_enable)
                    setOnPreferenceClickListener {
                        startActivityForResult(
                                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                                REQUEST_CODE_ENABLE_BLUETOOTH
                        )
                        true
                    }
                    discoverableSwitchPreference.isEnabled = false
                }
                BluetoothAdapter.STATE_TURNING_ON -> {
                    summary = null
                    onPreferenceClickListener = null
                    discoverableSwitchPreference.isEnabled = false
                }
            }
        }
        discoverableSwitchPreference.apply {
            Log.v(TAG, "discoverableSwitchPreference - BluetoothAdapter.state : " + BluetoothAdapter.getDefaultAdapter().state)
            when (BluetoothAdapter.getDefaultAdapter().state) {
                BluetoothAdapter.STATE_ON -> {
                    updateDiscoverableState(BluetoothAdapter.getDefaultAdapter().scanMode)
                    setOnPreferenceClickListener {
                        startActivityForResult(
                                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                                    putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60)
                                },
                                REQUEST_CODE_MAKE_DISCOVERABLE
                        )
                        it.isEnabled = false
                        true
                    }
                }
                else -> {
                    isEnabled = false
                    isChecked = false
                    onPreferenceClickListener = null
                }
            }
        }
    }

    private fun updateDiscoverableState(scanMode: Int) {
        Log.v(TAG, "updateDiscoverableState - scanMode : $scanMode")

        if (scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            discoverableSwitchPreference.isEnabled = false
            discoverableSwitchPreference.isChecked = true
        } else {
            discoverableSwitchPreference.isEnabled = true
            discoverableSwitchPreference.isChecked = false
        }
    }

    private fun updateUserVerificationPreferencesState() {
        //val userVerificationState = getUserVerificationState(context)
        val userVerificationState = true
        Log.v(TAG, "updateUserVerificationPreferencesState - userVerificationState : $userVerificationState")

        manageCredentialsPreference.apply {
            if (userVerificationState != false) {
                isEnabled = true
                setIcon(R.drawable.ic_btn_key)
                summary = null
            } else {
                isEnabled = false
                icon = null
                summary = getString(R.string.preference_manage_credentials_summary_disabled)
            }
            /*setOnPreferenceClickListener {
                val intent =
                        Intent(
                                context,
                                ConfirmDeviceCredentialActivity::class.java
                        ).apply {
                            putExtra(
                                    EXTRA_CONFIRM_DEVICE_CREDENTIAL_RECEIVER,
                                    object : ResultReceiver(Handler()) {
                                        override fun onReceiveResult(
                                                resultCode: Int,
                                                resultData: Bundle?
                                        ) {
                                            if (resultCode == Activity.RESULT_OK)
                                                context.startActivity(
                                                        Intent(
                                                                context,
                                                                ResidentCredentialsList::class.java
                                                        )
                                                )
                                        }
                                    })
                        }
                context.startActivity(intent)
                true
            }*/
        }
    }



}