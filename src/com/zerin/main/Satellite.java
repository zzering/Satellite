package com.zerin.main;

import java.util.Scanner;

import static com.zerin.service.SatelliteService.*;
//第一题:
//计算卫星星座对每个点目标的可见时间窗口 对每个点目标的二重覆盖时间窗口
//计算卫星星座对每个点目标的覆盖时间间隙 统计每个点目标时间间隙的最大值和平均值

public class Satellite {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        long startTime =  System.currentTimeMillis();
        readSatInfo();
        readTarInfo();
        long t1 =  System.currentTimeMillis();
        long readTime = (t1-startTime)/1000;
        System.out.println("readTime: "+readTime+"s");
        int ch=0;
        while (true){
            ch=scanner.nextInt();
            switch (ch){
                case 1:{
                    long t2 =  System.currentTimeMillis();
                    timeWindow();
                    long t3 =  System.currentTimeMillis();
                    long calcTime = (t3-t2)/1000;
                    System.out.println("calcTime: "+calcTime+"s");
                }
                case 2:{

                }
            }

        }

    }
}









