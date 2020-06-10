package com.yok.api.ucenter;

import com.yok.framework.domain.course.CoursePub;
import com.yok.framework.domain.learning.request.ChooseCourseRequest;
import com.yok.framework.domain.ucenter.XcUser;
import com.yok.framework.domain.ucenter.ext.XcUserExt;
import com.yok.framework.domain.ucenter.request.UpdateMsgRequest;
import com.yok.framework.domain.ucenter.request.UserRequest;
import com.yok.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

@Api(value = "用户中心接口")
public interface UcenterControllerApi {
    @ApiOperation("根据用户账号查询用户信息")
    public XcUserExt getUserExt(String username);
    @ApiOperation("用户注册")
    public ResponseResult register(XcUser user);
    @ApiOperation("根据用户Id查询用户信息")
    public XcUser getUserMsg(UserRequest userRequset);
    @ApiOperation("根据用户Id查询用户信息")
    public Map<String, XcUser> findUserById(UserRequest userRequset);
    @ApiOperation("修改用户信息")
    public ResponseResult updateUserMsg(UpdateMsgRequest msgRequest);

}
