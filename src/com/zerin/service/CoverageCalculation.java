package com.zerin.service;

import com.zerin.model.*;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;
import java.util.Map.Entry;

import static com.zerin.utils.CommonUtils.calculateArea;
import static com.zerin.utils.CommonUtils.pointInCircle;
import static com.zerin.utils.Constant.*;

public class CoverageCalculation {
    // 每秒的覆盖率
    static ArrayList<LinkedHashMap<String, ArrayList<Position>>> coverageInfo = new ArrayList<>();
    /**
     * to store the unsure blocks
     */
    static ArrayList<Position> unsureKey = new ArrayList<>();
    /**
     * 用于存储不确定的网格面积之和
     */
    private static double curDefinedArea;
    /**
     * 网格区域总面积 34818362
     */
    private static double totalArea;

    // todo:探索合适的划分方法

    static {
        // 经度区间为75°E-135°E，纬度区间为 0°N-55°N
        totalArea = calculateArea(new Position(75, 0), new Position(135, 55));
    }


    /**
     * 将75°E-135°E  0°N-55°N划分为 12*11边长为5°的网格
     *
     * @param curCoverageInfo
     */
    public static void blockDivision(HashMap<Position, BlockStatus> curCoverageInfo) {
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 22; j++) {
                curCoverageInfo.put(new Position(75 + 2.5 * i, 2.5 * j), new BlockStatus(2.5, UNSURE));
            }
        }
