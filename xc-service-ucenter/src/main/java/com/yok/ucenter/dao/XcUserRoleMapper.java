package com.yok.ucenter.dao;

import com.yok.framework.domain.ucenter.XcUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface XcUserRoleMapper {
    public void save( XcUserRole xcUserRole);
}
