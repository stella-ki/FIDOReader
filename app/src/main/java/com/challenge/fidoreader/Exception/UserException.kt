package com.challenge.fidoreader.Exception

import java.lang.Exception

class UserException : Exception{

    constructor() : super() {
    }

    constructor(msg: String) : super(msg) {
    }
}