//        for (int i = 0; i < 48; i++) {
//            for (int j = 0; j < 44; j++) {
//                curCoverageInfo.put(new Position(75 + 1.25 * i, 1.25 * j), new BlockStatus(1.25, UNSURE));
//            }
//        }
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

        // 计算每一秒里9个卫星对网格的覆盖情况
        for (int i = 0; i < 86400; i++) {
            // 记录当前秒的覆盖情况
            HashMap<Position, BlockStatus> curCoverageInfo = new HashMap<>();
            // 先全初始化为UNSURE
            blockDivision(curCoverageInfo);
            int satNo = 0;
            curDefinedArea = 0;// 对于每秒的已确定面积都要归零
            HashMap<Position, BlockStatus> reDivideInfo = new HashMap<>();
            // 计算这一秒里9个卫星对网格的覆盖情况
            for (Map<Integer, Circle> curSat : circleSatInfo) {
                // curSat.get(i)用i控制curSat的秒数,得到秒数对应的圆
                // 若网格在某个卫星的圆内 直接break
                satNo++;
                // 情况已经确定（覆盖或未覆盖）
                blockInCircle(curCoverageInfo, curSat.get(i));
//                if (res == 4 || res == 0) {
//                    break;
//                }
            }
            while (curDefinedArea / totalArea <= 0.999) {
                // redivide
                reDivideInfo.putAll(reDivision(curCoverageInfo));
                for (Map<Integer, Circle> curSat : circleSatInfo) {
                    // curSat.get(i)用i控制curSat的秒数,得到秒数对应的圆
                    // 若网格在某个卫星的圆内 直接break
                    satNo++;
                    // 情况已经确定（覆盖或未覆盖）
                    blockInCircle(reDivideInfo, curSat.get(i));
//                    if (res == 4 || res == 0) {
//                        break;
//                    }
                }
            }
            // 合并两个map
            curCoverageInfo.putAll(reDivideInfo);
            reDivideInfo.clear();
            Coverage coverage = new Coverage(i, curDefinedArea / totalArea, curCoverageInfo);
        }
        ps.close();
        System.setOut(defaultOut);// 输出流->系统
        System.out.println("done");
        return coverageInfo;
    }

    /**
     * 判断block与某卫星某秒的相交情况
     *
     * @param
     * @param cSatEntry
     */
    public static void blockInCircle(HashMap<Position, BlockStatus> curCoverageInfo, Circle cSatEntry) {
        if (curCoverageInfo.isEmpty()) {
            System.out.println("curCoverageInfo is empty");
        }
        int testcount=0;
        int testcount2=0;
        // 遍历curCoverageInfo,判断其中的网格与圆的关系
        // todo 问题在于迭代器一边遍历边添加吗？ConcurrentModificationException
        for (Entry<Position, BlockStatus> curBlock : curCoverageInfo.entrySet()) {
            double x1 = curBlock.getKey().getLng();
            double y1 = curBlock.getKey().getLat();
            double edge = curBlock.getValue().getEdgeLen()/2;
            Position pos1 = new Position(x1, y1);// 左上
            Position pos2 = new Position(x1 + edge, y1);// 右上
            Position pos3 = new Position(x1, y1 + edge);// 左下
            Position pos4 = new Position(x1 + edge, y1 + edge);// 右下
            int count = 0;
            count += getCount(curCoverageInfo, cSatEntry, edge, pos1);
            count += getCount(curCoverageInfo, cSatEntry, edge, pos2);
            count += getCount(curCoverageInfo, cSatEntry, edge, pos3);
            count += getCount(curCoverageInfo, cSatEntry, edge, pos4);
            if (count == 2 || count == 3) {
                // 存放不确定的block的左上角坐标 这些坐标在后面会进行二次判断
                unsureKey.add(new Position(pos1));
                testcount++;
                System.out.println("unsure is "+testcount);
            } else {
                // 计算确定的block的面积
                curDefinedArea += calculateArea(pos1, pos4);
                testcount2++;
                System.out.println("sure is "+testcount2);
            }
        }
    }

    private static int getCount(HashMap<Position, BlockStatus> curCoverageInfo, Circle cSatEntry, double edgeLen, Position pos) {
        // 进行判断之前要先get curCoverageInfo中是否有过BlockStatus的信息
        try {
            int coverStatus = curCoverageInfo.get(new Position(pos)).getCoverStatus();
            // 如果原BlockStatus中是OUTSIDE的,可以转为INSIDE
            // 如果原BlockStatus中是INSIDE的,不可以转为OUTSIDE
            if (pointInCircle(pos, cSatEntry) && coverStatus == OUTSIDE) {
                curCoverageInfo.put(new Position(pos), new BlockStatus(edgeLen, INSIDE));
                return 1;
            }
        } catch (NullPointerException e) {
            // 如果curCoverageInfo未曾有过BlockStatus的信息 则可以将其置为INSIDE或OUTSIDE
            if (pointInCircle(pos, cSatEntry)) {
                curCoverageInfo.put(new Position(pos), new BlockStatus(edgeLen, INSIDE));
                return 1;
            } else {
                curCoverageInfo.put(new Position(pos), new BlockStatus(edgeLen, OUTSIDE));
            }
        }
        // 若为OUTSIDE
        return 0;
    }

    /**
     * 对不确定的block进行再划分
     */
    public static HashMap<Position, BlockStatus> reDivision(HashMap<Position, BlockStatus> curCoverageInfo) {
        if (curCoverageInfo.isEmpty()) {
            System.out.println("curCoverageInfo is empty");
        }
        HashMap<Position, BlockStatus> reDivideInfo = new HashMap<>();
        // 将新划分的放到一个新hash中
        for (Position pos : unsureKey) {
            double edgeLen = curCoverageInfo.get(new Position(pos.getLng(), pos.getLat())).getEdgeLen() / 2;
            // 向curCoverageInfo中覆盖或添加划分后的信息 左上角的网格是覆盖 其他三个网格是新增
            // 左上角的coverStatus是确定的 因为前面就是以它确定的一个网格，作为左上角这一个点，它一定经过了判断
            reDivideInfo.put(new Position(pos), curCoverageInfo.getOrDefault(new Position(pos.getLng(), pos.getLat()), new BlockStatus(edgeLen, UNSURE)));
            // 新建的网格一定是不确定的(未经判断)
            reDivideInfo.put(new Position(pos.getLng() + edgeLen, pos.getLat()), new BlockStatus(edgeLen, UNSURE));
            reDivideInfo.put(new Position(pos.getLng(), pos.getLat() + edgeLen), new BlockStatus(edgeLen, UNSURE));
            reDivideInfo.put(new Position(pos.getLng() + edgeLen, pos.getLat() + edgeLen), new BlockStatus(edgeLen, UNSURE));
        }
        // 已经全部添加
        unsureKey.clear();
        return reDivideInfo;
    }
}


