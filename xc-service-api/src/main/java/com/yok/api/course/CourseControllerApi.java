package com.yok.api.course;

import com.yok.framework.domain.course.*;
import com.yok.framework.domain.course.ext.CourseView;
import com.yok.framework.domain.course.ext.TeachplanNode;
import com.yok.framework.domain.course.request.CourseListRequest;
import com.yok.framework.domain.course.response.AddCourseResult;
import com.yok.framework.domain.course.response.CoursePublishResult;
import com.yok.framework.model.response.QueryResponseResult;
import com.yok.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Created by Administrator.
 */

@Api(value = "课程管理接口", description = "课程管理接口，提供课程的增、删、改、查")
public interface CourseControllerApi {
    @ApiOperation("课程计划查询")
    public TeachplanNode findTeachplanList(String courseId);

    @ApiOperation("添加课程计划")
    public ResponseResult addTeachplan(String id,Teachplan teachplan);

    @ApiOperation("删除课程计划")
    public ResponseResult deleteTeachplan(String id);

    @ApiOperation("获得课程计划")
    public Teachplan getTeachplan(String id);

    @ApiOperation("查询我的课程列表")
    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest);

    @ApiOperation("添加课程基础信息")
    public AddCourseResult addCourseBase(CourseBase courseBase);

    @ApiOperation("添加课程图片(与mongoDB关联)")
    public ResponseResult addCoursePic(String courseId,String pic);

    @ApiOperation("查询课程图片")
    public CoursePic findCoursePic(String courseId);

    @ApiOperation("删除课程图片")
    public ResponseResult deleteCoursePic(String courseId);

    @ApiOperation("获取课程基础信息")
    public CourseBase getCourseBaseById(String courseId);

    @ApiOperation("更新课程基础信息")
    public ResponseResult updateCourseBase(String id,CourseBase courseBase);

    @ApiOperation("获取课程营销信息")
    public CourseMarket getCourseMarketById(String courseId);

    @ApiOperation("更新课程营销信息")
    public ResponseResult updateCourseMarket(String id,CourseMarket courseMarket);

    @ApiOperation("课程视图查询")
    public CourseView courseview(String id);

    @ApiOperation("课程预览")
    public CoursePublishResult preview(String id);

    @ApiOperation("课程发布")
    public CoursePublishResult publish(String id);

    @ApiOperation("课程下线")
    public ResponseResult down(String id);

    @ApiOperation("课程上线")
    public ResponseResult up(String id);

    @ApiOperation("删除课程")
    public ResponseResult deleteCourse(String id);

    @ApiOperation("保存课程计划与媒资文件的关联信息")
    public ResponseResult saveMedia(TeachplanMedia teachplanMedia);

    @ApiOperation("判断是否全部关联课程")
    public ResponseResult rejectMediaIsAll(String courseId);

    @ApiOperation("删除媒资：删除teaplan_media和teaplan_media_pub的数据")
    public ResponseResult deleteMedia(String mediaId);
}
