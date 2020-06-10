package com.yok.framework.domain.order.request;

import com.yok.framework.model.request.RequestData;
import lombok.Data;
import lombok.ToString;

/**
 * Created by mrt on 2020/2/26.
 */
@Data
@ToString
public class CreateOrderRequest extends RequestData {

    String courseId;
    String userId;
}
