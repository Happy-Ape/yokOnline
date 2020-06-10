package com.yok.manager_cms.service;

import com.yok.framework.domain.cms.CmsSite;
import com.yok.framework.domain.cms.CmsTemplate;
import com.yok.framework.domain.cms.request.QueryTemplateRequest;
import com.yok.framework.domain.cms.response.CmsCode;
import com.yok.framework.domain.cms.response.CmsSiteResult;
import com.yok.framework.domain.cms.response.CmsTemplateResult;
import com.yok.framework.exception.ExceptionCast;
import com.yok.framework.model.response.CommonCode;
import com.yok.framework.model.response.QueryResponseResult;
import com.yok.framework.model.response.QueryResult;
import com.yok.framework.model.response.ResponseResult;
import com.yok.manager_cms.dao.CmsTemplateRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CmsTemplateService {

    @Autowired
    CmsTemplateRepository cmsTemplateRepository;

    /**
     * 分页查询数据
     *
     * @param page                 页码，从1开始，所以处理的时候要-1
     * @param size                 一页的容量
     * @param queryTemplateRequest 查询条件
     * @return
     */
    public QueryResponseResult findList(int page, int size, QueryTemplateRequest queryTemplateRequest) {
        //自定义条件查询：
        //如果queryPageRequest传进来是null的话，我们就new一个出来
        if (queryTemplateRequest == null) {
            queryTemplateRequest = new QueryTemplateRequest();
        }
        //定义条件查询匹配器：ExampleMatcher.GenericPropertyMatchers.contains()——包含匹配
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("templateName", ExampleMatcher.GenericPropertyMatchers.contains());
        //查询条件值对象
        CmsTemplate cmsTemplate = new CmsTemplate();
        //设置条件值：
        //设置模板名称作为查询条件
        if (StringUtils.isNotEmpty(queryTemplateRequest.getTemplateName())) {
            cmsTemplate.setTemplateName(queryTemplateRequest.getTemplateName());
        }
        //定义example条件对象
        Example<CmsTemplate> example = Example.of(cmsTemplate, exampleMatcher);
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
        Page<CmsTemplate> all = cmsTemplateRepository.findAll(example, pageable);
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all.getContent()); //数据列表
        queryResult.setTotal(all.getTotalElements());  //数据总记录数
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return queryResponseResult;
    }

    /**
     * 新增模板
     *
     * @param cmsTemplate
     * @return
     */
    public CmsTemplateResult add(CmsTemplate cmsTemplate) {
        if (cmsTemplate == null) {
            //抛出异常，非法参数异常..指定异常信息的内容
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //根据模板Id去查询cms_template集合，如果查到说明此模板已经存在，如果查询不到再继续添加
        CmsTemplate cmsTemplate1 = cmsTemplateRepository.findByTemplateNameAndSiteId(cmsTemplate.getTemplateName(), cmsTemplate.getSiteId());
        if (cmsTemplate1 != null) {
            //模板已经存在
            //抛出异常，异常内容就是模板已经存在
            ExceptionCast.cast(CmsCode.CMS_ADDTEMPLATE_EXISTSNAME);
        }
        //调用dao新增页面
        cmsTemplate.setTemplateId(null);
        cmsTemplateRepository.save(cmsTemplate);
        return new CmsTemplateResult(CommonCode.SUCCESS, cmsTemplate);
    }

    /**
     * 根据id查询模板
     *
     * @param id
     * @return
     */
    public CmsTemplate getById(String id) {
        //通过id找，返回的是一个optional
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(id);
        if (optional.isPresent()) {
            CmsTemplate cmsTemplate = optional.get();
            return cmsTemplate;
        }
        return null;
    }

    /**
     * 更新模板
     *
     * @param id
     * @param cmsTemplate
     * @return
     */
    public CmsTemplateResult update(String id, CmsTemplate cmsTemplate) {
        //根据id从数据库查询模板信息
        CmsTemplate one = this.getById(id);
        if (one != null) {
            //准备更新数据
            //设置要修改的数据
            //更新名称
            one.setTemplateName(cmsTemplate.getTemplateName());
            //更新站点ID
            one.setSiteId(cmsTemplate.getSiteId());
            //更新参数
            one.setTemplateParameter(cmsTemplate.getTemplateParameter());
            //更新模板文件ID
            one.setTemplateFileId(cmsTemplate.getTemplateFileId());
            //提交修改
            cmsTemplateRepository.save(one);
            return new CmsTemplateResult(CommonCode.SUCCESS, one);
        }
        //修改失败
        return new CmsTemplateResult(CommonCode.FAIL, null);
    }

    /**
     * 删除模板
     *
     * @param id
     * @return
     */
    public ResponseResult delete(String id) {
        //先查询有没有
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(id);
        //如果有
        if (optional.isPresent()) {
            //就删除
            cmsTemplateRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }
}
