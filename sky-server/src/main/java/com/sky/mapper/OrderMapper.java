package com.sky.mapper;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {
    /**
     * 用户提交订单
     * @param
     * @return
     */

    void insert(Orders orders);
}
