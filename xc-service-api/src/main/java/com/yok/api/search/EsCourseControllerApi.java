package com.yok.api.search;

import com.yok.framework.domain.course.CoursePub;
import com.yok.framework.domain.course.TeachplanMediaPub;
import com.yok.framework.domain.search.CourseSearchParam;
import com.yok.framework.model.response.QueryResponseResult;
import com.yok.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

/**
 * Created by Administrator.
 */
@Api(value = "课程搜索", description = "课程搜索")
public interface EsCourseControllerApi {
    //搜索课程信息
    @ApiOperation("课程综合搜索")
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam);

    @ApiOperation("根据课程id查询课程信息")
    public Map<String,CoursePub> getAll(String id);

    @ApiOperation("根据课程计划id查询课程媒资信息")
    public TeachplanMediaPub getMedia(String teachplanId);

    @ApiOperation("删除索引信息")
    public ResponseResult deleteIndex(String courseId);

    @ApiOperation("删除media索引信息")
    public ResponseResult deleteMediaIndex(String mediaId);

    @ApiOperation("根据id获取课程信息")
    public Map<String,CoursePub> getBase(String ids);

}
