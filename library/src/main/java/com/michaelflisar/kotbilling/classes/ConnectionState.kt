package com.michaelflisar.kotbilling.classes

sealed class ConnectionState {

    object Connected : ConnectionState() {
        override fun toString() = "ConnectionState::Connected"
    }

    object Disconnected : ConnectionState() {
        override fun toString() = "ConnectionState::Disconnected"
    }

    data class Error(val responseCode: Int, val error: String) : ConnectionState()

}