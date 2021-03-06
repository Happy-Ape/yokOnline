package com.yok.manage_media.client;

import com.yok.framework.client.YokServiceList;
import com.yok.framework.model.response.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Repository
@FeignClient(value = YokServiceList.XC_SERVICE_MANAGE_COURSE) //指定远程调用的服务名
public interface CourseClient {
    //删除mysql数据
    @PostMapping("/course/deletemedia/{mediaId}")
    public ResponseResult deleteMedia(@PathVariable("mediaId") String mediaId);
}
