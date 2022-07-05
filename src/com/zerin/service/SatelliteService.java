package com.zerin.service;

import com.zerin.model.Position;
import com.zerin.model.TimeWindow;

import java.io.*;
import java.util.*;

public class SatelliteService {
    static LinkedHashMap<Integer, ArrayList<Position>> sat = null;
    static ArrayList<LinkedHashMap<Integer, ArrayList<Position>>> satInfo = new ArrayList<>();
    //第一题的target city数据
    static LinkedHashMap<String, ArrayList<Position>> target = new LinkedHashMap<>();
    static ArrayList<LinkedHashMap<String, ArrayList<Position>>> targetInfo = new ArrayList<>();

    //读取原始卫星数据
    public static void readSatInfo() {
        Position pos = null;
        ArrayList<Position> posInfo = null;
        InputStream path = null;
        File file = new File("Data/SatelliteInfo");
        if (file.exists()) {
            File[] files = file.listFiles();
            assert files != null;
            if (files.length == 0) {
                System.out.println("ReadFileException:Empty file");
                return;
            } else {
                for (File iFile : files) {
                    String fileName = iFile.getName();
                    if (fileName.equals("SatCoverInfo_2.txt")) {//4 doubletimewin
                        break;
                    }
                    System.out.println("Reading file:" + fileName + "...  ");
                    try {
                        sat = new LinkedHashMap<>();
                        path = new FileInputStream(iFile.getAbsolutePath());
                        Scanner scanner = new Scanner(path);
                        scanner.nextLine();//除去第一行的日期
                        for (int i = 0; i < 86400; i++) {
                            posInfo = new ArrayList<>();//!
                            for (int j = 0; j < 21; j++) {
                                pos = new Position();
                                double a = scanner.nextDouble();
                                double b = scanner.nextDouble();
                                pos.setLongitude(a);
                                pos.setLatitude(b);
                                posInfo.add(pos);
//                                System.out.println(a+" "+b);
                            }
                            scanner.nextLine();//除去" "
                            scanner.nextLine();//不读取日期
                            sat.put(i, posInfo);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    satInfo.add(sat);
                }
            }
        } else {
            System.out.println("ReadFileException:Nonexistent path");
        }
    }

    //读第一题的target city数据
    public static void readTarInfo() {
        Position pos = null;
        ArrayList<Position> posInfo;
        InputStream path = null;
        File file = new File("Data/TargetInfo/target.txt");
        if (file.exists()) {
            String fileName = file.getName();
            System.out.println("Reading file:" + fileName + "...  ");
            try {
                path = new FileInputStream(file.getAbsolutePath());
                Scanner scanner = new Scanner(path);
                for (int i = 0; i < 24; i++) {//24 cities
                    pos = new Position();
                    posInfo = new ArrayList<>();
                    String cityName = scanner.next();
                    double a = scanner.nextDouble();
                    double b = scanner.nextDouble();
                    if (a < 0) {
                        a += 360;//longitude can't be a negative number
                    }
                    pos.setLongitude(a);
                    pos.setLatitude(b);
                    posInfo.add(pos);
//                    System.out.println(cityName + " " + a + " " + b);
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

    //秒数转日期
    public static StringBuilder toDate(int time) {
        if (time == 86400) {
            StringBuilder lastDate = new StringBuilder();
            lastDate.append("2022-01-02 0:00:00");
            return lastDate;
        }
        StringBuilder date = new StringBuilder();
        date.append("2022-01-01 ");
        int hour = time / 3600 % 24, minute = time / 60 % 60, second = time % 60;
        date.append(hour).append(":");
        if (minute < 10) date.append("0").append(minute).append(":");
        else date.append(minute).append(":");
        if (second < 10) date.append("0").append(second);
        else date.append(second);
        return date;
    }

    //从需要判断的点向x轴负方向引一条射线，判断多边形的每一条边与这条射线是否有交点
    public static boolean pointInPolygon(Position pos, ArrayList<Position> polyVertices) {
        int i, j = polyVertices.size() - 1;
        boolean inside = false;
        double x = pos.getLongitude();
        double y = pos.getLatitude();
        Position a = new Position();
        Position b = new Position();
        for (i = 0; i < polyVertices.size(); i++) {
            a = polyVertices.get(i);
            b = polyVertices.get(j);
            if (((a.getLatitude() < y && b.getLatitude() >= y)
                    || (b.getLatitude() < y && a.getLatitude() >= y))//保证射线在多边形这条边的y值范围内
                    && (a.getLongitude() <= x || b.getLongitude() <= x)) {//除去 需判断的点在边的左边的情况
                //射线与边交点的x坐标
                double abx = a.getLongitude() + (y - a.getLatitude()) / (b.getLatitude() - a.getLatitude()) * (b.getLongitude() - a.getLongitude());
                boolean bTmp;
                //点在多边形的边上，也算在多边形内
                if (abx == x) {
                    return true;
                } else {
                    bTmp = (abx < x);
                }
                inside ^= bTmp;
            }
            j = i;
        }
        return inside;
    }

    //计算卫星星座对每个点目标的可见时间窗口 对每个点目标的二重覆盖时间窗口
    //计算卫星星座对每个点目标的覆盖时间间隙 统计每个点目标时间间隙的最大值和平均值
    public static void timeWindow() {
        PrintStream defaultOut = System.out;//保存系统默认的打印输出流缓存
        PrintStream ps = null;
        try {
            ps = new PrintStream("Result/timeWindow.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.setOut(ps);//输出流->文件
        //遍历第一题中的24城市
        for (Map<String, ArrayList<Position>> curTar : targetInfo) {
            ArrayList<TimeWindow> allTimeWindows = new ArrayList<>();//可见时间窗口
            ArrayList<Integer> tmpMaxGap = new ArrayList<>();//每个点目标时间间隙的最大值
            ArrayList<Integer> allGaps = new ArrayList<>();//每个点目标时间间隙
            //对于一个具体的城市(24)
            for (Map.Entry<String, ArrayList<Position>> tarEntry : curTar.entrySet()) {
                System.out.println(tarEntry.getKey() + ":");//城市名
                if (tarEntry.getKey().equals("Abidjan")) continue;
                if (tarEntry.getKey().equals("Accra")) continue;
                TimeWindow tmpTimeWindow = null;
                int satNo = 0;
                //遍历9卫星
                for (Map<Integer, ArrayList<Position>> curSat : satInfo) {
                    boolean flag1 = true;
                    boolean flag2 = true;
                    ArrayList<Integer> visiableTime = new ArrayList<>();//时刻
                    ArrayList<Integer> tmp = new ArrayList<>();//时刻

                    ArrayList<Integer> unvisiableTime = new ArrayList<>();//时刻
                    ArrayList<ArrayList<Integer>> timeWindow = new ArrayList<>();//时间窗口(两个时刻)
                    System.out.println("卫星" + satNo + "对该目标的可见时间窗口:");
                    //对于一个具体的卫星(86400)
                    for (Map.Entry<Integer, ArrayList<Position>> satEntry : curSat.entrySet()) {
                        //找可见窗口->可见时间窗口
                        if (pointInPolygon(tarEntry.getValue().get(0), satEntry.getValue())) {
                            if (flag1) { //记录开始可见时刻
                                visiableTime.add(satEntry.getKey());//时刻
                                tmp.add(satEntry.getKey());
                                System.out.print(toDate(satEntry.getKey()) + "\t");
                                flag1 = false;//为了记录到停止服务的时刻
                            }
                        } else { //不可见的时刻
                            if (!flag1) {
                                visiableTime.add(satEntry.getKey());
                                tmp.add(satEntry.getKey());
                                int serviceTime = visiableTime.get(1) - visiableTime.get(0); //时间间隔
                                System.out.println(toDate(satEntry.getKey()) + "\t" + serviceTime + "s");
                                timeWindow.add(tmp);
                                visiableTime.clear();//!
                                flag1 = true;
                            }
                        }
//                        if (!gapTime.isEmpty()) { //存在时间间隙
//                            //保存时间窗口和对应的卫星序号
////                            tmpTimeWindow = new TimeWindow();
////                            tmpTimeWindow.setSatNo(satNo);
////                            tmpTimeWindow.setWindow(timeWindow);
////                            allTimeWindows.add(tmpTimeWindow);
//                            //保存某卫星对某目标的最大间隙数据
//                            int tempMaxGapTime = Collections.max(gapTime);
//                            tmpMaxGap.add(tempMaxGapTime);
//                            //保存间隙数据
//                            //保存间隙数据
//                            allGaps.addAll(gapTime);
//                        }

                        //找不可见窗口->覆盖时间间隙
                        ArrayList<Integer> gapTime = new ArrayList<>();//时间间隙
                        if (!pointInPolygon(tarEntry.getValue().get(0), satEntry.getValue())) {
                            if (flag2) { //记录开始不可见时刻
                                unvisiableTime.add(satEntry.getKey());//时刻
                                flag2 = false;//为了记录到停止服务的时刻
                            }
                        } else { //可见的时刻
                            if (!flag2) {
                                unvisiableTime.add(satEntry.getKey());
                                int unServiceTime = unvisiableTime.get(1) - unvisiableTime.get(0); //时间间隔
//                                System.out.print(" "+unServiceTime+ " ");
                                gapTime.add(unServiceTime);
                                unvisiableTime.clear();//!
                                flag2 = true;
                            }
                        }
                        if (!gapTime.isEmpty()) { //存在时间间隙
                            //保存某卫星对某目标的最大间隙数据
                            int tempMaxGapTime = Collections.max(gapTime);
                            tmpMaxGap.add(tempMaxGapTime);
                            //保存间隙数据
                            allGaps.addAll(gapTime);
                        }
                    }
                    satNo++;
                }
//                System.out.println("二重覆盖时间窗口：");
//                ArrayList<ArrayList<Integer>> allDoubleTimeWindow = new ArrayList<>();
//
//                ArrayList<ArrayList<Integer>> doubleTimeWindow = new ArrayList<>();
//                Iterator<TimeWindow> iterator1 = allTimeWindows.iterator();
//                Iterator<TimeWindow> iterator2 = allTimeWindows.iterator();
//                ArrayList<ArrayList<Integer>> tmp1 =null;
//                ArrayList<ArrayList<Integer>> tmp2 =null;
//                        iterator2.next();
//                while (iterator1.hasNext() && iterator2.hasNext()) {
//                    tmp1=new ArrayList<>();
//                    tmp2=new ArrayList<>();
//                    tmp1 = iterator1.next().getWindow();
//                    tmp2 = iterator2.next().getWindow();
//                    ArrayList<Integer> tmp=null;
//                    int k = 0,n=0;
//                    int length1 = tmp1.size();
//                    int length2 = tmp2.size();
//                    if (length1 == 0 || length2 == 0) break;
//                    int i = 0, j = 0;
//                    while (i < length1 && j < length2) {
//                        Integer start = Math.max(tmp1.get(i).get(0), tmp2.get(j).get(0));//时间窗口开始时刻
//                        Integer end = Math.max(tmp1.get(i).get(1), tmp2.get(j).get(1));
//                        if (start <= end) {
//                            tmp = new ArrayList<>();
//                            tmp.set(k, start);
//                            k++;
//                            tmp.set(k, end);
//                            k++;
//                            doubleTimeWindow.set(n, tmp);
//                            n++;
//                        }
//                        if (tmp1.get(i).get(1) < tmp2.get(j).get(1)) {
//                            i++;
//                        } else {
//                            j++;
//                        }
//                    }
//                    if(!doubleTimeWindow.isEmpty()){
//                        System.out.print("卫星"+iterator1.next().getSatNo()+"和卫星"+iterator2.next().getSatNo()+":");
//                        for(var it:doubleTimeWindow){
//                            System.out.println(toDate(it.get(0))+"\t"+toDate(it.get(1)));
//                        }
//                    }else {
//                        System.out.println("无");
//                    }
//                }

                int maxGap = 0;
                if (tmpMaxGap.size() != 0) {
                    maxGap = Collections.max(tmpMaxGap);
                }
                int sumGap = allGaps.stream().mapToInt(Integer::intValue).sum();
                System.out.println("星座对该目标的覆盖时间间隙: " + sumGap + "s");
                System.out.println("星座对点目标时间间隙的最大值: " + maxGap + "s");
                if (sumGap != 0 && allGaps.size() != 0) {
                    System.out.println("星座对点目标时间间隙的平均值: " + sumGap / allGaps.size() + "s\n");
                }
                tmpMaxGap.clear();
                allGaps.clear();
            }
        }
        ps.close();
        System.setOut(defaultOut);//输出流->系统
        System.out.println("done");
    }

    public static void doubleTimeWindow() {

    }
}
