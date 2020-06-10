package com.yok.manager_cms.controller;

import com.yok.api.cms.CmsSiteControllerApi;
import com.yok.framework.domain.cms.CmsHtml;
import com.yok.framework.domain.cms.CmsPage;
import com.yok.framework.domain.cms.CmsSite;
import com.yok.framework.domain.cms.request.QuerySiteRequest;
import com.yok.framework.domain.cms.response.CmsPageResult;
import com.yok.framework.domain.cms.response.CmsSiteResult;
import com.yok.framework.model.response.QueryResponseResult;
import com.yok.framework.model.response.ResponseResult;
import com.yok.manager_cms.service.CmsPageService;
import com.yok.manager_cms.service.CmsSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cms/site")
public class CmsSiteController implements CmsSiteControllerApi {

    @Autowired
    CmsSiteService cmsSiteService;

    @Override
    @PreAuthorize("hasAuthority('xc_sysmanager')")
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size") int size, QuerySiteRequest querySiteRequest) {
        //调用service层
        return cmsSiteService.findList(page, size, querySiteRequest);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_sysmanager')")
    @PostMapping("/add")
    //@RequestBody post请求过来的是json数据，它会将json数据转成对象
    public CmsSiteResult add(@RequestBody CmsSite cmsSite) {
        return cmsSiteService.add(cmsSite);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_sysmanager')")
    @GetMapping("/get/{id}")
    public CmsSite findById(@PathVariable("id") String id) {
        return cmsSiteService.getById(id);
    }


    @Override
    @PreAuthorize("hasAuthority('xc_sysmanager')")
    @PutMapping("/edit/{id}")
    public CmsSiteResult update(@PathVariable("id") String id, @RequestBody CmsSite cmsSite) {
        return cmsSiteService.update(id, cmsSite);

    }

    @Override
    @PreAuthorize("hasAuthority('xc_sysmanager')")
    @DeleteMapping("/del/{id}")
    public ResponseResult delete(@PathVariable("id") String id) {
        return cmsSiteService.delete(id);
    }

}
