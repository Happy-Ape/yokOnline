package com.yok.framework.domain.course.ext;

import com.yok.framework.domain.course.Category;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Created by admin on 2020/2/7.
 */
@Data
@ToString
public class CategoryNode extends Category {

    List<CategoryNode> children;

}
