package com.yok.ucenter.dao;


import com.yok.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcUserRepository extends JpaRepository<XcUser,String> {
    //根据username查询xcuser
    public XcUser findByUsername(String username);
}
