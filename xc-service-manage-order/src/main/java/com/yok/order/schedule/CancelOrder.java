package com.yok.order.schedule;

import com.yok.framework.domain.order.XcOrders;
import com.yok.framework.domain.task.XcTask;
import com.yok.order.service.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Component
public class CancelOrder {
    @Autowired
    OrderService orderService;

    @Scheduled(cron = "0/30 * * * * *")
    public void cancelOrder() {
        //得到当前的时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        Date time = calendar.getTime();
        List<XcOrders> overdueOrderList =  orderService.findOverdueOrderList(time,100);
        for (XcOrders xcOrders : overdueOrderList){
            if (xcOrders != null && StringUtils.isNotEmpty(xcOrders.getOrderNumber()))
            orderService.cancelOrder(xcOrders.getOrderNumber());
        }
    }
}
