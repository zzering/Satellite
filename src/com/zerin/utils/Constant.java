package com.zerin.utils;

public interface Constant {
    /** 第一题用到的城市总数*/
    int CITY_NUM = 24;
    /** 卫星数据文件的相对路径*/
    String SAT_DATA_PATH = "Data/SatelliteInfo";
    /** 第一题用到的城市数据文件的相对路径*/
    String CITY_DATA_PATH = "Data/TargetInfo/target.txt";
    /** 第一题计算结果输出文件的相对路径*/
    String RESULT1_DATA_PATH = "Result/timeWindow.txt";
    /** 第二题计算结果输出文件的相对路径*/
    String RESULT2_DATA_PATH = "Result/coverage.txt";
    /** 点（网格）在圆内*/
    int INSIDE=2;
//    /** 网格与圆的关系不确定，但有3个点在圆内*/
//    int UNSURE3=3;
//    /** */
//    int UNSURE2=2;
    /** 点（网格）与圆的关系尚未确定*/
    int UNSURE =1;
    /** 点（网格）在圆外*/
    int OUTSIDE=0;
    // todo r^2=40589641
    /** 地球半径*/
    int RADIUS=6371;




}
