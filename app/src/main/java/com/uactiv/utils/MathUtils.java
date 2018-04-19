package com.uactiv.utils;

public class MathUtils {

    public static int getValuesDifference(int firstValue, int secondValue) {
        return Math.abs(firstValue - secondValue);
    }

    public static int getBorderMax(int pressedBorderRing, int defaultBorder) {
        return pressedBorderRing > defaultBorder ? pressedBorderRing : defaultBorder;
    }

    public static int twiceValue(int value) {
        return value + value;
    }
}