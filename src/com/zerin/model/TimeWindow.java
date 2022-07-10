package com.zerin.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TimeWindow {
    int satNo;
    int startTime;
    int endTime;

    public TimeWindow(){
    }

    public TimeWindow(int sat, int start, int end){
        satNo=sat;
        startTime=start;
        endTime=end;
    }
}
