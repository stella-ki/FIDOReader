package com.challenge.fidoreader.frag.auth

import android.app.Activity
import android.content.DialogInterface
import com.challenge.fidoreader.R
import com.challenge.fidoreader.context.AuthenticatorContext
//import com.challenge.fidoreader.context.AuthenticatorContext
import com.challenge.fidoreader.context.AuthenticatorSpecialStatus
import com.challenge.fidoreader.context.AuthenticatorStatus
import com.challenge.fidoreader.context.RequestInfo
import com.challenge.fidoreader.ctap2.breakAt
import com.challenge.fidoreader.hid.HID_USER_PRESENCE_TIMEOUT_MS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

@ExperimentalUnsignedTypes
class HidAuthenticatorContext(private val activity: Activity) :  AuthenticatorContext(activity, isHidTransport = true) {

    override fun notifyUser(info: RequestInfo) {
        // No-op for HID transport since we already asked for confirmation during
        // confirmWithUser
    }

    override fun handleSpecialStatus(specialStatus: AuthenticatorSpecialStatus) {
        // No-op for HID transport since we always ask for confirmation before encountering
        // a special status.
    }

    /**
     * Returns true if the user confirms the request, false if the user denies it explicitly and
     * null if the confirmation dialog times out.
     */
    override suspend fun confirmRequestWithUser(info: RequestInfo): Boolean? {
        return try {
            status = AuthenticatorStatus.WAITING_FOR_UP
            withContext(Dispatchers.Main) {
                val dialog =
                    TimedAcceptDenyDialog(activity).apply {
                        setIcon(R.drawable.ic_launcher_outline)
                        setMessage(info.confirmationPrompt)
                        setTimeout(HID_USER_PRESENCE_TIMEOUT_MS)
                        setVibrateOnShow(true)
                        setWakeOnShow(true)
                    }
                suspendCancellableCoroutine<Boolean?> { continuation ->
                    dialog.apply {
                        setPositiveButton(DialogInterface.OnClickListener { _, _ ->
                            continuation.resume(true)
                        })
                        setNegativeButton(DialogInterface.OnClickListener { _, _ ->
                            continuation.resume(false)
                        })
                        setTimeoutListener(DialogInterface.OnCancelListener {
                            continuation.resume(null)
                        })
                    }.show()
                    continuation.invokeOnCancellation {
                        dialog.dismiss()
                    }
                }
            }
        } finally {
            status = AuthenticatorStatus.PROCESSING
        }
    }

    override suspend fun confirmTransactionWithUser(rpId: String, prompt: String): String? {
        return try {
            status = AuthenticatorStatus.WAITING_FOR_UP
            withContext(Dispatchers.Main) {
                val dialog =
                    TimedAcceptDenyDialog(activity).apply {
                        setIcon(R.drawable.ic_launcher_outline)
                        setTitle(rpId)
                        setMessage(prompt)
                        setTimeout(HID_USER_PRESENCE_TIMEOUT_MS)
                        setVibrateOnShow(true)
                        setWakeOnShow(true)
                    }
                suspendCancellableCoroutine<String?> { continuation ->
                    dialog.apply {
                        setPositiveButton(DialogInterface.OnClickListener { _, _ ->
                            val lineBreaks = messageLineBreaks
                            if (lineBreaks == null)
                                continuation.resume(null)
                            else
                                continuation.resume(prompt.breakAt(lineBreaks))
                        })
                        setNegativeButton(DialogInterface.OnClickListener { _, _ ->
                            continuation.resume(null)
                        })
                    }.show()
                    continuation.invokeOnCancellation {
                        dialog.dismiss()
                    }
                }
            }
        } finally {
            status = AuthenticatorStatus.PROCESSING
        }
    }
}
