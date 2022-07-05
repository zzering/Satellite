package com.zerin.service;

import org.junit.Test;

import static com.zerin.service.SatelliteService.timeWindow;
import static com.zerin.service.SatelliteService.toDate;

public class ServiceTest {
    @Test
    public void test() {
        System.out.println("ok");
        System.out.println(toDate(9094));
        System.out.println(toDate(67185));
//        pointInPolygon();

    }
}
