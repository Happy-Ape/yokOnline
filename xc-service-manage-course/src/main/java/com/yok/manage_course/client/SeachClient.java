package com.yok.manage_course.client;

import com.yok.framework.client.YokServiceList;
import com.yok.framework.model.response.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Repository
@FeignClient(value = YokServiceList.XC_SERVICE_SEARCH) //指定远程调用的服务名
public interface SeachClient {
    //删除es数据
    @PostMapping("/search/course/delete/{courseId}")//用GetMapping标识远程调用的http的方法类型
    public ResponseResult delete(@PathVariable("courseId") String courseId);
}
