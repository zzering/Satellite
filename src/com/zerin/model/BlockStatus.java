package com.zerin.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BlockStatus {
    double edgeLen;
    double coverStatus;

    public BlockStatus(){}

    public BlockStatus(double edgeLen, double coverStatus) {
        this.edgeLen = edgeLen;
        this.coverStatus = coverStatus;
    }
}
