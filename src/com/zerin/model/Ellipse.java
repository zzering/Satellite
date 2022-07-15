package com.zerin.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ellipse {
    double x;
    double y;
    double r;//=7.0068;//radius 经过验算，所有卫星的半径皆为7.0068

    public Ellipse(double x0,double y0,double r0){
        x=x0;
        y=y0;
        r=r0;
    }
}
