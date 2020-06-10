package com.yok.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.yok.framework.model.response.CommonCode;
import com.yok.framework.model.response.ResponseResult;
import com.yok.govern.gateway.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Repository
public class LoginFilter extends ZuulFilter {
    @Autowired
    AuthService authService;

    //过滤器的类型
    @Override
    public String filterType() {
        /**
         pre：请求在被路由之前执行
         routing：在路由请求时调用
         post：在routing和errror过滤器之后调用
         error：处理请求时发生错误调用

         */
        return "pre";
    }

    //过滤器序号，越小越被优先执行
    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        //返回true表示要执行此过滤器
        return true;
    }

    //过滤器的内容
    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        //得到request
        HttpServletRequest request = requestContext.getRequest();
        //得到Url
        String requestURL = request.getRequestURL().toString();
        //让获取课程评分的请求不被拒绝访问二拦截到登录页面
        String targetUrl = "http://api_server_pool/api/learning/course/getCourseScore";
        if (targetUrl.equals(requestURL)){
            return null;
        }
        //取cookie中的身份令牌
        String token = authService.getTokenFromCookie(request);
        if (StringUtils.isEmpty(token)) {
            //拒绝访问
            access_denied();
            return null;
        }
        //从header中取jwt
        String header = authService.getJwtFromHeader(request);
        if (StringUtils.isEmpty(header)) {
            //拒绝访问
            access_denied();
            return null;
        }
        //从redis取出jwt的过期时间
        long expire = authService.getExpire(token);
        if (expire < 0) {
            //拒绝访问
            access_denied();
            return null;
        }
        return null;
    }

    //拒绝访问
    private void access_denied() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        //得到response
        HttpServletResponse response = requestContext.getResponse();
        //拒绝访问
        requestContext.setSendZuulResponse(false);
        //设置响应代码
        requestContext.setResponseStatusCode(200);
        //构建响应的信息
        ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);
        //转成json
        String jsonString = JSON.toJSONString(responseResult);
        requestContext.setResponseBody(jsonString);
        //转成json，设置contentType
        response.setContentType("application/json;charset=utf-8");
    }
}
