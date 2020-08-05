package com.challenge.fidoreader.Util

import java.util.*

class MapList<K, V> {
    private var key: ArrayList<K> ?= null
    private var map: HashMap<K, V>
    private var expectedSize:Int

    init {
        key = ArrayList()
        map = HashMap()
        expectedSize = 0
    }

    fun add(k: K, v: V) {
        key!!.add(k)
        map[k] = v
    }

    operator fun get(key: Any?): V? {
        return map[key]
    }

    fun getKey(num: Int): K {
        return key!!.get(num)
    }

    fun getValue(num: Int): Any? {
        return this.map[key!![num]]
    }

    fun clear() {
        key!!.clear()
        map.clear()
    }

    fun getSize(): Int{
        return key!!.size
    }

    fun expectedSize(): Int {
        return expectedSize
    }

    fun setExpectedSize(expectedSize: Int) {
        this.expectedSize = expectedSize
    }


}