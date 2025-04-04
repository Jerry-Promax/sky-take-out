package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkSpaceService workSpaceService;


    /*
    统计时间内的营业额
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Double> turnoverList = new ArrayList<>();
        dateList.forEach(DL -> {
            LocalDateTime beginTime = LocalDateTime.of(DL, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(DL, LocalTime.MAX);
            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        });
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /*
    用户统计量
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        dateList.forEach(DL -> {
            LocalDateTime beginTime = LocalDateTime.of(DL, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(DL, LocalTime.MAX);
            Map map = new HashMap();
            map.put("endTime", endTime);
            Integer sumUser = userMapper.countByMap(map);
            sumUser = sumUser == null ? 0 : sumUser;
            totalUserList.add(sumUser);
            map.put("beginTime", beginTime);
            Integer newUser = userMapper.countByMap(map);
            newUser = newUser == null ? 0 : newUser;
            newUserList.add(newUser);
        });
        return UserReportVO.builder()
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .dateList(StringUtils.join(dateList, ","))
                .build();
    }

    /*
    统计订单量
     */
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        int sum = 0;
        int vailSum = 0;
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        for (LocalDate DL : dateList) {
            LocalDateTime endTime = LocalDateTime.of(DL, LocalTime.MAX);
            LocalDateTime beginTime = LocalDateTime.of(DL, LocalTime.MIN);
            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            Integer everydayOrders = orderMapper.countBymap(map);
            sum = sum + everydayOrders.intValue();
            orderCountList.add(everydayOrders);
            everydayOrders = everydayOrders == null ? 0 : everydayOrders;
            map.put("status", Orders.COMPLETED);
            Integer everydayVailOrders = orderMapper.countBymap(map);
            vailSum = vailSum + everydayVailOrders.intValue();
            everydayVailOrders = everydayOrders == null ? 0 : everydayVailOrders;
            validOrderCountList.add(everydayVailOrders);
        }
        Double D = (double) vailSum / sum;

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalOrderCount(Integer.valueOf(sum))
                .validOrderCount(Integer.valueOf(vailSum))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .orderCompletionRate(D)
                .build();


    }

    /*
    销量排名前10
     */
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        //查询商品销量
        //select od.name,sum(od.number) from order_detail od,orders o where od.order_id = o.id and o.status = 5 and o.order_time >= beginTime and o.order_time <= endTime group by od.name order by sum(od.number) desc limit 10
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getByTime(beginTime, endTime);
        //将goodsSalesDTOList中的name和number分别转成列表字符串封装进VO
        SalesTop10ReportVO salesTop10ReportVO = SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()), ","))
                .numberList(StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList()), ","))
                .build();
        return salesTop10ReportVO;
    }

    /*
    导出数据运营报表
     */
    public void exportBusinessData(HttpServletResponse response) {
        LocalDate dataBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().plusDays(1);
        BusinessDataVO businessData = workSpaceService.getBusinessData(LocalDateTime.of(dataBegin, LocalTime.MIN), LocalDateTime.of(dateEnd, LocalTime.MAX));
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("temple/运营数据报表模板.xlsx");
        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);
            XSSFSheet sheet1 = excel.getSheet("Sheet1");
            sheet1.getRow(1).getCell(1).setCellValue("时间:" + dataBegin + "至" + dateEnd);

            XSSFRow row = sheet1.getRow(3);
            row.getCell(3).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());

            XSSFRow row1 = sheet1.getRow(4);
            row1.getCell(2).setCellValue(businessData.getValidOrderCount());
            row1.getCell(4).setCellValue(businessData.getUnitPrice());

            for (int i = 0; i < 30; i++) {
                LocalDate date = dataBegin.plusDays(i);
                XSSFRow row2 = sheet1.getRow(7 + i);
                row2.getCell(1).setCellValue(String.valueOf(date));
                row2.getCell(2).setCellValue(workSpaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX)).getTurnover());
                row2.getCell(3).setCellValue(workSpaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX)).getValidOrderCount());
                row2.getCell(4).setCellValue(workSpaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX)).getOrderCompletionRate());
                row2.getCell(5).setCellValue(workSpaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX)).getUnitPrice());
                row2.getCell(6).setCellValue(workSpaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX)).getNewUsers());
            }

            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            out.close();
            excel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}