package com.zerin.test;

import com.zerin.model.BlockStatus;
import com.zerin.model.Position;
import org.junit.Test;

import java.util.*;

import static com.zerin.service.CoverageCalculation.blockDivision;
import static com.zerin.utils.CommonUtils.calculateArea;

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

        double totalArea = calculateArea(new Position(75, 0), new Position(135, 55));
        System.out.println(totalArea);

        // 记录当前秒的覆盖情况
        HashMap<Position, BlockStatus> curCoverageInfo = new HashMap<>();
        // 先全初始化为UNSURE
        blockDivision(curCoverageInfo);
        double area=0;
        for (Map.Entry<Position, BlockStatus> curBlock : curCoverageInfo.entrySet()) {
            double x1 = curBlock.getKey().getLng();
            double y1 = curBlock.getKey().getLat();
            double edge = curBlock.getValue().getEdgeLen();
            Position pos1 = new Position(x1, y1);// 左上
            Position pos4 = new Position(x1 + edge, y1 + edge);// 右下
            area+=calculateArea(pos1,pos4);
        }
        System.out.println(totalArea);
        System.out.println(area);

        }
}


