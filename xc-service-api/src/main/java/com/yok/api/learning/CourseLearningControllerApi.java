package com.yok.api.learning;

import com.yok.framework.domain.course.Teachplan;
import com.yok.framework.domain.learning.QuestionNode;
import com.yok.framework.domain.learning.request.ChooseCourseRequest;
import com.yok.framework.domain.learning.request.CommentRequest;
import com.yok.framework.domain.learning.request.NotesRequest;
import com.yok.framework.domain.learning.request.QuestionRequest;
import com.yok.framework.domain.learning.response.GetMediaResult;
import com.yok.framework.domain.learning.response.LearningResult;
import com.yok.framework.model.response.QueryResponseResult;
import com.yok.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "录播课程学习管理", description = "录播课程学习管理")
public interface CourseLearningControllerApi {

    @ApiOperation("获取课程学习地址")
    public GetMediaResult getMedia(String courseId, String teachplanId);

    @ApiOperation("获取章节信息")
    public Teachplan getChapter(String teachplanId);

    @ApiOperation("查询用户选课信息")
    public LearningResult findChooseCourse(ChooseCourseRequest chooseCourseRequest);

    @ApiOperation("获取课程评分")
    public String getCourseScore(ChooseCourseRequest courseRequest);

    @ApiOperation("添加免费课程到我的选课列表")
    public ResponseResult addOpenCourse(ChooseCourseRequest chooseCourseRequest);

    @ApiOperation("添加收藏")
    public ResponseResult addCollection(ChooseCourseRequest chooseCourseRequest);

    @ApiOperation("取消收藏")
    public ResponseResult deleteCollection(ChooseCourseRequest chooseCourseRequest);

    @ApiOperation("查看收藏状态")
    public ResponseResult getCollectionStatus(ChooseCourseRequest chooseCourseRequest);

    @ApiOperation("我的选课列表")
    public QueryResponseResult MyCourseList(int page, int size, ChooseCourseRequest chooseCourseRequest);

    @ApiOperation("删除用户的选课")
    public ResponseResult deleteChooseCourse(ChooseCourseRequest chooseCourseRequest);

    @ApiOperation("我的收藏列表")
    public QueryResponseResult MyCollectionList(int page, int size, ChooseCourseRequest chooseCourseRequest);

    @ApiOperation("我的笔记列表")
    public QueryResponseResult MyNotesList(NotesRequest notesRequest);

    @ApiOperation("他人笔记列表")
    public QueryResponseResult OtherNotesList(NotesRequest notesRequest);

    @ApiOperation("添加我的笔记")
    public ResponseResult addMyNotes(NotesRequest notesRequest);

    @ApiOperation("删除我的笔记")
    public ResponseResult deleteMyNotes(String id);

    @ApiOperation("点赞笔记")
    public ResponseResult praise(String id);

    @ApiOperation("获取评论列表")
    public QueryResponseResult getCommentList(String courseId);

    @ApiOperation("发布评论")
    public ResponseResult addComment(CommentRequest commentRequest);

    @ApiOperation("删除我的评论")
    public ResponseResult deleteComment(String id,CommentRequest commentRequest);

    @ApiOperation("获取我的回答列表")
    public QueryResponseResult getMyQuestionList(QuestionRequest questionRequest);

    @ApiOperation("获取他人回答列表")
    public QueryResponseResult getOtherQuestionList(QuestionRequest questionRequest);

    @ApiOperation("发布问题")
    public ResponseResult addQuestion(QuestionRequest questionRequest);

    @ApiOperation("删除我的问题")
    public ResponseResult deleteQuestion(String id);

    @ApiOperation("发布答案")
    public ResponseResult addAnswer(QuestionRequest questionRequest);

    @ApiOperation("删除我的答案")
    public ResponseResult deleteAnswer(String id,QuestionRequest questionRequest);
}
