package com.challenge.fidoreader.fidoReader

import com.challenge.fidoreader.Util.ArrayCustomList

class RPs (var rp:String, var rpIDHash: String){

    var credentials:ArrayCustomList<Credential> = ArrayCustomList()

    fun addCredential(cred: Credential?) {
        credentials.add(cred)
    }

    fun getCredential(num: Int): Credential? {
        return credentials[num] as Credential
    }

    fun setCredentialExpectedCnt(num: Int) {
        credentials.expectedCount = num
    }

    fun getCredentialExpectedCnt(): Int {
        return credentials.expectedCount
    }

    override fun toString(): String {
        return "RPs(rp='$rp', rpIDHash='$rpIDHash', credentials=$credentials)"
    }


}