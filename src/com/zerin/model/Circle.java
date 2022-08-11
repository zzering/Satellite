package com.zerin.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Circle {
    double lng;
    double lat;
    double r;// =7.0068;// radius 经过验算，所有卫星的半径皆为7.0068

    public Circle(){}

    public Circle(double x0, double y0, double r0){
        lng =x0;
        lat =y0;
        r=r0;
    }
}
