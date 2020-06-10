package com.yok.api.media;

import com.yok.framework.domain.media.MediaFile;
import com.yok.framework.domain.media.request.QueryMediaFileRequest;
import com.yok.framework.model.response.QueryResponseResult;
import com.yok.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "媒体文件管理",description = "媒体文件管理接口",tags = {"媒体文件管理接口"})
public interface MediaFileControllerApi {

    @ApiOperation("我的媒资文件查询列表")
    public QueryResponseResult<MediaFile> findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest);

    @ApiOperation("重新处理文件上传")
    public ResponseResult process(String id);

    @ApiOperation("删除媒资文件")
    public ResponseResult delete(String id);
}

