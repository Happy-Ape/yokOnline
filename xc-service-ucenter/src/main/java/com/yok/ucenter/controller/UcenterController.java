package com.yok.ucenter.controller;

import com.yok.api.ucenter.UcenterControllerApi;
import com.yok.framework.domain.learning.request.ChooseCourseRequest;
import com.yok.framework.domain.ucenter.XcUser;
import com.yok.framework.domain.ucenter.ext.XcUserExt;
import com.yok.framework.domain.ucenter.request.UpdateMsgRequest;
import com.yok.framework.domain.ucenter.request.UserRequest;
import com.yok.framework.model.response.ResponseResult;
import com.yok.ucenter.service.UcenterService;
import org.omg.PortableInterceptor.USER_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ucenter")
public class UcenterController implements UcenterControllerApi {
    @Autowired
    UcenterService ucenterService;

    @Override
    @GetMapping("/getuserext")
    public XcUserExt getUserExt(@RequestParam("username") String username) {
        return ucenterService.getUserExt(username);
    }
    @Override
    @PostMapping("/getUserMsg")
    public XcUser getUserMsg(@RequestBody UserRequest userRequset) {
        return ucenterService.getUserMag(userRequset);
    }

    @Override
    @PostMapping("/updateUserMsg")
    public ResponseResult updateUserMsg(@RequestBody UpdateMsgRequest msgRequest) {
        return ucenterService.updateUserMsg(msgRequest);
    }

    @Override
    @PostMapping("/register")
    public ResponseResult register(XcUser user) {
        return ucenterService.register(user);
    }

    @Override
    @PostMapping("/findUserById")
    public Map<String, XcUser> findUserById(@RequestBody UserRequest userRequset) {
        return ucenterService.findUserById(userRequset);
    }
}


