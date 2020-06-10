package com.yok.order.client;

import com.yok.framework.client.YokServiceList;
import com.yok.framework.domain.course.CourseBase;
import com.yok.framework.domain.course.CourseMarket;
import com.yok.framework.domain.course.ext.CourseView;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = YokServiceList.XC_SERVICE_MANAGE_COURSE)
public interface CourseClient {
    @GetMapping("/course/coursemarket/get/{courseId}")
    public CourseMarket getCourseMarketById(@PathVariable("courseId") String courseId);
    @GetMapping("/course/courseview/{id}")
    public CourseView courseview(@PathVariable("id") String id);
}