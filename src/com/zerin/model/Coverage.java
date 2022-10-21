package com.zerin.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class Coverage {
    int moment;
    double coverage;
    /**
     * 存放划分之后每个网格的情况 Block代表网格 Integer代表状态
     */
    HashMap<Position, BlockStatus> curCoverageInfo;

    public Coverage() {
    }

    public Coverage(int moment, double coverage, HashMap<Position, BlockStatus> curCoverageInfo) {
        this.moment = moment;
        this.coverage = coverage;
        this.curCoverageInfo = curCoverageInfo;
    }

}
