package com.yok.manage_media.controller;

import com.yok.api.media.MediaFileControllerApi;
import com.yok.framework.domain.media.MediaFile;
import com.yok.framework.domain.media.request.QueryMediaFileRequest;
import com.yok.framework.model.response.QueryResponseResult;
import com.yok.framework.model.response.ResponseResult;
import com.yok.manage_media.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/media/file")
public class MediaFileController implements MediaFileControllerApi {

    @Autowired
    MediaFileService mediaFileService;

    @Override
    @PreAuthorize("hasAuthority('xc_teachmanager_course')")
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult<MediaFile> findList( int page,  int size, QueryMediaFileRequest queryMediaFileRequest) {
        return mediaFileService.findList(page,size,queryMediaFileRequest);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_teachmanager_course')")
    @PostMapping("/process/{id}")
    public ResponseResult process(@PathVariable("id") String id) {
        return mediaFileService.process(id);
    }

    @Override
    @PreAuthorize("hasAuthority('xc_teachmanager_course')")
    @DeleteMapping("/delete/{id}")
    public ResponseResult delete(@PathVariable("id") String id) {
        return mediaFileService.delete(id);
    }
}
