package com.yok.ucenter.dao;

import com.yok.framework.domain.ucenter.XcCompanyUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface XcCompanyUserMapper {
    public void save(XcCompanyUser xcCompanyUser);
}
