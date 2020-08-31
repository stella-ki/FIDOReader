package com.challenge.fidoreader.reader

abstract class Sender {
    abstract fun startFIDO()
    abstract fun sendFIDO(cmd: String) : String

}