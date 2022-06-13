package com.longmaster.core.util;

import java.util.ArrayList;
import java.util.List;


public class ArrayListUtil {

    public static <T> List<T> maxLengthList(List<T> list, int maxLength) {
        if (list == null) {
            throw new NullPointerException("list");
        } else if (maxLength < 0) {
            throw new IndexOutOfBoundsException("maxLength = " + maxLength);
        } else {
            int capacity = list.size() < maxLength ? list.size() : maxLength;
            List<T> newList = new ArrayList(capacity);

            for (int i = 0; i < list.size() && i < maxLength; ++i) {
                newList.add(list.get(i));
            }

            return newList;
        }
    }
}
