package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 用户提交订单
     * @param
     * @return
     */

    void insert(Orders orders);
    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     *
     * @param orders
     * @return
     */
    void update(Orders orders);

    /**
     * 分页条件查询并按下单时间排序
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 各个状态订单的数量
     * @param toBeConfirmed
     * @return
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer toBeConfirmed);

    /**
     * 根据订单状态和下单时间查询订单
     * @param status
     * @param orderTime
     * @return
     */
    @Select("SELECT * FROM orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    /*
   根据时间段和订单状态计算总金额
    */
    Double sumByMap(Map map);
    /*
    订单统计量
     */
    Integer countBymap(Map map);
    /*
    统计销售量
     */
    List<GoodsSalesDTO> getByTime(LocalDateTime beginTime, LocalDateTime endTime);
    /*
    今天的有效订单数
     */
    @Select("select count(id) from orders where order_time > #{beginTime} and order_time < #{endTime} and status=#{status} ")
    Integer TodayVailOrders(Map map);
    /*
    获得今天下单成功的用户
     */
    @Select("select count(user_id) from orders where order_time > #{beginTime} and order_time < #{endTime} and status=5")
    Integer getSuccess(LocalDateTime beginTime, LocalDateTime endTime);
}
