package com.yok.framework.domain.cms.response;

import com.yok.framework.domain.cms.CmsSite;
import com.yok.framework.domain.cms.CmsTemplate;
import com.yok.framework.model.response.ResponseResult;
import com.yok.framework.model.response.ResultCode;
import lombok.Data;

/**
 * Created by mrt on 2020/1/31.
 */
@Data
public class CmsTemplateResult extends ResponseResult {
    CmsTemplate cmsTemplate;
    public CmsTemplateResult(ResultCode resultCode, CmsTemplate cmsTemplate) {
        super(resultCode);
        this.cmsTemplate = cmsTemplate;
    }
}
