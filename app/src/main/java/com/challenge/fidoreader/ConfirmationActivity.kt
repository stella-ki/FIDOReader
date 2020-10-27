package com.challenge.fidoreader

import android.app.Activity
import android.os.Bundle
import android.util.SparseIntArray

class ConfirmationActivity : Activity(), ConfirmationOverlay.OnAnimationFinishedListener {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //this.setTheme(style.ConfirmationActivity);
        val intent = this.intent
        val requestedType = intent.getIntExtra("android.support.wearable.activity.extra.ANIMATION_TYPE", 1)
        require(CONFIRMATION_OVERLAY_TYPES.indexOfKey(requestedType) >= 0) { StringBuilder(38).append("Unknown type of animation: ").append(requestedType).toString() }
        val type = CONFIRMATION_OVERLAY_TYPES[requestedType]
        val message = intent.getStringExtra("android.support.wearable.activity.extra.MESSAGE")
        ConfirmationOverlay().setType(type).setMessage(message).setFinishedAnimationListener(this).showOn(this)
    }

    override fun onAnimationFinished() {
        finish()
    }

    companion object {
        const val EXTRA_MESSAGE = "android.support.wearable.activity.extra.MESSAGE"
        const val EXTRA_ANIMATION_TYPE = "android.support.wearable.activity.extra.ANIMATION_TYPE"
        const val SUCCESS_ANIMATION = 1
        const val OPEN_ON_PHONE_ANIMATION = 2
        const val FAILURE_ANIMATION = 3
        private val CONFIRMATION_OVERLAY_TYPES = SparseIntArray()

        init {
            CONFIRMATION_OVERLAY_TYPES.append(1, 0)
            CONFIRMATION_OVERLAY_TYPES.append(2, 2)
            CONFIRMATION_OVERLAY_TYPES.append(3, 1)
        }
    }
}
