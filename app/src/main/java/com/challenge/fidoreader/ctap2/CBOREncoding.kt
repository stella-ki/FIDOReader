package com.challenge.fidoreader.ctap2

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

interface CBORValue {
    fun writeAsCBOR(out: ByteArrayOutputStream)

    fun toCBOR(): ByteArray {
        val out = ByteArrayOutputStream()
        writeAsCBOR(out)
        return out.toByteArray()
    }

}

interface CBORBoxedValue<out T> : CBORValue {
    val value: T
}

@ExperimentalUnsignedTypes
inline class CBORUnsignedInteger(override val value: ULong) :
        CBORBoxedValue<ULong> {
    override fun writeAsCBOR(out: ByteArrayOutputStream) {
        value.writeAsCBOR(out, MajorType.UNSIGNED_INTEGER)
    }
}

@ExperimentalUnsignedTypes
inline class CBORNegativeInteger(override val value: ULong) :
        CBORBoxedValue<ULong> {
    override fun writeAsCBOR(out: ByteArrayOutputStream) {
        value.writeAsCBOR(out, MajorType.NEGATIVE_INTEGER)
    }
}

@ExperimentalUnsignedTypes
inline class CBORLong(override val value: Long) :
        CBORBoxedValue<Long> {
    override fun writeAsCBOR(out: ByteArrayOutputStream) {
        if (value >= 0)
            CBORUnsignedInteger(value.toULong()).writeAsCBOR(out)
        else
            CBORNegativeInteger((-(value + 1)).toULong()).writeAsCBOR(out)
    }
}

@ExperimentalUnsignedTypes
inline class CBORTextString(override val value: String) :
        CBORBoxedValue<String> {
    override fun writeAsCBOR(out: ByteArrayOutputStream) {
        val array = value.toByteArray()
        array.size.toULong().writeAsCBOR(out, MajorType.TEXT_STRING)
        out.write(array)
    }
}

@ExperimentalUnsignedTypes
data class CBORByteString(override val value: ByteArray) :
        CBORBoxedValue<ByteArray> {
    override fun writeAsCBOR(out: ByteArrayOutputStream) {
        value.size.toULong().writeAsCBOR(out, MajorType.BYTE_STRING)
        out.write(value)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CBORByteString

        if (!value.contentEquals(other.value)) return false

        return true
    }

    override fun hashCode(): Int {
        return value.contentHashCode()
    }
}

@ExperimentalUnsignedTypes
data class CBORArray(override val value: Array<CBORValue>) :
        CBORBoxedValue<Array<CBORValue>> {
    override fun writeAsCBOR(out: ByteArrayOutputStream) {
        value.size.toULong().writeAsCBOR(out, MajorType.ARRAY)
        for (element in value)
            element.writeAsCBOR(out)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CBORArray

        if (!value.contentEquals(other.value)) return false

        return true
    }

    override fun hashCode(): Int {
        return value.contentHashCode()
    }
}

@ExperimentalUnsignedTypes
inline class CborTextStringMap(override val value: Map<String, CBORValue>) :
        CBORBoxedValue<Map<String, CBORValue>> {
    override fun writeAsCBOR(out: ByteArrayOutputStream) {
        CBORMap(value.mapKeys {
            CBORTextString(
                    it.key
            )
        }).writeAsCBOR(out)
    }
}

@ExperimentalUnsignedTypes
inline class CBORLongMap(override val value: Map<Long, CBORValue>) :
        CBORBoxedValue<Map<Long, CBORValue>> {
    override fun writeAsCBOR(out: ByteArrayOutputStream) {
        CBORMap(value.mapKeys {
            CBORLong(
                    it.key
            )
        }).writeAsCBOR(out)
    }
}

@ExperimentalUnsignedTypes
inline class CBORMap(override val value: Map<CBORValue, CBORValue>) :
        CBORBoxedValue<Map<CBORValue, CBORValue>> {
    override fun writeAsCBOR(out: ByteArrayOutputStream) {
        value.size.toULong().writeAsCBOR(out, MajorType.MAP)
        value.entries.asSequence()
                .map { entry -> Pair(entry.key.toCBOR(), entry.value) }
                .sortedWith(compareBy(ByteArrayComparator) { it.first })
                .forEach { (key, entry) ->
                    out.write(key)
                    entry.writeAsCBOR(out)
                }
    }
}

