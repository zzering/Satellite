package com.zerin.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Block {
    double x;//左上角顶点坐标
    double y;
    double edgelen;//edge length
    int statue;//inside 3 unsure 2 outside 1 init 0

    public Block(){}

    public Block(double x0,double y0,double edge,int statue0){
        x=x0;
        y=y0;
        edgelen=edge;
        statue=statue0;
    }

}
