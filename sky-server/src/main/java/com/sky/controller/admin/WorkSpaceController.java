package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/admin/workspace")
@Slf4j
public class WorkSpaceController {
    @Autowired
    private WorkSpaceService workSpaceService;

    /*
    查询今日运营数据
     */
    @GetMapping("/businessData")
    public Result<BusinessDataVO> businessData() {
        log.info("查询今日运营数据");
        //这里转化一下照搬的话会出错
        LocalDate now = LocalDate.now();
        LocalDateTime beginTime = LocalDateTime.of(now, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(now, LocalTime.MAX);
        return Result.success(workSpaceService.getBusinessData(beginTime, endTime));
    }

    /*
     查询套餐总览
     */
    @GetMapping("/overviewSetmeals")
    public Result<SetmealOverViewVO> overviewSetmeals() {
        LocalDate now = LocalDate.now();
        LocalDateTime endTime = LocalDateTime.of(now, LocalTime.MAX);
        return Result.success(workSpaceService.getOverviewSetmeals(endTime));
    }

    /*
    查询菜品总览
     */
    @GetMapping("/overviewDishes")
    public Result<DishOverViewVO> overviewDishes() {
        LocalDate now = LocalDate.now();
        LocalDateTime endTime = LocalDateTime.of(now, LocalTime.MAX);
        return Result.success(workSpaceService.getOverviewDishes(endTime));
    }

    /*
    查询订单管理数据
     */
    @GetMapping("/overviewOrders")
    public Result<OrderOverViewVO> overviewOrders() {
        LocalDate now = LocalDate.now();
        LocalDateTime endTime = LocalDateTime.of(now, LocalTime.MAX);
        return Result.success(workSpaceService.getOverviewOrders(endTime));
    }
}
