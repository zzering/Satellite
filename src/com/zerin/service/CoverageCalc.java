package com.zerin.service;

import com.zerin.model.Block;
import com.zerin.model.Circle;
import com.zerin.model.Position;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.zerin.utils.CommonUtils.*;
import static com.zerin.utils.Constant.RESULT2_DATA_PATH;

public class CoverageCalc {

    static ArrayList<Block> blocks = new ArrayList<>();

    /**
     * 将75°E-135°E  0°N-55°N划分为 12*11边长为5°的网格
     */
    public static void blockDivision() {
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 11; j++) {
                //inside 3 unsure 2 outside 1 init 0
                blocks.add(new Block(75 + 5 * i,5 * j,5,0));
            }
        }
    }

    public static ArrayList<LinkedHashMap<Integer, ArrayList<Block>>> coverage(ArrayList<LinkedHashMap<Integer, Circle>> cSatInfo) {
        PrintStream defaultOut = System.out;//保存系统默认的打印输出流缓存
        PrintStream ps = null;
        try {
            ps = new PrintStream(RESULT2_DATA_PATH);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.setOut(ps);//输出流->文件

        blockDivision();//初始化网格
        ArrayList<LinkedHashMap<Integer, ArrayList<Block>>> coverageInfo = new ArrayList<>();//每秒的瞬时覆盖率数据

        //for every satellites and every seconds
        for (Map<Integer, Circle> curSat : cSatInfo) {
            for (Map.Entry<Integer, Circle> cSatEntry : curSat.entrySet()) {//86400
                //iterate over the blocks
                for (Block curBlock : blocks) {
                    //当block仅初始化时||block状态不确定时
                    if (curBlock.getStatue() == 0||curBlock.getStatue() ==2) {
                        rectangleInCircle(curBlock, cSatEntry.getValue());
                    }

                }
            }
        }


        ps.close();
        System.setOut(defaultOut);//输出流->系统
        System.out.println("done");
        return coverageInfo;
    }

    /**
     * 判断block与某卫星某秒的相交情况
     * @param curBlock
     * @param cSatEntry
     */
    public static void rectangleInCircle(Block curBlock, Circle cSatEntry) {
        double x1 = curBlock.getX();
        double y1 = curBlock.getY();
        double edge = curBlock.getEdgelen();
        Position pos1 = new Position(x1, y1);//左上
        Position pos2 = new Position(x1 + edge, y1);//右上
        Position pos3 = new Position(x1, y1 + edge);//左下
        Position pos4 = new Position(x1 + edge, y1 + edge);//右下
        if (pointInCircle(pos1, cSatEntry)) {
            if (pointInCircle(pos2, cSatEntry)) {
                if (pointInCircle(pos3, cSatEntry) || pointInCircle(pos4, cSatEntry)) {
                    curBlock.setStatue(3);//四点全在圆内 inside 3 unsure 2 outside 1 init 0
                }
            } else curBlock.setStatue(2);//至少一点不在
        } else curBlock.setStatue(1);//四点全不在
    }
}
