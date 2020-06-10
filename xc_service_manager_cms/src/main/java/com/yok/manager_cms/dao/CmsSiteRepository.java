package com.yok.manager_cms.dao;

import com.yok.framework.domain.cms.CmsPage;
import com.yok.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CmsSiteRepository extends MongoRepository<CmsSite,String> {
    //根据页面名称查询
    CmsSite findBySiteName(String siteName);
}
