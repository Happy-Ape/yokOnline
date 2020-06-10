package com.yok.learning.dao;

import com.yok.framework.domain.learning.XcLearningQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcLearningQuestionRepository extends JpaRepository<XcLearningQuestion,String> {
    int deleteByIdOrAskId(String id,String askId);
}
