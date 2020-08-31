package com.challenge.fidoreader.fidoReader.data

class Credential(var user:String, var credentialID:String, var publickey:String) {

    var credProtect:String = ""

    override fun toString(): String {
        return "Credential(user='$user', credentialID='$credentialID', publickey='$publickey', credProtect='$credProtect')"
    }
}