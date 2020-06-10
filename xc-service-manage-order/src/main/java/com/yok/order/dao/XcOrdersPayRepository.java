package com.yok.order.dao;

import com.yok.framework.domain.order.XcOrders;
import com.yok.framework.domain.order.XcOrdersPay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcOrdersPayRepository extends JpaRepository<XcOrdersPay,String> {
}
