package com.yok.manage_course.dao;

import com.yok.framework.domain.course.CourseMarket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseMarketRepository extends JpaRepository<CourseMarket,String> {
    public void deleteById(String id);
}
