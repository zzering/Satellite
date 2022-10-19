package com.zerin.service;

import com.zerin.model.TimeWindow;
import com.zerin.model.Position;

import java.io.*;
import java.util.*;

import static com.zerin.utils.CommonUtils.pointInPolygon;
import static com.zerin.utils.CommonUtils.toDate;
import static com.zerin.utils.Constant.*;

public class TimeCalculation {

    // 存放第一题的target city数据
    static ArrayList<LinkedHashMap<String, ArrayList<Position>>> targetInfo = new ArrayList<>();

    /**
     * 读第一题的target city数据
     */
    public static void readTarInfo() {
        LinkedHashMap<String, ArrayList<Position>> target = new LinkedHashMap<>();
        Position pos = null;
        ArrayList<Position> posInfo;
        InputStream path = null;
        File file = new File(CITY_DATA_PATH);
        if (file.exists()) {
            String fileName = file.getName();
            System.out.println("Reading file:" + fileName + "...  ");
            try {
                path = new FileInputStream(file.getAbsolutePath());
                Scanner scanner = new Scanner(path);
                for (int i = 0; i < CITY_NUM; i++) {// 24 cities
                    pos = new Position();
                    posInfo = new ArrayList<>();
                    String cityName = scanner.next();
                    double a = scanner.nextDouble();
                    double b = scanner.nextDouble();
                    if (a < 0) {
                        a += 360;// longitude can't be a negative number
                    }
                    pos.setLng(a);
                    pos.setLat(b);
                    posInfo.add(pos);
//                     System.out.println(cityName + " " + a + " " + b);
                    target.put(cityName, posInfo);
                }
                targetInfo.add(target);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("ReadFileException:Nonexistent path");
        }
    }

    /**
     * 计算卫星星座对每个点目标的可见时间窗口 对每个点目标的二重覆盖时间窗口
     * 计算卫星星座对每个点目标的覆盖时间间隙 统计每个点目标时间间隙的最大值和平均值
     * @param satInfo
     */
    public static LinkedHashMap<Integer, ArrayList<TimeWindow>> timeWindowCalculate(ArrayList<LinkedHashMap<Integer, ArrayList<Position>>> satInfo) {
        PrintStream defaultOut = System.out;// 保存系统默认的打印输出流缓存
        PrintStream ps = null;
        try {
            ps = new PrintStream(RESULT1_DATA_PATH);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.setOut(ps);// 输出流->文件
        LinkedHashMap<Integer, ArrayList<TimeWindow>> timeWindowsInfo =new LinkedHashMap<>();
        // 遍历第一题中的24城市
        for (Map<String, ArrayList<Position>> curTar : targetInfo) {
            int cityNo=0;// 城市序号
            // 对于一个具体的城市(24)
            for (Map.Entry<String, ArrayList<Position>> tarEntry : curTar.entrySet()) {
                ArrayList<TimeWindow> timeWindows = new ArrayList<>();// 记录每个target的时间窗口 用于计算二重覆盖
                ArrayList<Integer> tmpMaxGap = new ArrayList<>();// 每个点目标时间间隙的最大值
                ArrayList<Integer> allGaps = new ArrayList<>();// 每个点目标时间间隙
                System.out.println(tarEntry.getKey() + ":");// 城市名
                int satNo = 0;// 卫星序号
                // 遍历9卫星
                for (Map<Integer, ArrayList<Position>> curSat : satInfo) {
                    boolean flag1 = true;
                    boolean flag2 = true;
                    ArrayList<Integer> visiableTime = new ArrayList<>();// 时刻
                    ArrayList<Integer> unvisiableTime = new ArrayList<>();// 时刻
                    System.out.println("卫星" + satNo + "对该目标的可见时间窗口:");
                    // 对于一个具体的卫星(86400)
                    for (Map.Entry<Integer, ArrayList<Position>> satEntry : curSat.entrySet()) {
                        // 找可见窗口->可见时间窗口
                        if (pointInPolygon(tarEntry.getValue().get(0), satEntry.getValue())) {
                            if (flag1) { // 记录开始可见时刻
                                visiableTime.add(satEntry.getKey());// 时刻
                                System.out.print(toDate(satEntry.getKey()) + "\t");
                                flag1 = false;// 为了记录到停止服务的时刻
                            }
                        } else { // 不可见的时刻
                            if (!flag1) {
                                visiableTime.add(satEntry.getKey());
                                int startTime = visiableTime.get(0);
                                int endTime = visiableTime.get(1);
                                int serviceTime = endTime - startTime; // 时间间隔
                                System.out.println(toDate(satEntry.getKey()) + "\t" + serviceTime + "s");
                                timeWindows.add(new TimeWindow(satNo, startTime, endTime) {
                                });// 每个时间窗口都加入
                                visiableTime.clear();// !
                                flag1 = true;
                            }
                        }
                        // 找不可见窗口->覆盖时间间隙
                        ArrayList<Integer> gapTime = new ArrayList<>();// 时间间隙
                        if (!pointInPolygon(tarEntry.getValue().get(0), satEntry.getValue())) {
                            if (flag2) { // 记录开始不可见时刻
                                unvisiableTime.add(satEntry.getKey());// 时刻
                                flag2 = false;// 为了记录到停止服务的时刻
                            }
                        } else { // 可见的时刻
                            if (!flag2) {
                                unvisiableTime.add(satEntry.getKey());
                                int unServiceTime = unvisiableTime.get(1) - unvisiableTime.get(0); // 时间间隔
                                gapTime.add(unServiceTime);
                                unvisiableTime.clear();// !
                                flag2 = true;
                            }
                        }
                        if (!gapTime.isEmpty()) { // 存在时间间隙
                            // 保存某卫星对某目标的最大间隙数据
                            int tempMaxGapTime = Collections.max(gapTime);
                            tmpMaxGap.add(tempMaxGapTime);
                            // 保存间隙数据
                            allGaps.addAll(gapTime);
                        }
                    }
                    satNo++;
                }
                doubleTimeWindow(timeWindows);// 计算二重窗口
                gapTimeWindow(tmpMaxGap, allGaps);// 计算覆盖时间间隙
                timeWindowsInfo.put(cityNo,timeWindows);
                cityNo++;
            }
        }
        ps.close();
        System.setOut(defaultOut);// 输出流->系统
        System.out.println("done");
        return timeWindowsInfo;
    }

    /**
     * 二重覆盖时间窗口
     * @param timeWindows
     */
    public static void doubleTimeWindow(ArrayList<TimeWindow> timeWindows) {
        System.out.println("二重覆盖时间窗口: ");
        Iterator<TimeWindow> iterator1 = timeWindows.iterator();
        int k = 0;
        while (iterator1.hasNext()) {
            TimeWindow tmp1 = new TimeWindow();
            tmp1 = iterator1.next();
            TimeWindow tmp2 = new TimeWindow();
            int i = tmp1.getStartTime();
            int j = tmp1.getEndTime();
            k++;
            Iterator<TimeWindow> iterator2 = timeWindows.iterator();
            for (int skip = 0; skip < k; skip++) {
                iterator2.next();
            }
            while (iterator2.hasNext()) {
                tmp2 = iterator2.next();
                // 时间窗口1的头在窗口2之间
                if (tmp2.getStartTime() <= i && i < tmp2.getEndTime()) {
                    System.out.println("卫星" + tmp1.getSatNo() + "与卫星" + tmp2.getSatNo() + "：\t" +
                            toDate(i) + "\t" + toDate(tmp2.getEndTime()));
                }
                // 时间窗口1的尾在窗口2之间
                if (tmp2.getStartTime() <= j && j < tmp2.getEndTime()) {
                    System.out.println("卫星" + tmp1.getSatNo() + "与卫星" + tmp2.getSatNo() + "：\t" +
                            toDate(tmp2.getStartTime()) + "\t" + toDate(j));
                    continue;
                }
                // 时间窗口1包含窗口2
                if (i <= tmp2.getStartTime() && tmp2.getEndTime() <= j) {
                    System.out.println("卫星" + tmp1.getSatNo() + "与卫星" + tmp2.getSatNo() + "：\t" +
                            toDate(tmp2.getStartTime()) + "\t" + toDate(tmp2.getEndTime()));
                }
            }
        }
    }

    /**
     * 覆盖时间间隙
     * @param tmpMaxGap
     * @param allGaps
     */
    public static void gapTimeWindow(ArrayList<Integer> tmpMaxGap, ArrayList<Integer> allGaps) {
        int maxGap = 0;
        if (tmpMaxGap.size() != 0) {
            maxGap = Collections.max(tmpMaxGap);
        }
        int sumGap = allGaps.stream().mapToInt(Integer::intValue).sum();
        System.out.println("星座对该目标的覆盖时间间隙:\t\t\t" + sumGap + "s");
        System.out.println("星座对点目标时间间隙的最大值:\t\t" + maxGap + "s");
        if (sumGap != 0 && allGaps.size() != 0) {
            System.out.println("星座对点目标时间间隙的平均值:\t\t" + sumGap / allGaps.size() + "s\n");
        }
    }

}

