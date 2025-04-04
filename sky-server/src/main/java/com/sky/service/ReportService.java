package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public interface ReportService {
    /*
 统计时间内的营业额
  */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);
    /*
    用户统计
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);
    /*
    订单统计
     */
    OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end);
    /*
    销量前10
     */
    SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end);
    /*
    导出数据报表
     */
    void exportBusinessData(HttpServletResponse response);
}
