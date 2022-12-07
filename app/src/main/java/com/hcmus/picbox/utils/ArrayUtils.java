package com.hcmus.picbox.utils;

public class ArrayUtils {
    static public <T> int indexOf(T[] array, T value){
        int index = -1;
        for (T a: array){
            index += 1;
            if (a == value){
                return index;
            }
        }
        return index;
    }
}
