package com.yok.manager_cms.service;

import com.yok.framework.domain.cms.CmsSite;
import com.yok.framework.domain.cms.request.QuerySiteRequest;
import com.yok.framework.domain.cms.response.CmsCode;
import com.yok.framework.domain.cms.response.CmsSiteResult;
import com.yok.framework.exception.ExceptionCast;
import com.yok.framework.model.response.CommonCode;
import com.yok.framework.model.response.QueryResponseResult;
import com.yok.framework.model.response.QueryResult;
import com.yok.framework.model.response.ResponseResult;
import com.yok.manager_cms.dao.CmsSiteRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CmsSiteService {

    @Autowired
    CmsSiteRepository cmsSiteRepository;

    /**
     * 分页查询数据
     *
     * @param page             页码，从1开始，所以处理的时候要-1
     * @param size             一页的容量
     * @param querySiteRequest 查询条件
     * @return
     */
    public QueryResponseResult findList(int page, int size, QuerySiteRequest querySiteRequest) {
        //自定义条件查询：
        //如果queryPageRequest传进来是null的话，我们就new一个出来
        if (querySiteRequest == null) {
            querySiteRequest = new QuerySiteRequest();
        }
        //定义条件查询匹配器：ExampleMatcher.GenericPropertyMatchers.contains()——包含匹配
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("siteName", ExampleMatcher.GenericPropertyMatchers.contains());
        //查询条件值对象
        CmsSite cmsSite = new CmsSite();
        //设置条件值：
        //设置站点名称作为查询条件
        if (StringUtils.isNotEmpty(querySiteRequest.getSiteName())) {
            cmsSite.setSiteName(querySiteRequest.getSiteName());
        }
        //定义example条件对象
        Example<CmsSite> example = Example.of(cmsSite, exampleMatcher);
        //判断page
        if (page <= 0) {
            page = 1;
        }
        page -= 1;
        //判断容量
        if (size <= 0) {
            size = 6;
        }
        //定义分页参数
        Pageable pageable = PageRequest.of(page, size);
        //自定义条件查询，并且根据分页查询
        Page<CmsSite> all = cmsSiteRepository.findAll(example, pageable);
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all.getContent()); //数据列表
        queryResult.setTotal(all.getTotalElements());  //数据总记录数
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return queryResponseResult;
    }

    /**
     * 新增站点
     *
     * @param cmsSite
     * @return
     */
    public CmsSiteResult add(CmsSite cmsSite) {
        if (cmsSite == null) {
            //抛出异常，非法参数异常..指定异常信息的内容
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //根据站点名去查询cms_site集合，如果查到说明此站点已经存在，如果查询不到再继续添加
        CmsSite site = cmsSiteRepository.findBySiteName(cmsSite.getSiteName());
        if (site != null) {
            //站点已经存在
            //抛出异常，异常内容就是站点已经存在
            ExceptionCast.cast(CmsCode.CMS_ADDSITE_EXISTSNAME);
        }
        //调用dao新增站点
        cmsSite.setSiteId(null);
        cmsSiteRepository.save(cmsSite);
        return new CmsSiteResult(CommonCode.SUCCESS, cmsSite);
    }

    /**
     * 根据id查询站点
     *
     * @param id
     * @return
     */
    public CmsSite getById(String id) {
        //通过id找，返回的是一个optional
        Optional<CmsSite> optional = cmsSiteRepository.findById(id);
        if (optional.isPresent()) {
            CmsSite cmsSite = optional.get();
            return cmsSite;
        }
        return null;
    }

    /**
     * 更新站点
     *
     * @param id
     * @param cmsSite
     * @return
     */
    public CmsSiteResult update(String id, CmsSite cmsSite) {
        //根据id从数据库查询站点信息
        CmsSite one = this.getById(id);
        if (one != null) {
            //准备更新数据
            //设置要修改的数据
            //更新站点名称
            one.setSiteName(cmsSite.getSiteName());
            //更新站点Domain
            one.setSiteDomain(cmsSite.getSiteDomain());
            //更新站点端口
            one.setSitePort(cmsSite.getSitePort());
            //更新访问路径
            one.setSiteWebPath(cmsSite.getSiteWebPath());
            CmsSite site = cmsSiteRepository.findBySiteName(one.getSiteName());
            //提交修改
            cmsSiteRepository.save(one);
            return new CmsSiteResult(CommonCode.SUCCESS, one);
        }
        //修改失败
        return new CmsSiteResult(CommonCode.FAIL, null);
    }

    /**
     * 删除页面
     *
     * @param id
     * @return
     */
    public ResponseResult delete(String id) {
        //先查询有没有
        Optional<CmsSite> optional = cmsSiteRepository.findById(id);
        //如果有
        if (optional.isPresent()) {
            //就删除
            cmsSiteRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

}
