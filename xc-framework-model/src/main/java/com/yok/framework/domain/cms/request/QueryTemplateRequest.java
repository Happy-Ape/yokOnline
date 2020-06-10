package com.yok.framework.domain.cms.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Administrator
 * @version 1.0
 * @create 2020-01-12 14:59
 **/
@Data
public class QueryTemplateRequest {
    //接收模板查询的查询条件
    //模板id
    @ApiModelProperty("模板id")
    private String templateId;
    //模板名称
    private String templateName;

    //....
}
