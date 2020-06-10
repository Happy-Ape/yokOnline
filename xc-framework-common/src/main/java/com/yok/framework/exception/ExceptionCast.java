package com.yok.framework.exception;

import com.yok.framework.model.response.ResultCode;

/**
 * @author Administrator
 * @version 1.0
 * @create 2020-01-14 17:31
 **/
public class ExceptionCast {

    public static void cast(ResultCode resultCode) {
        throw new CustomException(resultCode);
    }
}
