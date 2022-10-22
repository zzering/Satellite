package com.zerin.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Position {
    double lng;// longitude
    double lat;// latitude

    @Override
    public int hashCode() {
        int h = 0;
        h = (int) (31 * h + lng);
        h = (int) (31 * h + lat);
        return h;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Position &&
                this.lng == ((Position) obj).lng &&
                this.lat == ((Position) obj).lat;
    }

    public Position() {
    }

    public Position(double x, double y) {
        lng = x;
        lat = y;
    }

    public Position(Position position) {
        lng = position.lng;
        lat = position.lat;
    }


}
