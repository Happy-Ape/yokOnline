package com.yok.learning.mq;

import com.alibaba.fastjson.JSON;
import com.yok.framework.domain.task.XcTask;
import com.yok.framework.model.response.ResponseResult;
import com.yok.learning.config.RabbitMQConfig;
import com.yok.learning.service.LearningService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 **/
@Component
public class ChooseCourseTask {

    @Autowired
    LearningService learningService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_ADDCHOOSECOURSE)
    public void receiveChoosecourseTask(XcTask xcTask) {
        LOGGER.info("receive choose course task,taskId:{}", xcTask.getId());
        //接收到 的消息id
        String id = xcTask.getId();
        //添加选课
        try {
            String requestBody = xcTask.getRequestBody();
            Map map = JSON.parseObject(requestBody, Map.class);
            String userId = (String) map.get("userId");
            String courseId = (String) map.get("courseId");
            String valid = (String) map.get("valid");
            String startTime = (String)map.get("startTime");
            String endTime = (String)map.get("endTime");
            //添加选课
            ResponseResult addcourse = learningService.addcourse(userId, courseId, valid, startTime, endTime, xcTask);
            //选课成功发送响应消息
            if (addcourse.isSuccess()) {
                //添加选课成功，要向mq发送完成添加选课的消息
                rabbitTemplate.convertAndSend(RabbitMQConfig.EX_LEARNING_ADDCHOOSECOURSE, RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE_KEY, xcTask);
                LOGGER.info("send finish choose course taskId:{}", id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("send finish choose course taskId:{}", id);
        }
    }
}
