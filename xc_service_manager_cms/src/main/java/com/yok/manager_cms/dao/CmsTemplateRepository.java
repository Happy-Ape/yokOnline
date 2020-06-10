package com.yok.manager_cms.dao;

import com.yok.framework.domain.cms.CmsSite;
import com.yok.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CmsTemplateRepository extends MongoRepository<CmsTemplate,String> {
    CmsTemplate findByTemplateNameAndSiteId(String templateName,String siteId);
}
