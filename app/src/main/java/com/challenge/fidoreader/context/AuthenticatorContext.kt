package com.challenge.fidoreader.context

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.security.keystore.UserNotAuthenticatedException
import android.text.Html
import android.text.Spanned
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import com.challenge.fidoreader.R
import com.challenge.fidoreader.Util.defaultSharedPreferences
import com.challenge.fidoreader.Util.sharedPreferences
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.challenge.fidoreader.context.AuthenticatorAction.*
import com.challenge.fidoreader.ctap2.AttestationType
import com.challenge.fidoreader.ctap2.CBORValue
import com.challenge.fidoreader.ctap2.escapeHtml

private const val TAG = "AuthenticatorContext"

private const val COUNTERS_PREFERENCE_FILE = "counters"
private val COUNTERS_WRITE_LOCK = Object()

private const val CACHED_CREDENTIAL_ALIAS_PREFERENCE_KEY = "cached_credential_key_alias"
private val CACHED_CREDENTIAL_ALIAS_WRITE_LOCK = Object()

const val FUSE_CREATED_PREFERENCE_KEY = "fuse_created"
private const val USE_ANDROID_ATTESTATION_PREFERENCE_KEY = "use_android_attestation"

private const val RESIDENT_KEY_PREFERENCE_FILE_PREFIX = "rp_id_hash_"
private const val RESIDENT_KEY_RP_ID_HASHES_FILE = "rp_id_hashes"

enum class AuthenticatorAction {
    AUTHENTICATE,
    AUTHENTICATE_NO_CREDENTIALS,
    REGISTER,
    REGISTER_CREDENTIAL_EXCLUDED,
    PLATFORM_GET_TOUCH
}

sealed class RequestInfo(private val context: Context, protected val action: AuthenticatorAction) {
    protected abstract val formattedRp: String?
    protected abstract val shortRp: String?
    protected abstract val formattedUser: String?
    protected abstract val formattedAdditionalInfo: String
    protected abstract val shortAdditionalInfo: String

    private val formattedRpPart by lazy {
        if (formattedRp != null) context.getString(
                R.string.generic_part_to,
                formattedRp
        ) else ""
    }
    private val shortRpPart by lazy {
        if (shortRp != null) context.getString(
                R.string.generic_part_to,
                shortRp
        ) else ""
    }
    private val formattedUserPart by lazy {
        if (formattedUser != null) context.getString(
                R.string.generic_part_as,
                formattedUser
        ) else ""
    }

    val confirmationPrompt: Spanned
        get() = Html.fromHtml(
                when (action) {
                    AUTHENTICATE -> context.getString(
                            R.string.prompt_authenticate,
                            formattedRpPart,
                            formattedUserPart,
                            formattedAdditionalInfo
                    )
                    AUTHENTICATE_NO_CREDENTIALS -> context.getString(R.string.prompt_authenticate_no_credentials)
                    REGISTER -> context.getString(
                            R.string.prompt_register,
                            formattedRpPart,
                            formattedUserPart,
                            formattedAdditionalInfo
                    )
                    REGISTER_CREDENTIAL_EXCLUDED -> context.getString(
                            R.string.prompt_register_credential_excluded,
                            formattedRpPart
                    )
                    PLATFORM_GET_TOUCH -> context.getString(R.string.prompt_platform_get_touch)
                }, Html.FROM_HTML_MODE_LEGACY
        )

    val successMessage: String
        get() = when (action) {
            AUTHENTICATE -> context.getString(
                    R.string.message_authenticate,
                    shortRpPart,
                    shortAdditionalInfo
            )
            AUTHENTICATE_NO_CREDENTIALS -> context.getString(R.string.message_authenticate_no_credentials)
            REGISTER -> context.getString(
                    R.string.message_register,
                    shortRpPart,
                    shortAdditionalInfo
            )
            REGISTER_CREDENTIAL_EXCLUDED -> context.getString(
                    R.string.message_register_credential_excluded,
                    shortRpPart
            )
            PLATFORM_GET_TOUCH -> context.getString(R.string.message_platform_get_touch)
        }
}


class Ctap2RequestInfo(
        context: Context,
        action: AuthenticatorAction,
        private val rpId: String,
        private val rpName: String? = null,
        private val userName: String? = null,
        private val userDisplayName: String? = null,
        private val requiresUserVerification: Boolean = false,
        private val addResidentKeyHint: Boolean = false
) :
        RequestInfo(context, action) {

    override val formattedRp = if (!rpName.isNullOrBlank())
        "<br/><b>${rpId.escapeHtml()}</b><br/>(“${rpName.escapeHtml()}”)<br/>"
    else
        "<br/><b>${rpId.escapeHtml()}</b><br/>"

    override val shortRp = rpId

    override val formattedUser = if (!userName.isNullOrBlank())
        userName.escapeHtml()
    else if (!userDisplayName.isNullOrBlank())
        userDisplayName.escapeHtml()
    else null

    override val formattedAdditionalInfo = if (requiresUserVerification || addResidentKeyHint) {
        "<br/>"
    } else {
        ""
    } + if (requiresUserVerification) {
        when (action) {
            AUTHENTICATE -> context.getString(R.string.prompt_authenticate_user_verification)
            REGISTER -> context.getString(R.string.prompt_register_user_verification)
            else -> ""
        }
    } else {
        ""
    } + if (addResidentKeyHint) {
        when (action) {
            AUTHENTICATE -> context.getString(R.string.prompt_authenticate_resident_key)
            REGISTER -> context.getString(R.string.prompt_register_resident_key)
            else -> ""
        }
    } else {
        ""
    }

    override val shortAdditionalInfo = if (requiresUserVerification && action == REGISTER) {
        context.getString(R.string.message_register_user_verification)
    } else {
        ""
    }
}

enum class AuthenticatorStatus {
    IDLE,
    PROCESSING,
    WAITING_FOR_UP
}

enum class AuthenticatorSpecialStatus {
    RESET,
    USER_NOT_AUTHENTICATED
}

@ExperimentalUnsignedTypes
abstract class AuthenticatorContext(private val context: Context, val isHidTransport: Boolean){
    abstract fun notifyUser(info: RequestInfo)
    abstract fun handleSpecialStatus(specialStatus: AuthenticatorSpecialStatus)
    abstract suspend fun confirmRequestWithUser(info: RequestInfo): Boolean?
    abstract suspend fun confirmTransactionWithUser(rpId: String, prompt: String): String?

    // We use cached credentials only over NFC, where low latency responses are very important
    private val useCachedCredential = !isHidTransport
    var status: AuthenticatorStatus =
            AuthenticatorStatus.IDLE
    var getNextAssertionBuffer: Iterator<CBORValue>? = null
    var getNextAssertionRequestInfo: RequestInfo? = null

    private val counterPrefs by lazy { context.sharedPreferences(COUNTERS_PREFERENCE_FILE) }

    fun makeCtap2RequestInfo(
            action: AuthenticatorAction,
            rpId: String,
            rpName: String? = null,
            userName: String? = null,
            userDisplayName: String? = null,
            requiresUserVerification: Boolean = false,
            addResidentKeyHint: Boolean = false
    ) = Ctap2RequestInfo(
            context.applicationContext,
            action = action,
            rpId = rpId,
            rpName = rpName,
            userName = userName,
            userDisplayName = userDisplayName,
            requiresUserVerification = requiresUserVerification,
            addResidentKeyHint = addResidentKeyHint
    )
}

