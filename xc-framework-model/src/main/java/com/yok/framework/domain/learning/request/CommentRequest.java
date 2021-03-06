package com.yok.framework.domain.learning.request;

import com.yok.framework.model.request.RequestData;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CommentRequest extends RequestData {
    String courseId;
    String userId;
    Float score;
    String comment;
}
