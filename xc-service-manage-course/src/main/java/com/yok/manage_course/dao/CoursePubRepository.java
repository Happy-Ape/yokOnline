package com.yok.manage_course.dao;

import com.yok.framework.domain.course.CourseBase;
import com.yok.framework.domain.course.CoursePub;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CoursePubRepository extends JpaRepository<CoursePub,String> {
}
