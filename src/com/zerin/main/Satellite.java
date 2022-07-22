package com.zerin.main;

import com.zerin.model.Circle;
import com.zerin.model.Position;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;

import static com.zerin.service.TimeCalc.*;
import static com.zerin.utils.CommonUtils.*;
//第一题:
//计算卫星星座对每个点目标的可见时间窗口 对每个点目标的二重覆盖时间窗口
//计算卫星星座对每个点目标的覆盖时间间隙 统计每个点目标时间间隙的最大值和平均值

//第二题:
//计算每个时刻的瞬时覆盖率，并将结果绘制成曲线
//对于仿真周期内的某个时刻，将该时刻区域内被覆盖的网格，不被覆盖的网格，不确定的网格用不同的颜色绘制出来
//将不同时刻的覆盖率结果，以动态形式展现出来（即添加时间，能够对时间进行调整，让时间流动）
//需要计算的地面区域目标数据为：一个经纬度矩形范围 经度区间为75°E-135°E，纬度区间为 0°N-55°N

public class Satellite {
    public static void main(String[] args) {
        statelliteService();
    }

    public static void statelliteService() {
        ArrayList<LinkedHashMap<Integer, ArrayList<Position>>> satInfo = new ArrayList<>();
        ArrayList<LinkedHashMap<Integer, ArrayList<Circle>>> cSatInfo = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);

        double startTime = System.currentTimeMillis();
        bufferReadSatInfo(satInfo);
        readTarInfo();
        double t0 = System.currentTimeMillis();
        double readTime = (t0 - startTime) / 1000;
        System.out.println("readTime: " + readTime + "s");
//        System.out.printf("%.2fs\n", readTime);
        int ch = 0;
        while (true) {
            System.out.println("卫星对地覆盖计算及任务规划");
            System.out.println("请输入指令序号");
            System.out.println("1.计算点目标的时间窗口和间隙");
            System.out.println("2.计算区域目标的覆盖率");
            System.out.println("3.计算");
            System.out.println("4.重新读取原始卫星数据");
            ch = scanner.nextInt();
            switch (ch) {
                case 1: {
                    if (satInfo.isEmpty()) {
                        System.out.println("请先读取原始卫星数据");
                    }
                    double t1 = System.currentTimeMillis();
                    timeWindow(satInfo);
                    double t2 = System.currentTimeMillis();
                    double calcTime = (t2 - t1) / 1000;
                    System.out.println("calcTime: " + calcTime + "s");
                    break;
                }
                case 2: {
                    double t1 = System.currentTimeMillis();
                    cSatInfo = toCircle(satInfo);
                    double t2 = System.currentTimeMillis();
                    double calcTime = (t2 - t1) / 1000;
                    System.out.println("calcTime: " + calcTime + "s");
                    break;
                }
                case 3: {

                    break;
                }
                case 4: {
                    if (!satInfo.isEmpty()) {
                        satInfo.clear();
                    }
                    double t1 = System.currentTimeMillis();
                    bufferReadSatInfo(satInfo);
                    double t2 = System.currentTimeMillis();
                    double calcTime = (t2 - t1) / 1000;
                    System.out.println("readTime: " + calcTime + "s");
                    break;
                }
            }
        }
    }
}




