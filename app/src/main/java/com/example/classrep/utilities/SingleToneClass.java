package com.example.classrep.utilities;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SingleToneClass {
    private static  Map<String, Integer> s;
    private static final SingleToneClass ourInstance = new SingleToneClass();

    public static SingleToneClass getInstance() {
        return ourInstance;
    }

    private SingleToneClass() {
        this.s = new HashMap<>();
    }

    public void setData(String key, int value) {
        if(this.s.containsKey(key)){
            this.s.remove(key);
        }

        this.s.put(key, value);
    }

    public int getData(String key) {
        if(this.s.containsKey(key)){
            return this.s.get(key);
        } else {
            return 1234567890;
        }
    }

    public void deleteData(String key){
        this.s.remove(key);
    }
}
