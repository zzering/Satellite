package com.zerin.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;

@Setter
@Getter
public class TimeWindow {
    int satNo;
    //第一维的记录有多少个窗口 时间记录在第二维
    ArrayList<ArrayList<Integer>> window;
//    LinkedHashMap<Integer,ArrayList<ArrayList<Integer>>> a;
}
