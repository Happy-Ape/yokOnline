package com.yok.order.client;

import com.yok.framework.client.YokServiceList;
import com.yok.framework.domain.ucenter.response.JwtResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = YokServiceList.XC_SERVICE_UCENTER_AUTH)
public interface AuthClient {
    @GetMapping("/userjwt")
    public JwtResult userjwt();
}
