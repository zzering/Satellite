package com.zerin.service;

import com.zerin.model.Circle;
import com.zerin.model.Coverage;
import com.zerin.model.Position;
import com.zerin.model.TimeWindow;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;

import static com.zerin.service.CoverageCalculation.coverageCalculate;
import static com.zerin.service.TimeCalculation.readTarInfo;
import static com.zerin.service.TimeCalculation.timeWindowCalculate;
import static com.zerin.utils.CommonUtils.bufferReadSatInfo;
import static com.zerin.utils.CommonUtils.toCircle;

public class statelliteService {
    /**
     * 原始卫星数据 ArrayList<>:satNo Integer:moment
     */
    ArrayList<LinkedHashMap<Integer, ArrayList<Position>>> satInfo = new ArrayList<>();
    /**
     * 转化成圆之后的卫星数据 ArrayList<>:satNo Integer:moment
     */
    ArrayList<LinkedHashMap<Integer, Circle>> circleSatInfo;
    /**
     * 第一题的答案数据:每个城市的时间窗口结果 Integer:cityNo
     */
    LinkedHashMap<Integer, ArrayList<TimeWindow>> timeWindowsInfo;
    /**
     * 第二题的答案数据:每秒的瞬时覆盖率结果 Integer:moment
     */
    LinkedHashMap<Integer, Coverage> coverageInfo = new LinkedHashMap<>();

    Scanner scanner = new Scanner(System.in);

    public statelliteService() {
        double startTime = System.currentTimeMillis();
        bufferReadSatInfo(satInfo);
        readTarInfo();
        double t0 = System.currentTimeMillis();
        double readTime = (t0 - startTime) / 1000;
        System.out.println("readTime: " + readTime + "s");
        // System.out.printf("%.2fs\n", readTime);
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
                    timeWindowsInfo = timeWindowCalculate(satInfo);
                    double t2 = System.currentTimeMillis();
                    double calcTime = (t2 - t1) / 1000;
                    System.out.println("calcTime: " + calcTime + "s");
                    break;
                }
                case 2: {
                    double t1 = System.currentTimeMillis();
                    circleSatInfo = toCircle(satInfo);
                    coverageCalculate(circleSatInfo);
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
