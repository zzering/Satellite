package com.zerin.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Position {
    double lng;// longitude
    double lat;// latitude

    public Position(){}

    public Position(double x,double y){
        lng=x;
        lat=y;
    }

    public Position(Position position){
        lng=position.lng;
        lat=position.lat;
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj){
            return true;
        }
        if(obj==null||getClass()!=obj.getClass()){
            return false;
        }
        Position pos=(Position) obj;
        if()
    }
}
