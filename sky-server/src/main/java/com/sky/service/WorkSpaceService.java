package com.sky.service;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

public interface WorkSpaceService {
    /*
    查询今日运营数据
     */
    BusinessDataVO getBusinessData(LocalDateTime beginTime, LocalDateTime endTime);

    /*
    查询套餐总览
     */
    SetmealOverViewVO getOverviewSetmeals(LocalDateTime endTime);
    /*
    查询菜品总览
     */
    DishOverViewVO getOverviewDishes(LocalDateTime endTime);
    /*
    查询订单管理数据
     */
    OrderOverViewVO getOverviewOrders(LocalDateTime endTime);
}