//        for (Map<Integer, Circle> curSat : circleSatInfo) {// 9sat
//            Iterator<Entry<Integer, ArrayList<Block>>> iterator = allBlocks.entrySet().iterator();
//            int secondNo = 0, moment = 0;
//            for (Entry<Integer, Circle> cSatEntry : curSat.entrySet()) {// 秒数86400
//
//                HashMap<Block, Integer> curCoverageInfo = new HashMap<>();
//
//                Entry<Integer, ArrayList<Block>> blocksEntry;
//                if (iterator.hasNext()) {
//                    blocksEntry = iterator.next();
//                } else {
//                    System.out.println("LinkedHashMap<Integer, ArrayList<Block>> allBlocks iterate error");
//                    return null;
//                }
//                // 将网格一分为四，然后判断新的网格是否会被卫星覆盖，直到不确定的网格面积之和与总面积之比小于 0.1%停止
//                double totalArea = 99, unsureArea = 0;
//                HashMap<Double, Integer> unsureBlocksLng = new HashMap<>();// 不能确定的表格坐标(只记录经度即可)
//                ArrayList<Block> unsureBlocks = new ArrayList<>();// 存放因不能确定而新分割的表格
//
//                for (Block curBlock : blocksEntry.getValue()) {// 得到每秒的网格情况
//                    // 四点全在圆内 inside 3 unsure 2 outside 1 init 0
//                    blockInCircle(curBlock, cSatEntry.getValue());
//
//                    if (unsureArea / totalArea <= 0.001) {
//                        //break;
//                    }
//                    // 通过curBlock.getStatue() == ?的状态判断是否进行划分
//                    // 当block状态不确定时
////                    if (curBlock.getStatue() == 2) {
////                        reDivision(curBlock, unsureBlocksLng,unsureBlocks);
////                    }
//                }
//                // 修改数组中对应网格的边长;加入新网格中存放的分化的那些表格
//
//                secondNo++;
//                moment++;
//            }
//        }


// 遍历curCoverageInfo,判断其中的网格与圆的关系
//        for (Entry<Block, Integer> curBlock : curCoverageInfo.entrySet()) {
//            double x1 = curBlock.getKey().getLng();
//            double y1 = curBlock.getKey().getLat();
//            double edge = curBlock.getKey().getEdgelen();
//            Position pos1 = new Position(x1, y1);// 左上
//            Position pos2 = new Position(x1 + edge, y1);// 右上
//            Position pos3 = new Position(x1, y1 + edge);// 左下
//            Position pos4 = new Position(x1 + edge, y1 + edge);// 右下
//        }


//        double x1 = curBlock.getLng();
//        double y1 = curBlock.getLat();
//        double edge = curBlock.getEdgelen() / 2;
//        // hashMap的put方法既是修改也是添加
//        if (unsureBlocksLng.containsKey(x1)) {// 之前划分过 则+1 代表增加了一次划分次数
//            unsureBlocksLng.put(x1, unsureBlocksLng.get(x1) + 1);
//        } else {
//            unsureBlocksLng.put(x1, 1);
//        }
//        curBlock.setEdgelen(edge);
//        blocks.add(new Block(x1 + edge, y1, edge));
//        blocks.add(new Block(x1, y1 + edge, edge));
//        blocks.add(new Block(x1 + edge, y1 + edge, edge));

// :refactor HashMap<Block, Integer> curCoverageInfo->HashMap<Position, <Integer,Integer>> curBlockInfo
//        for(Entry<Position,BlockStatus> curBlock:curCoverageInfo.entrySet()){
