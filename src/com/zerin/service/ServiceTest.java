package com.zerin.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.zerin.model.Position;
import org.junit.Test;

import static com.zerin.utils.CommonUtils.readSatInfo;

public class ServiceTest {
    @Test
    public void test() {
        ArrayList<LinkedHashMap<Integer, ArrayList<Position>>> satInfo = new ArrayList<>();
        double startTime =  System.currentTimeMillis();
        readSatInfo(satInfo);
        double t1 =  System.currentTimeMillis();
        double readTime = (t1-startTime)/1000;
        System.out.println("readTime: "+readTime+"s");

//        Point2D.Double first = polygon.get(0);
//        int inside = 0;
//        int noInside = 0;
//        int m = 2000;
//        Rectangle2D rect[][] = new Rectangle2D[m][m];
//        //因为正方形的边长都是1,下列cirlce的圆心一定是某个正方形的顶点
//        Ellipse2D.Double cirlce = new Ellipse2D.Double(0, 0, m, m);
////                for(int i = 0;i<m;i++){
////                for (int j = 0; j < m; j++) {
////                    rect[i][j] = new Rectangle2D.Double(i, j, 1, 1);
////                    if (cirlce.contains(rect[i][j])) {
////                        inside++;
////                    } else {
////                        noInside++;
////                    }
////                }
////            }
//        double percent = (double) inside / (m * m);
    }
}
