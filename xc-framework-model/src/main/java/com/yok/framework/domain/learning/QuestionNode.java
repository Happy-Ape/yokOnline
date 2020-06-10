package com.yok.framework.domain.learning;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class QuestionNode extends XcLearningQuestion {
    List<XcLearningQuestion> children;
}
