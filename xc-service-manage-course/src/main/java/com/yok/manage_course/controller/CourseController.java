package com.yok.manage_course.controller;

import com.yok.api.course.CourseControllerApi;
import com.yok.framework.domain.course.*;
import com.yok.framework.domain.course.ext.CourseView;
import com.yok.framework.domain.course.ext.TeachplanNode;
import com.yok.framework.domain.course.request.CourseListRequest;
import com.yok.framework.domain.course.response.AddCourseResult;
import com.yok.framework.domain.course.response.CoursePublishResult;
import com.yok.framework.model.response.CommonCode;
import com.yok.framework.model.response.QueryResponseResult;
import com.yok.framework.model.response.ResponseResult;
import com.yok.framework.utils.XcOauth2Util;
import com.yok.framework.web.BaseController;
import com.yok.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Id;

@RestController
@RequestMapping("/course")
public class CourseController extends BaseController implements CourseControllerApi {

    @Autowired
    CourseService courseService;

    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachplanList(@PathVariable("courseId") String courseId) {
        return courseService.findTeachplanList(courseId);
    }

    @Override
    @GetMapping("/teachplan/get/{id}")
    public Teachplan getTeachplan(@PathVariable("id") String id) {
        return courseService.getTeachplan(id);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_teachmanager_course')")
    @DeleteMapping("/teachplan/delete/{id}")
    public ResponseResult deleteTeachplan(@PathVariable("id") String id) {
        return courseService.deleteTeachplan(id);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_teachmanager_course')")
    @PostMapping("/down/{id}")
    public ResponseResult down(@PathVariable("id") String id) {
        return courseService.down(id);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_teachmanager_course')")
    @DeleteMapping("/delete/{id}")
    public ResponseResult deleteCourse(@PathVariable("id") String id) {
        return courseService.deleteCourse(id);
    }

    @Override
    @PostMapping("/savemedia")
    public ResponseResult saveMedia(@RequestBody TeachplanMedia teachplanMedia) {
        return courseService.saveMedia(teachplanMedia);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_teachmanager_course')")
    @PostMapping("/up/{id}")
    public ResponseResult up(@PathVariable("id") String id) {
        return courseService.up(id);
    }

    @Override
    @PostMapping("/reject/{courseId}")
    public ResponseResult rejectMediaIsAll(@PathVariable("courseId") String courseId) {
        return courseService.rejectMediaIsAll(courseId);
    }

    @Override
    @PostMapping("/deletemedia/{mediaId}")
    public ResponseResult deleteMedia(@PathVariable("mediaId") String mediaId) {
        return courseService.deleteMedia(mediaId);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_teachmanager_course')")
    @PostMapping("/teachplan/add/{id}")
    public ResponseResult addTeachplan(@PathVariable("id") String id, @RequestBody Teachplan teachplan) {
        return courseService.addTeachplan(id, teachplan);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_teachmanager_course')")
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult findCourseList(@PathVariable("page") int page, @PathVariable("size") int size, CourseListRequest courseListRequest) {
        XcOauth2Util xcOauth2Util = new XcOauth2Util();
        XcOauth2Util.UserJwt jwt = xcOauth2Util.getUserJwtFromHeader(request);
        String company_id = jwt.getCompanyId();
        return courseService.findCourseList(company_id, page, size, courseListRequest);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_teachmanager_course')")
    @PostMapping("/coursebase/add")
    public AddCourseResult addCourseBase(@RequestBody CourseBase courseBase) {
        return courseService.addCourseBase(courseBase);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_teachmanager_course')")
    @GetMapping("/coursebase/get/{courseId}")
    public CourseBase getCourseBaseById(@PathVariable("courseId") String courseId) {
        return courseService.getCoursebaseById(courseId);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_teachmanager_course')")
    @PutMapping("/coursebase/update/{id}")
    public ResponseResult updateCourseBase(@PathVariable("id") String id, @RequestBody CourseBase courseBase) {
        return courseService.updateCoursebase(id, courseBase);
    }

    @Override
    @GetMapping("/coursemarket/get/{courseId}")
    public CourseMarket getCourseMarketById(@PathVariable("courseId") String courseId) {
        return courseService.getCourseMarketById(courseId);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_teachmanager_course')")
    @PostMapping("/coursemarket/update/{id}")
    public ResponseResult updateCourseMarket(@PathVariable("id") String id, @RequestBody CourseMarket courseMarket) {
        CourseMarket courseMarket_u = courseService.updateCourseMarket(id, courseMarket);
        if (courseMarket_u != null) {
            return new ResponseResult(CommonCode.SUCCESS);
        } else {
            return new ResponseResult(CommonCode.FAIL);
        }
    }

    @Override
    @PreAuthorize("hasAuthority('xc_teachmanager_course')")
    @PostMapping("/publish/{id}")
    public CoursePublishResult publish(@PathVariable("id") String id) {
        return courseService.publish(id);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_teachmanager_course')")
    @PostMapping("/coursepic/add")
    public ResponseResult addCoursePic(@RequestParam("courseId") String courseId, @RequestParam("pic") String pic) {
        return courseService.addCoursePic(courseId, pic);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_teachmanager_course')")
    @GetMapping("/coursepic/list/{courseId}")
    public CoursePic findCoursePic(@PathVariable("courseId") String courseId) {
        return courseService.findCoursePic(courseId);
    }

    @Override
    @GetMapping("/courseview/{id}")
    public CourseView courseview(@PathVariable("id") String id) {
        return courseService.getCourseView(id);
    }

    @Override
    @PostMapping("/preview/{id}")
    public CoursePublishResult preview(@PathVariable("id") String id) {
        return courseService.preview(id);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_teachmanager_course')")
    @DeleteMapping("/coursepic/delete")
    public ResponseResult deleteCoursePic(@RequestParam("courseId") String courseId) {
        return courseService.deleteCoursePic(courseId);
    }
}
