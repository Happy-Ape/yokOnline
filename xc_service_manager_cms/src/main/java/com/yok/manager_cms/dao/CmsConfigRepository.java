package com.yok.manager_cms.dao;

import com.yok.framework.domain.cms.CmsConfig;
import com.yok.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CmsConfigRepository extends MongoRepository<CmsConfig,String> {

}
