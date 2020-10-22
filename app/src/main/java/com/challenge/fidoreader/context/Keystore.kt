package com.challenge.fidoreader.context

import com.challenge.fidoreader.ctap2.*
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec


private val authenticatorKeyAgreementKeyPair: KeyPair by lazy {
    val ecParameterSpec = ECGenParameterSpec("secp256r1")
    val kpg = KeyPairGenerator.getInstance("EC")
    kpg.initialize(ecParameterSpec)
    kpg.genKeyPair()
}

@ExperimentalUnsignedTypes
val authenticatorKeyAgreementKey
    get() = getCoseRepresentation(
            authenticatorKeyAgreementKeyPair.public as ECPublicKey,
            ECAlgorithm.KeyAgreement
    )

val authenticatorKeyAgreementParams: ECParameterSpec
    get() = (authenticatorKeyAgreementKeyPair.public as ECPublicKey).params

enum class ECAlgorithm {
    Signature,
    KeyAgreement
}

@ExperimentalUnsignedTypes
private fun getCoseRepresentation(publicKey: ECPublicKey, algorithm: ECAlgorithm): CBORLongMap {
    val xRaw = publicKey.w.affineX.toByteArray()
    require(xRaw.size <= 32 || (xRaw.size == 33 && xRaw[0] == 0.toByte())) { "Can only handle 256 bit keys." }
    val xPadded =
            ByteArray(Integer.max(32 - xRaw.size, 0)) + xRaw.slice(Integer.max(xRaw.size - 32, 0) until xRaw.size)
    check(xPadded.size == 32)
    val yRaw = publicKey.w.affineY.toByteArray()
    require(yRaw.size <= 32 || (yRaw.size == 33 && yRaw[0] == 0.toByte())) { "Can only handle 256 bit keys." }
    val yPadded =
            ByteArray(Integer.max(32 - yRaw.size, 0)) + yRaw.slice(Integer.max(yRaw.size - 32, 0) until yRaw.size)
    check(yPadded.size == 32)
    val template = when (algorithm) {
        ECAlgorithm.Signature -> COSE_KEY_ES256_TEMPLATE
        ECAlgorithm.KeyAgreement -> COSE_KEY_ECDH_TEMPLATE
    }
    return CBORLongMap(
            template + mapOf(
                    COSE_KEY_EC256_X to CBORByteString(xPadded),
                    COSE_KEY_EC256_Y to CBORByteString(yPadded)
            )
    )
}
