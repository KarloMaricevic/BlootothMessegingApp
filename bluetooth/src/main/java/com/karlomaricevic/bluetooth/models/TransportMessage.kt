package com.karlomaricevic.bluetooth.models

sealed class TransportMessage(open val address: String) {

    data class Text(
        override val address: String,
        val text: String
    ) : TransportMessage(address)

    data class Image(
        override val address: String,
        val image: ByteArray
    ) : TransportMessage(address) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Image

            if (address != other.address) return false
            if (!image.contentEquals(other.image)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = address.hashCode()
            result = 31 * result + image.contentHashCode()
            return result
        }
    }

    data class Audio(
        override val address: String,
        val audio: ByteArray
    ) : TransportMessage(address) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Audio

            if (address != other.address) return false
            if (!audio.contentEquals(other.audio)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = address.hashCode()
            result = 31 * result + audio.contentHashCode()
            return result
        }
    }
}
