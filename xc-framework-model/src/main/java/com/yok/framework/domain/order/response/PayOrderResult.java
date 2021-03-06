package com.yok.framework.domain.order.response;

import com.yok.framework.domain.order.XcOrdersPay;
import com.yok.framework.model.response.ResponseResult;
import com.yok.framework.model.response.ResultCode;
import lombok.Data;
import lombok.ToString;

/**
 * Created by mrt on 2020/2/27.
 */
@Data
@ToString
public class PayOrderResult extends ResponseResult {
    public PayOrderResult(ResultCode resultCode) {
        super(resultCode);
    }
    public PayOrderResult(ResultCode resultCode, XcOrdersPay xcOrdersPay) {
        super(resultCode);
        this.xcOrdersPay = xcOrdersPay;
    }
    private XcOrdersPay xcOrdersPay;
    private String orderNumber;

    //当tradeState为NOTPAY（未支付）时显示支付二维码
    private String codeUrl;
    private Float money;


}
