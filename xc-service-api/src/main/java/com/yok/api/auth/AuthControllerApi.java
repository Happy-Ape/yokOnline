package com.yok.api.auth;

import com.yok.framework.domain.learning.request.ChooseCourseRequest;
import com.yok.framework.domain.ucenter.XcUser;
import com.yok.framework.domain.ucenter.request.LoginRequest;
import com.yok.framework.domain.ucenter.response.JwtResult;
import com.yok.framework.domain.ucenter.response.LoginResult;
import com.yok.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Created by Administrator.
 */
@Api(value = "用户认证",description = "用户认证接口")
public interface AuthControllerApi {
    @ApiOperation("登录")
    public LoginResult login(LoginRequest loginRequest);

    @ApiOperation("退出")
    public ResponseResult logout();

    @ApiOperation("查询JWT令牌")
    public JwtResult userjwt();

}
