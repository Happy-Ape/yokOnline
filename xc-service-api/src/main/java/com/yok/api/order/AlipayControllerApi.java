package com.yok.api.order;

import com.yok.framework.domain.order.request.CreateOrderRequest;
import com.yok.framework.domain.order.response.OrderResult;
import com.yok.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.servlet.ModelAndView;

@Api(value = "支付宝支付接口",description = "支付宝支付接口",tags = {"支付宝支付接口"})
public interface AlipayControllerApi {
    @ApiOperation("去支付")
    public String goAliPay(String orderId) throws Exception;
    @ApiOperation("同步通知接口")
    public String goReturnUrl();
    @ApiOperation("异步通知接口")
    public String goNotifyUrl();
}
