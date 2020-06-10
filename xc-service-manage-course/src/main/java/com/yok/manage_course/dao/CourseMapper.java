package com.yok.manage_course.dao;

import com.github.pagehelper.Page;
import com.yok.framework.domain.course.CourseBase;
import com.yok.framework.domain.course.ext.CourseInfo;
import com.yok.framework.domain.course.request.CourseListRequest;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator.
 */
@Mapper
@Repository
public interface CourseMapper {
    CourseBase findCourseBaseById(String id);

    Page<CourseInfo> findCourseListPage(CourseListRequest courseListRequest);
}
