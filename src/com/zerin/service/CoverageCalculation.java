package com.zerin.service;

import com.zerin.model.Block;
import com.zerin.model.Circle;
import com.zerin.model.Coverage;
import com.zerin.model.Position;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;
import java.util.Map.Entry;

import static com.zerin.utils.CommonUtils.pointInCircle;
import static com.zerin.utils.Constant.*;

public class CoverageCalculation {
    // 每秒的覆盖率
    static ArrayList<LinkedHashMap<String, ArrayList<Position>>> coverageInfo = new ArrayList<>();

    /**
     * 将75°E-135°E  0°N-55°N划分为 12*11边长为5°的网格
     *
     * @param curCoverageInfo
     */
    public static void blockDivision(HashMap<Block, Integer> curCoverageInfo) {
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 11; j++) {
                curCoverageInfo.put(new Block(75 + 5 * i, 5 * j, 5), UNSURE);
            }
        }
    }

    /**
     * 计算每个时刻的瞬时覆盖率
     *
     * @param circleSatInfo 转化成圆之后的卫星数据 ArrayList<>:satNo Integer:moment
     * @return
     */
    public static ArrayList<LinkedHashMap<String, ArrayList<Position>>> coverageCalculate(ArrayList<LinkedHashMap<Integer, Circle>> circleSatInfo) {
        PrintStream defaultOut = System.out;// 保存系统默认的打印输出流缓存
        PrintStream ps = null;
        try {
            ps = new PrintStream(RESULT2_DATA_PATH);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.setOut(ps);// 输出流->文件

        LinkedHashMap<Integer, ArrayList<Block>> allBlocks = new LinkedHashMap<>();


        double coverageRate = 0.00;
        // 计算每一秒里9个卫星对网格的覆盖情况
        for (int i = 0; i < 86400; i++) {
            // 记录当前秒的覆盖情况
            HashMap<Block, Integer> curCoverageInfo = new HashMap<>();
            // 先全初始化为UNSURE
            blockDivision(curCoverageInfo);
            for (Map<Integer, Circle> curSat : circleSatInfo) {
                // curSat.get(i)用i控制curSat的秒数,得到秒数对应的圆
                blockInCircle(curCoverageInfo, curSat.get(i));

            }
            Coverage coverage = new Coverage(i, coverageRate, curCoverageInfo);
        }
        for (Map<Integer, Circle> curSat : circleSatInfo) {// 9sat
            Iterator<Entry<Integer, ArrayList<Block>>> iterator = allBlocks.entrySet().iterator();
            int secondNo = 0, moment = 0;
            for (Entry<Integer, Circle> cSatEntry : curSat.entrySet()) {// 秒数86400

                HashMap<Block, Integer> curCoverageInfo = new HashMap<>();

                Entry<Integer, ArrayList<Block>> blocksEntry;
                if (iterator.hasNext()) {
                    blocksEntry = iterator.next();
                } else {
                    System.out.println("LinkedHashMap<Integer, ArrayList<Block>> allBlocks iterate error");
                    return null;
                }
                // 将网格一分为四，然后判断新的网格是否会被卫星覆盖，直到不确定的网格面积之和与总面积之比小于 0.1%停止
                double totalArea = 99, unsureArea = 0;
                HashMap<Double, Integer> unsureBlocksLng = new HashMap<>();// 不能确定的表格坐标(只记录经度即可)
                ArrayList<Block> unsureBlocks = new ArrayList<>();// 存放因不能确定而新分割的表格

                for (Block curBlock : blocksEntry.getValue()) {// 得到每秒的网格情况
                    // 四点全在圆内 inside 3 unsure 2 outside 1 init 0
                    blockInCircle(curBlock, cSatEntry.getValue());

                    if (unsureArea / totalArea <= 0.001) {
                        //break;
                    }
                    // 通过curBlock.getStatue() == ?的状态判断是否进行划分
                    // 当block状态不确定时
//                    if (curBlock.getStatue() == 2) {
//                        reDivision(curBlock, unsureBlocksLng,unsureBlocks);
//                    }
                }
                // 修改数组中对应网格的边长;加入新网格中存放的分化的那些表格

                secondNo++;
                moment++;
            }
        }

        ps.close();
        System.setOut(defaultOut);// 输出流->系统
        System.out.println("done");
        return coverageInfo;
    }

    /**
     * 判断block与某卫星某秒的相交情况
     *
     * @param curBlock
     * @param cSatEntry
     */
    public static boolean blockInCircle(HashMap<Block, Integer> curCoverageInfo, Circle cSatEntry) {
        if(curCoverageInfo.isEmpty()){
            System.out.println("curCoverageInfo is empty");
            return false;
        }
        // 遍历curCoverageInfo,判断其中的网格与圆的关系
        for (Entry<Block, Integer> curBlock : curCoverageInfo.entrySet()) {
            double x1 = curBlock.getKey().getLng();
            double y1 = curBlock.getKey().getLat();
            double edge = curBlock.getKey().getEdgelen();
            Position pos1 = new Position(x1, y1);// 左上
            Position pos2 = new Position(x1 + edge, y1);// 右上
            Position pos3 = new Position(x1, y1 + edge);// 左下
            Position pos4 = new Position(x1 + edge, y1 + edge);// 右下
            int count = 0;
            count += getCount(curCoverageInfo, cSatEntry, edge, pos1, count);
            count += getCount(curCoverageInfo, cSatEntry, edge, pos2, count);
            count += getCount(curCoverageInfo, cSatEntry, edge, pos3, count);
            count += getCount(curCoverageInfo, cSatEntry, edge, pos4, count);
            // 四点全在圆内
            if (count == 4) {
                return true;
            }
            // 至少有一点不在圆内
            return false;
        }
    }

    private static int getCount(HashMap<Block, Integer> curCoverageInfo, Circle cSatEntry, double edge, Position pos, int count) {
        if (pointInCircle(pos, cSatEntry)) {
            curCoverageInfo.put(new Block(pos, edge), INSIDE);
            count++;
        } else {
            curCoverageInfo.put(new Block(pos, edge), OUTSIDE);
        }
        return count;
    }

    /**
     * 对不确定的block进行再划分
     *
     * @param curBlock
     * @param blocks
     */
    public static void reDivision(Block curBlock, HashMap<Double, Integer> unsureBlocksLng, ArrayList<Block> blocks) {
        double x1 = curBlock.getLng();
        double y1 = curBlock.getLat();
        double edge = curBlock.getEdgelen() / 2;
        // hashMap的put方法既是修改也是添加
        if (unsureBlocksLng.containsKey(x1)) {// 之前划分过 则+1 代表增加了一次划分次数
            unsureBlocksLng.put(x1, unsureBlocksLng.get(x1) + 1);
        } else {
            unsureBlocksLng.put(x1, 1);
        }
        curBlock.setEdgelen(edge);
        blocks.add(new Block(x1 + edge, y1, edge));
        blocks.add(new Block(x1, y1 + edge, edge));
        blocks.add(new Block(x1 + edge, y1 + edge, edge));
    }
}

// todo:建一个哈希表，把所有划分情况在哈希表里面更新！ 可以实现对每一个坐标进行标记，这样UNSURE情况里已经确定的节点就不必重复计算
