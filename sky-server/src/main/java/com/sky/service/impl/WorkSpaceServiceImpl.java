package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;


    /*
    查询今日运营数据
     */
    public BusinessDataVO getBusinessData(LocalDateTime beginTime, LocalDateTime endTime) {
        Map map = new HashMap();
        map.put("endTime", endTime);
        //总用户数
        Integer sumUser = userMapper.countByMap(map);
        map.put("beginTime", beginTime);
        //今天总订单
        Double SumOrder = orderMapper.sumByMap(map);
        SumOrder = SumOrder == null ? 0 : SumOrder;
        map.put("status", Orders.COMPLETED);
        //今天有效订单数
        Integer vailOrders = orderMapper.TodayVailOrders(map);
        vailOrders = vailOrders == null ? 0 : vailOrders;
        //今天总有效订单金额
        Double vailOrder = orderMapper.sumByMap(map);
        vailOrder = vailOrder == null ? 0 : vailOrder;
        //今天注册数量
        Integer TodayUser = userMapper.countByMap(map);
        TodayUser = TodayUser == null ? 0 : TodayUser;
        //今天下单成功的客户数量
        Integer successUser = orderMapper.getSuccess(beginTime, endTime);
        successUser = successUser == null ? 0 : successUser;


        Double A = successUser == 0 ? 0 : Double.valueOf(vailOrder.intValue() / successUser.intValue());
        Double B = SumOrder == 0 ? 0 : (vailOrder / SumOrder);
        return BusinessDataVO.builder()
                .turnover(vailOrder)
                .newUsers(TodayUser)
                .orderCompletionRate(B)
                .unitPrice(A)
                .validOrderCount(vailOrders)
                .build();
    }

    /*
    查询套餐总览
     */
    public SetmealOverViewVO getOverviewSetmeals(LocalDateTime endTime) {
        Map map1 = new HashMap();
        map1.put("endTime", endTime);
        map1.put("status", 1);
        Integer one = setmealMapper.getmealBymap(map1);
        Map map2 = new HashMap();
        map2.put("endTime", endTime);
        map2.put("status", 0);
        Integer zero = setmealMapper.getmealBymap(map2);
        return SetmealOverViewVO.builder()
                .sold(one)
                .discontinued(zero)
                .build();
    }
    /*
    查询菜品总览
     */

    public DishOverViewVO getOverviewDishes(LocalDateTime endTime) {
        Map map1 = new HashMap();
        map1.put("endTime", endTime);
        map1.put("status", 1);
        Integer one = dishMapper.getmealBymap(map1);
        Map map2 = new HashMap();
        map2.put("endTime", endTime);
        map2.put("status", 0);
        Integer zero = dishMapper.getmealBymap(map2);
        return DishOverViewVO.builder()
                .sold(one)
                .discontinued(zero)
                .build();
    }

    /*
    查询订单管理数据
     */
    public OrderOverViewVO getOverviewOrders(LocalDateTime endTime) {
        Map map1 =new HashMap();
        map1.put("endTime", endTime);
        Integer allOrders = orderMapper.countBymap(map1);


        Map map2 =new HashMap();
        map2.put("endTime", endTime);
        map2.put("status", Orders.CANCELLED);
        Integer cancelledOrders = orderMapper.countBymap(map2);


        Map map3 =new HashMap();
        map3.put("endTime", endTime);
        map3.put("status", Orders.COMPLETED);
        Integer completedOrders = orderMapper.countBymap(map3);



        Map map4 =new HashMap(map3);
        map4.put("endTime", endTime);
        map4.put("status", Orders.CONFIRMED);
        Integer deliveredOrders = orderMapper.countBymap(map4);

        Map map5 =new HashMap(map3);
        map5.put("endTime", endTime);
        map5.put("status", Orders.TO_BE_CONFIRMED);
        Integer waitingOrders = orderMapper.countBymap(map5);
        return OrderOverViewVO.builder()
                .allOrders(allOrders)
                .cancelledOrders(cancelledOrders)
                .completedOrders(completedOrders)
                .deliveredOrders(deliveredOrders)
                .waitingOrders(waitingOrders)
                .build();
    }
}