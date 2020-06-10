package com.yok.framework.domain.order.request;

import com.yok.framework.model.request.RequestData;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ListOrderRequest extends RequestData {

    String status;
    String userId;
}
