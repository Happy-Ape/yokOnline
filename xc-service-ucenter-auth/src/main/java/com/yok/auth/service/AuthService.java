package com.yok.auth.service;

import com.alibaba.fastjson.JSON;
import com.yok.auth.client.UserClient;
import com.yok.framework.client.YokServiceList;
import com.yok.framework.domain.learning.request.ChooseCourseRequest;
import com.yok.framework.domain.ucenter.XcUser;
import com.yok.framework.domain.ucenter.ext.AuthToken;
import com.yok.framework.domain.ucenter.response.AuthCode;
import com.yok.framework.exception.ExceptionCast;
import com.yok.framework.model.response.CommonCode;
import com.yok.framework.model.response.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    UserClient userClient;

    @Value("${auth.tokenValiditySeconds}")
    int tokenValiditySeconds;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    /**
     * 用户认证申请令牌，将令牌存储到redis
     *
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @return
     */
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        //请求spring security申请令牌
        AuthToken authToken = this.applyToken(username, password, clientId, clientSecret);
        if (authToken == null) {
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }
        //将令牌存储到redis
        String access_token = authToken.getAccess_token();
        String jsonString = JSON.toJSONString(authToken);
        boolean saveToken = saveToken(access_token, jsonString, tokenValiditySeconds);
        if (!saveToken) {
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_TOKEN_SAVEFAIL);
        }
        return authToken;
    }

    /**
     * 将令牌存储到redis
     *
     * @param access_token 用户身份令牌
     * @param content      内容就是AuthToken对象的内容
     * @param ttl          过期时间
     * @return
     */
    private boolean saveToken(String access_token, String content, long ttl) {
        String key = "user_token:" + access_token;
        stringRedisTemplate.boundValueOps(key).set(content, ttl, TimeUnit.SECONDS);
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire > 0;
    }

    /**
     * 删除redis中的令牌，实现退出
     *
     * @param access_token
     * @return
     */
    public boolean delToken(String access_token) {
        String key = "user_token:" + access_token;
        stringRedisTemplate.delete(key);
        return true;
    }

    /**
     * 拿身份令牌,从redis中查询jwt令牌
     *
     * @param access_token
     * @return
     */
    public AuthToken getUserToken(String access_token) {
        String key = "user_token:" + access_token;
        String token = stringRedisTemplate.opsForValue().get(key);
        try {
            AuthToken authToken = JSON.parseObject(token, AuthToken.class);
            return authToken;
        } catch (Exception e) {
            LOGGER.error("getUserToken from redis and execute JSON.parseObject error  {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用户认证申请令牌
     *
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @return
     */
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {
        //从eureka中获取认证服务的地址（因为spring security在认证服务中）
        //从eureka中获取认证服务的一个实例的地址
        ServiceInstance serviceInstance = loadBalancerClient.choose(YokServiceList.XC_SERVICE_UCENTER_AUTH);
        //此地址就是http://ip:port
        URI uri = serviceInstance.getUri();
        //令牌申请的地址 http://localhost:40400/auth/oauth/token
        String authUrl = uri + "/auth/oauth/token";
        //定义header
        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        String httpBasic = getHttpBasic(clientId, clientSecret);
        header.add("Authorization", httpBasic);

        //定义body
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("username", username);
        body.add("password", password);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);
        //设置restTemplate远程调用时候，对400和401不让报错，正确返回数据
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(response);
                }
            }
        });

        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);

        //申请令牌信息
        Map bodyMap = exchange.getBody();
        //判断令牌信息
        if (bodyMap == null || bodyMap.get("access_token") == null
                || bodyMap.get("refresh_token") == null
                || bodyMap.get("jti") == null) {
            if (bodyMap != null && bodyMap.get("error_description") != null) {
                String description = (String) bodyMap.get("error_description");
                //账号不存在返回的信息：error_description -> UserDetailsService returned null, which is an interface contract violation
                if (description.indexOf("UserDetailsService returned null") >= 0) {
                    ExceptionCast.cast(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
                    //密码错误返回的信息：error_description ->
                } else if (description.indexOf("坏的凭证") >= 0) {
                    ExceptionCast.cast(AuthCode.AUTH_CREDENTIAL_ERROR);
                }
            }
            return null;
        }
        //取出数据封装返回
        AuthToken authToken = new AuthToken();
        authToken.setJwt_token((String) bodyMap.get("access_token"));//jwt令牌
        authToken.setAccess_token((String) bodyMap.get("jti"));//用户身份令牌
        authToken.setRefresh_token((String) bodyMap.get("refresh_token"));//刷新令牌
        return authToken;

    }

    /**
     * 获取httpbasic的串
     *
     * @param clientId
     * @param clientSecret
     * @return
     */
    private String getHttpBasic(String clientId, String clientSecret) {
        String string = clientId + ":" + clientSecret;
        //将串进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic " + new String(encode);
    }

}
