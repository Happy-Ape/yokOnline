package com.yok.api.order;

import com.yok.framework.domain.order.request.CreateOrderRequest;
import com.yok.framework.domain.order.request.ListOrderRequest;
import com.yok.framework.domain.order.response.OrderResult;
import com.yok.framework.model.response.QueryResponseResult;
import com.yok.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "订单接口", description = "订单接口", tags = {"订单接口"})
public interface OrderControllerApi {
    @ApiOperation("创建订单")
    public OrderResult createOrder(CreateOrderRequest createOrderRequest);

    @ApiOperation("获取当前订单")
    public OrderResult getOrder(String orderId);

    @ApiOperation("取消当前订单")
    public ResponseResult cancelOrder(String orderId);

    @ApiOperation("根据UserId获取订单列表")
    public QueryResponseResult listOrder(int page, int size, ListOrderRequest orderRequest);

    @ApiOperation("删除当前订单")
    public ResponseResult deleteOrder(String orderId);
}
