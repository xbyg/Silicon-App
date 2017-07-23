package com.xbyg_plus.silicon.utils;

import java.util.ArrayList;

public class TwoWayMap<K, V> {
    private ArrayList<K> keyList = new ArrayList<>();
    private ArrayList<V> valueList = new ArrayList<>();

    public ArrayList<K> getKeyList() {
        return keyList;
    }

    public ArrayList<V> getValueList() {
        return valueList;
    }

    public V getValueByKey(K key) {
        int index = keyList.indexOf(key);
        return index == -1 ? null : valueList.get(index);
    }

    public K getKeyByValue(V value) {
        int index = valueList.indexOf(value);
        return index == -1 ? null : keyList.get(index);
    }

    public void put(K key, V value) {
        keyList.add(key);
        valueList.add(value);
    }

    public void removeByKey(K key) {
        if (keyList.contains(key)) {
            int index = keyList.indexOf(key);
            keyList.remove(index);
            valueList.remove(index);
        }
    }

    public void removeByValue(V value) {
        if (valueList.contains(value)) {
            int index = valueList.indexOf(value);
            keyList.remove(index);
            valueList.remove(index);
        }
    }
}