@ExperimentalUnsignedTypes
inline class CBORSimpleValue(override val value: UByte) :
        CBORBoxedValue<UByte> {
    override fun writeAsCBOR(out: ByteArrayOutputStream) {
        if (value >= 32U)
            out.write(SIMPLE_VALUE.toInt())
        out.write(value.toInt())
    }
}

@ExperimentalUnsignedTypes
inline class CBORFloatingPointNumber(override val value: Double) :
        CBORBoxedValue<Double> {
    override fun writeAsCBOR(out: ByteArrayOutputStream) {
        out.write(SIMPLE_VALUE_DOUBLE.toInt())
        out.write(value.toBits().toULong().toBytes())
    }
}

@ExperimentalUnsignedTypes
inline class CBORBoolean(override val value: Boolean) :
        CBORBoxedValue<Boolean> {
    override fun writeAsCBOR(out: ByteArrayOutputStream) {
        if (value)
            out.write(SIMPLE_VALUE_TRUE.toInt())
        else
            out.write(SIMPLE_VALUE_FALSE.toInt())
    }
}

@ExperimentalUnsignedTypes
object CborNull : CBORBoxedValue<Nothing?> {
    override val value = null

    override fun writeAsCBOR(out: ByteArrayOutputStream) {
        out.write(SIMPLE_VALUE_NULL.toInt())
    }
}

@ExperimentalUnsignedTypes
object CborUndefined : CBORBoxedValue<Nothing> {
    override val value
        get() = throw UninitializedPropertyAccessException()

    override fun writeAsCBOR(out: ByteArrayOutputStream) {
        out.write(SIMPLE_VALUE_UNDEFINED.toInt())
    }
}

private object ByteArrayComparator : Comparator<ByteArray> {
    override fun compare(a: ByteArray, b: ByteArray): Int {
        var res = a.size.compareTo(b.size)
        if (res != 0)
            return res

        for (i in a.indices) {
            res = a[i].compareTo(b[i])
            if (res != 0)
                return res
        }
        return 0
    }
}

@ExperimentalUnsignedTypes
private fun UByte.toBytes() = byteArrayOf(this.toByte())

@ExperimentalUnsignedTypes
private fun UShort.toBytes(): ByteArray =
        ByteBuffer.allocate(Short.SIZE_BYTES)
                .putShort(this.toShort())
                .array()

@ExperimentalUnsignedTypes
private fun UInt.toBytes(): ByteArray =
        ByteBuffer.allocate(Int.SIZE_BYTES)
                .putInt(this.toInt())
                .array()

@ExperimentalUnsignedTypes
private fun ULong.toBytes(): ByteArray =
        ByteBuffer.allocate(Long.SIZE_BYTES)
                .putLong(this.toLong())
                .array()

@ExperimentalUnsignedTypes
private fun ULong.writeAsCBOR(out: ByteArrayOutputStream, majorType: MajorType) {
    val initialByte: UByte
    val additionalBytes: ByteArray
    when (this) {
        in 0U..23U -> {
            initialByte = majorType.mask or this.toUByte()
            additionalBytes = byteArrayOf()
        }
        in 24U..UByte.MAX_VALUE.toUInt() -> {
            initialByte = majorType.mask or 24U
            additionalBytes = this.toUByte().toBytes()
        }
        in UByte.MAX_VALUE.toUInt() + 1U..UShort.MAX_VALUE.toUInt() -> {
            initialByte = majorType.mask or 25U
            additionalBytes = this.toUShort().toBytes()
        }
        in UShort.MAX_VALUE.toUInt() + 1U..UInt.MAX_VALUE -> {
            initialByte = majorType.mask or 26U
            additionalBytes = this.toUInt().toBytes()
        }
        else -> {
            initialByte = majorType.mask or 27U
            additionalBytes = this.toBytes()
        }
    }

    out.write(ubyteArrayOf(initialByte).toByteArray())
    out.write(additionalBytes)
}

