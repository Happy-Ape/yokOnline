package com.yok.order.dao;

import com.yok.framework.domain.order.XcOrders;
import com.yok.framework.domain.order.XcOrdersDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcOrdersDetailRepository extends JpaRepository<XcOrdersDetail,String> {
    public long deleteByOrderNumber(String orderId);
}
