package com.yok.framework.domain.course.ext;

import com.yok.framework.domain.course.Teachplan;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Created by admin on 2020/2/7.
 */
@Data
@ToString
public class TeachplanNode extends Teachplan {

    List<TeachplanNode> children;
    //媒资文件id
    String mediaId;
    //媒资文件原始名称
    String mediaFileOriginalName;
}
