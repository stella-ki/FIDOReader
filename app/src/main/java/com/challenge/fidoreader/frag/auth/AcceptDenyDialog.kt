package com.challenge.fidoreader.frag.auth

import android.annotation.TargetApi
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Space
import android.widget.TextView
import androidx.annotation.StyleRes
import com.challenge.fidoreader.R


/**
 * A dialog to display a title, a message, and/or an icon with a positive and a negative button.
 *
 *
 * The buttons are hidden away unless there is a listener attached to the button. Since there's
 * no click listener attached by default, the buttons are hidden be default.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class AcceptDenyDialog @JvmOverloads constructor(context: Context?, @StyleRes themeResId: Int = 0 /* use default context theme */) : Dialog(context!!, themeResId) {
    /** Icon at the top of the dialog.  */
    protected var mIcon: ImageView

    /** Title at the top of the dialog.  */
    protected var mTitle: TextView

    /** Message content of the dialog.  */
    protected var mMessage: TextView

    /** Panel containing the buttons.  */
    protected var mButtonPanel: View

    /** Positive button in the button panel.  */
    protected lateinit var  mPositiveButton: ImageButton

    /** Negative button in the button panel.  */
    protected lateinit var mNegativeButton: ImageButton

    /**
     * Click listener for the positive button. Positive button should hide if this is `null
    ` * .
     */
    protected var mPositiveButtonListener: DialogInterface.OnClickListener? = null

    /**
     * Click listener for the negative button. Negative button should hide if this is `null
    ` * .
     */
    protected var mNegativeButtonListener: DialogInterface.OnClickListener? = null

    /** Spacer between the positive and negative button. Hidden if one button is hidden.  */
    protected var mSpacer: View
    private val mButtonHandler = View.OnClickListener { v: View ->
        if (v === mPositiveButton && mPositiveButtonListener != null) {
            mPositiveButtonListener!!.onClick(this, DialogInterface.BUTTON_POSITIVE)
            dismiss()
        } else if (v === mNegativeButton && mNegativeButtonListener != null) {
            mNegativeButtonListener!!.onClick(this, DialogInterface.BUTTON_NEGATIVE)
            dismiss()
        }
    }

    fun getButton(whichButton: Int): ImageButton? {
        return when (whichButton) {
            DialogInterface.BUTTON_POSITIVE -> mPositiveButton
            DialogInterface.BUTTON_NEGATIVE -> mNegativeButton
            else -> null
        }
    }

    fun setIcon(icon: Drawable?) {
        mIcon.visibility = if (icon == null) View.GONE else View.VISIBLE
        mIcon.setImageDrawable(icon)
    }

    /**
     * @param resId the resourceId of the drawable to use as the icon or 0 if you don't want an icon.
     */
    fun setIcon(resId: Int) {
        mIcon.visibility = if (resId == 0) View.GONE else View.VISIBLE
        mIcon.setImageResource(resId)
    }

    /** @param message the content message text of the dialog.
     */
    fun setMessage(message: CharSequence?) {
        mMessage.text = message
        mMessage.visibility = if (message == null) View.GONE else View.VISIBLE
    }

    /** @param title the title text of the dialog.
     */
    override fun setTitle(title: CharSequence?) {
        mTitle.text = title
    }

    /**
     * Sets a click listener for a button.
     *
     *
     * Will hide button bar if all buttons are hidden (i.e. their click listeners are `null
    ` * ).
     *
     * @param whichButton [DialogInterface.BUTTON_POSITIVE] or [     ]
     * @param listener the listener to set for the button. Hide button if `null`.
     */
    fun setButton(whichButton: Int, listener: DialogInterface.OnClickListener?) {
        when (whichButton) {
            DialogInterface.BUTTON_POSITIVE -> mPositiveButtonListener = listener
            DialogInterface.BUTTON_NEGATIVE -> mNegativeButtonListener = listener
            else -> return
        }
        mSpacer.visibility = if (mPositiveButtonListener == null || mNegativeButtonListener == null) View.GONE else View.INVISIBLE
        mPositiveButton.visibility = if (mPositiveButtonListener == null) View.GONE else View.VISIBLE
        mNegativeButton.visibility = if (mNegativeButtonListener == null) View.GONE else View.VISIBLE
        mButtonPanel.visibility = if (mPositiveButtonListener == null && mNegativeButtonListener == null) View.GONE else View.VISIBLE
    }

    /**
     * Convenience method for `setButton(DialogInterface.BUTTON_POSITIVE, listener)`.
     *
     * @param listener the listener for the positive button.
     */
    fun setPositiveButton(listener: DialogInterface.OnClickListener?) {
        setButton(DialogInterface.BUTTON_POSITIVE, listener)
    }

    /**
     * Convenience method for `setButton(DialogInterface.BUTTON_NEGATIVE, listener)`.
     *
     * @param listener the listener for the positive button.
     */
    fun setNegativeButton(listener: DialogInterface.OnClickListener?) {
        setButton(DialogInterface.BUTTON_NEGATIVE, listener)
    }

    init {
        setContentView(R.layout.accept_deny_dialog)
        mTitle = findViewById<View>(R.id.texttitle) as TextView
        mMessage = findViewById<View>(R.id.textmessage) as TextView
        mIcon = findViewById<View>(R.id.imageicon) as ImageView
        mPositiveButton = findViewById<View>(R.id.mPositiveButton) as ImageButton
        mPositiveButton.setOnClickListener(mButtonHandler)
        mNegativeButton = findViewById<View>(R.id.mNegativeButton) as ImageButton
        mNegativeButton.setOnClickListener(mButtonHandler)
        mSpacer = findViewById<View>(R.id.spacer) as Space
        mButtonPanel = findViewById(R.id.mButtonPanel)
    }
}






