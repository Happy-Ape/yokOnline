package com.yok.framework.domain.cms.response;

import com.yok.framework.domain.cms.CmsHtml;
import com.yok.framework.domain.cms.CmsPage;
import com.yok.framework.domain.cms.CmsSite;
import com.yok.framework.model.response.ResponseResult;
import com.yok.framework.model.response.ResultCode;
import lombok.Data;

/**
 * Created by mrt on 2020/1/31.
 */
@Data
public class CmsSiteResult extends ResponseResult {
   CmsSite cmsSite;
    public CmsSiteResult(ResultCode resultCode,CmsSite cmsSite) {
        super(resultCode);
        this.cmsSite = cmsSite;
    }
}
