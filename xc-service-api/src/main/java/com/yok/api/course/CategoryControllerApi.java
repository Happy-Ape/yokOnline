package com.yok.api.course;

import com.yok.framework.domain.course.ext.CategoryNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="课程分类管理",description="课程分类管理")
public interface CategoryControllerApi {
    @ApiOperation("查询分类")
    public CategoryNode findList();
}
