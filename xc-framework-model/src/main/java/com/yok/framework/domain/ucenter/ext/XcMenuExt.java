package com.yok.framework.domain.ucenter.ext;

import com.yok.framework.domain.course.ext.CategoryNode;
import com.yok.framework.domain.ucenter.XcMenu;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Created by admin on 2020/3/20.
 */
@Data
@ToString
public class XcMenuExt extends XcMenu {

    List<CategoryNode> children;
}
