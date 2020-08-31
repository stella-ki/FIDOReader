package com.challenge.fidoreader.fidoReader.data

import com.challenge.fidoreader.Util.byteArrayToHexString
import com.challenge.fidoreader.Util.sha_256
import org.spongycastle.jce.ECNamedCurveTable
import org.spongycastle.jce.spec.ECPublicKeySpec
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Security
import java.security.interfaces.ECPublicKey
import javax.crypto.KeyAgreement

class SharedSecretObject {

    var isSharedSecretReady: Boolean = false
    var shareSecret:String = ""
    var publickey:String = ""
    var privatekey:String = ""

    init {
        shareSecret = ""
        publickey = ""
        privatekey = ""
    }

    fun reset(){
        shareSecret = ""
        publickey = ""
        privatekey = ""
    }

    fun getPublicKey():String{
        if(!isSharedSecretReady){
            throw Exception("Generated key is required")
        }
        return publickey;
    }

    fun getPrivateKey():String{
        if(!isSharedSecretReady){
            throw Exception("Generated key is required")
        }
        return privatekey;
    }

    fun generateSharedSecret(authenticator_pubKey: String){
        var kpg: KeyPairGenerator = KeyPairGenerator.getInstance("EC")
        kpg.initialize(256)
        var kp: KeyPair = kpg.generateKeyPair()
        var outPubK: ByteArray = kp.public.encoded
        var outPriK: ByteArray = kp.private.encoded

        //platform public key and private key
        publickey = outPubK.byteArrayToHexString().substring(54)
        privatekey = outPriK.byteArrayToHexString()

        Security.insertProviderAt(org.spongycastle.jce.provider.BouncyCastleProvider(), 1)
        var keyFactory: KeyFactory = KeyFactory.getInstance("ECDSA")
        var pubkey: ECPublicKey = keyFactory.generatePublic(
                ECPublicKeySpec(ECNamedCurveTable.getParameterSpec("secp256r1").curve.createPoint(
                        BigInteger(authenticator_pubKey.substring(0, 64), 16), BigInteger(authenticator_pubKey.substring(64), 16)),
                        ECNamedCurveTable.getParameterSpec("secp256r1")
                )) as ECPublicKey

        var ka: KeyAgreement  = KeyAgreement.getInstance("ECDH")
        ka.init(kp.private)
        ka.doPhase(pubkey, true)

        var b_sharedSecret:ByteArray = ka.generateSecret()

        shareSecret = b_sharedSecret.sha_256().byteArrayToHexString()
        isSharedSecretReady = true

    }

    override fun toString(): String {
        return "SharedSecretObject(shareSecret='$shareSecret', publickey='$publickey', privatekey='$privatekey')"
    }


}