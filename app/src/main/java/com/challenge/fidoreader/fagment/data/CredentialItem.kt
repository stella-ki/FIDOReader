package com.challenge.fidoreader.fagment.data

import android.os.Parcel
import android.os.Parcelable
import com.challenge.fidoreader.R

class CredentialItem() : Parcelable{
    lateinit var credential_id: String
    lateinit var rpid: String
    lateinit var name: String
    var resid = 0

    constructor(credential_id: String, rpid: String, name: String, resid: Int) : this() {
        this.credential_id = credential_id
        this.rpid = rpid
        this.resid = resid
        this.name = name
    }

    constructor(credential_id: String, rpid: String, name: String) :
            this(credential_id, rpid, name, R.drawable.authenticator_key)

    constructor(parcel: Parcel) : this() {
        credential_id = parcel.readString().toString()
        rpid = parcel.readString().toString()
        name = parcel.readString().toString()
        resid = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(credential_id)
        parcel.writeString(rpid)
        parcel.writeString(name)
        parcel.writeInt(resid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CredentialItem> {
        override fun createFromParcel(parcel: Parcel): CredentialItem {
            return CredentialItem(parcel)
        }

        override fun newArray(size: Int): Array<CredentialItem?> {
            return arrayOfNulls(size)
        }
    }
}