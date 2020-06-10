package com.yok.framework.domain.learning.response;

import com.yok.framework.model.response.ResultCode;
import lombok.ToString;


@ToString
public enum LearningCode implements ResultCode {
    CHOOSECOURSE_USERISNULl(false,23002,"用户不存在！"),
    CHOOSECOURSE_TASKISNULL(false,23003,"任务不存在！"),
    COURSE_NOT_COLLECTION(false,23004,"您尚未收藏该课程！"),
    COURSE_HAVE_COLLECTION(false,23005,"您已经收藏该课程，无需重复收藏！"),
    COMMENT_ISNOT_YOURS(false,23006,"这条评论不是您的！"),
    QUESTION_ISNOT_YOURS(false,23008,"这条回答不是您的！"),
    COMMENT_NOT_HAVE(false,23007,"未获取到这条评论！"),
    QUESTION_NOT_HAVE(false,23009,"未获取到这条回答！"),
    LEARNING_GETMEDIA_ERROR(false,23001,"获取学习地址失败！");
    //操作代码
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;
    private LearningCode(boolean success, int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
