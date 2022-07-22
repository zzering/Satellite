package com.zerin.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Position {
    double lng;//longitude
    double lat;//latitude

    public Position(){}

    public Position(double x,double y){
        lng=x;
        lat=y;
    }
}
