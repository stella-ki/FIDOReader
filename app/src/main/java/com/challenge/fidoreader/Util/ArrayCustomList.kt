package com.challenge.fidoreader.Util

import java.util.*

class ArrayCustomList<Object> : ArrayList<Any?>() {
    var expectedCountNum: Int = 0

    fun setExpectedCount(cnt: Int) {
        expectedCountNum = cnt
    }

    fun getExpectedCoun(): Int{
        return expectedCountNum
    }
}