package com.yok.order.dao;

import com.yok.framework.domain.order.XcOrders;
import com.yok.framework.domain.task.XcTask;
import com.yok.framework.domain.task.XcTaskHis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface XcOrdersRepository extends JpaRepository<XcOrders,String> {
    Page<XcOrders> findByEndTimeBeforeAndStatus(Pageable pageable, Date endTime,String status);
}
