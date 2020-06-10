package com.yok.learning.dao;

import com.yok.framework.domain.learning.QuestionNode;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface LearningCourseMapper {
    //获取课程评分
    public Float getCouseScore(String courseId);
    //获取我的问答列表
    public List<QuestionNode> getMyQuestionList(String userId, String courseId, String teachPlanId);
    //获取他人问答列表
    public List<QuestionNode> getOtherQuestionList(String userId, String courseId, String teachPlanId);
}
