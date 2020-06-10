package com.yok.manage_media.service;

import com.yok.framework.domain.media.MediaFile;
import com.yok.framework.domain.media.request.QueryMediaFileRequest;
import com.yok.framework.domain.media.response.MediaCode;
import com.yok.framework.exception.ExceptionCast;
import com.yok.framework.model.response.CommonCode;
import com.yok.framework.model.response.QueryResponseResult;
import com.yok.framework.model.response.QueryResult;
import com.yok.framework.model.response.ResponseResult;
import com.yok.manage_media.client.CourseClient;
import com.yok.manage_media.client.SeachClient;
import com.yok.manage_media.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
public class MediaFileService {

    @Autowired
    MediaFileRepository mediaFileRepository;

    @Autowired
    MediaUploadService uploadService;

    @Autowired
    SeachClient seachClient;

    @Autowired
    CourseClient courseClient;

    @Value("${xc-service-manage-media.upload-location}")
    String location;

    /**
     * 查询我的媒资列表
     *
     * @param page
     * @param size
     * @param queryMediaFileRequest
     * @return
     */
    public QueryResponseResult<MediaFile> findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest) {
        if (queryMediaFileRequest == null) {
            queryMediaFileRequest = new QueryMediaFileRequest();
        }
        //条件值对象
        MediaFile mediaFile = new MediaFile();
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getFileOriginalName())) {
            mediaFile.setFileOriginalName(queryMediaFileRequest.getFileOriginalName());
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getProcessStatus())) {
            mediaFile.setProcessStatus(queryMediaFileRequest.getProcessStatus());
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getTag())) {
            mediaFile.setTag(queryMediaFileRequest.getTag());
        }
        //条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("fileOriginalName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("tag", ExampleMatcher.GenericPropertyMatchers.contains());
        //定义Example条件查询对象
        Example<MediaFile> example = Example.of(mediaFile, exampleMatcher);
        //处理参数
        if (page <= 0) {
            page = 1;
        }
        page -= 1;
        if (size <= 0) {
            size = 6;
        }
        //定义分页对象
        Pageable pageable = new PageRequest(page, size);
        //查询所有数据
        Page<MediaFile> all = mediaFileRepository.findAll(example, pageable);
        long totalElements = all.getTotalElements(); //总条数
        List<MediaFile> content = all.getContent();  //数据列表
        //包装返回对象
        QueryResult<MediaFile> queryResult = new QueryResult<>();
        queryResult.setTotal(totalElements);
        queryResult.setList(content);
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return queryResponseResult;
    }

    /**
     * 重新处理上传(重新向mq发消息请求）
     *
     * @param id
     * @return
     */
    public ResponseResult process(String id) {
        Optional<MediaFile> optional = mediaFileRepository.findById(id);
        if (!optional.isPresent()) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        MediaFile mediaFile = optional.get();
        String status = mediaFile.getProcessStatus();
        if (("303002").equals(status)) {
            return new ResponseResult(MediaCode.MERGE_FILE_STATUS_SUCCESS);
        }
        ResponseResult responseResult = uploadService.sendProcessVideoMsg(id);
        return responseResult;
    }

    /**
     * 删除媒资文件
     * (需要删除mongodb的media_file,
     * mysql的teachplan_media、teachplan_media_pub,
     * es的xc_course_media,
     * 视频文件)
     *
     * @param id
     * @return
     */
    public ResponseResult delete(String id) {
        //删除mongodb的media_file
        Optional<MediaFile> optional = mediaFileRepository.findById(id);
        if (!optional.isPresent()) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        mediaFileRepository.deleteById(id);
        //删除ES数据
        seachClient.deleteMedia(id);
        //删除mysql的teachplan_media、teachplan_media_pub
        courseClient.deleteMedia(id);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
