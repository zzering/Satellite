package com.zerin.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Block {
    double lng;// 左上角顶点坐标
    double lat;
    double edgelen;// edge length
    int statue;// inside 3 unsure 2 outside 1 init 0 初始化为0

    public Block(){}

    public Block(double x0,double y0,double edge,int statue0){
        lng =x0;
        lat =y0;
        edgelen=edge;
        statue=statue0;
    }

}
