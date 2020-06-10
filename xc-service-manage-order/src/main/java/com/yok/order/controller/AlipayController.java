package com.yok.order.controller;

import com.yok.api.order.AlipayControllerApi;
import com.yok.order.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/order/pay")
public class AlipayController implements AlipayControllerApi {
    @Autowired
    PayService payService;

    @Override
    @RequestMapping("/aliPayReturnNotice")
    public String goReturnUrl() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return payService.goReturnUrl(request);

    }

    @Override
    @RequestMapping("/aliPayNotifyNotice")
    public String goNotifyUrl() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return  payService.goNotifyUrl(request);
    }

    @Override
    @PostMapping("/aliPay")
    public String goAliPay(@RequestParam("orderNumber") String orderId) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        return  payService.goAliPay(orderId, request, response);
    }
}
