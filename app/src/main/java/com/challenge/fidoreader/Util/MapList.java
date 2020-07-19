package com.challenge.fidoreader.Util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class MapList<K, V> {
    ArrayList<K> key;
    HashMap<K, V> map;

    int expectedSize = 0;

    public MapList(){
        key = new ArrayList<>();
        map = new HashMap<>();
        expectedSize = 0;
    }

    public void add(K k, V v){
        key.add(k);
        map.put(k,v);
    }

    public V get(Object key){
        return map.get(key);
    }

    public Object getKey(int num){
        return key.get(num);
    }

    public Object getValue(int num){
        return map.get(key.get(num));
    }

    public void clear(){
        key.clear();
        map.clear();
    }

    public int getSize(){
        return key.size();
    }

    public int expectedSize(){
        return expectedSize;
    }

    public void setExpectedSize(int expectedSize){
        this.expectedSize = expectedSize;
    }



}
