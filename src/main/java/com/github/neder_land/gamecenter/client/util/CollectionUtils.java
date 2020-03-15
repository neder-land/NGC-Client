package com.github.neder_land.gamecenter.client.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CollectionUtils {
    public static <T extends Comparable<? super T>> List<T> unique(List<T> col) {
        if (col.size() < 2) return col;
        List<T> ret = new ArrayList<>(col);
        ret.sort(Comparator.naturalOrder());
        int j = 0;
        for (int i = 1; i < ret.size(); ++i) {
            T backward = ret.get(j);
            T forward = ret.get(i);
            if (!Objects.equals(backward, forward) && ++j != i)
                ret.set(j, forward);
        }
        return ret.subList(0, ++j);
    }
}
