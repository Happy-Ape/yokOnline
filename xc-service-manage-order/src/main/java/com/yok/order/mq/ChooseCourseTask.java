package com.yok.order.mq;

import com.yok.framework.domain.task.XcTask;
import com.yok.order.config.RabbitMQConfig;
import com.yok.order.service.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 **/
@Component
public class ChooseCourseTask {

    @Autowired
    TaskService taskService;

    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE)
    public void receiveFinishChoosecourseTask(XcTask xcTask) {
        if (xcTask != null && StringUtils.isNotEmpty(xcTask.getId())) {
            taskService.finishTask(xcTask.getId());
        }
    }

    /**
     * 定时发送加选课任务
     */
    @Scheduled(cron = "0/3 * * * * *")
    public void sendChoosecourseTask() {
        //得到1秒之前的时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.set(GregorianCalendar.SECOND,-1);
        Date time = calendar.getTime();
        List<XcTask> xcTaskList = taskService.findXcTaskList(time, 100);
        //System.out.println(xcTaskList);
        //调用service发布消息，将添加选课的任务发送给mq
        for (XcTask xcTask : xcTaskList) {
            //取任务
            if (taskService.getTask(xcTask.getId(), xcTask.getVersion()) > 0) {
                String ex = xcTask.getMqExchange();//要发送的交换机
                String routingKey = xcTask.getMqRoutingkey();//发送消息要带routingKey
                taskService.publish(xcTask, ex, routingKey);
            }

        }
    }

}
