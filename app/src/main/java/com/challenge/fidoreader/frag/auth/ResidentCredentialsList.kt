package com.challenge.fidoreader.frag.auth

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.preference.DialogPreference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.challenge.fidoreader.R
import com.challenge.fidoreader.context.AuthenticatorContext


class ResidentCredentialsList : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.resident)
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.resident, ResidentCredentialsPreferenceFragment())
        fragmentTransaction.commit()
    }
}

@ExperimentalUnsignedTypes
class ResidentCredentialsPreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        createCredentialList()
    }

    private fun createCredentialList() {
        Log.i("ResidentCredentialsList", "createCredentialList")
        preferenceScreen = preferenceManager.createPreferenceScreen(context)
        val credentialsPerRp = context?.let { AuthenticatorContext.getAllResidentCredentials(it) }
        if (credentialsPerRp != null) {
            preferenceScreen.title =
                    if (credentialsPerRp.isEmpty()) {
                        Log.i("ResidentCredentialsList", "credentialsPerRp empty ")
                        getString(R.string.credential_management_title_no_credentials)
                    } else {
                        getString(R.string.credential_management_title)
                    }

            for ((rpId, credentials) in credentialsPerRp) {
                if (credentials.isEmpty()){
                    Log.i("ResidentCredentialsList", "credentials empty ")
                    continue
                }else{
                    Log.i("ResidentCredentialsList", "credentials not empty ")
                }
                val rpCategory = PreferenceCategory(context)
                // PreferenceCategory has to be added to the PreferenceScreen before customizing it
                // https://stackoverflow.com/a/49108303/297261
                preferenceScreen.addPreference(rpCategory)
                rpCategory.apply {
                    title = rpId
                    for ((index, credential) in credentials.withIndex()) {
                        Log.i("ResidentCredentialsList", "credential " + credential.rpName)
                        credential.unlockUserInfoIfNecessary()
                        val credentialTwoLineInfo = credential.getTwoLineInfo(index + 1)
                        val credentialFormattedInfo = credential.getFormattedInfo()
                        addPreference(object : DialogPreference(context) {
                            init {
                                isIconSpaceReserved = false
                                title = credentialTwoLineInfo.first
                                summary = credentialTwoLineInfo.second
                            }

                            override fun onClick() {
                                var builder: AlertDialog.Builder = AlertDialog.Builder(context);
                                val preference = this
                                builder.apply {
                                    setTitle(rpId)
                                    setMessage(credentialFormattedInfo)
                                    setPositiveButton(R.string.button_delete) { _, _ ->
                                        AcceptDenyDialog(context).run {
                                            setTitle(rpId)
                                            setMessage(
                                                    getString(
                                                            R.string.prompt_delete_resident_credential_message,
                                                            credentialTwoLineInfo.first
                                                    )
                                            )
                                            setPositiveButton { _, _ ->
                                                AuthenticatorContext.deleteResidentCredential(
                                                        context,
                                                        credential
                                                )
                                                rpCategory.removePreference(preference)
                                                if (rpCategory.preferenceCount == 0)
                                                    preferenceScreen.removePreference(rpCategory)
                                            }
                                            setNegativeButton { _, _ -> }
                                            show()
                                        }
                                    }
                                }
                                builder.create()
                                builder.show()
                            }
                        })
                    }
                }
            }
        }else{
            Log.i("ResidentCredentialsList", "credentialsPerRp  null")
        }
    }
}