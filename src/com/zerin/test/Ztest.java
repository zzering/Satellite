package com.zerin.test;

import com.zerin.model.Block;
import com.zerin.model.Position;
import org.junit.Test;

import java.util.*;

import static com.zerin.service.CoverageCalculation.blockDivision;

/**
 * 用于测试，可删
 */
public class Ztest {
    void quickSort(String[] strs, int l, int r) {
        if (l >= r) {
            return;
        }
        int i = l, j = r;
        String tmp = strs[i];
        while (i < j) {
            while ((strs[j] + strs[l]).compareTo(strs[l] + strs[j]) >= 0 && i < j) {
                j--;
            }
            while ((strs[i] + strs[l]).compareTo(strs[l] + strs[i]) <= 0 && i < j) {
                i++;
            }
            tmp = strs[i];
            strs[i] = strs[j];
            strs[j] = tmp;
        }
        strs[i] = strs[l];
        strs[l] = tmp;
        quickSort(strs, l, i - 1);
        quickSort(strs, i + 1, r);
    }

    @Test
    public void tr() {
        Map<Position, String> map1 = new HashMap<Position, String>();
        Position a= new Position(1, 1);

        map1.put(a, "1");
        map1.put(new Position(2, 2), "2");
        map1.put(new Position(3, 3), "3");
        map1.put(new Position(4, 2), "4");
        String s = map1.get(a);
        System.out.println(s);
    }
}


