package com.yok.govern.gateway.service;

import com.yok.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.RegEx;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 从头取出jwt令牌
     *
     * @param request
     * @return
     */
    public String getJwtFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.isEmpty(header)) {
            return null;
        }
        if (!header.startsWith("Bearer ")) {
            return null;
        }
        //取到jwt令牌
        String jwt = header.substring(7);
        return jwt;
    }

    /**
     * 从cookie取出token
     *
     * @param request
     * @return
     */
    public String getTokenFromCookie(HttpServletRequest request) {
        Map<String, String> cookieMap = CookieUtil.readCookie(request, "uid");
        String access_token = cookieMap.get("uid");
        if (StringUtils.isEmpty(access_token)) {
            return null;
        }
        return access_token;
    }

    /**
     * 查询令牌的有效期
     *
     * @param access_token
     * @return
     */
    public long getExpire(String access_token) {
        String key = "user_token:" + access_token;
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire;
    }
}
