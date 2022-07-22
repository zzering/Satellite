package com.zerin.utils;

import com.zerin.model.Circle;
import com.zerin.model.Position;

import java.io.*;
import java.util.*;

import static com.zerin.utils.Constant.*;

public class CommonUtils {

    /**
     * 读取原始卫星数据
     * 无缓冲的读取 用时110s左右
     * @param satInfo
     */
    public static void readSatInfo(ArrayList<LinkedHashMap<Integer, ArrayList<Position>>> satInfo) {
        LinkedHashMap<Integer, ArrayList<Position>> sat = null;
        Position pos = null;
        ArrayList<Position> posInfo = null;
        InputStream path = null;
        File file = new File(SAT_DATA_PATH);
        if (file.exists()) {
            System.out.println("正在读取原始卫星数据...");
            File[] files = file.listFiles();
            assert files != null;
            if (files.length == 0) {
                System.out.println("ReadFileException:Empty file");
                return;
            } else {
                for (File iFile : files) {
                    String fileName = iFile.getName();
                    System.out.println("Reading file:" + fileName + "...");
                    try {
                        sat = new LinkedHashMap<>();
                        path = new FileInputStream(iFile.getAbsolutePath());
                        Scanner scanner = new Scanner(path);
                        scanner.nextLine();//只除去第一行的日期
                        for (int i = 0; i < 86400; i++) {
                            posInfo = new ArrayList<>();//!
                            for (int j = 0; j < 21; j++) {
                                pos = new Position();
                                double a = scanner.nextDouble();
                                double b = scanner.nextDouble();
                                pos.setLng(a);
                                pos.setLat(b);
                                posInfo.add(pos);
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

    /**
     * 读取原始卫星数据
     * 带缓冲的读取 用时15s左右
     * @param satInfo
     */
    public static void bufferReadSatInfo(ArrayList<LinkedHashMap<Integer, ArrayList<Position>>> satInfo) {
        LinkedHashMap<Integer, ArrayList<Position>> sat = null;
        Position pos = null;
        ArrayList<Position> posInfo = null;
        InputStream path = null;
        File file = new File(SAT_DATA_PATH);
        if (file.exists()) {
            System.out.println("正在读取原始卫星数据...");
            File[] files = file.listFiles();
            assert files != null;
            if (files.length == 0) {
                System.out.println("ReadFileException:Empty file");
                return;
            } else {
                for (File iFile : files) {
                    String fileName = iFile.getName();
                    System.out.println("Reading file:" + fileName + "...");
                    try {
                        sat = new LinkedHashMap<>();
                        path = new FileInputStream(iFile.getAbsolutePath());
                        BufferedReader br = new BufferedReader(new InputStreamReader(path));
                        for (int i = 0; i < 86400; i++) {
                            br.readLine();//除去每个日期
                            posInfo = new ArrayList<>();//!
                            for (int j = 0; j < 21; j++) {
                                String str = null;
                                pos = new Position();
                                str = br.readLine();
                                StringTokenizer st = new StringTokenizer(str, "\t");//分割经纬度
                                //while(st.hasMoreElements()){}
                                double a = Double.parseDouble((String) st.nextElement());
                                double b = Double.parseDouble((String) st.nextElement());
                                pos.setLng(a);
                                pos.setLat(b);
                                posInfo.add(pos);
                            }
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

    /**
     * 秒数转日期
     * @param time
     * @return
     */
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

    /**
     * 从需要判断的点向x轴负方向引一条射线，判断多边形的每一条边与这条射线是否有交点
     * @param pos
     * @param polyVertices
     * @return
     */
    public static boolean pointInPolygon(Position pos, ArrayList<Position> polyVertices) {
        int i, j = polyVertices.size() - 1;
        boolean inside = false;
        double x = pos.getLng();
        double y = pos.getLat();
        Position a = new Position();
        Position b = new Position();
        for (i = 0; i < polyVertices.size(); i++) {
            a = polyVertices.get(i);
            b = polyVertices.get(j);
            if (((a.getLat() < y && b.getLat() >= y)
                    || (b.getLat() < y && a.getLat() >= y))//保证射线在多边形这条边的y值范围内
                    && (a.getLng() <= x || b.getLng() <= x)) {//除去 需判断的点在边的左边的情况
                //射线与边交点的x坐标
                double abx = a.getLng() + (y - a.getLat()) / (b.getLat() - a.getLat()) * (b.getLng() - a.getLng());
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

    /**
     * Convert points to circle
     * @param satInfo
     * @return
     */
    public static ArrayList<LinkedHashMap<Integer, Circle>> toCircle(ArrayList<LinkedHashMap<Integer, ArrayList<Position>>> satInfo) {
        if (satInfo.isEmpty()) {
            System.out.println("请先读取原始卫星数据");
            return null;
        }
        ArrayList<LinkedHashMap<Integer, Circle>> cSatInfo = new ArrayList<>();
        LinkedHashMap<Integer, Circle> cSat = new LinkedHashMap<>();
        //遍历9卫星
        for (Map<Integer, ArrayList<Position>> curSat : satInfo) {
            int sNo=0;
            //对于一个具体的卫星(86400)
            for (Map.Entry<Integer, ArrayList<Position>> satEntry : curSat.entrySet()) {
                ArrayList<Position> polyVertices = new ArrayList<>();
                polyVertices = satEntry.getValue();
                double x1 = polyVertices.get(0).getLng();
                double y1 = polyVertices.get(0).getLat();
                double x2 = polyVertices.get(10).getLng();
                double y2 = polyVertices.get(10).getLat();
                double radius = Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2))/2;
                cSat.put(sNo,new Circle((x1 + x2) / 2, (y1 + y2) / 2, radius));
                sNo++;
            }
            cSatInfo.add(cSat);
        }
//        satInfo.clear();// 释放原始卫星数据占用的内存  todo:open satInfo.clear();
        return cSatInfo;
    }

    /**
     * 判断点是否在圆内
     * @param pos
     * @param circle
     * @return
     */
    public static boolean pointInCircle(Position pos, Circle circle) {
        double x0,y0,x,y,r,distance;
        x0=pos.getLng();
        y0=pos.getLat();
        x=circle.getX();
        y=circle.getY();
        r=circle.getR();
        distance=Math.sqrt(Math.pow((x-x0),2)+Math.pow((y-y0),2));
        if(distance>r){
            return false;
        } else {
            return true;
        }
    }


}

