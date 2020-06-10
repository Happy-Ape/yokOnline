package com.yok.learning.dao;

import com.yok.framework.domain.learning.XcLearningNotes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface XcLearningNotesRepository extends JpaRepository<XcLearningNotes,String> {
    //根据用户id和课程id和章节Id查询，时间排序
    List<XcLearningNotes> findByUserIdAndCourseIdAndTeachPlanIdOrderByCreateTimeDesc(String userId, String courseId, String teachPlanId);
    //根据用户id和课程id和章节Id查询（排除掉符合UserId的数据），时间排序
    List<XcLearningNotes> findAllByUserIdIsNotAndCourseIdAndTeachPlanIdOrderByCreateTimeDesc(String userId,String courseId,String teachPlanId);
}
