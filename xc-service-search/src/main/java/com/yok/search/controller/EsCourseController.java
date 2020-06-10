package com.yok.search.controller;

import com.yok.api.search.EsCourseControllerApi;
import com.yok.framework.domain.course.CoursePub;
import com.yok.framework.domain.course.TeachplanMediaPub;
import com.yok.framework.domain.search.CourseSearchParam;
import com.yok.framework.model.response.CommonCode;
import com.yok.framework.model.response.QueryResponseResult;
import com.yok.framework.model.response.QueryResult;
import com.yok.framework.model.response.ResponseResult;
import com.yok.search.service.EsCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 **/
@RestController
@RequestMapping("/search/course")
public class EsCourseController implements EsCourseControllerApi {
    @Autowired
    EsCourseService esCourseService;

    @Override
    @GetMapping(value = "/list/{page}/{size}")
    public QueryResponseResult<CoursePub> list(@PathVariable("page") int page, @PathVariable("size") int size, CourseSearchParam courseSearchParam) {
        return esCourseService.list(page, size, courseSearchParam);
    }

    @Override
    @GetMapping("/getall/{id}")
    public Map<String, CoursePub> getAll(@PathVariable("id") String id) {
        return esCourseService.getAll(id);
    }

    @Override
    @PostMapping("/delete/{courseId}")
    public ResponseResult deleteIndex(@PathVariable("courseId") String courseId) {
        return esCourseService.deleteIndex(courseId);
    }

    @Override
    @PostMapping("/deletemedia/{mediaId}")
    public ResponseResult deleteMediaIndex(@PathVariable("mediaId") String mediaId) {
        return esCourseService.deleteMediaIndex(mediaId);
    }

    @Override
    @GetMapping("/getbase/{ids}")
    public Map<String, CoursePub> getBase(@PathVariable("ids") String ids) {
        return esCourseService.getBase(ids);
    }

    @Override
    @GetMapping("/getmedia/{teachplanId}")
    public TeachplanMediaPub getMedia(@PathVariable("teachplanId") String teachplanId) {
        String[] teachplanIds = new String[]{teachplanId};
        QueryResponseResult<TeachplanMediaPub> queryResponseResult = esCourseService.getMedia(teachplanIds);
            QueryResult<TeachplanMediaPub> queryResult = queryResponseResult.getQueryResult();
            if (queryResult != null) {
                List<TeachplanMediaPub> list = queryResult.getList();
                if (list != null && list.size() > 0) {
                    return list.get(0);
                }
            }
        return new TeachplanMediaPub();
    }

}
