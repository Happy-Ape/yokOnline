package com.yok.framework.domain.learning.request;

import com.yok.framework.model.request.RequestData;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class NotesRequest extends RequestData {
    String teachPlanId;
    String courseId;
    String userId;
    String notes;
}
