package com.yok.manage_course.dao;

import com.yok.framework.domain.course.CoursePic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoursePicRepository extends JpaRepository<CoursePic,String> {
    //返回类型为long，如果大于0，表示删除成功影响的记录数
    long deleteByCourseid(String courseId);
    List<CoursePic> findByCourseid(String courseId);
}
