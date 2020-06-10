package com.yok.api.cms;

import com.yok.framework.domain.cms.CmsSite;
import com.yok.framework.domain.cms.request.QuerySiteRequest;
import com.yok.framework.domain.cms.response.CmsSiteResult;
import com.yok.framework.model.response.QueryResponseResult;
import com.yok.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
@Api(value = "cms站点管理接口", description = "cms站点管理接口，提供站点的增、删、改、查")
public interface CmsSiteControllerApi {
    @ApiOperation("分页查询站点列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页记录数", required = true, paramType = "path", dataType = "int")
    })
    public QueryResponseResult findList(int page, int size, QuerySiteRequest querySiteRequest);
    @ApiOperation("新增站点")
    public CmsSiteResult add(CmsSite cmsSite);

    //根据站点id查询站点信息
    @ApiOperation("根据站点id查询站点信息")
    public CmsSite findById(String id);

    //修改站点
    @ApiOperation("修改站点")
    public CmsSiteResult update(String id,CmsSite cmsSite);

    //删除站点
    @ApiOperation("删除站点")
    public ResponseResult delete(String id);

}
