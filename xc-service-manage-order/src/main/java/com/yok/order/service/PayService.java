package com.yok.order.service;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.yok.framework.domain.course.ext.CourseView;
import com.yok.framework.domain.order.XcOrders;
import com.yok.framework.domain.order.XcOrdersDetail;
import com.yok.framework.domain.order.response.OrderResult;
import com.yok.framework.exception.ExceptionCast;
import com.yok.framework.model.response.CommonCode;
import com.yok.framework.model.response.ResponseResult;
import com.yok.order.client.CourseClient;
import com.yok.order.config.AlipayConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class PayService {

    @Autowired
    OrderService orderService;
    @Autowired
    AlipayClient alipayClient;
    @Value("${alipay.pay.notify-url}")
    private String notifyUrl;
    @Value("${alipay.pay.return-url}")
    private String returnUrl;
    @Value("${alipay.pay.gatewayUrl}")
    private String gatewayUrl;
    @Value("${alipay.pay.appid}")
    private String appid;
    @Value("${alipay.pay.app-private-key}")
    private String appPrivateKey;
    @Value("${alipay.pay.alipay-public-key}")
    private String alipayPublicKey;
    @Autowired
    CourseClient courseClient;

    private final Logger LOGGER = LoggerFactory.getLogger(PayService.class);

    public String goAliPay(String orderId, HttpServletRequest request, HttpServletResponse response) {
        OrderResult orderResult = orderService.getOrder(orderId);
        if (orderResult == null || orderResult.getXcOrders() == null) {
            return null;
        }
        XcOrders order = orderResult.getXcOrders(); //取得订单对象
        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(returnUrl);
        alipayRequest.setNotifyUrl(notifyUrl);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = orderId;
        //付款金额，必填
        String total_amount = order.getPrice().toString();
        //取课程名称作为订单名称
        String details = order.getDetails();
        XcOrdersDetail detail = JSON.parseObject(details, XcOrdersDetail.class);
        String courseId = detail.getCourseId();
        CourseView courseview = courseClient.courseview(courseId);//远程调用course服务，取得课程信息
        String courseName = courseview.getCourseBase().getName();
        //订单名称，必填
        String subject = "购买课程——" + courseName;
        //商品描述，可空
        String body = "购买课程:" + courseName + ",数量为：" + detail.getCourseNum() + "件,支付宝支付";

        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"timeout_express\":\"10m\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //请求
        String result = null;
        try {
            result = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            ExceptionCast.cast(CommonCode.SERVER_ERROR);
        }
        return result;
    }

    /**
     * 异步通知
     *
     * @param request
     */
    public String goNotifyUrl(HttpServletRequest request) {
        String result = "";
        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
//            try {
//                //乱码解决，这段代码在出现乱码时使用
//                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            params.put(name, valueStr);
        }

        boolean signVerified = false; //调用SDK验证签名
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, alipayPublicKey, "UTF-8", "RSA2");
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        String trade_status = "";
        String out_trade_no = "";
        String trade_no = "";
        if (signVerified) {//验证成功
            try {
                //商户订单号
                out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
                //支付宝交易号
                trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
                //交易状态
                trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (trade_status.equals("TRADE_FINISHED")) {
                LOGGER.info("TRADE_FINISHED status SUCCESS");
            } else if (trade_status.equals("TRADE_SUCCESS")) {
                LOGGER.info("TRADE_SUCCESS status SUCCESS");
            }
            result = "success";
        } else {//验证失败
            result = "fail";
        }
        return result;
    }

    public String goReturnUrl(HttpServletRequest request) {
        String result = "";
        //获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
//            try {
//                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
            params.put(name, valueStr);
        }

        boolean signVerified = false; //调用SDK验证签名
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, alipayPublicKey, "UTF-8", "RSA2");
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        if (signVerified) {

            String out_trade_no = null;
            String trade_no = null;
            String total_amount = null;
            try {
                //商户订单号
                out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
                //支付宝交易号
                trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
                //付款金额
                total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //处理后续操作:
            //修改支付状态
            orderService.updateOrderStatus(out_trade_no);
            //添加支付信息
            orderService.addOrderPay(out_trade_no,trade_no);
            //添加OrderTask
            orderService.addTask(out_trade_no);
            String head = "<html>\n" +
                    "<head>\n" +
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                    "<title>付款成功</title>\n" +
                    "</head>";
            result ="<body>\n" +
                    "<p>\n" +
                    "<span id=\"time\">付款成功,自动跳转到订单页</span>\n" +
                    "</p>\n" +
                    "</body>";
            result +="<script>location.href='http://ucenter.yok.com/#/pay/"+out_trade_no+"';</script>";
        } else {
            result = "付款失败，如已扣款，请联系客服！";
        }
        return result;
    }
}
