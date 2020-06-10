package com.yok.learning.dao;

import com.yok.framework.domain.learning.XcLearningCollection;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcLearningCollectionRepository extends JpaRepository<XcLearningCollection,String> {
    //根据用户id和课程id查询
    XcLearningCollection findByUserIdAndCourseId(String userId, String courseId);
}
