package com.yok.framework.domain.ucenter.request;

import com.yok.framework.model.request.RequestData;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdateMsgRequest extends RequestData {
    String value;
    String userId;
    String flag;
}
