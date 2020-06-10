package com.yok.order.controller;

import com.yok.api.order.OrderControllerApi;
import com.yok.framework.domain.order.request.CreateOrderRequest;
import com.yok.framework.domain.order.request.ListOrderRequest;
import com.yok.framework.domain.order.response.OrderResult;
import com.yok.framework.model.response.QueryResponseResult;
import com.yok.framework.model.response.ResponseResult;
import com.yok.framework.utils.CookieUtil;
import com.yok.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.Id;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/order")
public class OrderController implements OrderControllerApi {
    @Autowired
    OrderService orderService;

    @Override
    @GetMapping("/get/{id}")
    public OrderResult getOrder(@PathVariable("id") String orderId) {
        return orderService.getOrder(orderId);
    }

    @Override
    @PostMapping("/cancel/{id}")
    public ResponseResult cancelOrder(@PathVariable("id") String orderId) {
        return orderService.cancelOrder(orderId);
    }

    @Override
    @PostMapping("/delete/{id}")
    public ResponseResult deleteOrder(@PathVariable("id") String orderId) {
        return orderService.deleteOrder(orderId);
    }

    @Override
    @PostMapping("/create")
    public OrderResult createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        return orderService.createOrder(createOrderRequest);
    }

    @Override
    @PostMapping("/list/{page}/{size}")
    public QueryResponseResult listOrder(@PathVariable("page") int page, @PathVariable("size") int size, @RequestBody ListOrderRequest orderRequest) {
        return orderService.listOrder(page,size,orderRequest);
    }
}
