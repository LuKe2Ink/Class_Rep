package com.example.classrep.utilities;

public class SingleToneClass {
    private int s;
    private static final SingleToneClass ourInstance = new SingleToneClass();

    public static SingleToneClass getInstance() {
        return ourInstance;
    }

    private SingleToneClass() {
    }

    public void setData(int s) {
        this.s = s;
    }

    public int getData() {
        return s;
    }
}
