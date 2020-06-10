package com.yok.learning.client;

import com.yok.framework.client.YokServiceList;
import com.yok.framework.domain.course.CourseMarket;
import com.yok.framework.domain.course.Teachplan;
import com.yok.framework.domain.course.ext.CourseView;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = YokServiceList.XC_SERVICE_MANAGE_COURSE)
public interface CourseClient {
    @GetMapping("/course/coursemarket/get/{courseId}")
    public CourseMarket getCourseMarketById(@PathVariable("courseId") String courseId);
    @GetMapping("/course/teachplan/get/{id}")
    public Teachplan getTeachplan(@PathVariable("id")String id);
}