package com.yok.manager_cms.controller;

import com.yok.api.cms.CmsTemplateControllerApi;
import com.yok.framework.domain.cms.CmsTemplate;
import com.yok.framework.domain.cms.request.QueryTemplateRequest;
import com.yok.framework.domain.cms.response.CmsTemplateResult;
import com.yok.framework.model.response.QueryResponseResult;
import com.yok.framework.model.response.ResponseResult;
import com.yok.manager_cms.service.CmsTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cms/template")
public class CmsTemplateController implements CmsTemplateControllerApi {

    @Autowired
    CmsTemplateService cmsTemplateService;

    @Override
    @PreAuthorize("hasAuthority('xc_sysmanager')")
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size") int size, QueryTemplateRequest queryTemplateRequest) {
        //调用service层
        return cmsTemplateService.findList(page, size, queryTemplateRequest);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_sysmanager')")
    @PostMapping("/add")
    //@RequestBody post请求过来的是json数据，它会将json数据转成对象
    public CmsTemplateResult add(@RequestBody CmsTemplate cmsTemplate) {
        return cmsTemplateService.add(cmsTemplate);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_sysmanager')")
    @GetMapping("/get/{id}")
    public CmsTemplate findById(@PathVariable("id") String id) {
        return cmsTemplateService.getById(id);
    }


    @Override
    @PreAuthorize("hasAuthority('xc_sysmanager')")
    @PutMapping("/edit/{id}")
    public CmsTemplateResult update(@PathVariable("id") String id, @RequestBody CmsTemplate cmsTemplate) {
        return cmsTemplateService.update(id, cmsTemplate);

    }

    @Override
    @PreAuthorize("hasAuthority('xc_sysmanager')")
    @DeleteMapping("/del/{id}")
    public ResponseResult delete(@PathVariable("id") String id) {
        return cmsTemplateService.delete(id);
    }

}
