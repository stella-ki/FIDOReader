package com.challenge.fidoreader

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IntDef
import androidx.annotation.MainThread
import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.*


class ConfirmationOverlay {
    /**
     * Interface for listeners to be notified when the [ConfirmationOverlay] animation has
     * finished and its [View] has been removed.
     */
    interface OnAnimationFinishedListener {
        /**
         * Called when the confirmation animation is finished.
         */
        fun onAnimationFinished()
    }

    /** Types of animations to display in the overlay.  */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(SUCCESS_ANIMATION, FAILURE_ANIMATION, OPEN_ON_PHONE_ANIMATION)
    annotation class OverlayType

    @OverlayType
    private var mType = SUCCESS_ANIMATION
    private var mDurationMillis = DEFAULT_ANIMATION_DURATION_MS
    private var mListener: OnAnimationFinishedListener? = null
    private var mMessage: String? = null
    private var mOverlayView: View? = null
    private var mOverlayDrawable: Drawable? = null
    private var mIsShowing = false
    private val mMainThreadHandler = Handler(Looper.getMainLooper())
    private val mHideRunnable = Runnable { hide() }

    /**
     * Sets a message which will be displayed at the same time as the animation.
     *
     * @return `this` object for method chaining.
     */
    fun setMessage(message: String?): ConfirmationOverlay {
        mMessage = message
        return this
    }

    /**
     * Sets the [OverlayType] which controls which animation is displayed.
     *
     * @return `this` object for method chaining.
     */
    fun setType(@OverlayType type: Int): ConfirmationOverlay {
        mType = type
        return this
    }

    /**
     * Sets the duration in milliseconds which controls how long the animation will be displayed.
     * Default duration is [.DEFAULT_ANIMATION_DURATION_MS].
     *
     * @return `this` object for method chaining.
     */
    fun setDuration(millis: Int): ConfirmationOverlay {
        mDurationMillis = millis
        return this
    }

    /**
     * Sets the [OnAnimationFinishedListener] which will be invoked once the overlay is no
     * longer visible.
     *
     * @return `this` object for method chaining.
     */
    fun setFinishedAnimationListener(
            listener: OnAnimationFinishedListener?): ConfirmationOverlay {
        mListener = listener
        return this
    }

    /**
     * Adds the overlay as a child of `view.getRootView()`, removing it when complete. While
     * it is shown, all touches will be intercepted to prevent accidental taps on obscured views.
     */
    @MainThread
    fun showAbove(view: View) {
        if (mIsShowing) {
            return
        }
        mIsShowing = true
        updateOverlayView(view.context)
        (view.rootView as ViewGroup).addView(mOverlayView)
        animateAndHideAfterDelay()
    }

    /**
     * Adds the overlay as a content view to the `activity`, removing it when complete. While
     * it is shown, all touches will be intercepted to prevent accidental taps on obscured views.
     */
    @MainThread
    fun showOn(activity: Activity) {
        if (mIsShowing) {
            return
        }
        mIsShowing = true
        updateOverlayView(activity)
        activity.window.addContentView(mOverlayView, mOverlayView!!.layoutParams)
        animateAndHideAfterDelay()
    }

    @MainThread
    private fun animateAndHideAfterDelay() {
        if (mOverlayDrawable is Animatable) {
            val animatable = mOverlayDrawable as Animatable
            animatable.start()
        }
        mMainThreadHandler.postDelayed(mHideRunnable, mDurationMillis.toLong())
    }

    /**
     * Starts a fadeout animation and removes the view once finished. This is invoked by [ ][.mHideRunnable] after [.mDurationMillis] milliseconds.
     *
     * @hide
     */
    @MainThread
    @VisibleForTesting
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun hide() {
        val fadeOut = AnimationUtils.loadAnimation(mOverlayView!!.context, android.R.anim.fade_out)
        fadeOut.setAnimationListener(
                object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {
                        mOverlayView!!.clearAnimation()
                    }

                    override fun onAnimationEnd(animation: Animation) {
                        (mOverlayView!!.parent as ViewGroup).removeView(mOverlayView)
                        mIsShowing = false
                        if (mListener != null) {
                            mListener!!.onAnimationFinished()
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation) {}
                })
        mOverlayView!!.startAnimation(fadeOut)
    }

    @MainThread
    private fun updateOverlayView(context: Context) {
        if (mOverlayView == null) {
            mOverlayView = LayoutInflater.from(context).inflate(R.layout.ws_overlay_confirmation, null)
        }
        mOverlayView!!.setOnTouchListener { v, event -> true }
        mOverlayView!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        updateImageView(context, mOverlayView)
        updateMessageView(context, mOverlayView)
    }

    @MainThread
    private fun updateMessageView(context: Context, overlayView: View?) {
        val messageView = overlayView!!.findViewById<TextView>(R.id.wearable_support_confirmation_overlay_message)
        if (mMessage != null) {
            /*int screenWidthPx = ResourcesUtil.getScreenWidthPx(context);
            int topMarginPx = ResourcesUtil.getFractionOfScreenPx(
                    context, screenWidthPx, R.fraction.confirmation_overlay_margin_above_text);
            int sideMarginPx =
                    ResourcesUtil.getFractionOfScreenPx(
                            context, screenWidthPx, R.fraction.confirmation_overlay_margin_side);

            MarginLayoutParams layoutParams = (MarginLayoutParams) messageView.getLayoutParams();
            layoutParams.topMargin = topMarginPx;
            layoutParams.leftMargin = sideMarginPx;
            layoutParams.rightMargin = sideMarginPx;

            messageView.setLayoutParams(layoutParams);*/
            messageView.text = mMessage
            messageView.visibility = View.VISIBLE
        } else {
            messageView.visibility = View.GONE
        }
    }

    @MainThread
    private fun updateImageView(context: Context, overlayView: View?) {
        mOverlayDrawable = when (mType) {
            SUCCESS_ANIMATION -> ContextCompat.getDrawable(context,
                    R.drawable.accept_deny_dialog_negative_bg)
            FAILURE_ANIMATION -> ContextCompat.getDrawable(context, R.drawable.common_google_signin_btn_icon_dark_focused)
            OPEN_ON_PHONE_ANIMATION -> ContextCompat.getDrawable(context, R.drawable.common_google_signin_btn_icon_dark)
            else -> {
                val errorMessage = String.format(Locale.US, "Invalid ConfirmationOverlay type [%d]", mType)
                throw IllegalStateException(errorMessage)
            }
        }
        val imageView = overlayView!!.findViewById<ImageView>(R.id.wearable_support_confirmation_overlay_image)
        imageView.setImageDrawable(mOverlayDrawable)
    }

    companion object {
        /** Default animation duration in ms.  */
        const val DEFAULT_ANIMATION_DURATION_MS = 1000

        /** [OverlayType] indicating the success animation overlay should be displayed.  */
        const val SUCCESS_ANIMATION = 0

        /**
         * [OverlayType] indicating the failure overlay should be shown. The icon associated with
         * this type, unlike the others, does not animate.
         */
        const val FAILURE_ANIMATION = 1

        /** [OverlayType] indicating the "Open on Phone" animation overlay should be displayed.  */
        const val OPEN_ON_PHONE_ANIMATION = 2
    }
}