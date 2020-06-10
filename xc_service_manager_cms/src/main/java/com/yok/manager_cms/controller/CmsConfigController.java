package com.yok.manager_cms.controller;

import com.yok.api.cms.CmsConfigControllerApi;
import com.yok.framework.domain.cms.CmsConfig;
import com.yok.framework.domain.cms.CmsPage;
import com.yok.manager_cms.service.CmsPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cms/config")
public class CmsConfigController implements CmsConfigControllerApi {

    @Autowired
    CmsPageService cmsPageService;

    @Override
    @GetMapping("/getmodel/{id}")
    public CmsConfig getmodel(@PathVariable("id") String id) {
        return cmsPageService.getConfigById(id);
    }
}
