package com.challenge.fidoreader.ctap2

import kotlin.experimental.and

@ExperimentalUnsignedTypes
fun fromCBORToEnd(iter: Iterator<Byte>): CBORValue? {
    return fromCBOR(iter).takeUnless { iter.hasNext() }
}

@ExperimentalUnsignedTypes
fun fromCBORToEnd(bytes: ByteArray): CBORValue? {
    return fromCBORToEnd(bytes.iterator())
}

@ExperimentalUnsignedTypes
fun fromCBOR(bytes: Iterable<Byte>): CBORValue? {
    val iter = bytes.iterator()
    return fromCBOR(iter).takeUnless { iter.hasNext() }
}

@ExperimentalUnsignedTypes
private fun fromCBOR(iter: Iterator<Byte>): CBORValue? {
    with(iter) {
        if (!hasNext())
            return null
        val initialByte = next()
        val additionalInfo = initialByte and 0x1f
        val value: ULong = when (additionalInfo) {
            in 0..23 -> {
                additionalInfo.toULong()
            }
            24.toByte() -> {
                nextInteger(UByte.SIZE_BYTES)?.takeUnless { it <= 23.toULong() }
            }
            25.toByte() -> {
                nextInteger(UShort.SIZE_BYTES)?.takeUnless { it <= UByte.MAX_VALUE }
            }
            26.toByte() -> {
                nextInteger(UInt.SIZE_BYTES)?.takeUnless { it <= UShort.MAX_VALUE }
            }
            27.toByte() -> {
                nextInteger(ULong.SIZE_BYTES)?.takeUnless { it <= UInt.MAX_VALUE }
            }
            else -> null
        }
                ?: return null
        return when (val majorType = (initialByte.toUByte().toUInt() shr 5).toUByte()) {
            MajorType.UNSIGNED_INTEGER.value -> {
                if (value <= Long.MAX_VALUE.toULong()) CBORLong(
                        value.toLong()
                )
                else CBORUnsignedInteger(value)
            }
            MajorType.NEGATIVE_INTEGER.value -> {
                if (value <= Long.MAX_VALUE.toULong()) CBORLong(
                        -1 - value.toLong()
                )
                else CBORNegativeInteger(value)
            }
            MajorType.BYTE_STRING.value, MajorType.TEXT_STRING.value -> {
                if (value > Int.MAX_VALUE.toULong())
                    return null
                val array = ByteArray(value.toInt()) {
                    if (!hasNext())
                        return null
                    next()
                }
                if (majorType == MajorType.BYTE_STRING.value) {
                    CBORByteString(array)
                } else {
                    CBORTextString(array.decodeToStringOrNull() ?: return null)
                }
            }
            MajorType.ARRAY.value -> {
                if (value > Int.MAX_VALUE.toULong())
                    return null
                val array = Array(value.toInt()) {
                    fromCBOR(iter) ?: return null
                }
                CBORArray(array)
            }
            MajorType.MAP.value -> {
                if (value > Int.MAX_VALUE.toULong())
                    return null
                val size = value.toInt()
                val map = HashMap<CBORValue, CBORValue>(size)
                repeat(size) {
                    val key = fromCBOR(this) ?: return null
                    if (map.containsKey(key))
                        return null
                    map[key] = fromCBOR(this) ?: return null
                }
                when {
                    map.keys.all { it is CBORTextString } ->
                        CborTextStringMap(map.mapKeys { (it.key as CBORTextString).value })
                    map.keys.all { it is CBORLong } ->
                        CBORLongMap(map.mapKeys { (it.key as CBORLong).value })
                    else -> CBORMap(map)
                }
            }
            MajorType.SIMPLE.value -> {
                when (additionalInfo) {
                    in 0..19 -> CBORSimpleValue(
                            additionalInfo.toUByte()
                    )
                    20.toByte() -> CBORBoolean(false)
                    21.toByte() -> CBORBoolean(true)
                    22.toByte() -> CborNull
                    23.toByte() -> CborUndefined
                    24.toByte() ->
                        CBORSimpleValue(value.toUByte()).takeUnless { value < 32.toULong() }
                    // TODO: Support half-precision floating point numbers.
                    25.toByte() -> CborUndefined
                    26.toByte() -> CBORFloatingPointNumber(
                            Float.fromBits(
                                    value.toInt()
                            ).toDouble()
                    )
                    27.toByte() -> CBORFloatingPointNumber(
                            Double.fromBits(
                                    value.toLong()
                            )
                    )
                    else -> null
                }
            }
            else -> null
        }
    }
}

@ExperimentalUnsignedTypes
private fun Iterator<Byte>.nextInteger(numBytes: Int): ULong? {
    var value: ULong = 0U
    require(numBytes in 1..8)
    repeat(numBytes) {
        if (!hasNext())
            return null
        value = (value shl 8) + next().toUByte().toULong()
    }
    return value
}

