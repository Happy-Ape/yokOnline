package com.yok.framework.domain.learning.request;

import com.yok.framework.model.request.RequestData;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class QuestionRequest extends RequestData {
    String teachPlanId;
    String courseId;
    String userId;
    String askId;
    String title;
    String content;
}
