package com.challenge.fidoreader.fagment.data


import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.challenge.fidoreader.R


class FingerItem() : Parcelable {
    lateinit var templateID: String
    lateinit var fingerName: String
    var resid = 0

    constructor(templateID: String, fingerName: String, resid: Int) : this() {
        Log.v("FingerItem", fingerName)
        this.templateID = templateID
        this.fingerName = fingerName
        this.resid = R.drawable.fingerprint
    }

    constructor(fingerName: String) : this() {
        Log.v("FingerItem", fingerName)
        this.fingerName = fingerName
    }

    constructor(parcel: Parcel) : this() {
        templateID = parcel.readString().toString()
        fingerName = parcel.readString().toString()
        resid = parcel.readInt()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(templateID)
        dest.writeString(fingerName)
        dest.writeInt(resid)
    }

    override fun describeContents(): Int {
        return 0
    }


    companion object CREATOR : Parcelable.Creator<FingerItem> {
        override fun createFromParcel(parcel: Parcel): FingerItem {
            return FingerItem(parcel)
        }

        override fun newArray(size: Int): Array<FingerItem?> {
            return arrayOfNulls(size)
        }
    }
}