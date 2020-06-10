package com.yok.manage_course.dao;

import com.yok.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author Administrator
 * @version 1.0
 **/
@Mapper
@Repository
public interface TeachplanMapper {
    //课程计划查询
    public TeachplanNode selectList(String courseId);
}
