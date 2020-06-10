package com.yok.framework.domain.learning.response;

import com.yok.framework.domain.learning.XcLearningCourse;
import com.yok.framework.domain.order.XcOrders;
import com.yok.framework.model.response.ResponseResult;
import com.yok.framework.model.response.ResultCode;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LearningResult extends ResponseResult {
    private XcLearningCourse xcLearningCourse;

    public LearningResult(ResultCode resultCode, XcLearningCourse xcLearningCourse) {
        super(resultCode);
        this.xcLearningCourse = xcLearningCourse;
    }


}
