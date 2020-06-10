package com.yok.learning.dao;

import com.yok.framework.domain.learning.XcLearningComment;
import com.yok.framework.domain.learning.XcLearningNotes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface XcLearningCommentRepository extends JpaRepository<XcLearningComment,String> {
    //根据课程id查询,发布时间排序
    List<XcLearningComment> findByCourseIdOrderByCreateTimeDesc(String courseId);
}
