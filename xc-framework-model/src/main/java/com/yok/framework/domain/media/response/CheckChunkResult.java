package com.yok.framework.domain.media.response;

import com.yok.framework.model.response.ResponseResult;
import com.yok.framework.model.response.ResultCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by admin on 2020/1/5.
 */
@Data
@ToString
@NoArgsConstructor
public class CheckChunkResult extends ResponseResult {

    public CheckChunkResult(ResultCode resultCode, boolean fileExist) {
        super(resultCode);
        this.fileExist = fileExist;
    }
    @ApiModelProperty(value = "文件分块存在标记", example = "true", required = true)
    boolean fileExist;
}
