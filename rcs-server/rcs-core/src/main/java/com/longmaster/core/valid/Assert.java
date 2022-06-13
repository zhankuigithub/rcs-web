package com.longmaster.core.valid;

public class Assert {

    public static void isTrue(boolean expression, RuntimeException runtimeException) {
        if (!expression) {
            throw runtimeException;
        }
    }

    public static void isFalse(boolean expression, RuntimeException runtimeException) {
        if (expression) {
            throw runtimeException;
        }
    }

    public static void isNull(Object object, RuntimeException runtimeException) {
        if (object != null) {
            throw runtimeException;
        }
    }

    public static void notNull(Object object,  RuntimeException runtimeException) {
        if (object == null) {
            throw runtimeException;
        }
    }
}
