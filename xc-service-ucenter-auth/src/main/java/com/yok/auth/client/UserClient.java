package com.yok.auth.client;

import com.yok.framework.client.YokServiceList;
import com.yok.framework.domain.ucenter.XcUser;
import com.yok.framework.domain.ucenter.ext.XcUserExt;
import com.yok.framework.model.response.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = YokServiceList.XC_SERVICE_UCENTER)
public interface UserClient {
    @GetMapping("/ucenter/getuserext")
    public XcUserExt getUserExt(@RequestParam("username") String username);
}
