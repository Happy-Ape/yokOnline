package com.yok.order.dao;

import com.yok.framework.domain.order.XcOrders;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface XcOrdersMapper {
    public XcOrders findByCourseidAndUserAndStatus(String courseId, String userId,String status);
}
