package com.yok.auth.controller;

import com.yok.api.auth.AuthControllerApi;
import com.yok.auth.service.AuthService;
import com.yok.framework.domain.learning.request.ChooseCourseRequest;
import com.yok.framework.domain.ucenter.XcUser;
import com.yok.framework.domain.ucenter.ext.AuthToken;
import com.yok.framework.domain.ucenter.request.LoginRequest;
import com.yok.framework.domain.ucenter.response.AuthCode;
import com.yok.framework.domain.ucenter.response.JwtResult;
import com.yok.framework.domain.ucenter.response.LoginResult;
import com.yok.framework.exception.ExceptionCast;
import com.yok.framework.model.response.CommonCode;
import com.yok.framework.model.response.ResponseResult;
import com.yok.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/")
public class AuthController implements AuthControllerApi {

    @Value("${auth.clientId}")
    String clientId;

    @Value("${auth.clientSecret}")
    String clientSecret;

    @Value("${auth.cookieDomain}")
    String cookieDomain;

    @Value("${auth.cookieMaxAge}")
    int cookieMaxAge;

    @Autowired
    AuthService authService;

    @Override
    @PostMapping("/userlogin")
    public LoginResult login(LoginRequest loginRequest) {
        if (loginRequest == null || StringUtils.isEmpty(loginRequest.getUsername())) {
            ExceptionCast.cast(AuthCode.AUTH_USERNAME_NONE);
        }
        if (loginRequest == null || StringUtils.isEmpty(loginRequest.getPassword())) {
            ExceptionCast.cast(AuthCode.AUTH_PASSWORD_NONE);
        }
        //账号
        String username = loginRequest.getUsername();
        //密码
        String password = loginRequest.getPassword();
        //申请存储令牌
        AuthToken authToken = authService.login(username, password, clientId, clientSecret);
        //用户身份令牌
        String access_token = authToken.getAccess_token();
        //保存身份令牌到cookie
        this.saveCookie(access_token);
        return new LoginResult(CommonCode.SUCCESS, access_token);
    }

    @Override
    @GetMapping("/userjwt")
    public JwtResult userjwt() {
        //取出cookie中的身份令牌
        String access_token = this.getTokenFormCookie();
        if (access_token == null){
            return new JwtResult(CommonCode.FAIL,null);
        }
        //拿身份令牌从redis中查询jwt令牌
        AuthToken token = authService.getUserToken(access_token);
        if (token != null){
            //将jwt令牌返回给用户
            String jwt_token = token.getJwt_token();
            return new JwtResult(CommonCode.SUCCESS,jwt_token);
        }
        return null;
    }

    private String getTokenFormCookie() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, String> cookieMap = CookieUtil.readCookie(request,"uid");
        if (cookieMap != null && cookieMap.get("uid") != null) {
            String access_token = cookieMap.get("uid");
            return access_token;
        }
        return null;
    }

    private void saveCookie(String token) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, cookieMaxAge, false);
    }

    private void clearCookie(String token) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, 0, false);
    }

    @Override
    @PostMapping("/userlogout")
    public ResponseResult logout() {
        //出cookie中渠道身份令牌
        String cookie = this.getTokenFormCookie();
        //删除redis数据
        authService.delToken(cookie);
        //删除cookie
        this.clearCookie(cookie);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
