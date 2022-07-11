package com.zerin.main;

import com.zerin.model.Position;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;

import static com.zerin.utils.CommonUtils.bufferReadSatInfo;
import static com.zerin.utils.CommonUtils.readSatInfo;
import static com.zerin.service.TimeCalc.*;
//第一题:
//计算卫星星座对每个点目标的可见时间窗口 对每个点目标的二重覆盖时间窗口
//计算卫星星座对每个点目标的覆盖时间间隙 统计每个点目标时间间隙的最大值和平均值

public class Satellite {
    public static void main(String[] args) {
        statelliteService();
    }

    public static void statelliteService(){
        ArrayList<LinkedHashMap<Integer, ArrayList<Position>>> satInfo = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        double startTime =  System.currentTimeMillis();
        bufferReadSatInfo(satInfo);
        readTarInfo();
        double t1 =  System.currentTimeMillis();
        double readTime = (t1-startTime)/1000;
        System.out.println("readTime: "+readTime+"s");
//        System.out.printf("%.2fs\n", readTime);
        int ch=0;
        while (true){
            System.out.println("卫星对地覆盖计算及任务规划");
            System.out.println("请输入指令序号");
            System.out.println("1.计算点目标的时间窗口和间隙");
            System.out.println("2.计算区域目标的覆盖率");
            ch=scanner.nextInt();
            switch (ch){
                case 1:{
                    double t2 =  System.currentTimeMillis();
                    timeWindow(satInfo);
                    double t3 =  System.currentTimeMillis();
                    double calcTime = (t3-t2)/1000;
                    System.out.println("calcTime: "+calcTime+"s");
                }
                case 2:{

                }
            }
        }
    }


}





