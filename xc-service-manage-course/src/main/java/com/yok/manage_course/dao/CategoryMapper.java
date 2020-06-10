package com.yok.manage_course.dao;

import com.yok.framework.domain.course.ext.CategoryNode;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Repository;
@Repository
@Mapper
public interface CategoryMapper {
    //查询分类    
    public CategoryNode selectList();
}
