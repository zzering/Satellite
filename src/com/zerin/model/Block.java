package com.zerin.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Block extends Position{
    // 继承的坐标为网格左上角顶点坐标
//    double lng;
//    double lat;
    double edgelen;// edge length

    public Block(){}

    public Block(double x0,double y0,double edge){
        lng =x0;
        lat =y0;
        edgelen=edge;
    }

    public Block(Position pos,double edge){
        lng =pos.lng;
        lat =pos.lat;
        edgelen=edge;
    }

}
