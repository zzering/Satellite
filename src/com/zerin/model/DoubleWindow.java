package com.zerin.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DoubleWindow {
    int satNo;
    int startTime;
    int endTime;

    public DoubleWindow(){

    }

    public DoubleWindow(int sat, int start,int end){
        satNo=sat;
        startTime=start;
        endTime=end;
    }
}
