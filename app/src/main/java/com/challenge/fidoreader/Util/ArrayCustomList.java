package com.challenge.fidoreader.Util;

import java.util.ArrayList;

public class ArrayCustomList<Object> extends ArrayList {

    int expectedCount = 0;

    public void setExpectedCount(int cnt){
        this.expectedCount = cnt;

    }

    public int getExpectedCount(){
        return expectedCount;
    }

}
