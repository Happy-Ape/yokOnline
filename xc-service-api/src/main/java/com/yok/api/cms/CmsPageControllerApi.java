package com.yok.api.cms;

import com.yok.framework.domain.cms.CmsHtml;
import com.yok.framework.domain.cms.CmsPage;
import com.yok.framework.domain.cms.request.QueryPageRequest;
import com.yok.framework.domain.cms.response.CmsPageResult;
import com.yok.framework.domain.cms.response.CmsPostPageResult;
import com.yok.framework.model.response.QueryResponseResult;
import com.yok.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;

@Api(value = "cms页面管理接口", description = "cms页面管理接口，提供页面的增、删、改、查")
public interface CmsPageControllerApi {
    //页面查询
    @ApiOperation("分页查询页面列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页记录数", required = true, paramType = "path", dataType = "int")
    })
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);

    @ApiOperation("查询站站点列表")
    public QueryResponseResult findSite();

    @ApiOperation("查询站模板列表")
    public QueryResponseResult findTemplates();

    @ApiOperation("新增页面")
    public CmsPageResult add(CmsPage cmsPage);

    //根据页面id查询页面信息
    @ApiOperation("根据页面id查询页面信息")
    public CmsPage findById(String id);

    //修改页面
    @ApiOperation("修改页面")
    public CmsPageResult update(String id, CmsPage cmsPage);

    //删除页面
    @ApiOperation("删除页面")
    public ResponseResult delete(String id);

    //获取静态化页面
    @ApiOperation("获取静态化页面")
    public ResponseResult getHtml(String pageId);

    //修改静态化页面
    @ApiOperation("获取静态化页面")
    public ResponseResult generateHtml(String pageId, CmsHtml cmsHtml);

    //发布页面
    @ApiOperation("发布页面")
    public ResponseResult post(String pageId);

    @ApiOperation("保存页面")
    public CmsPageResult save(CmsPage cmsPage);

    @ApiOperation("一键发布页面")
    public CmsPostPageResult postPageQuick(CmsPage cmsPage);

}
