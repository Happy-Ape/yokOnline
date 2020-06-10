package com.yok.manager_cms.dao;

import com.yok.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysDictionaryDao extends MongoRepository<SysDictionary,String> {
    //根据字典分类查询字典信息      
    SysDictionary findBydType(String dType);
}
