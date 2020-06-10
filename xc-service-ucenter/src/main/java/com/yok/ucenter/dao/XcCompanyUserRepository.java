package com.yok.ucenter.dao;

import com.yok.framework.domain.ucenter.XcCompany;
import com.yok.framework.domain.ucenter.XcCompanyUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcCompanyUserRepository extends JpaRepository<XcCompanyUser,String> {
    //根据userid查询XcCompanyUser
    public XcCompanyUser findByUserId(String id);
}
