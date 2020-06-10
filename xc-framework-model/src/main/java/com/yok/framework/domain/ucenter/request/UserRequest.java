package com.yok.framework.domain.ucenter.request;

import com.yok.framework.model.request.RequestData;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserRequest extends RequestData {
    String userId;
}
