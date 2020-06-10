package com.yok.order.service;

import com.alibaba.fastjson.JSON;
import com.yok.framework.domain.course.CourseMarket;
import com.yok.framework.domain.course.ext.CourseView;
import com.yok.framework.domain.course.response.CourseCode;
import com.yok.framework.domain.learning.XcLearningCourse;
import com.yok.framework.domain.order.XcOrders;
import com.yok.framework.domain.order.XcOrdersDetail;
import com.yok.framework.domain.order.XcOrdersPay;
import com.yok.framework.domain.order.request.CreateOrderRequest;
import com.yok.framework.domain.order.request.ListOrderRequest;
import com.yok.framework.domain.order.response.OrderCode;
import com.yok.framework.domain.order.response.OrderResult;
import com.yok.framework.domain.task.XcTask;
import com.yok.framework.exception.ExceptionCast;
import com.yok.framework.model.response.CommonCode;
import com.yok.framework.model.response.QueryResponseResult;
import com.yok.framework.model.response.QueryResult;
import com.yok.framework.model.response.ResponseResult;
import com.yok.framework.utils.CookieUtil;
import com.yok.framework.utils.Snowflake;
import com.yok.order.client.AuthClient;
import com.yok.order.client.CourseClient;
import com.yok.order.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.Id;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderService {
    @Autowired
    XcOrdersDetailRepository xcOrdersDetailRepository;
    @Autowired
    XcOrdersRepository xcOrdersRepository;
    @Autowired
    CourseClient courseClient;
    @Autowired
    XcOrdersMapper xcOrdersMapper;
    @Autowired
    XcOrdersPayRepository xcOrdersPayRepository;
    @Autowired
    AuthClient authClient;
    @Autowired
    XcTaskRepository xcTaskRepository;

    /**
     * 创建订单
     *
     * @param createOrderRequest
     * @return
     */
    @Transactional
    public OrderResult createOrder(CreateOrderRequest createOrderRequest) {
        if (createOrderRequest == null || StringUtils.isEmpty(createOrderRequest.getCourseId()) || StringUtils.isEmpty(createOrderRequest.getUserId())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String courseId = createOrderRequest.getCourseId();
        String userId = createOrderRequest.getUserId();
        //根据userid和courseid和status查询是否有未支付的订单
       XcOrders order= xcOrdersMapper.findByCourseidAndUserAndStatus(courseId, userId,"30101");
            //如果还未支付，就直接返回这个订单，否则（订单取消和支付都重新创建一个订单）
            if (order != null) {
                return new OrderResult(CommonCode.SUCCESS, order);
            }
        XcOrders xcOrders = new XcOrders();
        CourseMarket course = courseClient.getCourseMarketById(courseId);
        if (course == null || StringUtils.isEmpty(course.getId())) {
            ExceptionCast.cast(CourseCode.COURSE_ISNOTEXIST);
        }
        xcOrders.setInitialPrice(course.getPrice_old());
        xcOrders.setPrice(course.getPrice());
        xcOrders.setStartTime(new Date());
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(xcOrders.getStartTime());
        calendar.add(Calendar.MINUTE, 30);
        Date time = calendar.getTime();
        xcOrders.setEndTime(time);
        Snowflake snowflake = new Snowflake(1, 3);
        long id = snowflake.nextId();
        long currentTimeMillis = System.currentTimeMillis();
        String s = String.valueOf(currentTimeMillis).substring(0, 8);
        String orderId = s + id;
        xcOrders.setOrderNumber(orderId);
        xcOrders.setStatus("30101");
        xcOrders.setUserId(userId);
        XcOrdersDetail xcOrdersDetail = new XcOrdersDetail();
        xcOrdersDetail.setCourseId(courseId);
        xcOrdersDetail.setCourseNum(1);
        xcOrdersDetail.setCoursePrice(course.getPrice());
        xcOrdersDetail.setStartTime(course.getStartTime());
        xcOrdersDetail.setEndTime(course.getEndTime());
        xcOrdersDetail.setValid(course.getValid());
        xcOrdersDetail.setOrderNumber(xcOrders.getOrderNumber());
        xcOrdersDetail.setId(xcOrders.getOrderNumber());
        String detail = JSON.toJSONString(xcOrdersDetail);
        xcOrders.setDetails(detail);
        xcOrdersRepository.save(xcOrders);
        xcOrdersDetailRepository.save(xcOrdersDetail);
        return new OrderResult(CommonCode.SUCCESS, xcOrders);
    }

    /**
     * 获取当前订单
     *
     * @param orderId
     * @return
     */
    public OrderResult getOrder(String orderId) {
        if (StringUtils.isEmpty(orderId)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
    }
        Optional<XcOrders> optionalXcOrders = xcOrdersRepository.findById(orderId);
        if (optionalXcOrders.isPresent()) {
            XcOrders xcOrders = optionalXcOrders.get();
            return new OrderResult(CommonCode.SUCCESS, xcOrders);
        }
        return new OrderResult(CommonCode.FAIL, null);
    }

    /**
     * 更新支付状态
     *
     * @param orderId
     * @return
     */
    public ResponseResult updateOrderStatus(String orderId) {
        Optional<XcOrders> optional = xcOrdersRepository.findById(orderId);
        if (optional.isPresent()) {
            XcOrders xcOrders = optional.get();
            xcOrders.setStatus("30102");
            xcOrdersRepository.save(xcOrders);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    /**
     * 往Xc_task数据表中插入数据
     *
     * @param orderId
     * @return
     */
    public ResponseResult addTask(String orderId) {
        XcTask xcTask = new XcTask();
        //设置属性
        Snowflake snowflake = new Snowflake(1, 2);
        long id = snowflake.nextId();
        xcTask.setId(String.valueOf(id));
        xcTask.setCreateTime(new Date());
        xcTask.setUpdateTime(new Date());
        xcTask.setMqExchange("ex_learning_addchoosecourse");
        xcTask.setMqRoutingkey("addchoosecourse");
        OrderResult orderResult = getOrder(orderId);
        XcOrders xcOrders = orderResult.getXcOrders();
        String details = xcOrders.getDetails();
        XcOrdersDetail xcOrdersDetail = JSON.parseObject(details, XcOrdersDetail.class);
        String courseId = xcOrdersDetail.getCourseId();
        //取userId
        String userId = xcOrders.getUserId();
        //取课程信息
        CourseView courseview = courseClient.courseview(courseId);
        CourseMarket courseMarket = courseview.getCourseMarket();
        String valid = courseMarket.getValid();
        Date startTime = courseMarket.getStartTime();
        Date endTime = courseMarket.getEndTime();
        String startTimeString;
        String endTimeString;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        if (startTime == null) {
            startTimeString = "";
        } else {
            startTimeString = simpleDateFormat.format(startTime);
        }
        if (endTime == null) {
            endTimeString = "";
        } else {
            endTimeString = simpleDateFormat.format(endTime);
        }
        //设置request_body
        //{"userId":"49","courseId":"4028e581617f945f01617f9dabc40000","valid":"204001","startTime":"","endTime":""}
        String body = "{\"userId\":\"" + userId + "\",\"courseId\":\"" + courseId + "\",\"valid\":\"" + valid + "\",\"startTime\":\"" + startTimeString + "\",\"endTime\":\"" + endTimeString + "\"}";
        xcTask.setRequestBody(body);
        xcTask.setStatus("30102");
        xcTask.setVersion(1);
        xcTaskRepository.save(xcTask);
        return new ResponseResult(CommonCode.SUCCESS);

    }

    /**
     * 往Xc_order_pay数据表中插入数据
     *
     * @param orderId
     * @param payNumber
     * @return
     */
    public ResponseResult addOrderPay(String orderId, String payNumber) {
        XcOrdersPay ordersPay = new XcOrdersPay();
        ordersPay.setOrderNumber(orderId);
        Snowflake snowflake = new Snowflake(1, 2);
        long id = snowflake.nextId();
        ordersPay.setId(String.valueOf(id));
        ordersPay.setPayNumber(payNumber);
        ordersPay.setStatus("30102");
        xcOrdersPayRepository.save(ordersPay);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 查找去当前时间下已经过期的订单
     *
     * @param time
     * @param size
     * @return
     */
    public List<XcOrders> findOverdueOrderList(Date time, int size) {
        //设置分页参数
        Pageable pageable = new PageRequest(0, size);
        //查询前n条任务
        Page<XcOrders> all = xcOrdersRepository.findByEndTimeBeforeAndStatus(pageable, time,"30101");
        //取出过期订单
        List<XcOrders> overdueOrderList = all.getContent();
        return overdueOrderList;

    }

    /**
     * 取消订单，将订单状态标记为30103
     *
     * @param orderId
     */
    public ResponseResult cancelOrder(String orderId) {
        //取出订单
        Optional<XcOrders> optional = xcOrdersRepository.findById(orderId);
        //修改订单状态
        if (optional.isPresent()) {
            XcOrders xcOrders = optional.get();
            xcOrders.setStatus("30103");
            xcOrdersRepository.save(xcOrders);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    /**
     * 查询订单列表
     *
     * @param page
     * @param size
     * @param orderRequest
     * @return
     */
    public QueryResponseResult listOrder(int page, int size, ListOrderRequest orderRequest) {
        if (orderRequest == null
                || StringUtils.isEmpty(orderRequest.getUserId())
                || StringUtils.isEmpty(orderRequest.getStatus())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String userId = orderRequest.getUserId();
        String status = orderRequest.getStatus();
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 1;
        }
        page -= 1;
        XcOrders xcOrders = new XcOrders();
        xcOrders.setUserId(userId);
        if (status.equals("1")) {
            xcOrders.setStatus("30102");
        } else if (status.equals("2")) {
            xcOrders.setStatus("30101");
        } else if (status.equals("3")) {
            xcOrders.setStatus("30103");
        }
        //定义example条件对象
        Example<XcOrders> example = Example.of(xcOrders);
        //设置排序
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC,"startTime"));
        //设置分页参数        
        Pageable pageable = PageRequest.of(page,size,sort);

        //分页查询
        Page<XcOrders> orderListPage = xcOrdersRepository.findAll(example, pageable);
        //查询列表         
        List<XcOrders> list = orderListPage.getContent();
        //总记录数        
        long total = orderListPage.getTotalElements();
        //查询结果集         
        QueryResult<XcOrders> orderIncfoQueryResult = new QueryResult<XcOrders>();
        orderIncfoQueryResult.setTotal(total);
        orderIncfoQueryResult.setList(list);
        return new QueryResponseResult(CommonCode.SUCCESS, orderIncfoQueryResult);
    }

    /**
     * 根据订单号删除订单
     * @param orderId
     * @return
     */
    @Transactional
    public ResponseResult deleteOrder(String orderId) {
        if(StringUtils.isEmpty(orderId)){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Optional<XcOrders> optional = xcOrdersRepository.findById(orderId);
        if (!optional.isPresent()){
            return new ResponseResult(OrderCode.ORDER_FINISH_NOTFOUNDORDER);
        }
        XcOrders order = optional.get();
        xcOrdersRepository.delete(order);
        xcOrdersDetailRepository.deleteByOrderNumber(orderId);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
