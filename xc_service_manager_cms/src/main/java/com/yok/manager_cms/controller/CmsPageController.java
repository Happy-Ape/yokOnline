package com.yok.manager_cms.controller;

import com.yok.api.cms.CmsPageControllerApi;
import com.yok.framework.domain.cms.CmsHtml;
import com.yok.framework.domain.cms.CmsPage;
import com.yok.framework.domain.cms.request.QueryPageRequest;
import com.yok.framework.domain.cms.response.CmsPageResult;
import com.yok.framework.domain.cms.response.CmsPostPageResult;
import com.yok.framework.model.response.QueryResponseResult;
import com.yok.framework.model.response.ResponseResult;
import com.yok.manager_cms.service.CmsPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cms/page")
public class CmsPageController implements CmsPageControllerApi {

    @Autowired
    CmsPageService cmsPageService;

    @Override
    @PreAuthorize("hasAuthority('xc_sysmanager')")
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size") int size, QueryPageRequest queryPageRequest) {
        //调用service层
        return cmsPageService.findList(page, size, queryPageRequest);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_sysmanager')")
    @GetMapping("/site_list")
    public QueryResponseResult findSite() {
        return cmsPageService.findSite();
    }

    @Override
    @PreAuthorize("hasAuthority('xc_sysmanager')")
    @GetMapping("/template_list")
    public QueryResponseResult findTemplates() {
        return cmsPageService.findTemplate();
    }

    @Override
    @PostMapping("/add")
    //@RequestBody post请求过来的是json数据，它会将json数据转成对象
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {
        return cmsPageService.add(cmsPage);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_sysmanager')")
    @GetMapping("/get/{id}")
    public CmsPage findById(@PathVariable("id") String id) {
        return cmsPageService.getById(id);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_sysmanager')")
    @PutMapping("/edit/{id}")
    public CmsPageResult update(@PathVariable("id") String id, @RequestBody CmsPage cmsPage) {
        return cmsPageService.update(id,cmsPage);

    }

    @Override
    @PreAuthorize("hasAuthority('xc_sysmanager')")
    @DeleteMapping("/del/{id}")
    public ResponseResult delete(@PathVariable("id") String id) {
        return cmsPageService.delete(id);
    }

    @Override
    @GetMapping("/getHtml/{pageId}")
    public ResponseResult getHtml(@PathVariable("pageId") String pageId) {
        return cmsPageService.getHtml(pageId);
    }

    @Override
    @PostMapping("/generateHtml/{pageId}")
    public ResponseResult generateHtml(@PathVariable("pageId") String pageId, @RequestBody CmsHtml cmsHtml){
        return cmsPageService.generateHtml(pageId,cmsHtml.getHtmlValue());
    }

    @Override
    @PostMapping("/postPageQuick")
    public CmsPostPageResult postPageQuick(@RequestBody CmsPage cmsPage) {
        return cmsPageService.postPageQuick(cmsPage);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_sysmanager')")
    @PostMapping("/postPage/{pageId}")
    public ResponseResult post(@PathVariable("pageId") String pageId) {
        return cmsPageService.post(pageId);
    }

    @Override
    @PostMapping("/save")
    public CmsPageResult save(@RequestBody CmsPage cmsPage) {
        return cmsPageService.save(cmsPage);
    }
}
