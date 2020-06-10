package com.yok.api.cms;

import com.yok.framework.domain.cms.CmsTemplate;
import com.yok.framework.domain.cms.request.QueryTemplateRequest;
import com.yok.framework.domain.cms.response.CmsTemplateResult;
import com.yok.framework.model.response.QueryResponseResult;
import com.yok.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
@Api(value = "cms模板管理接口", description = "cms模板管理接口，提供模板的增、删、改、查")
public interface CmsTemplateControllerApi {
    @ApiOperation("分页查询模板列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页记录数", required = true, paramType = "path", dataType = "int")
    })
    public QueryResponseResult findList(int page, int size, QueryTemplateRequest queryTemplateRequest);

    @ApiOperation("新增模板")
    public CmsTemplateResult add(CmsTemplate cmsTemplate);

    //根据模板id查询模板信息
    @ApiOperation("根据模板id查询模板信息")
    public CmsTemplate findById(String id);

    //修改模板
    @ApiOperation("修改模板")
    public CmsTemplateResult update(String id,CmsTemplate cmsTemplate);

    //删除模板
    @ApiOperation("删除模板")
    public ResponseResult delete(String id);

}
