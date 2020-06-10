package com.yok.learning.service;

import com.yok.framework.domain.course.CourseMarket;
import com.yok.framework.domain.course.Teachplan;
import com.yok.framework.domain.course.TeachplanMediaPub;
import com.yok.framework.domain.learning.*;
import com.yok.framework.domain.learning.request.ChooseCourseRequest;
import com.yok.framework.domain.learning.request.CommentRequest;
import com.yok.framework.domain.learning.request.NotesRequest;
import com.yok.framework.domain.learning.request.QuestionRequest;
import com.yok.framework.domain.learning.response.GetMediaResult;
import com.yok.framework.domain.learning.response.LearningCode;
import com.yok.framework.domain.learning.response.LearningResult;
import com.yok.framework.domain.task.XcTask;
import com.yok.framework.domain.task.XcTaskHis;
import com.yok.framework.exception.ExceptionCast;
import com.yok.framework.model.response.CommonCode;
import com.yok.framework.model.response.QueryResponseResult;
import com.yok.framework.model.response.QueryResult;
import com.yok.framework.model.response.ResponseResult;
import com.yok.learning.client.CourseClient;
import com.yok.learning.client.CourseSearchClient;
import com.yok.learning.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LearningService {

    @Autowired
    CourseSearchClient courseSearchClient;

    @Autowired
    XcLearningCourseRepository xcLearningCourseRepository;

    @Autowired
    XcTaskHisRepository xcTaskHisRepository;

    @Autowired
    LearningCourseMapper learningCourseMapper;

    @Autowired
    XcLearningCollectionRepository xcLearningCollectionRepository;

    @Autowired
    XcLearningNotesRepository xcLearningNotesRepository;

    @Autowired
    XcLearningCommentRepository xcLearningCommentRepository;

    @Autowired
    XcLearningQuestionRepository xcLearningQuestionRepository;

    @Autowired
    CourseClient courseClient;

    /**
     * 获取课程学习地址（视频播放地址）
     *
     * @param courseId
     * @param teachplanId
     * @return
     */
    public GetMediaResult getMedia(String courseId, String teachplanId) {
        //远程调用搜索服务查询课程计划所对应的课程媒资信息
        TeachplanMediaPub media = courseSearchClient.getMedia(teachplanId);
        if (media == null || StringUtils.isEmpty(media.getMediaUrl())) {
            //获取学习地址错误
            ExceptionCast.cast(LearningCode.LEARNING_GETMEDIA_ERROR);
        }
        String mediaUrl = media.getMediaUrl();
        GetMediaResult getMediaResult = new GetMediaResult(CommonCode.SUCCESS, mediaUrl);
        return getMediaResult;
    }

    /**
     * 添加选课
     *
     * @param userId
     * @param courseId
     * @param valid
     * @param startTime
     * @param endTime
     * @param xcTask
     * @return
     */
    @Transactional
    public ResponseResult addcourse(String userId, String courseId, String valid, String startTime, String endTime, XcTask xcTask) {
        if (StringUtils.isEmpty(courseId)) {
            ExceptionCast.cast(LearningCode.LEARNING_GETMEDIA_ERROR);
        }
        if (StringUtils.isEmpty(userId)) {
            ExceptionCast.cast(LearningCode.CHOOSECOURSE_USERISNULl);
        }
        if (xcTask == null || StringUtils.isEmpty(xcTask.getId())) {
            ExceptionCast.cast(LearningCode.CHOOSECOURSE_TASKISNULL);
        }
        XcLearningCourse xcLearningCourse = xcLearningCourseRepository.findByUserIdAndCourseId(userId, courseId);

        if (xcLearningCourse != null) {
            //更新选课记录
            //课程的开始时间
            xcLearningCourse.setStartTime(startTime);
            xcLearningCourse.setEndTime(endTime);
            xcLearningCourse.setValid(valid);
            xcLearningCourse.setStatus("501001");
            xcLearningCourse.setCreateTime(new Date());
            xcLearningCourseRepository.save(xcLearningCourse);
        } else {
            //添加新的选课记录
            xcLearningCourse = new XcLearningCourse();
            xcLearningCourse.setUserId(userId);
            xcLearningCourse.setCourseId(courseId);
            if (StringUtils.isEmpty(startTime)) {
                xcLearningCourse.setStartTime(null);
            } else {
                xcLearningCourse.setStartTime(startTime);
            }
            if (StringUtils.isEmpty(endTime)) {
                xcLearningCourse.setEndTime(null);
            } else {
                xcLearningCourse.setEndTime(endTime);
            }
            xcLearningCourse.setValid(valid);
            xcLearningCourse.setStatus("501001");
            xcLearningCourse.setCreateTime(new Date());
            xcLearningCourseRepository.save(xcLearningCourse);

        }
        //向历史任务表插入记录
        Optional<XcTaskHis> optional = xcTaskHisRepository.findById(xcTask.getId());
        if (!optional.isPresent()) {
            //添加历史任务
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask, xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 根据课程id和用户id查找相应的选课记录
     *
     * @param courseId
     * @param userId
     * @return
     */
    public LearningResult findChooseCourse(String courseId, String userId) {
        XcLearningCourse xcLearningCourse = xcLearningCourseRepository.findByUserIdAndCourseId(userId, courseId);
        if (xcLearningCourse == null) {
            return new LearningResult(CommonCode.FAIL, null);
        }
        return new LearningResult(CommonCode.SUCCESS, xcLearningCourse);
    }

    /**
     * 根据课程id和用户id添加免费课的选课记录
     *
     * @param courseId
     * @param userId
     * @return
     */
    public ResponseResult addOpenCourse(String courseId, String userId) {
        if (StringUtils.isEmpty(courseId) || StringUtils.isEmpty(userId)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        XcLearningCourse xcLearningCourse = new XcLearningCourse();
        //设置对象属性
        xcLearningCourse.setCourseId(courseId);
        xcLearningCourse.setUserId(userId);
        //远程调用课程微服务，获取课程营销信息
        CourseMarket courseMarket = courseClient.getCourseMarketById(courseId);
        xcLearningCourse.setValid(courseMarket.getValid());
        Date startTime = courseMarket.getStartTime();
        Date endTime = courseMarket.getEndTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTimeString;
        if (startTime != null) {
            startTimeString = dateFormat.format(startTime);
        }else{
            startTimeString = null;
        }
        String endTimeString;
        if (endTime != null) {
            endTimeString = dateFormat.format(endTime);
        }else{
            endTimeString = null;
        }
        xcLearningCourse.setStartTime(startTimeString);
        xcLearningCourse.setEndTime(endTimeString);
        xcLearningCourse.setStatus("501001");
        xcLearningCourse.setCreateTime(new Date());
        xcLearningCourseRepository.save(xcLearningCourse);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 添加收藏
     *
     * @param chooseCourseRequest
     * @return
     */
    public ResponseResult addCollection(ChooseCourseRequest chooseCourseRequest) {
        if (chooseCourseRequest == null
                || StringUtils.isEmpty(chooseCourseRequest.getCourseId())
                || StringUtils.isEmpty(chooseCourseRequest.getUserId())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //先看是否已经收藏
        XcLearningCollection collection = xcLearningCollectionRepository.findByUserIdAndCourseId(chooseCourseRequest.getUserId(), chooseCourseRequest.getCourseId());
        if (collection == null) {
            XcLearningCollection xcLearningCollection = new XcLearningCollection();
            xcLearningCollection.setCourseId(chooseCourseRequest.getCourseId());
            xcLearningCollection.setUserId(chooseCourseRequest.getUserId());
            xcLearningCollection.setCreateTime(new Date());
            xcLearningCollectionRepository.save(xcLearningCollection);
            return new ResponseResult(CommonCode.SUCCESS);
        } else {
            return new ResponseResult(LearningCode.COURSE_HAVE_COLLECTION);
        }
    }

    /**
     * 获取是否收藏的状态
     *
     * @param chooseCourseRequest
     * @return
     */
    public ResponseResult getCollectionStatus(ChooseCourseRequest chooseCourseRequest) {
        if (chooseCourseRequest == null
                || StringUtils.isEmpty(chooseCourseRequest.getCourseId())
                || StringUtils.isEmpty(chooseCourseRequest.getUserId())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        XcLearningCollection collection = xcLearningCollectionRepository.findByUserIdAndCourseId(chooseCourseRequest.getUserId(), chooseCourseRequest.getCourseId());
        if (collection != null) {
            return new ResponseResult(CommonCode.SUCCESS);
        } else {
            return new ResponseResult(CommonCode.FAIL);
        }
    }

    /**
     * 根据userid查询该用户的选课列表
     *
     * @param page
     * @param size
     * @param courseRequest
     * @return
     */
    public QueryResponseResult getCourseList(int page, int size, ChooseCourseRequest courseRequest) {
        if (courseRequest == null || StringUtils.isEmpty(courseRequest.getUserId())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String userId = courseRequest.getUserId();
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 1;
        }
        page -= 1;
        XcLearningCourse course = new XcLearningCourse();
        course.setUserId(userId);
        course.setStatus("501001");
        //定义example条件对象
        Example<XcLearningCourse> example = Example.of(course);
        //设置排序
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC,"createTime"));
        //设置分页参数        
        Pageable pageable = PageRequest.of(page, size, sort);
        //分页查询
        Page<XcLearningCourse> courseListPage = xcLearningCourseRepository.findAll(example, pageable);
        //查询列表         
        List<XcLearningCourse> list = courseListPage.getContent();
        //总记录数        
        long total = courseListPage.getTotalElements();
        //查询结果集         
        QueryResult<XcLearningCourse> courseIncfoQueryResult = new QueryResult<XcLearningCourse>();
        courseIncfoQueryResult.setList(list);
        courseIncfoQueryResult.setTotal(total);
        return new QueryResponseResult(CommonCode.SUCCESS, courseIncfoQueryResult);
    }

    /**
     * 删除用户的选课
     *
     * @param chooseCourseRequest
     * @return
     */
    public ResponseResult deleteChooseCourse(ChooseCourseRequest chooseCourseRequest) {
        if (chooseCourseRequest == null
                || StringUtils.isEmpty(chooseCourseRequest.getCourseId())
                || StringUtils.isEmpty(chooseCourseRequest.getUserId())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String courseId = chooseCourseRequest.getCourseId();
        String userId = chooseCourseRequest.getUserId();
        XcLearningCourse chooseCourse = xcLearningCourseRepository.findByUserIdAndCourseId(userId, courseId);
        if (chooseCourse != null) {
            xcLearningCourseRepository.delete(chooseCourse);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 查询收藏列表
     *
     * @param page
     * @param size
     * @param chooseCourseRequest
     * @return
     */
    public QueryResponseResult getCollectionList(int page, int size, ChooseCourseRequest chooseCourseRequest) {
        if (chooseCourseRequest == null || StringUtils.isEmpty(chooseCourseRequest.getUserId())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String userId = chooseCourseRequest.getUserId();
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 1;
        }
        page -= 1;
        XcLearningCollection collection = new XcLearningCollection();
        collection.setUserId(chooseCourseRequest.getUserId());
        //定义example条件对象
        Example<XcLearningCollection> example = Example.of(collection);
        //设置排序
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC,"createTime"));
        //设置分页参数        
        Pageable pageable = PageRequest.of(page, size, sort);
        //分页查询
        Page<XcLearningCollection> collectionListPage = xcLearningCollectionRepository.findAll(example, pageable);
        //查询列表         
        List<XcLearningCollection> list = collectionListPage.getContent();
        //总记录数        
        long total = collectionListPage.getTotalElements();
        //查询结果集         
        QueryResult<XcLearningCollection> result = new QueryResult<XcLearningCollection>();
        result.setList(list);
        result.setTotal(total);
        return new QueryResponseResult(CommonCode.SUCCESS, result);
    }

    /**
     * 取消收藏
     *
     * @param chooseCourseRequest
     * @return
     */
    public ResponseResult deleteCollection(ChooseCourseRequest chooseCourseRequest) {
        if (chooseCourseRequest == null
                || StringUtils.isEmpty(chooseCourseRequest.getCourseId())
                || StringUtils.isEmpty(chooseCourseRequest.getUserId())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //先找到收藏
        XcLearningCollection collection = xcLearningCollectionRepository.findByUserIdAndCourseId(chooseCourseRequest.getUserId(), chooseCourseRequest.getCourseId());
        if (collection != null) {
            xcLearningCollectionRepository.delete(collection);
            return new ResponseResult(CommonCode.SUCCESS);
        } else {
            return new ResponseResult(LearningCode.COURSE_NOT_COLLECTION);
        }
    }

    /**
     * 我的笔记列表
     *
     * @param notesRequest
     * @return
     */
    public QueryResponseResult MyNotesList(NotesRequest notesRequest) {
        if (notesRequest == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String courseId = notesRequest.getCourseId();
        String teachPlanId = notesRequest.getTeachPlanId();
        String userId = notesRequest.getUserId();
        if (StringUtils.isEmpty(courseId) || StringUtils.isEmpty(teachPlanId)
                || StringUtils.isEmpty(userId)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        List<XcLearningNotes> notes = xcLearningNotesRepository.findByUserIdAndCourseIdAndTeachPlanIdOrderByCreateTimeDesc(userId, courseId, teachPlanId);
        QueryResult<XcLearningNotes> queryResult = new QueryResult<>();
        queryResult.setList(notes);
        queryResult.setTotal(notes.size());
        QueryResponseResult result = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return result;
    }

    /**
     * 他人笔记列表
     *
     * @param notesRequest
     * @return
     */
    public QueryResponseResult OtherNotesList(NotesRequest notesRequest) {
        if (notesRequest == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String courseId = notesRequest.getCourseId();
        String teachPlanId = notesRequest.getTeachPlanId();
        String userId = notesRequest.getUserId();
        if (StringUtils.isEmpty(courseId) || StringUtils.isEmpty(teachPlanId)
                || StringUtils.isEmpty(userId)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        List<XcLearningNotes> notes = xcLearningNotesRepository.findAllByUserIdIsNotAndCourseIdAndTeachPlanIdOrderByCreateTimeDesc(userId, courseId, teachPlanId);
        QueryResult<XcLearningNotes> queryResult = new QueryResult<>();
        queryResult.setList(notes);
        queryResult.setTotal(notes.size());
        QueryResponseResult result = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return result;
    }

    /**
     * 添加我的笔记
     *
     * @param notesRequest
     * @return
     */
    public ResponseResult addMyNotes(NotesRequest notesRequest) {
        if (notesRequest == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String courseId = notesRequest.getCourseId();
        String teachPlanId = notesRequest.getTeachPlanId();
        String userId = notesRequest.getUserId();
        String notesContent = notesRequest.getNotes();
        if (StringUtils.isEmpty(courseId) || StringUtils.isEmpty(teachPlanId)
                || StringUtils.isEmpty(userId) || StringUtils.isEmpty(notesContent)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        XcLearningNotes notes = new XcLearningNotes();
        notes.setUserId(userId);
        notes.setCourseId(courseId);
        notes.setTeachPlanId(teachPlanId);
        notes.setNotes(notesContent);
        notes.setPraise(0);
        notes.setCreateTime(new Date());
        xcLearningNotesRepository.save(notes);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 获取章节信息
     *
     * @param teachplanId
     * @return
     */
    public Teachplan getChapter(String teachplanId) {
        if (StringUtils.isEmpty(teachplanId)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Teachplan teachplan = courseClient.getTeachplan(teachplanId);
        return teachplan;
    }

    /**
     * 删除我的笔记
     *
     * @param id
     * @return
     */
    public ResponseResult deleteMyNotes(String id) {
        if (StringUtils.isEmpty(id)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        xcLearningNotesRepository.deleteById(id);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 点赞笔记
     *
     * @param id 笔记的id
     * @return
     */
    public ResponseResult praise(String id) {
        if (StringUtils.isEmpty(id)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Optional<XcLearningNotes> optional = xcLearningNotesRepository.findById(id);
        if (optional.isPresent()) {
            XcLearningNotes notes = optional.get();
            notes.setPraise(notes.getPraise() + 1);
            xcLearningNotesRepository.save(notes);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    /**
     * 发布评论
     *
     * @param commentRequest
     * @return
     */
    public ResponseResult addComment(CommentRequest commentRequest) {
        if (commentRequest == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String userId = commentRequest.getUserId();
        String courseId = commentRequest.getCourseId();
        Float score = commentRequest.getScore();
        String comment = commentRequest.getComment();
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(courseId)
                || StringUtils.isEmpty(comment)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        if (score == null) {
            score = Float.valueOf(5.0f);
        }
        XcLearningComment xcLearningComment = new XcLearningComment();
        xcLearningComment.setUserId(userId);
        xcLearningComment.setCourseId(courseId);
        xcLearningComment.setScore(score);
        xcLearningComment.setComment(comment);
        xcLearningComment.setCreateTime(new Date());
        xcLearningCommentRepository.save(xcLearningComment);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 获取评论列表
     *
     * @param courseId
     * @return
     */
    public QueryResponseResult getCommentList(String courseId) {
        if (StringUtils.isEmpty(courseId)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        List<XcLearningComment> commentList = xcLearningCommentRepository.findByCourseIdOrderByCreateTimeDesc(courseId);
        QueryResult<XcLearningComment> queryResult = new QueryResult<>();
        queryResult.setList(commentList);
        queryResult.setTotal(commentList.size());
        QueryResponseResult result = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return result;
    }

    /**
     * 根据Id删除我的评论
     *
     * @param id
     * @param commentRequest
     * @return
     */
    public ResponseResult deleteComment(String id, CommentRequest commentRequest) {
        if (StringUtils.isEmpty(id) || commentRequest == null
                || StringUtils.isEmpty(commentRequest.getUserId())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Optional<XcLearningComment> optional = xcLearningCommentRepository.findById(id);
        if (optional.isPresent()) {
            XcLearningComment xcLearningComment = optional.get();
            String userId = xcLearningComment.getUserId();
            if (commentRequest.getUserId().equals(userId)) {
                xcLearningCommentRepository.deleteById(id);
            } else {
                ExceptionCast.cast(LearningCode.COMMENT_ISNOT_YOURS);
            }
        } else {
            ExceptionCast.cast(LearningCode.COMMENT_NOT_HAVE);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 获取课程评分
     *
     * @param courseRequest
     * @return
     */
    public String getCourseScore(ChooseCourseRequest courseRequest) {
        if (courseRequest == null) {
            return "5.0";
        }
        String courseId = courseRequest.getCourseId();
        if (StringUtils.isEmpty(courseId)) {
            return "5.0";
        }
        Float score = learningCourseMapper.getCouseScore(courseId);
        if (score == null) {
            return "5.0";
        }
        DecimalFormat df = new DecimalFormat(".#");
        String format = df.format(score);
        return format;
    }

    /**
     * 发布问题
     *
     * @param questionRequest
     * @return
     */
    public ResponseResult addQuestion(QuestionRequest questionRequest) {
        if (questionRequest == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String userId = questionRequest.getUserId();
        String courseId = questionRequest.getCourseId();
        String teachPlanId = questionRequest.getTeachPlanId();
        String title = questionRequest.getTitle();
        String content = questionRequest.getContent();
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(courseId)
                || StringUtils.isEmpty(teachPlanId)
                || StringUtils.isEmpty(title)
                || StringUtils.isEmpty(content)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        XcLearningQuestion question = new XcLearningQuestion();
        question.setUserId(userId);
        question.setCourseId(courseId);
        question.setTeachPlanId(teachPlanId);
        question.setTitle(title);
        question.setContent(content);
        question.setAskId("0");
        question.setCreateTime(new Date());
        xcLearningQuestionRepository.save(question);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 删除问题
     *
     * @param id
     * @return
     */
    @Transactional
    public ResponseResult deleteQuestion(String id) {
        if (StringUtils.isEmpty(id)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        int i = xcLearningQuestionRepository.deleteByIdOrAskId(id, id);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 获取我的问答列表
     *
     * @param questionRequest
     * @return
     */
    public QueryResponseResult getMyQuestionList(QuestionRequest questionRequest) {
        if (questionRequest == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String userId = questionRequest.getUserId();
        String courseId = questionRequest.getCourseId();
        String teachPlanId = questionRequest.getTeachPlanId();
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(courseId)
                || StringUtils.isEmpty(teachPlanId)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        List<QuestionNode> questionList = learningCourseMapper.getMyQuestionList(userId, courseId, teachPlanId);
        QueryResult<QuestionNode> queryResult = new QueryResult();
        queryResult.setTotal(questionList.size());
        queryResult.setList(questionList);
        QueryResponseResult result = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return result;
    }

    /**
     * 获取他人问答列表
     *
     * @param questionRequest
     * @return
     */
    public QueryResponseResult getOtherQuestionList(QuestionRequest questionRequest) {
        if (questionRequest == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String userId = questionRequest.getUserId();
        String courseId = questionRequest.getCourseId();
        String teachPlanId = questionRequest.getTeachPlanId();
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(courseId)
                || StringUtils.isEmpty(teachPlanId)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        List<QuestionNode> questionList = learningCourseMapper.getOtherQuestionList(userId, courseId, teachPlanId);
        QueryResult<QuestionNode> queryResult = new QueryResult();
        queryResult.setTotal(questionList.size());
        queryResult.setList(questionList);
        QueryResponseResult result = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return result;
    }

    /**
     * 发布答案
     *
     * @param questionRequest
     * @return
     */
    public ResponseResult addAnswer(QuestionRequest questionRequest) {
        if (questionRequest == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String userId = questionRequest.getUserId();
        String courseId = questionRequest.getCourseId();
        String teachPlanId = questionRequest.getTeachPlanId();
        String askId = questionRequest.getAskId();
        String content = questionRequest.getContent();
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(courseId)
                || StringUtils.isEmpty(teachPlanId)
                || StringUtils.isEmpty(askId)
                || StringUtils.isEmpty(content)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        XcLearningQuestion question = new XcLearningQuestion();
        question.setUserId(userId);
        question.setCourseId(courseId);
        question.setTeachPlanId(teachPlanId);
        question.setCreateTime(new Date());
        question.setAskId(askId);
        question.setContent(content);
        xcLearningQuestionRepository.save(question);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 删除我的答案
     *
     * @param id
     * @param questionRequest
     * @return
     */
    public ResponseResult deleteAnswer(String id, QuestionRequest questionRequest) {
        if (StringUtils.isEmpty(id) || questionRequest == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String userId = questionRequest.getUserId();
        if (StringUtils.isEmpty(userId)){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Optional<XcLearningQuestion> optional = xcLearningQuestionRepository.findById(id);
        if (!optional.isPresent()){
            ExceptionCast.cast(LearningCode.QUESTION_NOT_HAVE);
        }
        XcLearningQuestion question = optional.get();
        if (!userId.equals(question.getUserId())){
            ExceptionCast.cast(LearningCode.QUESTION_ISNOT_YOURS);
        }
        xcLearningQuestionRepository.deleteById(id);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
