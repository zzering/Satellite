package com.zerin.main;

import com.zerin.service.statelliteService;

/**
 * 第一题:
 * 计算卫星星座对每个点目标的可见时间窗口 对每个点目标的二重覆盖时间窗口
 * 计算卫星星座对每个点目标的覆盖时间间隙 统计每个点目标时间间隙的最大值和平均值
 *
 * 第二题:
 * 计算每个时刻的瞬时覆盖率，并将结果绘制成曲线
 * 对于仿真周期内的某个时刻，将该时刻区域内被覆盖的网格，不被覆盖的网格，不确定的网格用不同的颜色绘制出来
 * 对于不确定的网格，将网格一分为四，然后判断新的网格是否会被卫星覆盖，直到不确定的网格面积之和与总面积之比小于 0.1%停止
 * 将不同时刻的覆盖率结果，以动态形式展现出来（即添加时间，能够对时间进行调整，让时间流动）
 * 需要计算的地面区域目标数据为：一个经纬度矩形范围 经度区间为75°E-135°E，纬度区间为 0°N-55°N
 */
public class SatelliteLaunchApplication {
    public static void main(String[] args) {
        new statelliteService();
    }
}




