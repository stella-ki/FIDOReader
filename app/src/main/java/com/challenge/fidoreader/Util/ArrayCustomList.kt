package com.challenge.fidoreader.Util

import java.util.*

class ArrayCustomList<Object> : ArrayList<Any?>() {
    private var expectedCount: Int = 0

    fun setExpectedCount(cnt: Int) {
        expectedCount = cnt
    }

    fun getExpectedCoun(): Int{
        return expectedCount
    }
}