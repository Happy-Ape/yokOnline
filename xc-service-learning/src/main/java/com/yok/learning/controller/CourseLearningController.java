package com.yok.learning.controller;

import com.yok.api.learning.CourseLearningControllerApi;
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
import com.yok.learning.service.LearningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/learning/course")
public class CourseLearningController implements CourseLearningControllerApi {

    @Autowired
    LearningService learningService;

    @Override
    @GetMapping("/getmedia/{courseId}/{teachplanId}")
    public GetMediaResult getMedia(@PathVariable("courseId") String courseId, @PathVariable("teachplanId") String teachplanId) {
        return learningService.getMedia(courseId, teachplanId);
    }

    @Override
    @PostMapping("/getchoosecourse")
    public LearningResult findChooseCourse(@RequestBody ChooseCourseRequest chooseCourseRequest) {
        return learningService.findChooseCourse(chooseCourseRequest.getCourseId(), chooseCourseRequest.getUserId());
    }

    @Override
    @PostMapping("/addopencourse")
    public ResponseResult addOpenCourse(@RequestBody ChooseCourseRequest chooseCourseRequest) {
        return learningService.addOpenCourse(chooseCourseRequest.getCourseId(), chooseCourseRequest.getUserId());
    }

    @Override
    @PostMapping("/getCourseScore")
    public String getCourseScore(@RequestBody ChooseCourseRequest courseRequest) {
        return learningService.getCourseScore(courseRequest);
    }

    @Override
    @PostMapping("/collection")
    public ResponseResult addCollection(@RequestBody ChooseCourseRequest chooseCourseRequest) {
        return learningService.addCollection(chooseCourseRequest);
    }

    @Override
    @PostMapping("/collect_list/{page}/{size}")
    public QueryResponseResult MyCollectionList(@PathVariable("page") int page, @PathVariable("size") int size, @RequestBody ChooseCourseRequest chooseCourseRequest) {
        return learningService.getCollectionList(page,size,chooseCourseRequest);
    }

    @Override
    @PostMapping("/cancelCollection")
    public ResponseResult deleteCollection(@RequestBody ChooseCourseRequest chooseCourseRequest) {
        return learningService.deleteCollection(chooseCourseRequest);
    }

    @Override
    @PostMapping("/getcollectstatus")
    public ResponseResult getCollectionStatus(@RequestBody ChooseCourseRequest chooseCourseRequest) {
        return learningService.getCollectionStatus(chooseCourseRequest);
    }

    @Override
    @PostMapping("/course_list/{page}/{size}")
    public QueryResponseResult MyCourseList(@PathVariable("page") int page, @PathVariable("size") int size, @RequestBody ChooseCourseRequest courseRequest) {
        return learningService.getCourseList(page, size, courseRequest);

    }

    @Override
    @PostMapping("/deleteCourse")
    public ResponseResult deleteChooseCourse(@RequestBody ChooseCourseRequest chooseCourseRequest) {
        return learningService.deleteChooseCourse(chooseCourseRequest);
    }

    @Override
    @PostMapping("/chapter/getMyNotes")
    public QueryResponseResult MyNotesList(@RequestBody NotesRequest notesRequest) {
        return learningService.MyNotesList(notesRequest);
    }

    @Override
    @PostMapping("/chapter/getOtherNotes")
    public QueryResponseResult OtherNotesList(@RequestBody NotesRequest notesRequest) {
        return learningService.OtherNotesList(notesRequest);
    }

    @Override
    @PostMapping("/chapter/publishNotes")
    public ResponseResult addMyNotes(@RequestBody NotesRequest notesRequest) {
        return learningService.addMyNotes(notesRequest);
    }

    @Override
    @DeleteMapping("/chapter/deleteNotes/{id}")
    public ResponseResult deleteMyNotes(@PathVariable("id") String id) {
        return learningService.deleteMyNotes(id);
    }

    @Override
    @GetMapping("/getChapter/{chapter}")
    public Teachplan getChapter(@PathVariable("chapter") String teachplanId) {
        return learningService.getChapter(teachplanId);
    }

    @Override
    @PostMapping("/notes/praise/{id}")
    public ResponseResult praise(@PathVariable("id") String id) {
        return learningService.praise(id);
    }

    @Override
    @PostMapping("/comment/getComment/{courseId}")
    public QueryResponseResult getCommentList(@PathVariable("courseId") String courseId) {
        return learningService.getCommentList(courseId);
    }

    @Override
    @PostMapping("/comment/publishComment")
    public ResponseResult addComment(@RequestBody CommentRequest commentRequest) {
        return learningService.addComment(commentRequest);
    }

    @Override
    @PostMapping("/comment/deleteComment/{id}")
    public ResponseResult deleteComment(@PathVariable("id") String id, @RequestBody CommentRequest commentRequest) {
        return learningService.deleteComment(id,commentRequest);
    }

    @Override
    @PostMapping("/question/getMyQuestion")
    public QueryResponseResult getMyQuestionList(@RequestBody QuestionRequest questionRequest) {
        return learningService.getMyQuestionList(questionRequest);
    }

    @Override
    @PostMapping("/question/getOtherQuestion")
    public QueryResponseResult getOtherQuestionList(@RequestBody QuestionRequest questionRequest) {
        return learningService.getOtherQuestionList(questionRequest);
    }

    @Override
    @PostMapping("/question/publishQuestion")
    public ResponseResult addQuestion(@RequestBody QuestionRequest questionRequest) {
        return learningService.addQuestion(questionRequest);
    }

    @Override
    @DeleteMapping("/question/deleteQuestion/{id}")
    public ResponseResult deleteQuestion(@PathVariable("id") String id) {
        return learningService.deleteQuestion(id);
    }

    @Override
    @PostMapping("/question/publishAnswer")
    public ResponseResult addAnswer(@RequestBody QuestionRequest questionRequest) {
        return learningService.addAnswer(questionRequest);
    }

    @Override
    @PostMapping("/question/deleteAnswer/{id}")
    public ResponseResult deleteAnswer(@PathVariable("id") String id, @RequestBody QuestionRequest questionRequest) {
        return learningService.deleteAnswer(id,questionRequest);
    }
}
