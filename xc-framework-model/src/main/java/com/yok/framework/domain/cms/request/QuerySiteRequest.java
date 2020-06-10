package com.yok.framework.domain.cms.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Administrator
 * @version 1.0
 * @create 2020-01-12 14:59
 **/
@Data
public class QuerySiteRequest {
    //接收页面查询的查询条件
    //站点id
    @ApiModelProperty("站点id")
    private String siteId;
    //站点名称
    private String siteName;

    //....
}
